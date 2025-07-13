package com.sast.approval.service;

import com.sast.approval.entity.User;

public interface UserService {
    /**
     * 验证用户
     * @param code 学号
     * @param password 密码
     */
    User authenticate(String code, String password);
}
