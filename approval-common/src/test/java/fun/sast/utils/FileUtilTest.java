package fun.sast.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fun.sast.Exception.BaseException;
import fun.sast.enums.ErrorEnum;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class FileUtilTest {

    @Mock private OSSUtil ossUtil;

    private FileUtil fileUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileUtil = new FileUtil(ossUtil);
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

    @Test
    void testGetObjectKey_validUrl() {
        String url = "https://oss-cn-hangzhou.aliyuncs.com/path/to/file.txt";
        String objectKey = FileUtil.getObjectKey(url);
        assertEquals("path/to/file.txt", objectKey);
    }

    @Test
    void testGetObjectKey_invalidUrl_shouldThrow() {
        String invalidUrl = "not-a-url";
        BaseException ex =
                assertThrows(BaseException.class, () -> FileUtil.getObjectKey(invalidUrl));
        assertEquals(ErrorEnum.INVALID_URL_ERROR, ex.getErrorEnum());
    }

    @Test
    void testGetObjectKey_urlWithSpaces() {
        String url = "  https://oss-cn-hangzhou.aliyuncs.com/path/to/file.txt  ";
        String objectKey = FileUtil.getObjectKey(url);
        assertEquals("path/to/file.txt", objectKey);
    }

    @Test
    void testDeleteFileOSS() {
        String url = "https://bucket.oss-cn-hangzhou.aliyuncs.com/folder/file.png";
        fileUtil.deleteFileOSS(url, FileUtil.PRIVATE_FOLDER);

        // 验证OSSUtil的deleteFileOSS方法被调用
        verify(ossUtil).deleteFileOSS(url, FileUtil.PRIVATE_FOLDER);
    }

    @Test
    void testGetUploadCertificate_validFile() {
        // 准备测试数据
        String filename = "test.txt";
        Long comId = 1L;
        Long id = 100L;
        String input = "description";

        // 模拟CommonUtil的行为
        try (var mockStatic = mockStatic(CommonUtil.class)) {
            mockStatic.when(() -> CommonUtil.getTypeByFilename(filename)).thenReturn("txt");
            mockStatic.when(() -> CommonUtil.isAllowUploadType("txt")).thenReturn(true);
            mockStatic.when(CommonUtil::creatShortUUID).thenReturn("uuid");

            // 模拟OSSUtil的返回值
            Map<String, String> mockResult = new HashMap<>();
            mockResult.put("url", "https://upload-url.com");
            mockResult.put("clearUrl", "https://clear-url.com");
            when(ossUtil.getUploadCertificateOSS(anyString(), anyInt())).thenReturn(mockResult);

            // 执行测试
            Map<String, String> result = fileUtil.getUploadCertificate(filename, comId, id, input);

            // 验证结果
            assertNotNull(result);
            assertEquals("https://upload-url.com", result.get("url"));
            assertEquals("https://clear-url.com", result.get("clearUrl"));

            // 验证OSSUtil方法被调用
            verify(ossUtil).getUploadCertificateOSS(anyString(), eq(FileUtil.PRIVATE_FOLDER));
        }
    }

    @Test
    void testGetUploadCertificate_invalidFileType() {
        String filename = "test.exe";
        Long comId = 1L;
        Long id = 100L;
        String input = "description";

        try (var mockStatic = mockStatic(CommonUtil.class)) {
            mockStatic.when(() -> CommonUtil.getTypeByFilename(filename)).thenReturn("exe");
            mockStatic.when(() -> CommonUtil.isAllowUploadType("exe")).thenReturn(false);

            BaseException ex =
                    assertThrows(
                            BaseException.class,
                            () -> fileUtil.getUploadCertificate(filename, comId, id, input));
            assertEquals(ErrorEnum.INVALID_FILE_TYPE_ERROR, ex.getErrorEnum());

            // 验证OSSUtil方法没有被调用
            verify(ossUtil, never()).getUploadCertificateOSS(anyString(), anyInt());
        }
    }
}
