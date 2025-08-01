package fun.sast.service;

import fun.sast.dto.UserLoginDTO;
import fun.sast.entity.User;
import fun.sast.vo.UserLoginVO;

public interface UserService {
    /**
     * 验证用户
     *
     * @param code 学号
     * @param password 密码
     */
    User authenticate(String code, String password);

    // 注册
    // void register(UserRegisterDTO userRegisterDTO);

    // 登录
    UserLoginVO login(UserLoginDTO userLoginDTO, String captcha);
}
