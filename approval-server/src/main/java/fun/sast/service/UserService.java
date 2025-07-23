package fun.sast.service;

import fun.sast.entity.User;
import fun.sast.vo.UserProfileVO;

public interface UserService {
    /**
     * 验证用户
     *
     * @param code 学号
     * @param password 密码
     */
    User authenticate(String code, String password);

    /**
     * 获取用户信息
     *
     * @param user 用户
     */
    UserProfileVO getUserProfile(User user);
}
