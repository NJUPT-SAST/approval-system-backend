package fun.sast.controller.userController;

import fun.sast.annotation.ResponseResult;
import fun.sast.entity.User;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.service.UserService;
import fun.sast.vo.UserProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired private UserService userService;

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/profile")
    @ResponseResult
    public UserProfileVO getUserProfile() {
        User user = UserInterceptor.userHolder.get();
        return userService.getUserProfile(user);
    }
}
