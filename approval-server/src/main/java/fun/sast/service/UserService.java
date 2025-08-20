package fun.sast.service;

import fun.sast.dto.UserLoginDTO;
import fun.sast.vo.UserLoginVO;

public interface UserService {

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录信息
     * @param captcha 验证码ID
     */
    UserLoginVO login(UserLoginDTO userLoginDTO, String captcha);
}
