package fun.sast.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import jakarta.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

    @Autowired private OSSRateLimiterUtil ossRateLimiterUtil;
    @Autowired private JwtUtil jwtUtil;

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
     * @param url 文件url例如https://mock-bucket.oss-cn-hangzhou.aliyuncs.com/list/list2/text2.txt
     * @return 带有凭证的url
     */
    public String getDownloadCertificate(String url) {
        // 获取code作为key
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest requestToGetToken = attributes.getRequest();
        String userCode = jwtUtil.resolveJwt(requestToGetToken.getHeader("Token"));

        // 提取文件名
        String fileName = FileUtil.getObjectNameOSS(url);

        // 全局限流：限制用户总下载次数
        if (!ossRateLimiterUtil.tryAcquire("download:" + userCode, 30, 60000, 120)) {
            throw new BaseException(ErrorEnum.TOO_MANY_REQUESTS);
        }

        // 单文件限流：限制用户单文件下载次数
        if (!ossRateLimiterUtil.tryAcquire(
                "download:" + userCode + ":" + fileName, 5, 60000, 120)) {
            throw new BaseException(ErrorEnum.TOO_MANY_REQUESTS);
        }

        // 设置预签名URL过期时间
        Date expiration = new Date(System.currentTimeMillis() + downloadExpiredTime * 60 * 1000);

        // 创建预签名请求
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileName);
        request.setExpiration(expiration);
        request.setMethod(HttpMethod.GET);

        return ossClient.generatePresignedUrl(request).toString();
    }
}
