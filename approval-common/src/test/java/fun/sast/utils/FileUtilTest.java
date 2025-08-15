package fun.sast.utils;

import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {

    @Test
    void testGetObjectNameOSS_WithValidUrl() {
        // Given
        String validUrl = "https://mock-bucket.oss-cn-hangzhou.aliyuncs.com/public/test-file.txt";
        String expectedPath = "public/test-file.txt";

        // When
        String result = FileUtil.getObjectNameOSS(validUrl);

        // Then
        assertEquals(expectedPath, result);
    }

    @Test
    void testGetObjectNameOSS_WithUrlContainingSpaces() {
        // Given
        String urlWithSpaces = "  https://mock-bucket.oss-cn-hangzhou.aliyuncs.com/public/test-file.txt  ";
        String expectedPath = "public/test-file.txt";

        // When
        String result = FileUtil.getObjectNameOSS(urlWithSpaces);

        // Then
        assertEquals(expectedPath, result);
    }

    @Test
    void testGetObjectNameOSS_WithRootPath() {
        // Given
        String rootUrl = "https://mock-bucket.oss-cn-hangzhou.aliyuncs.com/";
        String expectedPath = "";

        // When
        String result = FileUtil.getObjectNameOSS(rootUrl);

        // Then
        assertEquals(expectedPath, result);
    }

    @Test
    void testGetObjectNameOSS_WithComplexPath() {
        // Given
        String complexUrl = "https://mock-bucket.oss-cn-hangzhou.aliyuncs.com/folder/subfolder/file.name.ext";
        String expectedPath = "folder/subfolder/file.name.ext";

        // When
        String result = FileUtil.getObjectNameOSS(complexUrl);

        // Then
        assertEquals(expectedPath, result);
    }

    @Test
    void testGetObjectNameOSS_WithInvalidUrl_ThrowsException() {
        // Given
        String invalidUrl = "invalid-url";

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            FileUtil.getObjectNameOSS(invalidUrl);
        });

        assertEquals(ErrorEnum.URL_EMPTY_ERROR, exception.getErrorEnum());
    }

    @Test
    void testGetObjectNameOSS_WithNullUrl_ThrowsException() {
        // Given
        String nullUrl = null;

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            FileUtil.getObjectNameOSS(nullUrl);
        });

        assertEquals(ErrorEnum.INVALID_URL_ERROR, exception.getErrorEnum());
    }

    @Test
    void testGetObjectNameOSS_WithEmptyUrl_ThrowsException() {
        // Given
        String emptyUrl = "";

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> {
            FileUtil.getObjectNameOSS(emptyUrl);
        });

        assertEquals(ErrorEnum.INVALID_URL_ERROR, exception.getErrorEnum());
    }

    @Test
    void testGetObjectNameOSS_WithProtocolOnly_ThrowsException() {
        // Given
        String protocolOnly = "https://";

        // When
        String result = FileUtil.getObjectNameOSS(protocolOnly);

        // Then
        assertEquals("", result);
    }
}
