package fun.sast.utils;

import cn.hutool.core.io.FileTypeUtil;
import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
@Slf4j
public class FileUtil {

    private final COSUtil cosUtil;

    public static final int PUBLIC_FOLDER = 1;
    public static final int PRIVATE_FOLDER = 2;

    /**
     * @param urlString 文件的完整url
     * @return 文件名(不判断前缀)
     */
    public static String getFileName(String urlString) {
        String objectName = getObjectKey(urlString);
        return objectName.substring(objectName.lastIndexOf("/") + 1);
    }

    /**
     * 通过URL获取文件路径
     *
     * @param urlString 文件的地址 例：https://endpoint/path/filename.zip,不校验前缀
     * @return 文件路径 例：path/filename.zip
     */
    public static String getObjectKey(String urlString) {
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
     * 删除文件
     *
     * @param url 文件的URL
     */
    public void deleteFileCOS(String url, int folderNumber) {
        cosUtil.deleteFileCOS(url, folderNumber);
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
        return cosUtil.getUploadCertificate(objectName, FileUtil.PRIVATE_FOLDER);
    }

    /**
     * 向公共Bucket上传比赛封面（仅允许jpg png格式，且大小小于5M） 文件路径格式 //buckName.endpoint/comId/cover/fileName
     *
     * @param file 封面
     * @param comId 比赛ID
     * @return 封面的URL
     */
    public String uploadCover(MultipartFile file, Long comId) {
        if (file.getSize() > 5242880) {
            throw new BaseException(ErrorEnum.FILE_SIZE_ERROR);
        }
        // 获取后缀
        String typeName;
        try {
            typeName = FileTypeUtil.getType(file.getInputStream());
        } catch (IOException e) {
            log.error("获取文件类型出错", e);
            return null;
        }
        if (typeName != null && !isImage(typeName)) {
            throw new BaseException(ErrorEnum.INVALID_FILE_TYPE_ERROR);
        }
        // 文件路径格式 comId/cover/fileName
        String objectName = comId + "/cover/" + file.getOriginalFilename();

        return cosUtil.uploadFile(file, objectName, PUBLIC_FOLDER);
    }

    private boolean isImage(String typeName) {
        return switch (typeName) {
            case "jpg", "jpeg", "png" -> true;
            default -> false;
        };
    }
}
