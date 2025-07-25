package fun.sast.service;

import fun.sast.dto.RegisterUserDTO;
import fun.sast.dto.UserLoginDTO;
import fun.sast.entity.User;

public interface UserService {
    /**
     * 验证用户
     *
     * @param code 学号
     * @param password 密码
     */
    User authenticate(String code, String password);

    // 注册
    void register(RegisterUserDTO registerUserDTO);

    // 登录
    String login(UserLoginDTO loginUserDTO);
}
