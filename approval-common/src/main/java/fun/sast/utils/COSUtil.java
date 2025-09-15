package fun.sast.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.Headers;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class COSUtil {
    private final COSClient cosClient;
    private final String bucketName;
    private final String endpoint;
    private final String publicFolder;
    private final String privateFolder;
    private final Integer uploadExpiredTime;
    private final Integer downloadExpiredTime;

    @Value("${file.COS.bucket-url-prefix}")
    private String bucketUrlPrefix;

    public COSUtil(
            @Value("${file.COS.secretId:}") String secretId,
            @Value("${file.COS.secretKey:}") String secretKey,
            @Value("${file.COS.region}") String region,
            @Value("${file.COS.uploadExpiredTime:}") Integer uploadExpiredTime,
            @Value("${file.COS.downloadExpiredTime:}") Integer downloadExpiredTime,
            @Value("${file.COS.bucketName:}") String bucketName,
            @Value("${file.COS.publicFolder:}") String publicFolder,
            @Value("${file.COS.privateFolder:}") String privateFolder) {
        COSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        this.cosClient = new COSClient(credentials, clientConfig);

        this.bucketName = bucketName;
        this.publicFolder = publicFolder;
        this.privateFolder = privateFolder;
        this.endpoint = "https://" + bucketName + ".cos." + region + ".myqcloud.com";
        this.uploadExpiredTime = uploadExpiredTime;
        this.downloadExpiredTime = downloadExpiredTime;

        log.info("COSUtil2 初始化完成，endpoint = {}", this.endpoint);
    }

    /**
     * 获取上传凭证
     *
     * @param objectName 文件名
     * @param folderType 1为公开，2为私有
     * @return 带有凭证的url
     */
    public Map<String, String> getUploadCertificate(String objectName, int folderType) {
        String folder = (folderType == 1) ? publicFolder : privateFolder;
        String key = folder + "/" + objectName;
        String clearUrl = endpoint + "/" + key;

        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(bucketName, key, HttpMethodName.PUT);
        Date expiration = new Date(System.currentTimeMillis() + uploadExpiredTime * 60 * 1000);
        request.setExpiration(expiration);
        request.putCustomRequestHeader(
                Headers.HOST,
                cosClient
                        .getClientConfig()
                        .getEndpointBuilder()
                        .buildGeneralApiEndpoint(bucketName));

        URL url = cosClient.generatePresignedUrl(request);
        Map<String, String> result = new HashMap<>();
        result.put("url", url.toString());
        result.put("clearUrl", clearUrl);
        return result;
    }

    /**
     * 获取下载凭证
     *
     * @param url 文件url
     * @return 带有凭证的url
     */
    public String getDownloadCertificate(String url) {
        if (!StringUtils.hasText(url) || !StringUtils.hasText(bucketUrlPrefix)) {
            throw new BaseException(ErrorEnum.OSS_BUCKET_NOT_EXIST);
        }

        String key = extractObjectKey(url);
        // 欲签名过期时间
        Date expiration = new Date(System.currentTimeMillis() + downloadExpiredTime * 60 * 1000);
        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(bucketName, key, HttpMethodName.GET);
        request.setExpiration(expiration);
        request.putCustomRequestHeader(
                Headers.HOST,
                cosClient
                        .getClientConfig()
                        .getEndpointBuilder()
                        .buildGeneralApiEndpoint(bucketName));

        return cosClient.generatePresignedUrl(request).toString();
    }

    /** 判断是否为合法COS URL */
    public boolean isLegalCOSUrl(String url) {
        if (!StringUtils.hasText(url) || !StringUtils.hasText(bucketUrlPrefix)) {
            return false;
        }
        return url.startsWith(bucketUrlPrefix);
    }

    /** 提取相对key（自动修复 / 前缀） */
    public String extractObjectKey(String url) {
        if (!StringUtils.hasText(url) || !StringUtils.hasText(bucketUrlPrefix)) return null;

        if (!url.startsWith(bucketUrlPrefix)) return null;

        String key = url.substring(bucketUrlPrefix.length());
        // 去掉开头的 /
        if (key.startsWith("/")) key = key.substring(1);
        // 去掉 query 参数
        int idx = key.indexOf("?");
        if (idx != -1) key = key.substring(0, idx);
        return key;
    }

    /** 删除文件 */
    public void deleteFileCOS(String url, int folderType) {
        String folder = (folderType == 1) ? publicFolder : privateFolder;
        String key = folder + "/" + extractObjectKey(url);
        cosClient.deleteObject(bucketName, key);
    }


    /**
     * 上传文件
     *
     * @param file 文件
     * @param objectName 文件名
     * @param folder 文件夹类型
     * @return 带有凭证的url
     */
    public String uploadFile(MultipartFile file, String objectName, int folder) {
        String folderName = (folder == 1) ? publicFolder : privateFolder;
        String key = folderName + "/" + objectName;
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            cosClient.putObject(bucketName, key, file.getInputStream(), objectMetadata);
            return endpoint + "/" + key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
