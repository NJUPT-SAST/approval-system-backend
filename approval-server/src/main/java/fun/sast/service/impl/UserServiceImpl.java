package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import fun.sast.Exception.BaseException;
import fun.sast.dto.UserLoginDTO;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.UserMapper;
import fun.sast.service.UserService;
import fun.sast.utils.JwtUtil;
import fun.sast.utils.RedisUtil;
import fun.sast.vo.UserLoginVO;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final RedisUtil redisUtil;

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
     * @param userLoginDTO 验证码，账号，密码
     * @param captcha 存在header的验证码id
     * @return 包含token的角色类，token里为code
     */
    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO, String captcha) {

        String currentCode = (String) redisUtil.get(captcha);
        // 验证验证码
        // TODO:现在任何验证码都是对的，记得删除
        currentCode = userLoginDTO.getValidateCode();
        if (currentCode == null) {
            throw new BaseException(ErrorEnum.INVALID_CAPTCHA);
        } else if (!currentCode.equalsIgnoreCase(userLoginDTO.getValidateCode())) {
            throw new BaseException(ErrorEnum.INCORRECT_CAPTCHA);
        }
        redisUtil.delete(captcha);
        if ("".equals(userLoginDTO.getCode()) || "".equals(userLoginDTO.getValidateCode())) {
            throw new BaseException(ErrorEnum.USERNAME_OR_PASSWORD_EMPTY);
        }

        // 查询用户是否存在
        User user = userMapper.selectOne(Wrappers.<User>query().eq("code", userLoginDTO.getCode()));

        if (user == null) {
            throw new BaseException(ErrorEnum.USER_NOT_EXIST);
        }
        // 密码校验
        String md5Password =
                DigestUtils.md5DigestAsHex(
                        (userLoginDTO.getPassword() + user.getSalt())
                                .getBytes(StandardCharsets.UTF_8));

        if (!md5Password.equals(user.getPassword())) {
            throw new BaseException(ErrorEnum.LOGIN_ERROR);
        }
        // 生成token
        String token = jwtUtil.createJwt(user.getCode());
        // 构造返回结果
        UserLoginVO vo = new UserLoginVO();
        vo.setToken(token);
        vo.setName(user.getName());
        vo.setRole(user.getRole());
        vo.setDepId(user.getDepId());

        return vo;
    }
}
