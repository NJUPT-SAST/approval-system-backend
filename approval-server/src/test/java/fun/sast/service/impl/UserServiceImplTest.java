package fun.sast.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.sast.Exception.BaseException;
import fun.sast.entity.Competition;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.utils.JwtUtil;
import fun.sast.utils.RedisUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImplTest.class);

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CompetitionMapper competitionMapper;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private List<Competition> mockCompetitionList;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        mockCompetitionList = new ArrayList<>();
        
        Competition comp1 = new Competition();
        comp1.setId(1);
        comp1.setName("人工智能大赛");
        comp1.setIntroduce("这是一场关于人工智能技术的比赛");
        comp1.setIsReview(Competition.REVIEWED);
        mockCompetitionList.add(comp1);
        
        Competition comp2 = new Competition();
        comp2.setId(2);
        comp2.setName("大数据挑战赛");
        comp2.setIntroduce("面向大数据分析领域的挑战赛");
        comp2.setIsReview(Competition.REVIEWED);
        mockCompetitionList.add(comp2);
        
        Competition comp3 = new Competition();
        comp3.setId(3);
        comp3.setName("未审批比赛");
        comp3.setIntroduce("这个比赛尚未通过审批");
        comp3.setIsReview(Competition.NOT_REVIEWED);
        mockCompetitionList.add(comp3);
    }

    /**
     * 测试正常搜索比赛功能
     */
    @Test
    void testSearchComName_Success() {
        // 准备模拟数据
        String searchKey = "人工智能";
        Integer curPage = 1;
        Integer pageSize = 10;
        
        // 准备预期结果
        List<Competition> expectedResults = new ArrayList<>();
        expectedResults.add(mockCompetitionList.get(0)); // 只有"人工智能大赛"应该被匹配
        
        // 创建分页对象
        IPage<Competition> page = new Page<>(curPage, pageSize);
        page.setRecords(expectedResults);
        page.setTotal(expectedResults.size());
        page.setPages(1);
        
        // 设置mock行为
        when(competitionMapper.selectPage(any(IPage.class), any())).thenReturn(page);
        
        // 执行测试方法
        Map<String, Object> result = userService.searchComName(searchKey, curPage, pageSize);
        
        // 验证结果
        assertNotNull(result);
        assertTrue((boolean) result.get("success"));
        assertEquals(expectedResults.size(), ((Number)result.get("total")).intValue());
        assertEquals(curPage, ((Number)result.get("pageNum")).intValue());
        assertEquals(pageSize, ((Number)result.get("pageSize")).intValue());
        assertEquals(1, ((Number)result.get("pages")).intValue());
        
        List<Competition> actualList = (List<Competition>) result.get("list");
        assertNotNull(actualList);
        assertEquals(expectedResults.size(), actualList.size());
        assertEquals(expectedResults.get(0).getName(), actualList.get(0).getName());
        
        log.info("测试搜索比赛成功: 关键词={}, 结果数量={}", searchKey, actualList.size());
    }

    /**
     * 测试空关键词搜索
     */
    @Test
    void testSearchComName_EmptyKey() {
        // 准备模拟数据
        String searchKey = "";
        Integer curPage = 1;
        Integer pageSize = 10;
        
        // 准备预期结果 - 只包含已审批的比赛
        List<Competition> expectedResults = new ArrayList<>();
        expectedResults.add(mockCompetitionList.get(0));
        expectedResults.add(mockCompetitionList.get(1));
        
        // 创建分页对象
        IPage<Competition> page = new Page<>(curPage, pageSize);
        page.setRecords(expectedResults);
        page.setTotal(expectedResults.size());
        page.setPages(1);
        
        // 设置mock行为
        when(competitionMapper.selectPage(any(IPage.class), any())).thenReturn(page);
        
        // 执行测试方法
        Map<String, Object> result = userService.searchComName(searchKey, curPage, pageSize);
        
        // 验证结果
        assertNotNull(result);
        assertTrue((boolean) result.get("success"));
        assertEquals(expectedResults.size(), ((Number)result.get("total")).intValue());
        
        List<Competition> actualList = (List<Competition>) result.get("list");
        assertNotNull(actualList);
        assertEquals(expectedResults.size(), actualList.size());
        
        log.info("测试空关键词搜索成功: 结果数量={}", actualList.size());
    }

    /**
     * 测试空参数处理
     */
    @Test
    void testSearchComName_NullParameters() {
        // 准备模拟数据
        String searchKey = null;
        Integer curPage = null;
        Integer pageSize = null;
        
        // 准备预期结果 - 只包含已审批的比赛
        List<Competition> expectedResults = new ArrayList<>();
        expectedResults.add(mockCompetitionList.get(0));
        expectedResults.add(mockCompetitionList.get(1));
        
        // 创建分页对象
        IPage<Competition> page = new Page<>(1, 10); // 应该使用默认值
        page.setRecords(expectedResults);
        page.setTotal(expectedResults.size());
        page.setPages(1);
        
        // 设置mock行为
        when(competitionMapper.selectPage(any(IPage.class), any())).thenReturn(page);
        
        // 执行测试方法
        Map<String, Object> result = userService.searchComName(searchKey, curPage, pageSize);
        
        // 验证结果
        assertNotNull(result);
        assertTrue((boolean) result.get("success"));
        assertEquals(expectedResults.size(), ((Number)result.get("total")).intValue());
        assertEquals(1, ((Number)result.get("pageNum")).intValue()); // 验证使用了默认页码
        assertEquals(10, ((Number)result.get("pageSize")).intValue()); // 验证使用了默认每页条数
        
        log.info("测试空参数处理成功: 使用默认页码={}, 默认每页条数={}", 
                 result.get("pageNum"), result.get("pageSize"));
    }

    /**
     * 测试边界条件 - 页码小于1
     */
    @Test
    void testSearchComName_PageLessThanOne() {
        // 准备模拟数据
        String searchKey = "比赛";
        Integer curPage = 0; // 小于1的页码
        Integer pageSize = 5;
        
        // 准备预期结果
        List<Competition> expectedResults = new ArrayList<>();
        expectedResults.add(mockCompetitionList.get(0));
        expectedResults.add(mockCompetitionList.get(1));
        
        // 创建分页对象
        IPage<Competition> page = new Page<>(1, pageSize); // 应该使用默认值1
        page.setRecords(expectedResults);
        page.setTotal(expectedResults.size());
        page.setPages(1);
        
        // 设置mock行为
        when(competitionMapper.selectPage(any(IPage.class), any())).thenReturn(page);
        
        // 执行测试方法
        Map<String, Object> result = userService.searchComName(searchKey, curPage, pageSize);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, ((Number)result.get("pageNum")).intValue()); // 验证页码被修正为1
        
        log.info("测试页码小于1边界条件成功: 页码被修正为{}", result.get("pageNum"));
    }

    /**
     * 测试边界条件 - 每页条数大于100
     */
    @Test
    void testSearchComName_PageSizeGreaterThanOneHundred() {
        // 准备模拟数据
        String searchKey = "比赛";
        Integer curPage = 1;
        Integer pageSize = 200; // 大于100的页码
        
        // 准备预期结果
        List<Competition> expectedResults = new ArrayList<>();
        expectedResults.add(mockCompetitionList.get(0));
        expectedResults.add(mockCompetitionList.get(1));
        
        // 创建分页对象
        IPage<Competition> page = new Page<>(curPage, 10); // 应该使用默认值10
        page.setRecords(expectedResults);
        page.setTotal(expectedResults.size());
        page.setPages(1);
        
        // 设置mock行为
        when(competitionMapper.selectPage(any(IPage.class), any())).thenReturn(page);
        
        // 执行测试方法
        Map<String, Object> result = userService.searchComName(searchKey, curPage, pageSize);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(10, ((Number)result.get("pageSize")).intValue()); // 验证每页条数被修正为10
        
        log.info("测试每页条数大于100边界条件成功: 每页条数被修正为{}", result.get("pageSize"));
    }

    /**
     * 测试异常处理
     */
    @Test
    void testSearchComName_Exception() {
        // 准备模拟数据
        String searchKey = "测试关键词";
        Integer curPage = 1;
        Integer pageSize = 10;
        
        // 设置mock行为抛出异常
        when(competitionMapper.selectPage(any(IPage.class), any())).thenThrow(new RuntimeException("Database error"));
        
        // 执行测试方法并验证异常
        try {
            userService.searchComName(searchKey, curPage, pageSize);
            // 如果没有抛出异常，测试失败
            assertTrue(false, "Expected BaseException to be thrown");
        } catch (BaseException e) {
            // 验证异常类型和错误码
            assertEquals(ErrorEnum.COMMON_ERROR, e.getErrorEnum());
            log.info("测试异常处理成功: 正确捕获并转换异常");
        }
    }

    /**
     * 测试通过介绍内容搜索
     */
    @Test
    void testSearchComName_SearchByIntroduce() {
        // 准备模拟数据
        String searchKey = "大数据";
        Integer curPage = 1;
        Integer pageSize = 10;
        
        // 准备预期结果
        List<Competition> expectedResults = new ArrayList<>();
        expectedResults.add(mockCompetitionList.get(1)); // 只有"大数据挑战赛"的介绍包含"大数据"
        
        // 创建分页对象
        IPage<Competition> page = new Page<>(curPage, pageSize);
        page.setRecords(expectedResults);
        page.setTotal(expectedResults.size());
        page.setPages(1);
        
        // 设置mock行为
        when(competitionMapper.selectPage(any(IPage.class), any())).thenReturn(page);
        
        // 执行测试方法
        Map<String, Object> result = userService.searchComName(searchKey, curPage, pageSize);
        
        // 验证结果
        assertNotNull(result);
        assertTrue((boolean) result.get("success"));
        assertEquals(expectedResults.size(), ((Number)result.get("total")).intValue());
        
        List<Competition> actualList = (List<Competition>) result.get("list");
        assertNotNull(actualList);
        assertEquals(expectedResults.size(), actualList.size());
        assertEquals(expectedResults.get(0).getId(), ((Number)actualList.get(0).getId()).intValue());
        
        log.info("测试通过介绍内容搜索成功: 关键词={}, 结果数量={}", searchKey, actualList.size());
    }
}