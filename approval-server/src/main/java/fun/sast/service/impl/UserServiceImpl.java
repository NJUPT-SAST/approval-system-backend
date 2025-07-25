package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import fun.sast.Exception.BaseException;
import fun.sast.dto.RegisterUserDTO;
import fun.sast.dto.UserLoginDTO;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.UserMapper;
import fun.sast.service.UserService;
import fun.sast.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired JwtUtil jwtUtil;
    @Autowired private UserMapper userMapper;

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

    @Override
    public void register(RegisterUserDTO dto) {
        // 创建User对象
        User user = new User();

        // 加密密码
        String password = dto.getPassword();
        String salt = user.getSalt();
        String md5Password = DigestUtils.md5DigestAsHex((password + salt).getBytes());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", dto.getCode());
        User existingPerson = userMapper.selectOne(queryWrapper);
        user.setCode(dto.getCode());
        user.setPassword(md5Password);
        user.setSalt(salt);
        user.setCreateUser(null);
        user.setDepId(null);
        if (existingPerson != null) {
            throw new BaseException(ErrorEnum.USER_EXIST);
        } else {
            userMapper.insert(user);
        }
    }

    @Override
    public String login(UserLoginDTO dto) {
        // 查询用户
        User user = userMapper.selectOne(Wrappers.<User>query().eq("code", dto.getCode()));
        if (user == null) {
            throw new BaseException(ErrorEnum.USER_NOT_EXIST);
        }
        user.setCode(dto.getCode());
        // 计算 MD5 加密后的密码
        String md5Password =
                DigestUtils.md5DigestAsHex((dto.getPassword() + user.getSalt()).getBytes());
        // 验证密码
        if (!md5Password.equals(user.getPassword())) {
            return null;
            // 生成并返回 JWT Token
        }
        String jwt = jwtUtil.createJwt(user.getCode());
        System.out.println(jwtUtil.resolveJwt(jwt));
        return jwtUtil.createJwt(user.getCode());
    }
}
