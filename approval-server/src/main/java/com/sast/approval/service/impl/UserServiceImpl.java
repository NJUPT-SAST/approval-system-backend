package com.sast.approval.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sast.approval.entity.User;
import com.sast.approval.mapper.UserMapper;
import com.sast.approval.service.UserService;
import com.sast.approval.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    /**
     * 登录验证
     *
     * @param code
     * @param password
     * @return
     */
    @Override
    public User authenticate(String code, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        return userMapper.selectOne(queryWrapper);
        }
    }
}
