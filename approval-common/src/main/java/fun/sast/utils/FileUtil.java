package fun.sast.utils;

import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class FileUtil {
    public static final int PUBLIC_FOLDER = 1;

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
     * @param urlString 文件的完整url
     * @return 文件名(不判断前缀)
     */
    public static String getFileName(String urlString) {
        String objectName = getObjectNameOSS(urlString);
        return objectName.substring(objectName.lastIndexOf("/") + 1);
    }
}
