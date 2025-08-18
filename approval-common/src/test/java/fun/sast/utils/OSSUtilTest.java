package fun.sast.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class OSSUtilTest {

    @Mock private OSS ossClient;

    @InjectMocks private OSSUtil ossUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 使用反射设置私有字段
        ReflectionTestUtils.setField(ossUtil, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(
                ossUtil, "endpoint", "https://test-bucket.oss-cn-hangzhou.aliyuncs.com");
        ReflectionTestUtils.setField(ossUtil, "publicFolder", "public");
        ReflectionTestUtils.setField(ossUtil, "privateFolder", "private");
        ReflectionTestUtils.setField(ossUtil, "uploadExpiredTime", 60);
        ReflectionTestUtils.setField(ossUtil, "downloadExpiredTime", 60);
        ReflectionTestUtils.setField(
                ossUtil, "bucketUrlPrefix", "https://test-bucket.oss-cn-hangzhou.aliyuncs.com/");
    }

    @Test
    void testIsOSSBucketURL_withValidOSSUrl_shouldReturnTrue() throws MalformedURLException {
        // Given
        String validOSSUrl = "https://test-bucket.oss-cn-hangzhou.aliyuncs.com/some/file.txt";

        // When
        Boolean result = ossUtil.isOSSBucketURL(validOSSUrl);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsOSSBucketURL_withInvalidOSSUrl_shouldReturnFalse() throws MalformedURLException {
        // Given
        String invalidOSSUrl = "https://other-bucket.oss-cn-hangzhou.aliyuncs.com/some/file.txt";

        // When
        Boolean result = ossUtil.isOSSBucketURL(invalidOSSUrl);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsOSSBucketURL_withNullUrl_shouldReturnFalse() {
        // Given
        String nullUrl = null;

        // When
        Boolean result = ossUtil.isOSSBucketURL(nullUrl);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsOSSBucketURL_withEmptyUrl_shouldReturnFalse() {
        // Given
        String emptyUrl = "";

        // When
        Boolean result = ossUtil.isOSSBucketURL(emptyUrl);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsOSSBucketURL_withInvalidUrlFormat_shouldReturnFalse() {
        // Given
        String invalidUrl = "not-a-valid-url";

        // When
        Boolean result = ossUtil.isOSSBucketURL(invalidUrl);

        // Then
        assertFalse(result);
    }

    @Test
    void testGetDownloadCertificate_withValidUrl_shouldReturnSignedUrl() {
        // Given
        String fileUrl = "https://test-bucket.oss-cn-hangzhou.aliyuncs.com/private/test-file.txt";
        URL expectedUrl = mock(URL.class);
        when(expectedUrl.toString()).thenReturn("https://signed-url.com/test");
        when(ossClient.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(expectedUrl);

        // When
        String result = ossUtil.getDownloadCertificate(fileUrl);

        // Then
        assertNotNull(result);
        assertEquals("https://signed-url.com/test", result);

        // 验证generatePresignedUrl方法被正确调用
        ArgumentCaptor<GeneratePresignedUrlRequest> requestCaptor =
                ArgumentCaptor.forClass(GeneratePresignedUrlRequest.class);
        verify(ossClient).generatePresignedUrl(requestCaptor.capture());

        GeneratePresignedUrlRequest capturedRequest = requestCaptor.getValue();
        assertEquals("test-bucket", capturedRequest.getBucketName());
        assertEquals("private/test-file.txt", capturedRequest.getKey());
        assertEquals(HttpMethod.GET, capturedRequest.getMethod());
    }

    @Test
    void testIsLegalOSSUrl_withValidUrl_shouldReturnTrue() {
        // Given
        String validUrl = "https://test-bucket.oss-cn-hangzhou.aliyuncs.com/some/file.txt";

        // When
        boolean result = ossUtil.isLegalOSSUrl(validUrl);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsLegalOSSUrl_withInvalidUrl_shouldReturnFalse() {
        // Given
        String invalidUrl = "https://other-domain.com/some/file.txt";

        // When
        boolean result = ossUtil.isLegalOSSUrl(invalidUrl);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsLegalOSSUrl_withNullUrl_shouldReturnFalse() {
        // Given
        String nullUrl = null;

        // When
        boolean result = ossUtil.isLegalOSSUrl(nullUrl);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsLegalOSSUrl_withEmptyUrl_shouldReturnFalse() {
        // Given
        String emptyUrl = "";

        // When
        boolean result = ossUtil.isLegalOSSUrl(emptyUrl);

        // Then
        assertFalse(result);
    }

    @Test
    void testExtractObjectKey_withValidUrl_shouldReturnObjectKey() {
        // Given
        String validUrl = "https://test-bucket.oss-cn-hangzhou.aliyuncs.com/path/to/file.txt";
        String expectedKey = "path/to/file.txt";

        // When
        String result = ossUtil.extractObjectKey(validUrl);

        // Then
        assertEquals(expectedKey, result);
    }

    @Test
    void testExtractObjectKey_withInvalidUrl_shouldReturnNull() {
        // Given
        String invalidUrl = "https://other-domain.com/path/to/file.txt";

        // When
        String result = ossUtil.extractObjectKey(invalidUrl);

        // Then
        assertNull(result);
    }

    @Test
    void testExtractObjectKey_withNullUrl_shouldReturnNull() {
        // Given
        String nullUrl = null;

        // When
        String result = ossUtil.extractObjectKey(nullUrl);

        // Then
        assertNull(result);
    }
}
