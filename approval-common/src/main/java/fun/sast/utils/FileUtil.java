package fun.sast.utils;

import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
@AllArgsConstructor
public class FileUtil {
    public static final int PUBLIC_FOLDER = 1;

    private final OSSUtil ossUtil;

    /**
     * 通过URL获取文件路径 OSS
     *
     * @param urlString 文件的地址 例：https://endpoint/path/filename.zip,不校验前缀
     * @return 文件路径 例：path/filename.zip
     */
    public static String getObjectNameOSS(String urlString) {
        URL url;
        try {
            urlString = urlString.trim();
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new BaseException(ErrorEnum.INVALID_URL_ERROR);
        }
        return url.getPath().substring(1);
    }

    /**
     * 判断是否为合法 OSS 文件地址（是否以配置的前缀开头）
     *
     * @param url 待校验的 OSS URL
     * @param bucketUrlPrefix 配置的 OSS 前缀
     * @return true 合法，false 非法
     */
    public static boolean isLegalOSSUrl(String url, String bucketUrlPrefix) {
        if (!StringUtils.hasText(url) || !StringUtils.hasText(bucketUrlPrefix)) {
            return false;
        }
        return url.startsWith(bucketUrlPrefix);
    }

    /**
     * 向公共Bucket上传比赛封面（仅允许jpg png格式，且大小小于5M）
     * 文件路径格式 //buckName.endpoint/comId/cover/fileName
     *
     * @param file  封面
     * @param comId 比赛ID
     * @return 封面的URL
     */
    public String uploadCover(MultipartFile file, Long comId) {
        if (file.getSize() > 5242880) {
            throw new BaseException(ErrorEnum.FILE_SIZE_ERROR);
        }
        // 文件路径格式 comId/cover/fileName
        String objectName = comId +
                "/cover/" +
                file.getOriginalFilename();

        return ossUtil.uploadFile(file, objectName, PUBLIC_FOLDER);
    }

    /**
     * @param urlString 文件的完整url
     * @return 文件名(不判断前缀)
     */
    public static String getFileName(String urlString) {
        String objectName = getObjectNameOSS(urlString);
        return objectName.substring(objectName.lastIndexOf("/") + 1);
    }


}
