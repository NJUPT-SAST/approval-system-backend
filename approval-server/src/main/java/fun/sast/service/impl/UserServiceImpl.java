package fun.sast.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fun.sast.Exception.BaseException;
import fun.sast.entity.Competition;
import fun.sast.entity.Department;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import fun.sast.dto.UserLoginDTO;
import fun.sast.entity.User;
import fun.sast.entity.Work;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.mapper.DepartmentMapper;
import fun.sast.mapper.UserMapper;
import fun.sast.mapper.WorkMapper;
import fun.sast.service.UserService;
import fun.sast.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import fun.sast.utils.JwtUtil;
import fun.sast.utils.RedisUtil;
import fun.sast.vo.UserLoginVO;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final WorkMapper workMapper;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final CompetitionMapper competitionMapper;

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

    /**
     * 获取需要提交的资料表单
     *
     * @param comId 比赛id
     * @return 资料表单
     */
    @Override
    public JSONObject getComSchemaTemplate(Long comId) {
        Competition competition = competitionMapper.selectById(comId);
        if (competition == null) {
            throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
        }
        JSONObject table = competition.getTable();
        if (table == null) {
            throw new BaseException(ErrorEnum.SCHEMA_ERROR);
        }
        return table;
    }

    /**
     * 获取已提交的比赛表单
     *
     * @param user 用户
     * @param comId 比赛id
     * @return 比赛表单
     */
    @Override
    public JSONArray getSubmittedComSchemaTemplate(User user, Long comId) {
        Work work =
                workMapper.selectOne(
                        new QueryWrapper<Work>()
                                .eq("com_id", comId)
                                .eq("user_code", user.getCode()));
        if (work == null) {
            throw new BaseException(ErrorEnum.HAVE_NOT_UPLOAD_WORK);
        }
        return JSONArray.parseArray(work.getSchemaContent());
    }

    /**
     * 获取上传凭证
     *
     * @param user 用户
     * @param id 文件id
     * @param input 文件输入
     * @param filename 文件名
     * @return 上传凭证
     */
    @Override
    public Map<String, String> getUploadCertificate(User user, Long id, String input, String filename) {
        return Map.of();
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * @param userLoginDTO 验证码，账号，密码
     * @param captcha 存在header的验证码id
     * @return 包含token的角色类，token里为code
     */
    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO, String captcha) {

        String currentCode = (String) redisUtil.get(captcha);

        if (currentCode == null) {
            throw new BaseException(ErrorEnum.CAPTCHA_NOT_EXIST);
        } else if (!currentCode.equalsIgnoreCase(userLoginDTO.getValidateCode())) {
            throw new BaseException(ErrorEnum.INCORRECT_CAPTCHA);
        }
        redisUtil.delete(captcha);

        // 空密码
        if (isBlank(userLoginDTO.getCode()) || isBlank(userLoginDTO.getPassword())) {
            throw new BaseException(ErrorEnum.USERNAME_OR_PASSWORD_EMPTY);
        }

        // 查询用户是否存在
        User user = userMapper.selectOne(Wrappers.<User>query().eq("code", userLoginDTO.getCode()));

        if (user == null) {
            throw new BaseException(ErrorEnum.LOGIN_ERROR);
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
