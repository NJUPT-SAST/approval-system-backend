package fun.sast.controller.userController;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import fun.sast.dto.WorkSchemaDTO;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.handler.GlobalExceptionHandler;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.service.UserService;
import fun.sast.vo.UserProfileVO;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private UserService userService;

    @InjectMocks private UserController userController;

    private MockMvc mockMvc;

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @BeforeEach
    void setUp() {
        // 创建测试用户
        User testUser = new User();
        testUser.setId(1);
        testUser.setName("testUser");

        // 设置到线程变量
        UserInterceptor.userHolder.set(testUser);

        // 初始化MockMvc并集成全局异常处理器
        mockMvc =
                MockMvcBuilders.standaloneSetup(userController)
                        .setControllerAdvice(globalExceptionHandler) // 关键：添加全局异常处理器
                        .build();
    }

    @AfterEach
    void tearDown() {
        // 清除线程变量
        UserInterceptor.userHolder.remove();
    }

    // 测试：获取用户信息 - 成功
    @Test
    void getUserProfile_Success() throws Exception {
        UserProfileVO mockProfile = new UserProfileVO();
        mockProfile.setName("testUser");

        when(userService.getUserProfile(any())).thenReturn(mockProfile);

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andDo(
                        result ->
                                System.out.println(
                                        "Response: "
                                                + result.getResponse()
                                                        .getContentAsString())) // 打印响应用于调试
                .andExpect(jsonPath("$.name").value("testUser")); // 根据实际响应结构调整
    }

    // 测试：获取用户信息 - 用户不存在
    @Test
    void getUserProfile_UserNotExist() throws Exception {
        // 清除用户模拟
        UserInterceptor.userHolder.remove();

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errCode").value(ErrorEnum.USER_NOT_EXIST.getErrCode()))
                .andExpect(jsonPath("$.errMsg").exists());
    }

    // 测试：获取比赛表单模板
    @Test
    void getComSchemaTemplate_Success() throws Exception {
        JSONObject mockSchema = new JSONObject();
        mockSchema.put("field", "value");
        when(userService.getComSchemaTemplate(anyLong())).thenReturn(mockSchema);

        mockMvc.perform(get("/user/com/schema/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.field").value("value"));
    }

    // 测试：获取已提交的表单
    @Test
    void getSubmittedComSchemaTemplate_Success() throws Exception {
        JSONArray mockArray = new JSONArray();
        mockArray.add(new JSONObject().fluentPut("data", "test"));
        when(userService.getSubmittedComSchemaTemplate(any(), anyLong())).thenReturn(mockArray);

        mockMvc.perform(get("/user/com/getSchema/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].data").value("test"));
    }

    // 测试：提交作品资料表单
    @Test
    void uploadComSchema_Success() throws Exception {
        // 创建测试数据
        LinkedList<WorkSchemaDTO> requestBody = new LinkedList<>();
        WorkSchemaDTO dto1 = new WorkSchemaDTO();
        dto1.setInput("input1");
        dto1.setContent("content1");
        dto1.setIsFile(false);
        requestBody.add(dto1);

        WorkSchemaDTO dto2 = new WorkSchemaDTO();
        dto2.setInput("input2");
        dto2.setContent("file_key");
        dto2.setIsFile(true);
        requestBody.add(dto2);

        // 配置mock行为
        doNothing().when(userService).uploadComSchema(any(), anyLong(), any(LinkedList.class));

        // 执行测试
        mockMvc.perform(
                        post("/user/com/uploadSchema/123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSONObject.toJSONString(requestBody)))
                .andExpect(status().isOk());

        // 验证mock方法被正确调用
        verify(userService, times(1)).uploadComSchema(any(), eq(123L), any(LinkedList.class));
    }

    // 测试：获取上传凭证
    @Test
    void getUploadCertificate_Success() throws Exception {
        Map<String, String> mockCert =
                Collections.singletonMap("url", "https://upload.example.com");
        when(userService.getUploadCertificate(any(), anyLong(), anyString(), anyString()))
                .thenReturn(mockCert);

        mockMvc.perform(
                        get("/user/com/uploadCertificate")
                                .param("comId", "123")
                                .param("input", "fileInput")
                                .param("filename", "test.jpg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://upload.example.com"));
    }
}
