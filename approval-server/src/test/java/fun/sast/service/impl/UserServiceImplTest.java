package fun.sast.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fun.sast.Exception.BaseException;
import fun.sast.entity.Competition;
import fun.sast.entity.Team;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.mapper.TeamMapper;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock private CompetitionMapper competitionMapper;

    @Mock private TeamMapper teamMapper;

    @InjectMocks private UserServiceImpl userService;

    private User testUser;
    private Competition testCompetition;
    private String validJsonData;

    @BeforeEach
    void setUp() {
        // 初始化测试用户
        testUser = new User();
        testUser.setId(1);
        testUser.setCode("123456");
        testUser.setName("Test User");

        // 清理ThreadLocal，确保测试独立性
        UserInterceptor.userHolder.remove();
        UserInterceptor.competitionIdHolder.remove();

        // 初始化测试比赛
        testCompetition = new Competition();
        testCompetition.setId(1);
        testCompetition.setName("Test Competition");
        testCompetition.setIsReview(Competition.REVIEWED); // 已审批
        testCompetition.setType(Competition.TEAM); // 团队赛
        testCompetition.setMinTeamMembers(2);
        testCompetition.setMaxTeamMembers(5);

        // 设置当前时间在报名时间范围内，使用yyyy-MM-dd HH:mm:ss格式
        testCompetition.setRegBeginTime(
                formatDateToPattern(new Date(System.currentTimeMillis() - 10000)));
        testCompetition.setRegEndTime(
                formatDateToPattern(new Date(System.currentTimeMillis() + 10000)));

        // 初始化有效的JSON数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comId", 1L);
        jsonObject.put("teamName", "Test Team");

        JSONArray teamMember = new JSONArray();
        JSONObject member1 = new JSONObject();
        member1.put("code", "654321");
        member1.put("name", "Member One");
        teamMember.add(member1);
        jsonObject.put("teamMember", teamMember);

        validJsonData = jsonObject.toString();
    }

    @Test
    void testSignUpCom_Success() {
        // 配置mock行为
        when(competitionMapper.selectById(1L)).thenReturn(testCompetition);
        when(teamMapper.selectOne(any(QueryWrapper.class))).thenReturn(null); // 用户未报名过
        when(teamMapper.insert(any(Team.class))).thenReturn(1); // 插入成功

        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法
        assertDoesNotThrow(() -> userService.signUpCom(testUser, validJsonData));

        // 验证mock调用
        verify(competitionMapper).selectById(1L);
        verify(teamMapper).selectOne(any(QueryWrapper.class));
        verify(teamMapper).insert(any(Team.class));
    }

    @Test
    void testSignUpCom_UserIsNull() {
        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(BaseException.class, () -> userService.signUpCom(null, validJsonData));

        // 验证异常信息
        assertEquals(ErrorEnum.NO_LOGIN, exception.getErrorEnum());
    }

    @Test
    void testSignUpCom_JsonDataIsNull() {
        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(BaseException.class, () -> userService.signUpCom(testUser, null));

        // 验证异常信息
        assertEquals(ErrorEnum.JSON_DATA_EMPTY, exception.getErrorEnum());
    }

    @Test
    void testSignUpCom_JsonDataEmpty() {
        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(BaseException.class, () -> userService.signUpCom(testUser, ""));

        // 验证异常信息
        assertEquals(ErrorEnum.JSON_DATA_EMPTY, exception.getErrorEnum());
    }

    @Test
    void testSignUpCom_JsonFormatError() {
        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(
                        BaseException.class, () -> userService.signUpCom(testUser, "invalid json"));

        // 验证异常信息
        assertEquals(ErrorEnum.JSON_FORMAT_ERROR, exception.getErrorEnum());
    }

    @Test
    void testSignUpCom_CompetitionNotExist() {
        // 配置mock行为
        when(competitionMapper.selectById(1L)).thenReturn(null);

        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(
                        BaseException.class, () -> userService.signUpCom(testUser, validJsonData));

        // 验证异常信息
        assertEquals(ErrorEnum.CONTEST_NOT_EXIST, exception.getErrorEnum());
    }

    @Test
    void testSignUpCom_CompetitionNotReviewed() {
        // 修改比赛状态为未审批
        testCompetition.setIsReview(Competition.NOT_REVIEWED);

        // 配置mock行为
        when(competitionMapper.selectById(1L)).thenReturn(testCompetition);

        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(
                        BaseException.class, () -> userService.signUpCom(testUser, validJsonData));

        // 验证异常信息
        assertEquals(ErrorEnum.CONTEST_NOT_REVIEWED, exception.getErrorEnum());
    }

    @Test
    void testSignUpCom_AlreadySignedUp() {
        // 配置mock行为
        when(competitionMapper.selectById(1L)).thenReturn(testCompetition);

        // 模拟用户已报名
        Team existingTeam = new Team();
        existingTeam.setId(1L);
        existingTeam.setComId(1L);
        existingTeam.setCaptain("123456");
        when(teamMapper.selectOne(any(QueryWrapper.class))).thenReturn(existingTeam);

        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(
                        BaseException.class, () -> userService.signUpCom(testUser, validJsonData));

        // 验证异常信息
        assertEquals(ErrorEnum.ALREADY_SIGNED_UP_CONTEST, exception.getErrorEnum());
    }

    @Test
    void testSignUpCom_TeamNameEmpty() {
        // 创建缺少团队名称的JSON数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comId", 1L);
        // 不设置团队名称

        JSONArray teamMember = new JSONArray();
        JSONObject member1 = new JSONObject();
        member1.put("code", "654321");
        member1.put("name", "Member One");
        teamMember.add(member1);
        jsonObject.put("teamMember", teamMember);

        String jsonDataWithoutTeamName = jsonObject.toString();

        // 配置mock行为
        when(competitionMapper.selectById(1L)).thenReturn(testCompetition);
        when(teamMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(
                        BaseException.class,
                        () -> userService.signUpCom(testUser, jsonDataWithoutTeamName));

        // 验证异常信息
        assertEquals(ErrorEnum.TEAM_NAME_EMPTY, exception.getErrorEnum());
    }

    @Test
    void testSignUpCom_TeamMembersEmpty() {
        // 创建团队成员为空的JSON数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comId", 1L);
        jsonObject.put("teamName", "Test Team");
        jsonObject.put("teamMember", new JSONArray()); // 空成员数组

        String jsonDataWithEmptyMembers = jsonObject.toString();

        // 配置mock行为
        when(competitionMapper.selectById(1L)).thenReturn(testCompetition);
        when(teamMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(
                        BaseException.class,
                        () -> userService.signUpCom(testUser, jsonDataWithEmptyMembers));

        // 验证异常信息
        assertEquals(ErrorEnum.TEAM_MEMBERS_EMPTY, exception.getErrorEnum());
    }

    @Test
    void testSignUpCom_TeamSaveFailed() {
        // 配置mock行为
        when(competitionMapper.selectById(1L)).thenReturn(testCompetition);
        when(teamMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(teamMapper.insert(any(Team.class))).thenReturn(0); // 插入失败

        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(
                        BaseException.class, () -> userService.signUpCom(testUser, validJsonData));

        // 验证异常信息
        assertEquals(ErrorEnum.TEAM_SAVE_FAILED, exception.getErrorEnum());
    }

    @Test
    void testSignUpCom_RegistrationTimeNotStarted() {
        // 设置报名时间未开始，使用yyyy-MM-dd HH:mm:ss格式
        testCompetition.setRegBeginTime(
                formatDateToPattern(new Date(System.currentTimeMillis() + 10000)));
        testCompetition.setRegEndTime(
                formatDateToPattern(new Date(System.currentTimeMillis() + 20000)));

        // 配置mock行为
        when(competitionMapper.selectById(1L)).thenReturn(testCompetition);

        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(
                        BaseException.class, () -> userService.signUpCom(testUser, validJsonData));

        // 验证异常信息
        assertEquals(ErrorEnum.SIGN_UP_TIME_NOT_STARTED, exception.getErrorEnum());
    }

    @Test
    void testSignUpCom_RegistrationTimeEnded() {
        // 设置报名时间已结束，使用yyyy-MM-dd HH:mm:ss格式
        testCompetition.setRegBeginTime(
                formatDateToPattern(new Date(System.currentTimeMillis() - 20000)));
        testCompetition.setRegEndTime(
                formatDateToPattern(new Date(System.currentTimeMillis() - 10000)));

        // 配置mock行为
        when(competitionMapper.selectById(1L)).thenReturn(testCompetition);

        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(
                        BaseException.class, () -> userService.signUpCom(testUser, validJsonData));

        // 验证异常信息
        assertEquals(ErrorEnum.SIGN_UP_TIME_EXPIRED, exception.getErrorEnum());
    }

    @Test
    void testSignUpCom_TimeFormatError() {
        // 设置时间格式错误
        testCompetition.setRegBeginTime("invalid_time_format");
        testCompetition.setRegEndTime("invalid_time_format");

        // 配置mock行为
        when(competitionMapper.selectById(1L)).thenReturn(testCompetition);

        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(
                        BaseException.class, () -> userService.signUpCom(testUser, validJsonData));

        // 验证异常信息
        assertEquals(ErrorEnum.DATE_FORMAT_ERROR, exception.getErrorEnum());
    }

    @Test
    void testSignUpCom_TeamSizeExceedLimit() {
        // 创建超出人数限制的团队成员数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comId", 1L);
        jsonObject.put("teamName", "Test Team");

        JSONArray teamMember = new JSONArray();
        // 添加5个成员，加上队长总共6人，超过最大限制5人
        for (int i = 0; i < 5; i++) {
            JSONObject member = new JSONObject();
            member.put("code", "member" + i);
            member.put("name", "Member " + i);
            teamMember.add(member);
        }
        jsonObject.put("teamMember", teamMember);

        String jsonDataWithTooManyMembers = jsonObject.toString();

        // 配置mock行为
        when(competitionMapper.selectById(1L)).thenReturn(testCompetition);

        // 设置competitionIdHolder
        UserInterceptor.competitionIdHolder.set(1L);

        // 执行测试方法，预期抛出异常
        BaseException exception =
                assertThrows(
                        BaseException.class,
                        () -> userService.signUpCom(testUser, jsonDataWithTooManyMembers));

        // 验证异常信息
        assertEquals(ErrorEnum.LIMIT_ERROR, exception.getErrorEnum());
    }

    @AfterEach
    void tearDown() {
        // 清理ThreadLocal，避免测试污染
        UserInterceptor.userHolder.remove();
        UserInterceptor.competitionIdHolder.remove();
    }

    /** 将Date对象格式化为yyyy-MM-dd HH:mm:ss格式的字符串 */
    private String formatDateToPattern(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
