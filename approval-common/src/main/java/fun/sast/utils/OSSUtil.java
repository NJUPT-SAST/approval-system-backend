package fun.sast.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OSSUtil {
    private final OSS ossClient;
    private final String bucketName;
    private final String endpoint;
    private final String publicFolder;
    private final String privateFolder;
    private final Integer uploadExpiredTime;
    private final Integer downloadExpiredTime;

    public OSSUtil(
            @Value("${file.OSS.accessKeyId:}") String accessKeyId,
            @Value("${file.OSS.accessKeySecret:}") String accessKeySecret,
            @Value("${file.OSS.endpoint}") String endpoint,
            @Value("${file.OSS.uploadExpiredTime:}") Integer uploadExpiredTime,
            @Value("${file.OSS.downloadExpiredTime:}") Integer downloadExpiredTime,
            @Value("${file.OSS.bucketName:}") String bucketName,
            @Value("${file.OSS.publicFolder:}") String publicFolder,
            @Value("${file.OSS.privateFolder:}") String privateFolder) {
        // 创建凭证提供者
        CredentialsProvider credentialsProvider =
                new DefaultCredentialProvider(accessKeyId, accessKeySecret);

        // 创建客户端配置（可选）
        ClientBuilderConfiguration config = new ClientBuilderConfiguration();

        // 创建OSS客户端
        this.ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider, config);
        this.bucketName = bucketName;
        this.publicFolder = publicFolder;
        this.privateFolder = privateFolder;
        this.endpoint = "https://" + bucketName + "." + endpoint;
        this.uploadExpiredTime = uploadExpiredTime;
        this.downloadExpiredTime = downloadExpiredTime;
    }

    private String getBaseFolderName(int number) {
        if (FileUtil.PUBLIC_FOLDER == number) return publicFolder;
        else return privateFolder;
    }

    /**
     * 判断字符串是否为Bucket上的文件地址
     *
     * @param content 字符串内容
     */
    public Boolean isOSSBucketURL(String content) {
        if (content == null || content.isEmpty()) return false;
        try {
            String hostFromUser = new URL(content).getHost();
            String host = new URL(endpoint).getHost();
            return host.equalsIgnoreCase(hostFromUser);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * 删除OSS上的文件
     *
     * @param url 文件的完整URL
     */
    public void deleteFileOSS(String url) {
        log.info("删除OSS中的文件，文件地址：{}", url);

        try {
            // 解析URL
            URL parsedUrl = new URL(url);

            String key = parsedUrl.getPath();
            if (key.startsWith("/")) {
                key = key.substring(1); // 去掉前导斜杠
            }

            log.info("解析出的OSS对象Key: {}", key);

            ossClient.deleteObject(bucketName, key);
            log.info("删除成功！");
        } catch (Exception e) {
            log.error("删除OSS文件失败", e);
        }
    }

    /**
     * 获取下载凭证（仅针对私有的Bucket）
     *
     * @param url 文件url
     * @return 带有凭证的url
     */
    public String getDownloadCertificate(String url) {
        log.info("获取从OSS下载凭证，文件地址：{}", url);
        // String objectName = FileUtil.getObjectNameOSS(url);
        // String key = privateFolder + "/" + objectName;
        String key = FileUtil.getObjectNameOSS(url);
        System.out.println(key);
        System.out.println(key);

        // 设置预签名URL过期时间
        Date expiration = new Date(System.currentTimeMillis() + downloadExpiredTime * 60 * 1000);

        // 创建预签名请求
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key);
        request.setExpiration(expiration);
        request.setMethod(HttpMethod.GET);

        return ossClient.generatePresignedUrl(request).toString();
    }

    // 关闭OSS客户端的方法
    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
