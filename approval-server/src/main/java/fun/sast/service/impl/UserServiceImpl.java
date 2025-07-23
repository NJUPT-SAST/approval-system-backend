package fun.sast.service.impl;

import fun.sast.entity.Department;
import fun.sast.entity.User;
import fun.sast.mapper.DepartmentMapper;
import fun.sast.mapper.UserMapper;
import fun.sast.service.UserService;
import fun.sast.vo.UserProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired private UserMapper userMapper;

    @Autowired private DepartmentMapper departmentMapper;

    /**
     * 验证用户信息
     *
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

    /**
     * 获取用户信息
     *
     * @param user 用户
     * @return 用户信息
     */
    @Override
    public UserProfileVO getUserProfile(User user) {
        UserProfileVO userProfileVO =
                UserProfileVO.builder()
                        .name(user.getName())
                        .major(user.getMajor())
                        .contact(user.getContact())
                        .code(user.getCode())
                        .build();
        Department department = departmentMapper.selectById(user.getDepId());
        if (department != null) {
            userProfileVO.setDepartmentName(department.getName());
        }
        return userProfileVO;
    }
}
