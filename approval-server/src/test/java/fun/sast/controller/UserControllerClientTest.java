package fun.sast.controller;

import com.alibaba.fastjson2.JSON;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class UserControllerClientTest {

    /** 测试报名比赛接口的HttpClient调用示例 注意：此测试需要在服务运行时执行，且需要有效的Token */
    @Test
    void testSignUpComWithHttpClient() throws IOException, InterruptedException {
        // 设置API URL，根据实际部署情况修改
        String url = "http://localhost:9090/user/com/signUp";

        // 准备报名数据
        String jsonData =
                "{\"comId\":1,\"teamName\":\"测试团队\",\"members\":[{\"code\":\"654321\",\"name\":\"团队成员1\"}]}";

        // 创建HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // 创建HttpRequest，包含请求头和请求体
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        // 注意：实际调用时需要添加有效的Token
                        // .header("Token", "your-valid-token-here")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                        .build();

        // 发送请求并获取响应
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 打印响应状态码和响应体
        System.out.println("Response status code: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        // 实际测试中可以添加断言来验证响应
        // assertEquals(200, response.statusCode());
    }

    /** 测试报名比赛接口的HttpClient调用示例 - 使用复杂数据结构 包含团队成员和指导老师的详细信息 */
    @Test
    void testSignUpComWithComplexData() throws IOException, InterruptedException {
        // 设置API URL
        String url = "http://localhost:9090/user/com/signUp";

        // 准备复杂的报名数据
        String jsonData = generateComplexSignUpData();

        // 创建HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // 创建HttpRequest
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        // 注意：实际调用时需要添加有效的Token
                        // .header("Token", "your-valid-token-here")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                        .build();

        // 发送请求并获取响应
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 打印响应状态码和响应体
        System.out.println("Complex data test - Response status code: " + response.statusCode());
        System.out.println("Complex data test - Response body: " + response.body());
    }

    /**
     * 生成复杂的报名数据JSON字符串 包含团队成员和指导老师的详细信息
     *
     * @return JSON格式的字符串
     */
    private String generateComplexSignUpData() {
        // 创建主数据对象
        Map<String, Object> data = new HashMap<>();
        data.put("comId", 1);
        data.put("teamName", "示例团队");

        // 创建团队成员列表
        List<Map<String, Object>> teamMembers = new ArrayList<>();

        // 添加第一个团队成员
        Map<String, Object> member1 = new HashMap<>();
        member1.put("name", "张三");
        member1.put("code", "20230001");
        member1.put("college", "计算机学院");
        member1.put("major", "计算机科学与技术");
        member1.put("contact", "13800138000");
        teamMembers.add(member1);

        // 添加第二个团队成员
        Map<String, Object> member2 = new HashMap<>();
        member2.put("name", "李四");
        member2.put("code", "20230002");
        member2.put("college", "计算机学院");
        member2.put("major", "软件工程");
        member2.put("contact", "13900139000");
        teamMembers.add(member2);

        // 添加团队成员到主数据
        data.put("teamMember", teamMembers);

        // 创建指导老师列表
        List<Map<String, Object>> teachers = new ArrayList<>();

        // 添加第一个指导老师
        Map<String, Object> teacher1 = new HashMap<>();
        teacher1.put("name", "王老师");
        teacher1.put("code", "T001");
        teachers.add(teacher1);

        // 添加第二个指导老师
        Map<String, Object> teacher2 = new HashMap<>();
        teacher2.put("name", "李老师");
        teacher2.put("code", "T002");
        teachers.add(teacher2);

        // 添加指导老师到主数据
        data.put("teacherMember", teachers);

        // 转换为JSON字符串
        return JSON.toJSONString(data);
    }
}
