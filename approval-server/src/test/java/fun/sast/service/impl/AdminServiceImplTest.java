package fun.sast.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.alibaba.fastjson2.JSONObject;
import fun.sast.entity.Competition;
import fun.sast.mapper.*;
import fun.sast.utils.FileUtil;
import java.time.LocalDateTime;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

class AdminServiceImplTest {

    @Mock private CompetitionMapper competitionMapper;

    @Mock private UserMapper userMapper;

    @Mock private DepartmentMapper departmentMapper;

    @Mock private FileUtil fileUtil;

    @InjectMocks private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCompetition_Success() {
        // 准备测试数据
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setUserCode("user001");
        competition.setMinTeamMembers(1);
        competition.setMaxTeamMembers(5);
        competition.setTable(JSONObject.from(new HashMap<>()));
        competition.setReviewSettings(new HashMap<>());

        MultipartFile cover = mock(MultipartFile.class);

        // 设置mock行为
        when(competitionMapper.insert(any(Competition.class))).thenReturn(1);
        when(competitionMapper.updateById(any(Competition.class))).thenReturn(1);
        when(userMapper.exists(any())).thenReturn(true);
        when(cover.isEmpty()).thenReturn(false);
        when(fileUtil.uploadCover(any(MultipartFile.class), anyLong()))
                .thenReturn("http://example.com/cover.jpg");

        // 执行测试
        assertDoesNotThrow(() -> adminService.createCompetition(competition, cover));

        // 验证调用
        verify(competitionMapper, times(1)).insert(any(Competition.class));
        verify(competitionMapper, times(1)).updateById(any(Competition.class));
    }

    @Test
    void testCreateCompetition_WithInvalidUser() {
        // 准备测试数据
        Competition competition = new Competition();
        competition.setUserCode("user001");
        competition.setMinTeamMembers(1);
        competition.setMaxTeamMembers(5);
        competition.setTable(JSONObject.from(new HashMap<>()));
        competition.setReviewSettings(new HashMap<>());

        MultipartFile cover = null;

        // 设置mock行为
        when(userMapper.exists(any())).thenReturn(false);

        // 执行测试和验证
        assertThrows(Exception.class, () -> adminService.createCompetition(competition, cover));
    }

    @Test
    void testEditCompetition_Success() {
        // 准备测试数据
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setUserCode("user001");
        competition.setMinTeamMembers(1);
        competition.setMaxTeamMembers(5);
        competition.setTable(JSONObject.from(new HashMap<>()));
        competition.setReviewSettings(new HashMap<>());

        MultipartFile cover = mock(MultipartFile.class);

        // 设置mock行为
        when(competitionMapper.selectOne(any())).thenReturn(competition);
        when(competitionMapper.updateById(any(Competition.class))).thenReturn(1);
        when(userMapper.exists(any())).thenReturn(true);
        when(cover.isEmpty()).thenReturn(false);
        when(fileUtil.uploadCover(any(MultipartFile.class), anyLong()))
                .thenReturn("http://example.com/cover.jpg");

        // 执行测试
        assertDoesNotThrow(() -> adminService.editCompetition(competition, cover));

        // 验证调用
        verify(competitionMapper, times(1)).selectOne(any());
        verify(competitionMapper, times(1)).updateById(any(Competition.class));
    }

    @Test
    void testEditCompetition_NotExist() {
        // 准备测试数据
        Competition competition = new Competition();
        competition.setId(1L);

        MultipartFile cover = null;

        // 设置mock行为
        when(competitionMapper.selectOne(any())).thenReturn(null);

        // 执行测试和验证
        assertThrows(Exception.class, () -> adminService.editCompetition(competition, cover));
    }

    @Test
    void testDeleteCompetition_Success() {
        // 准备测试数据
        Long competitionId = 1L;
        Competition competition = new Competition();
        competition.setId(competitionId);

        // 设置mock行为
        when(competitionMapper.selectById(competitionId)).thenReturn(competition);

        // 执行测试
        assertDoesNotThrow(() -> adminService.deleteCompetition(competitionId));

        // 验证调用
        verify(competitionMapper, times(1)).selectById(competitionId);
        verify(competitionMapper, times(1)).deleteById(competitionId);
    }

    @Test
    void testDeleteCompetition_NotExist() {
        // 准备测试数据
        Long competitionId = 1L;

        // 设置mock行为
        when(competitionMapper.selectById(competitionId)).thenReturn(null);

        // 执行测试和验证
        assertThrows(Exception.class, () -> adminService.deleteCompetition(competitionId));
    }

    @Test
    void testValidateCompetitionDates_ValidDates() {
        // 准备测试数据
        Competition competition = new Competition();
        competition.setRegBeginTime(LocalDateTime.of(2023, 1, 1, 0, 0));
        competition.setRegEndTime(LocalDateTime.of(2023, 1, 10, 0, 0));
        competition.setSubmitBeginTime(LocalDateTime.of(2023, 1, 11, 0, 0));
        competition.setSubmitEndTime(LocalDateTime.of(2023, 1, 20, 0, 0));
        competition.setReviewBeginTime(LocalDateTime.of(2023, 1, 21, 0, 0));
        competition.setReviewEndTime(LocalDateTime.of(2023, 1, 30, 0, 0));

        // 执行测试
        assertDoesNotThrow(
                () -> {
                    // 使用反射调用私有方法
                    java.lang.reflect.Method method =
                            AdminServiceImpl.class.getDeclaredMethod(
                                    "validateCompetitionDates", Competition.class);
                    method.setAccessible(true);
                    method.invoke(adminService, competition);
                });
    }

    @Test
    void testValidateCompetitionDates_InvalidDates() {
        // 准备测试数据 - 提交开始时间早于报名开始时间
        Competition competition = new Competition();
        competition.setRegBeginTime(LocalDateTime.of(2023, 1, 10, 0, 0));
        competition.setSubmitBeginTime(LocalDateTime.of(2023, 1, 5, 0, 0));

        // 执行测试和验证
        assertThrows(
                Exception.class,
                () -> {
                    // 使用反射调用私有方法
                    java.lang.reflect.Method method =
                            AdminServiceImpl.class.getDeclaredMethod(
                                    "validateCompetitionDates", Competition.class);
                    method.setAccessible(true);
                    method.invoke(adminService, competition);
                });
    }

    @Test
    void testDepIsExist_Exists() {
        // 设置mock行为
        when(departmentMapper.exists(any())).thenReturn(true);

        // 执行测试
        boolean result = adminService.depIsExist(1);

        // 验证结果
        assertTrue(result);
        verify(departmentMapper, times(1)).exists(any());
    }

    @Test
    void testDepIsExist_NotExists() {
        // 设置mock行为
        when(departmentMapper.exists(any())).thenReturn(false);

        // 执行测试
        boolean result = adminService.depIsExist(1);

        // 验证结果
        assertFalse(result);
        verify(departmentMapper, times(1)).exists(any());
    }

    @Test
    void testUserIsExist_Exists() {
        // 设置mock行为
        when(userMapper.exists(any())).thenReturn(true);

        // 执行测试
        boolean result = adminService.userIsExist("user001");

        // 验证结果
        assertTrue(result);
        verify(userMapper, times(1)).exists(any());
    }

    @Test
    void testUserIsExist_NotExists() {
        // 设置mock行为
        when(userMapper.exists(any())).thenReturn(false);

        // 执行测试
        boolean result = adminService.userIsExist("user001");

        // 验证结果
        assertFalse(result);
        verify(userMapper, times(1)).exists(any());
    }

    @Test
    void testIsImage_ValidFormats() {
        // 执行测试
        assertTrue(adminService.isImage("jpg"));
        assertTrue(adminService.isImage("jpeg"));
        assertTrue(adminService.isImage("png"));
    }

    @Test
    void testIsImage_InvalidFormat() {
        // 执行测试
        assertFalse(adminService.isImage("gif"));
        assertFalse(adminService.isImage("bmp"));
    }
}
