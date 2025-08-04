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
import org.springframework.util.StringUtils;

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

        // 创建客户端配置
        ClientBuilderConfiguration config = new ClientBuilderConfiguration();

        // 创建OSS客户端
        this.ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider, config);
        this.bucketName = bucketName;
        this.publicFolder = publicFolder;
        this.privateFolder = privateFolder;
        this.endpoint = "https://" + bucketName + "." + endpoint;
        this.uploadExpiredTime = uploadExpiredTime;
        this.downloadExpiredTime = downloadExpiredTime;

        System.out.println(endpoint);
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
     * 获取下载凭证
     *
     * @param url 文件url例如https://baiyaoshi.oss-cn-hangzhou.aliyuncs.com/list/list2/text2.txt
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

        System.out.println(bucketName);

        return ossClient.generatePresignedUrl(request).toString();
    }

    @Value("${file.OSS.bucket-url-prefix}")
    private String bucketUrlPrefix;

    /**
     * 判断是否为合法 OSS 文件地址（是否以配置的前缀开头）
     *
     * @param url 待校验的 OSS URL
     * @return true 合法，false 非法
     */
    public boolean isLegalOSSUrl(String url) {
        if (!StringUtils.hasText(url) || !StringUtils.hasText(bucketUrlPrefix)) {
            return false;
        }
        return url.startsWith(bucketUrlPrefix);
    }

    /**
     * 去除 OSS URL 的前缀，获得相对路径
     *
     * @param url 完整 OSS URL
     * @return 相对路径
     */
    public String extractObjectKey(String url) {
        if (isLegalOSSUrl(url)) {
            return url.substring(bucketUrlPrefix.length());
        }
        return null;
    }
}
