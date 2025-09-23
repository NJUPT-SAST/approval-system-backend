package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fun.sast.Exception.BaseException;
import fun.sast.entity.File;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.enums.UserRoleEnum;
// import fun.sast.interceptor.UserInterceptor;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.mapper.FileMapper;
import fun.sast.service.FileService;
import fun.sast.utils.COSUtil;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileMapper fileMapper;
    private final COSUtil cosUtil;

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

        // 判断url是否合法
        if (!cosUtil.isLegalCOSUrl(url)) {
            throw new BaseException(ErrorEnum.INVALID_URL_ERROR);
        }
        String cosCert = cosUtil.getDownloadCertificate(url);
        // 提取 objectKey 对比数据库看有没有文件
        String objectKey = cosUtil.extractObjectKey(cosCert);
        if (objectKey == null) {
            throw new BaseException(ErrorEnum.INVALID_URL_ERROR);
        }

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

        return cosUtil.getDownloadCertificate(url);
    }
}
