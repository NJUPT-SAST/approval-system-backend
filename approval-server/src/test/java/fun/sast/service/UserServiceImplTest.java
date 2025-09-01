package fun.sast.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import fun.sast.Exception.BaseException;
import fun.sast.dto.WorkSchemaDTO;
import fun.sast.entity.*;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.*;
import fun.sast.service.impl.UserServiceImpl;
import fun.sast.utils.FileUtil;
import fun.sast.utils.OSSUtil;
import fun.sast.utils.RedisUtil;
import fun.sast.vo.UserProfileVO;
import java.util.LinkedList;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks private UserServiceImpl userService;

    @Mock private FileUtil fileUtil;

    @Mock private OSSUtil ossUtil;

    @Mock private RedisUtil redisUtil;

    @Mock private ReviewService reviewService;

    @Mock private CompetitionService competitionService;

    @Mock private WorkMapper workMapper;

    @Mock private FileService fileService;

    @Mock private DepartmentMapper departmentMapper;

    @Mock private CompetitionMapper competitionMapper;

    @Mock private TeamMapper teamMapper;

    private User testUser;
    private Department testDepartment;
    private Competition testCompetition;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setCode("test001");
        testUser.setName("Test User");
        testUser.setMajor("Computer Science");
        testUser.setContact("test@example.com");
        testUser.setDepId(1);

        testDepartment = new Department();
        testDepartment.setId(1);
        testDepartment.setName("Test Department");

        testCompetition = new Competition();
        testCompetition.setId(1);
        testCompetition.setTable(new JSONObject());
    }

    @Test
    void getUserProfile_ReturnsUserProfileVO() {
        // Given
        when(departmentMapper.selectById(1)).thenReturn(testDepartment);

        // When
        UserProfileVO profile = userService.getUserProfile(testUser);

        // Then
        assertNotNull(profile);
        assertEquals(testUser.getName(), profile.getName());
        assertEquals(testUser.getMajor(), profile.getMajor());
        assertEquals(testUser.getContact(), profile.getContact());
        assertEquals(testUser.getCode(), profile.getCode());
        assertEquals(testDepartment.getName(), profile.getDepartmentName());
    }

    @Test
    void getUserProfile_WithNullDepartment_ReturnsUserProfileWithoutDepartment() {
        // Given
        when(departmentMapper.selectById(1)).thenReturn(null);

        // When
        UserProfileVO profile = userService.getUserProfile(testUser);

        // Then
        assertNotNull(profile);
        assertNull(profile.getDepartmentName());
    }

    @Test
    void getComSchemaTemplate_WithValidComId_ReturnsSchemaTemplate() {
        // Given
        JSONObject expectedSchema = new JSONObject();
        expectedSchema.put("field1", "value1");
        testCompetition.setTable(expectedSchema);
        when(competitionMapper.selectById(anyLong())).thenReturn(testCompetition);

        // When
        JSONObject result = userService.getComSchemaTemplate(1L);

        // Then
        assertNotNull(result);
        assertEquals(expectedSchema, result);
    }

    @Test
    void getComSchemaTemplate_WithInvalidComId_ThrowsException() {
        // Given
        when(competitionMapper.selectById(anyLong())).thenReturn(null);

        // When & Then
        BaseException exception =
                assertThrows(
                        BaseException.class,
                        () -> {
                            userService.getComSchemaTemplate(1L);
                        });

        assertEquals(ErrorEnum.UNKNOWN_COMPETITION_ID, exception.getErrorEnum());
    }

    @Test
    void getComSchemaTemplate_WithNullTable_ThrowsException() {
        // Given
        testCompetition.setTable(null);
        when(competitionMapper.selectById(anyLong())).thenReturn(testCompetition);

        // When & Then
        BaseException exception =
                assertThrows(
                        BaseException.class,
                        () -> {
                            userService.getComSchemaTemplate(1L);
                        });

        assertEquals(ErrorEnum.SCHEMA_ERROR, exception.getErrorEnum());
    }

    @Test
    void getSubmittedComSchemaTemplate_WithValidData_ReturnsSchema() {
        // Given
        Work work = new Work();
        work.setSchemaContent("[{\"input\":\"test\",\"content\":\"value\"}]");
        when(workMapper.selectOne(any())).thenReturn(work);

        // When
        JSONArray result = userService.getSubmittedComSchemaTemplate(testUser, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getSubmittedComSchemaTemplate_WithNoWork_ThrowsException() {
        // Given
        when(workMapper.selectOne(any())).thenReturn(null);

        // When & Then
        BaseException exception =
                assertThrows(
                        BaseException.class,
                        () -> {
                            userService.getSubmittedComSchemaTemplate(testUser, 1L);
                        });

        assertEquals(ErrorEnum.HAVE_NOT_UPLOAD_WORK, exception.getErrorEnum());
    }

    @Test
    void getUploadCertificate_ReturnsCertificate() {
        // Given
        Team team = new Team();
        team.setId(1L);
        team.setCaptain("test001");
        team.setComId(1L);

        when(teamMapper.selectOne(any())).thenReturn(team);
        when(redisUtil.hasKey(anyString())).thenReturn(false);
        when(fileUtil.getUploadCertificate(anyString(), anyLong(), anyLong(), anyString()))
                .thenReturn(Map.of("url", "test-url", "clearUrl", "clear-test-url"));

        // When
        Map<String, String> result =
                userService.getUploadCertificate(testUser, 1L, "input1", "test.txt");

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("url"));
        verify(redisUtil, times(1)).set(anyString(), anyString());
    }

    @Test
    void getUploadCertificate_WithExistingCache_DeletesOldFileAndCache() {
        // Given
        Team team = new Team();
        team.setId(1L);
        team.setCaptain("test001");
        team.setComId(1L);

        when(teamMapper.selectOne(any())).thenReturn(team);
        when(redisUtil.hasKey(anyString())).thenReturn(true);
        when(redisUtil.get(anyString()))
                .thenReturn(
                        "{\"comId\":1,\"userCode\":\"test001\",\"input\":\"input1\",\"url\":\"old-url\"}");
        when(fileUtil.getUploadCertificate(anyString(), anyLong(), anyLong(), anyString()))
                .thenReturn(Map.of("url", "test-url", "clearUrl", "clear-test-url"));

        // When
        Map<String, String> result =
                userService.getUploadCertificate(testUser, 1L, "input1", "test.txt");

        // Then
        assertNotNull(result);
        verify(fileUtil, times(1)).deleteFileOSS(anyString(), anyInt());
        verify(redisUtil, times(1)).delete(anyString());
        verify(redisUtil, times(1)).set(anyString(), anyString());
    }

    @Test
    void uploadComSchema_WithExistingWork_UpdatesWork() {
        // Given
        LinkedList<WorkSchemaDTO> schemaList = new LinkedList<>();
        WorkSchemaDTO schemaDTO = new WorkSchemaDTO();
        schemaDTO.setInput("作品名称");
        schemaDTO.setContent("Test Work");
        schemaDTO.setIsFile(false);
        schemaList.add(schemaDTO);

        Work existingWork = new Work();
        existingWork.setId(1L);
        existingWork.setComId(1L);
        existingWork.setUserCode("test001");

        when(workMapper.selectOne(any())).thenReturn(existingWork);
        doNothing().when(competitionService).validateSubmissionPeriod(anyLong());

        // When
        userService.uploadComSchema(testUser, 1L, schemaList);

        // Then
        verify(workMapper, times(1)).updateById(any(Work.class));
        verify(reviewService, times(1)).updateReviewStatus(anyLong(), anyString());
    }

    @Test
    void uploadComSchema_WithNewWork_InsertsWork() {
        // Given
        LinkedList<WorkSchemaDTO> schemaList = new LinkedList<>();
        WorkSchemaDTO schemaDTO = new WorkSchemaDTO();
        schemaDTO.setInput("作品名称");
        schemaDTO.setContent("Test Work");
        schemaDTO.setIsFile(false);
        schemaList.add(schemaDTO);

        when(workMapper.selectOne(any())).thenReturn(null);
        doNothing().when(competitionService).validateSubmissionPeriod(anyLong());

        // When
        userService.uploadComSchema(testUser, 1L, schemaList);

        // Then
        verify(workMapper, times(1)).insert(any(Work.class));
        verify(reviewService, times(1)).updateReviewStatus(anyLong(), anyString());
    }

    @Test
    void uploadComSchema_WithFileContent_ProcessesFile() {
        // Given
        LinkedList<WorkSchemaDTO> schemaList = new LinkedList<>();
        WorkSchemaDTO schemaDTO = new WorkSchemaDTO();
        schemaDTO.setInput("作品文件");
        schemaDTO.setContent("https://oss.example.com/test-file.pdf");
        schemaDTO.setIsFile(false); // Will be detected as file
        schemaList.add(schemaDTO);

        when(workMapper.selectOne(any())).thenReturn(null);
        doNothing().when(competitionService).validateSubmissionPeriod(anyLong());
        when(ossUtil.isOSSBucketURL(anyString())).thenReturn(true);

        // When
        userService.uploadComSchema(testUser, 1L, schemaList);

        // Then
        verify(fileService, times(1))
                .processSubmissionFiles(any(User.class), anyLong(), anyString(), anyString());
        verify(workMapper, times(1)).insert(any(Work.class));
        verify(reviewService, times(1)).updateReviewStatus(anyLong(), anyString());
    }

    @Test
    void uploadComSchema_WithDifferentWorkNameFields_ProcessesCorrectly() {
        // Given
        LinkedList<WorkSchemaDTO> schemaList = new LinkedList<>();
        WorkSchemaDTO schemaDTO = new WorkSchemaDTO();
        schemaDTO.setInput("项目名称"); // Different work name field
        schemaDTO.setContent("Test Project");
        schemaDTO.setIsFile(false);
        schemaList.add(schemaDTO);

        when(workMapper.selectOne(any())).thenReturn(null);
        doNothing().when(competitionService).validateSubmissionPeriod(anyLong());

        // When
        userService.uploadComSchema(testUser, 1L, schemaList);

        // Then
        verify(workMapper, times(1)).insert(any(Work.class));
        verify(reviewService, times(1)).updateReviewStatus(anyLong(), anyString());
    }

    @Test
    void uploadComSchema_WithFileURL_ProcessesAsFile() {
        // Given
        LinkedList<WorkSchemaDTO> schemaList = new LinkedList<>();
        WorkSchemaDTO schemaDTO = new WorkSchemaDTO();
        schemaDTO.setInput("附件");
        schemaDTO.setContent("https://oss.example.com/file.pdf");
        schemaDTO.setIsFile(false);
        schemaList.add(schemaDTO);

        when(workMapper.selectOne(any())).thenReturn(null);
        doNothing().when(competitionService).validateSubmissionPeriod(anyLong());
        when(ossUtil.isOSSBucketURL(anyString())).thenReturn(true);

        // When
        userService.uploadComSchema(testUser, 1L, schemaList);

        // Then
        verify(fileService, times(1))
                .processSubmissionFiles(any(User.class), anyLong(), anyString(), anyString());
        verify(workMapper, times(1)).insert(any(Work.class));
        verify(reviewService, times(1)).updateReviewStatus(anyLong(), anyString());
    }

    @Test
    void getUploadCertificate_WithNoTeam_ThrowsException() {
        // Given
        when(teamMapper.selectOne(any())).thenReturn(null);

        // When & Then
        BaseException exception =
                assertThrows(
                        BaseException.class,
                        () -> {
                            userService.getUploadCertificate(testUser, 1L, "input1", "test.txt");
                        });

        assertEquals("找不到相应的队伍", exception.getMessage());
    }
}
