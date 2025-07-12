package com.sast.approval.service;

import com.sast.approval.entity.User;

public interface UserService {
    /**
     * 验证用户
     *
     * @param code
     * @param password
     */
    User authenticate(String code, String password);
}
