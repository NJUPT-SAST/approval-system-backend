package fun.sast.controller;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import fun.sast.annotation.OperateLog;
import fun.sast.annotation.ResponseResult;
import fun.sast.entity.User;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.service.UserService;
import fun.sast.vo.CompetitionBriefVO;
import fun.sast.vo.CompetitionDetailVO;
import fun.sast.vo.CompetitionSignUpInfoVO;
import fun.sast.vo.PageResultVO;
import fun.sast.vo.SearchCompetitionResultVO;
import fun.sast.vo.TeamInfoVO;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
    @ResponseResult
    @OperateLog("获取所有比赛列表")
    @GetMapping("/com/list")
    public PageResultVO<CompetitionBriefVO> getAllComList(
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
    @ResponseResult
    @OperateLog("获取已报名比赛列表")
    @GetMapping("/com/signList")
    public PageResultVO<CompetitionBriefVO> getSignedComList(
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
    @ResponseResult
    @OperateLog("获取比赛详情")
    @GetMapping("/com/info/{comId}")
    public CompetitionDetailVO getComInfo(@PathVariable Long comId) {
        return userService.getComInfo(comId);
    }

    /**
     * 获取用户在指定比赛中的报名信息
     *
     * @param comId 比赛ID
     * @return 报名信息
     */
    @ResponseResult
    @OperateLog("获取用户在指定比赛中的报名信息")
    @GetMapping("/com/signup/info/{comId}")
    public CompetitionSignUpInfoVO getUserComInfo(@PathVariable Long comId) {
        User user = UserInterceptor.userHolder.get();
        return userService.getComSignUpInfo(user, comId);
    }

    /**
     * 获取比赛团队信息
     *
     * @param comId 比赛ID
     * @return 团队成员List
     */
    @ResponseResult
    @OperateLog("获取比赛团队信息")
    @GetMapping("/com/teamInfo/{comId}")
    public TeamInfoVO getTeamInfo(@PathVariable Long comId) {
        User user = UserInterceptor.userHolder.get();
        return userService.getTeamInfo(user, comId);
    }

    /**
     * 获取比赛报名信息
     *
     * @param comId 比赛ID
     * @return 报名信息
     */
    @ResponseResult
    @OperateLog("获取比赛报名信息")
    @GetMapping("/com/signInfo/{comId}")
    public CompetitionSignUpInfoVO getComSignUpInfo(@PathVariable Long comId) {
        User user = UserInterceptor.userHolder.get();
        return userService.getComSignUpInfo(user, comId);
    }

    /**
     * 根据关键词搜索比赛
     *
     * @param key 关键词
     * @return 比赛列表
     */
    @ResponseResult
    @OperateLog("查找比赛")
    @GetMapping("/com/search")
    public SearchCompetitionResultVO searchCom(
            @RequestParam(defaultValue = "") String key,
            @RequestParam(defaultValue = "1") Integer cur,
            @RequestParam(defaultValue = "10") Integer limit) {
        return userService.searchComName(key, cur, limit);
    }

    /**
     * 报名比赛
     *
     * @param jsonData 报名信息（包含comId）
     */
    @ResponseResult
    @OperateLog("报名比赛")
    @PostMapping("/com/signUp")
    public void signUpCom(@RequestBody(required = false) String jsonData) {
        User user = UserInterceptor.userHolder.get();
        // 从请求体中解析比赛ID并设置到ThreadLocal中
        if (jsonData != null && !jsonData.trim().isEmpty()) {
            try {
                JSONObject data = JSONObject.parseObject(jsonData);
                Long comId = data.getLong("comId");
                if (comId != null && comId > 0) {
                    UserInterceptor.competitionIdHolder.set(comId);
                }
            } catch (JSONException e) {
                log.error("解析报名数据失败", e);
            }
        }
        userService.signUpCom(user, jsonData);
    }

    /**
     * 修改比赛报名信息
     *
     * @param jsonData 修改后的报名信息
     */
    @ResponseResult
    @OperateLog("修改比赛报名信息")
    @PostMapping("/com/updateSignUpInfo")
    public void updateComSignUpInfo(@RequestBody String jsonData) {
        User user = UserInterceptor.userHolder.get();
        userService.updateComSignUpInfo(user, jsonData);
    }
}
