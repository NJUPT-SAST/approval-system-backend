package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fun.sast.Exception.BaseException;
import fun.sast.entity.File;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.enums.UserRoleEnum;
import fun.sast.mapper.FileMapper;
import fun.sast.service.FileService;
import fun.sast.utils.FileUtil;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {
    @Autowired private FileMapper fileMapper;
    @Autowired private FileUtil fileUtil;

    @Override
    public String getDownloadCertificate(User user, String url) {
        System.out.println("原始URL：" + url);

        // 解码
        url = URLDecoder.decode(url, StandardCharsets.UTF_8);

        // 提取 objectKey
        String prefix = "https://baiyaoshi.oss-cn-hangzhou.aliyuncs.com/";
        String objectKey = url.startsWith(prefix) ? url.substring(prefix.length()) : url;

        System.out.println("提取后的 objectKey：" + objectKey);

        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", objectKey);
        File file = fileMapper.selectOne(queryWrapper);

        System.out.println("查询到的 file：" + file);

        if (file == null) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }

        // user身份判断
        user.setCode("111");
        user.setRole(1);

        // 权限控制
        if (user.getRole().equals(UserRoleEnum.STUDENT.getRole())
                && !user.getCode().equals(file.getUserCode())) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }

        return fileUtil.getDownloadCertificate(url);
    }
}
