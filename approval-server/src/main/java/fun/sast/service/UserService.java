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

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录信息
     * @param captcha 验证码ID
     */
    UserLoginVO login(UserLoginDTO userLoginDTO, String captcha);
}
