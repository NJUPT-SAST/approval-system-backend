package fun.sast.utils;

import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FileUtil {
    private final OSSUtil ossUtil;
    public static final int PUBLIC_FOLDER = 1;

    /**
     * 通过URL获取文件路径 OSS
     *
     * @param urlString 文件的地址 例：https://endpoint/path/filename.zip
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
}
