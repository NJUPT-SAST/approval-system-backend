package fun.sast.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fun.sast.Exception.BaseException;
import fun.sast.constant.RedisKeyConstant;
import fun.sast.entity.File;
import fun.sast.entity.FileUploadCache;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.enums.UserRoleEnum;
// import fun.sast.interceptor.UserInterceptor;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.mapper.FileMapper;
import fun.sast.service.FileService;
import fun.sast.utils.FileUtil;
import fun.sast.utils.OSSUtil;
import fun.sast.utils.RedisUtil;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileMapper fileMapper;
    private final OSSUtil ossUtil;
    private final FileUtil fileUtil;
    private final RedisUtil redisUtil;

    @Value("${file.OSS.bucket-url-prefix:}")
    String prefix;

    /**
     * @param url 文件存储的url，如http://baiyaoshi.oss-cn-hangzhou.aliyuncs.com/文本.txt,在这里实现身份判断
     * @return 可以直接用于下载的凭证
     */
    @Override
    public String getDownloadCertificate(String url) {
        // 获取user身份信息
        User user = UserInterceptor.userHolder.get();

        if (user == null) {
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }

        // 解码
        url = URLDecoder.decode(url, StandardCharsets.UTF_8);

        // 提取 objectKey
        String objectKey = url.startsWith(prefix) ? url.substring(prefix.length()) : url;

        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", objectKey);
        File file = fileMapper.selectOne(queryWrapper);

        if (file == null) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }

        // 权限控制
        if (user.getRole().equals(UserRoleEnum.TOURIST.getRole())) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }

        if (user.getRole().equals(UserRoleEnum.STUDENT.getRole())
                && !user.getCode().equals(file.getUserCode())) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }

        return ossUtil.getDownloadCertificate(url);
    }

    /**
     * @param user 用户信息
     * @param comId 比赛id
     * @param content 文件内容
     * @param title 文件标题
     */
    @Override
    public void processSubmissionFiles(User user, Long comId, String content, String title) {
        String key = RedisKeyConstant.getWorkFileCacheKey(user.getCode(), title);

        if (!redisUtil.hasKey(key)) throw new BaseException(ErrorEnum.FILE_EXPIRED_ERROR);

        File file =
                fileMapper.selectOne(
                        new LambdaQueryWrapper<File>()
                                .eq(File::getComId, comId)
                                .eq(File::getUserCode, user.getCode())
                                .eq(File::getInput, title));
        FileUploadCache cache =
                JSON.parseObject((String) redisUtil.get(key), FileUploadCache.class);
        if (file == null) {
            file = cache.toFile();
            fileMapper.insert(file);
        } else if (!file.getUrl().equalsIgnoreCase(content)) {
            fileUtil.deleteFileOSS(file.getUrl(), FileUtil.PRIVATE_BUCKET);
            file.setUrl(content);
            fileMapper.updateById(file);
        }
        redisUtil.delete(key);
    }
}
