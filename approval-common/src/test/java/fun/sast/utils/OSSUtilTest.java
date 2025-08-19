package fun.sast.utils;

import static org.junit.jupiter.api.Assertions.*;

import fun.sast.Exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OSSUtilTest {

    private OSSUtil ossUtil;

    @BeforeEach
    void setUp() {
        ossUtil =
                new OSSUtil(
                        "testAccessKeyId",
                        "testAccessKeySecret",
                        "oss-cn-hangzhou.aliyuncs.com",
                        10,
                        5,
                        "test-bucket",
                        "public",
                        "private");

        // 设置 bucketUrlPrefix，防止空指针
        try {
            var prefixField = OSSUtil.class.getDeclaredField("bucketUrlPrefix");
            prefixField.setAccessible(true);
            prefixField.set(ossUtil, "https://test-bucket.oss-cn-hangzhou.aliyuncs.com/");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testIsOSSBucketURL_true() {
        String url = "https://test-bucket.oss-cn-hangzhou.aliyuncs.com/file.txt";
        assertTrue(ossUtil.isOSSBucketURL(url));
    }

    @Test
    void testIsOSSBucketURL_false_differentHost() {
        String url = "https://example.com/file.txt";
        assertFalse(ossUtil.isOSSBucketURL(url));
    }

    @Test
    void testIsOSSBucketURL_false_malformedURL() {
        String url = "not-a-url";
        assertFalse(ossUtil.isOSSBucketURL(url));
    }

    @Test
    void testGetDownloadCertificate_invalidUrl_shouldThrow() {
        // bucketUrlPrefix 已经有值，但传入的 url 是空字符串
        assertThrows(BaseException.class, () -> ossUtil.getDownloadCertificate(""));
    }
}
