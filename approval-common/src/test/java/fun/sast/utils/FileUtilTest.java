package fun.sast.utils;

import static org.junit.jupiter.api.Assertions.*;

import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import org.junit.jupiter.api.Test;

class FileUtilTest {

    @Test
    void testGetObjectNameOSS_validUrl() {
        String url = "https://oss-cn-hangzhou.aliyuncs.com/path/to/file.txt";
        String objectName = FileUtil.getObjectNameOSS(url);
        assertEquals("path/to/file.txt", objectName);
    }

    @Test
    void testGetObjectNameOSS_invalidUrl_shouldThrow() {
        String invalidUrl = "not-a-url";
        BaseException ex =
                assertThrows(BaseException.class, () -> FileUtil.getObjectNameOSS(invalidUrl));
        assertEquals(ErrorEnum.INVALID_URL_ERROR, ex.getErrorEnum());
    }

    @Test
    void testIsLegalOSSUrl_true() {
        String prefix = "https://bucket.oss-cn-hangzhou.aliyuncs.com/";
        String url = prefix + "folder/file.png";
        assertTrue(FileUtil.isLegalOSSUrl(url, prefix));
    }

    @Test
    void testIsLegalOSSUrl_false_emptyInputs() {
        assertFalse(FileUtil.isLegalOSSUrl("", "https://bucket.oss-cn-hangzhou.aliyuncs.com/"));
        assertFalse(
                FileUtil.isLegalOSSUrl("https://bucket.oss-cn-hangzhou.aliyuncs.com/file.txt", ""));
        assertFalse(FileUtil.isLegalOSSUrl("", ""));
    }

    @Test
    void testIsLegalOSSUrl_false_notMatchPrefix() {
        String prefix = "https://bucket.oss-cn-hangzhou.aliyuncs.com/";
        String url = "https://other-bucket.oss-cn-hangzhou.aliyuncs.com/file.txt";
        assertFalse(FileUtil.isLegalOSSUrl(url, prefix));
    }

    @Test
    void testGetFileName_normal() {
        String url = "https://oss-cn-hangzhou.aliyuncs.com/path/to/file.txt";
        String fileName = FileUtil.getFileName(url);
        assertEquals("file.txt", fileName);
    }

    @Test
    void testGetFileName_rootPath() {
        String url = "https://oss-cn-hangzhou.aliyuncs.com/file.txt";
        String fileName = FileUtil.getFileName(url);
        assertEquals("file.txt", fileName);
    }
}
