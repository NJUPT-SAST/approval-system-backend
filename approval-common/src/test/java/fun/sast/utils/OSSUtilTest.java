package fun.sast.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class OSSUtilTest {

    private OSSUtil ossUtil;
    private OSS mockOssClient;

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        mockOssClient = mock(OSS.class);

        ossUtil =
                new OSSUtil(
                        "testKeyId",
                        "testKeySecret",
                        "oss-cn-hangzhou.aliyuncs.com",
                        10,
                        20,
                        "mock-bucket",
                        "public",
                        "private");

        // 注入 mock 对象
        setField(ossUtil, "ossClient", mockOssClient);
        setField(ossUtil, "bucketUrlPrefix", "https://mock-bucket.oss-cn-hangzhou.aliyuncs.com");
    }

    @Test
    void testIsOSSBucketURL_Valid() {
        String url = "https://mock-bucket.oss-cn-hangzhou.aliyuncs.com/test.txt";
        assertTrue(ossUtil.isOSSBucketURL(url));
    }

    @Test
    void testIsOSSBucketURL_Invalid() {
        String url = "https://other-bucket.oss-cn-shanghai.aliyuncs.com/test.txt";
        assertFalse(ossUtil.isOSSBucketURL(url));
    }

    @Test
    void testGetDownloadCertificate() throws Exception {
        String fileUrl = "https://mock-bucket.oss-cn-hangzhou.aliyuncs.com/list/list2/text2.txt";
        String expectedSignedUrl = "https://signed-url";

        when(mockOssClient.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(new URL(expectedSignedUrl));

        String signedUrl = ossUtil.getDownloadCertificate(fileUrl);

        assertEquals(expectedSignedUrl, signedUrl);

        // 校验调用时用的 bucket 和文件名
        ArgumentCaptor<GeneratePresignedUrlRequest> captor =
                ArgumentCaptor.forClass(GeneratePresignedUrlRequest.class);
        verify(mockOssClient).generatePresignedUrl(captor.capture());

        GeneratePresignedUrlRequest request = captor.getValue();
        assertEquals("mock-bucket", request.getBucketName());
        assertEquals("list/list2/text2.txt", request.getKey());
        assertTrue(request.getExpiration().after(new Date()));
    }
}
