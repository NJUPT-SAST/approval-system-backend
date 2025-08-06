package fun.sast.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fun.sast.annotation.CheckRole;
import fun.sast.annotation.OperateLog;
import fun.sast.annotation.PassToken;
import fun.sast.controller.publicController.UserResponse;
import fun.sast.entity.User;
import fun.sast.enums.UserRoleEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.service.UserService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@CheckRole(UserRoleEnum.STUDENT)
public class UserController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取所有比赛列表（无需登录）
     *
     * @param cur 当前页数
     * @param limit 每页数据个数
     * @return 数据
     */
    @PassToken
    @OperateLog("获取所有比赛列表")
    @GetMapping("/com/list")
    public Map<String, Object> getAllComList(
            @RequestParam(defaultValue = "1") Integer cur,
            @RequestParam(defaultValue = "10") Integer limit) {
        return userService.getAllComList(cur, limit);
    }

    /**
     * 获取已报名比赛列表
     *
     * @param cur 当前页数
     * @param limit 每页数据个数
     * @return 数据
     */
    @OperateLog("获取已报名比赛列表")
    @GetMapping("/com/signList")
    public Map<String, Object> getSignedComList(
            @RequestParam(defaultValue = "1") Integer cur,
            @RequestParam(defaultValue = "10") Integer limit) {
        User user = UserInterceptor.userHolder.get();
        return userService.getSignedComList(user, cur, limit);
    }

    /**
     * 获取比赛详情（无需登录）
     *
     * @param comId 比赛ID
     * @return 数据
     */
    @PassToken
    @OperateLog("获取比赛详情")
    @GetMapping("/com/info/{comId}")
    public Map<String, Object> getComInfo(@PathVariable Long comId) {
        return userService.getComInfo(comId);
    }

    /**
     * 获取上传凭证
     *
     * @param id 比赛ID
     * @param input 输入框名称
     * @param filename 文件名
     */
    @OperateLog("获取上传凭证")
    @GetMapping("/com/uploadCertificate")
    public Map<String, String> getUploadCertificate(
            @RequestParam Long id, @RequestParam String input, @RequestParam String filename) {
        User user = UserInterceptor.userHolder.get();
        return userService.getUploadCertificate(user, id, input, filename);
    }

    /**
     * 获取当前用户信息
     *
     * @return 数据
     */
    @OperateLog("获取当前用户信息")
    @GetMapping("/profile")
    public UserResponse getUserProfile() {
        User user = UserInterceptor.userHolder.get();
        return userService.getUserProfile(user);
    }

    /**
     * 获取比赛团队信息
     *
     * @param comId 比赛ID
     * @return 团队成员List
     */
    @OperateLog("获取比赛团队信息")
    @GetMapping("/com/teamInfo/{comId}")
    public Map<String, Object> getTeamInfo(@PathVariable Long comId) {
        User user = UserInterceptor.userHolder.get();
        return userService.getTeamInfo(user, comId);
    }

    /**
     * 获取比赛报名信息
     *
     * @param comId 比赛ID
     * @return 数据
     */
    @OperateLog("获取比赛报名信息")
    @GetMapping("/com/signInfo/{comId}")
    public Map<String, Object> getComSignUpInfo(@PathVariable Long comId) {
        return userService.getComSignUpInfo(comId);
    }

    /**
     * 获取需要提交的资料表单
     *
     * @param comId 比赛ID
     * @return 表单Schema
     */
    @OperateLog("获取需要提交的资料表单")
    @GetMapping("/com/schema/{comId}")
    public JSONObject getComSchemaTemplate(@PathVariable Long comId) {
        return userService.getComSchemaTemplate(comId);
    }

    /**
     * 提交作品资料表单
     *
     * @param comId 比赛ID
     * @param jsonData 表单数据
     */
    @OperateLog("提交作品资料表单")
    @PostMapping("/com/uploadSchema/{comId}")
    public void uploadComSchema(@PathVariable Long comId, @RequestBody String jsonData) {
        User user = UserInterceptor.userHolder.get();
        userService.uploadComSchema(user, comId, jsonData);
    }

    /**
     * 获取已提交的资料表单
     *
     * @param comId 比赛ID
     * @return 表单数据
     */
    @OperateLog("获取已提交的资料表单")
    @GetMapping("/com/getSchema/{comId}")
    public JSONArray getComSchema(@PathVariable Long comId) {
        User user = UserInterceptor.userHolder.get();
        return userService.getComSchema(user, comId);
    }

    /**
     * 根据关键词搜索比赛
     *
     * @param key 关键词
     * @return 比赛列表
     */
    @PassToken
    @OperateLog("查找比赛")
    @GetMapping("/com/search")
    public Map<String, Object> searchCom(
            @RequestParam(defaultValue = "") String key,
            @RequestParam(defaultValue = "1") Integer cur,
            @RequestParam(defaultValue = "10") Integer limit) {
        return userService.searchComName(key, cur, limit);
    }
}
