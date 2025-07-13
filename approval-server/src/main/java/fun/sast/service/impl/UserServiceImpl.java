package fun.sast.service.impl;


import fun.sast.entity.User;
import fun.sast.mapper.UserMapper;
import fun.sast.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 验证用户信息
     * @param code 学号
     * @param password 密码
     * @return 用户信息
     */
    @Override
    public User authenticate(String code, String password) {
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("code", code).eq("password", password);
//        return userMapper.selectOne(queryWrapper);
        return new User();
        }

}
