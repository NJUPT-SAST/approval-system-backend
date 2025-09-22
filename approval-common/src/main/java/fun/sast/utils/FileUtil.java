package fun.sast.utils;

import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
@Slf4j
public class FileUtil {

    public static final int PUBLIC_FOLDER = 1;
    public static final int PRIVATE_FOLDER = 2;

    private final COSUtil cosUtil;

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
        return cosUtil.getUploadCertificateCOS(objectName, FileUtil.PRIVATE_FOLDER);
    }



    /**
     * 批量下载文件，多个附件打包成zip
     *
     * @param response HttpServletResponse
     * @param fileIntoList 附件信息列表 每个Map包含两个键值对
     * @param zipFileName 打包后的zip文件名
     */
    public void downloadPackFile(
            HttpServletResponse response,
            List<Map<String, String>> fileIntoList,
            String zipFileName) {
        if (fileIntoList == null || fileIntoList.isEmpty()) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }
        // 处理默认ZIP文件名
        if (!StringUtils.hasText(zipFileName)) {
            zipFileName = "work-attachment_" + System.currentTimeMillis() + ".zip";
        }

        try {
            String encodedFileName =
                    URLEncoder.encode(zipFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/zip");
            response.setCharacterEncoding("utf-8");
            response.setHeader(
                    "Content-disposition", "attachment;filename*=utf-8''" + encodedFileName);

            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
                for (Map<String, String> fileInfo : fileIntoList) {
                    String ossUrl = fileInfo.get("ossUrl");
                    String fileName = fileInfo.get("fileName");
                    if (!StringUtils.hasText(ossUrl) || !StringUtils.hasText(fileName)) {
                        log.warn("文件信息不完整，跳过该文件");
                        continue;
                    }
                    // 生成OSS临时授权链接
                    String authorizedUrl = cosUtil.getDownloadCertificate(ossUrl);
                    // 从授权链接下载文件流
                    try (InputStream fileIn = getInputStreamFromUrl(authorizedUrl)) {
                        if (fileIn == null) {
                            log.warn("无法获取文件输入流，跳过该文件");
                            continue;
                        }
                        // 创建zip条目
                        ZipEntry zipEntry = new ZipEntry(fileName);
                        zipOut.putNextEntry(zipEntry);

                        // 写入zip
                        byte[] buffer = new byte[1024 * 4];
                        int len;
                        while ((len = fileIn.read(buffer)) != -1) {
                            zipOut.write(buffer, 0, len);
                        }

                        // 关闭当前条目
                        zipOut.closeEntry();
                        log.info("文件{}添加到zip包中", fileName);
                    }
                }
                zipOut.flush();
            }
            log.info("附件打包完成，共{}个文件，文件名为{}", fileIntoList.size(), zipFileName);
        } catch (IOException e) {
            log.error("打包下载文件或响应写入失败", e);
            throw new BaseException(ErrorEnum.FILE_DOWNLOAD_ERROR);
        }
    }

    private InputStream getInputStreamFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 5秒连接超时
            connection.setReadTimeout(10000); // 10秒读取超时

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("无法连接到URL: {}，响应码: {}", urlString, connection.getResponseCode());
                throw new BaseException(ErrorEnum.FILE_DOWNLOAD_ERROR);
            }
            return connection.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
}
