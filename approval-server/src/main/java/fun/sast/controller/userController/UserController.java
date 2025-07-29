package fun.sast.controller.userController;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import fun.sast.Exception.BaseException;
import fun.sast.annotation.ResponseResult;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.service.UserService;
import fun.sast.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/profile")
    @ResponseResult
    public UserProfileVO getUserProfile() {
        User user = UserInterceptor.userHolder.get();
        if (user == null) {
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }
        return userService.getUserProfile(user);
    }

    /**
     * 获取需要提交的比赛表单
     *
     * @param comId 比赛id
     * @return 比赛表单
     */
    @GetMapping("/com/schema/{comId}")
    public JSONObject getComSchemaTemplate(@PathVariable Long comId) {
        return userService.getComSchemaTemplate(comId);
    }

    /**
     * 获取已提交的比赛表单
     *
     * @param comId 比赛id, 用户
     * @return 提交表单
     */
    @GetMapping("/com/schema/{comId}")
    public JSONArray getSubmittedComSchemaTemplate(@PathVariable Long comId) {
        User user = UserInterceptor.userHolder.get();
        return userService.getSubmittedComSchemaTemplate(user, comId);
    }
}
