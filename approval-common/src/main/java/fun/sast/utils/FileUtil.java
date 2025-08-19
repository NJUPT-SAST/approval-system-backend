package fun.sast.utils;

import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FileUtil {
    public static final int PUBLIC_BUCKET = 1;
    public static final int PRIVATE_BUCKET = 2;
    public static final int PUBLIC_FOLDER = 1;
    public static final int PRIVATE_FOLDER = 2;

    private final OSSUtil ossUtil;

    /**
     * 通过URL获取文件路径 OSS
     *
     * @param urlString 文件的地址 例：https://endpoint/path/filename.zip
     * @return 文件路径 例：path/filename.zip
     */
    public static String getObjectNameOSS(String urlString) {
        URL url;
        try {
            if (urlString == null || urlString.isEmpty()) {
                throw new BaseException(ErrorEnum.INVALID_URL_ERROR);
            }
            urlString = urlString.trim();
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new BaseException(ErrorEnum.URL_EMPTY_ERROR);
        }
        String path = url.getPath();
        if (path.isEmpty()) {
            return "";
        }
        return path.substring(1);
    }

    public Boolean isOSSBucketURL(String content) {
        return ossUtil.isOSSBucketURL(content);
    }

    /**
     * 删除文件
     *
     * @param url 文件的URL
     */
    public void deleteFileOSS(String url, int folderNumber) {
        if (ossUtil.isOSSBucketURL(url)) {
            ossUtil.deleteFileOSS(url, folderNumber);
        }
    }

    /**
     * 获取上传文件的凭证
     *
     * @param filename 文件名
     * @param comId 比赛ID
     * @param id 文件的ID
     * @param input 文件的输入
     * @return 上传文件的凭证
     */
    public Map<String, String> getUploadCertificate(
            String filename, Long comId, Long id, String input) {
        String typeName = CommonUtil.getTypeByFilename(filename);
        if (!CommonUtil.isAllowUploadType(typeName)) {
            throw new BaseException(ErrorEnum.INVALID_FILE_TYPE_ERROR);
        }
        // 文件路径格式 comId/work/teamId/input-fileName
        String objectName =
                comId
                        + "/work/"
                        + id
                        + "/"
                        + input
                        + "-"
                        + CommonUtil.creatShortUUID()
                        + "-"
                        + filename;
        return ossUtil.getUploadCertificateOSS(objectName, PRIVATE_FOLDER);
    }
}
