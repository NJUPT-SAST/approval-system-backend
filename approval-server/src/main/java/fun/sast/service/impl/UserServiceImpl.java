package fun.sast.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.sast.controller.publicController.UserResponse;
import fun.sast.entity.Competition;
import fun.sast.entity.User;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.mapper.UserMapper;
import fun.sast.service.UserService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import fun.sast.Exception.BaseException;
import fun.sast.dto.UserLoginDTO;
import fun.sast.enums.ErrorEnum;
import fun.sast.utils.JwtUtil;
import fun.sast.utils.RedisUtil;
import fun.sast.vo.UserLoginVO;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final RedisUtil redisUtil;
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
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code).eq("password", password);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.warn("用户验证失败: 学号={}", code);
            return null;
        }
        log.info("用户验证成功: 学号={}", code);
        return user;
    }

    

    // 分页查询所有已审批的比赛列表
    @Override
    public Map<String, Object> getAllComList(Integer cur, Integer limit) {
        IPage<Competition> page = new Page<>(cur, limit);
        QueryWrapper<Competition> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_review", Competition.REVIEWED);
        queryWrapper.orderByDesc("id");
        IPage<Competition> competitionPage = competitionMapper.selectPage(page, queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("total", competitionPage.getTotal());
        result.put("list", competitionPage.getRecords());
        return result;
    }

    // 分页查询用户已报名的比赛列表
    @Override
    public Map<String, Object> getSignedComList(User user, Integer cur, Integer limit) {
        // 实际应用中，这里应该查询用户已报名的比赛
        // 假设我们有一个关联表user_competition记录用户报名信息
        // 这里简单返回空列表作为示例
        Map<String, Object> result = new HashMap<>();
        result.put("total", 0);
        result.put("list", Collections.emptyList());
        return result;
    }

    // 查询比赛详情
    @Override
    public Map<String, Object> getComInfo(Long comId) {
        Competition competition = competitionMapper.selectById(comId);
        Map<String, Object> result = new HashMap<>();
        if (competition != null) {
            result.put("success", true);
            result.put("data", competition);
        } else {
            result.put("success", false);
            result.put("message", "比赛不存在");
        }
        return result;
    }

    // 查询比赛报名信息
    @Override
    public Map<String, Object> getComSignUpInfo(Long comId) {
        Competition competition = competitionMapper.selectById(comId);
        Map<String, Object> result = new HashMap<>();
        if (competition != null) {
            result.put("success", true);
            result.put("maxTeamMembers", competition.getMaxTeamMembers());
            result.put("minTeamMembers", competition.getMinTeamMembers());
            result.put("regBeginTime", competition.getRegBeginTime());
            result.put("regEndTime", competition.getRegEndTime());
        } else {
            result.put("success", false);
            result.put("message", "比赛不存在");
        }
        return result;
    }

    // 搜索比赛名称

    @Override
    public Map<String, Object> searchComName(String key, Integer cur, Integer limit) {
        IPage<Competition> page = new Page<>(cur, limit);
        QueryWrapper<Competition> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", key).eq("is_review", Competition.REVIEWED);
        IPage<Competition> competitionPage = competitionMapper.selectPage(page, queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("total", competitionPage.getTotal());
        result.put("list", competitionPage.getRecords());
        return result;
    }

    // 查询用户在指定比赛中的团队信息
    @Override
    public Map<String, Object> getTeamInfo(User user, Long comId) {
        // 实际应用中，这里应该查询用户在指定比赛中的团队信息
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "团队信息未找到");
        return result;
    }

    // 上传证书
    @Override
    public Map<String, String> getUploadCertificate(
            User user, Long comId, String input, String filename) {
        // 实际应用中，这里应该处理证书上传逻辑
        Map<String, String> result = new HashMap<>();
        result.put("success", "true");
        result.put("message", "证书上传成功");
        result.put("url", "/certificates/" + filename);
        return result;
    }

    // 查询用户个人信息
    @Override
    public UserResponse getUserProfile(User user) {
        UserResponse response = new UserResponse();
        if (user != null) {
            // 假设User实体类中有这些字段
            response.setName(user.getName());
            response.setCode(user.getCode());
            // 填充其他字段...
        }
        return response;
    }

    // 查询比赛报名表单模板
    @Override
    public JSONObject getComSchemaTemplate(Long comId) {
        JSONObject template = new JSONObject();
        JSONArray fields = new JSONArray();

        // 示例：添加基本字段
        JSONObject field1 = new JSONObject();
        field1.put("name", "teamName");
        field1.put("label", "团队名称");
        field1.put("type", "text");
        field1.put("required", true);
        fields.add(field1);

        JSONObject field2 = new JSONObject();
        field2.put("name", "teamMembers");
        field2.put("label", "团队成员");
        field2.put("type", "array");
        field2.put("required", true);
        fields.add(field2);

        template.put("fields", fields);
        template.put("comId", comId);
        return template;
    }

    // 查询用户已报名的比赛表单数据
    @Override
    public JSONArray getComSchema(User user, Long comId) {
        // 实际应用中，这里应该查询用户针对该比赛的报名表单数据
        JSONArray schema = new JSONArray();
        // 示例数据
        JSONObject data = new JSONObject();
        data.put("comId", comId);
        data.put("userId", user.getId());
        data.put("submitted", false);
        schema.add(data);
        return schema;
    }

    // 提交比赛报名表单
    @Override
    public void uploadComSchema(User user, Long comId, String jsonData) {
        // 参数校验
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }
        if (comId == null || comId <= 0) {
            throw new RuntimeException("比赛ID无效");
        }
        if (jsonData == null || jsonData.trim().isEmpty()) {
            throw new RuntimeException("表单数据不能为空");
        }

        // 业务逻辑校验 - 检查比赛是否存在
        Competition competition = competitionMapper.selectById(comId);
        if (competition == null) {
            throw new RuntimeException("比赛不存在");
        }
        // 可以添加更多业务校验，例如检查比赛是否已截止报名等

        try {
            // 校验JSON格式
            JSONObject data = JSONObject.parseObject(jsonData);
            if (data.isEmpty()) {
                throw new RuntimeException("表单数据不能为空");
            }

            // 可以添加表单字段校验
            // 例如检查必填字段是否存在
            if (!data.containsKey("teamName")) {
                throw new RuntimeException("团队名称不能为空");
            }
            // 保存数据到数据库...
            log.info("用户 {} 上传比赛 {} 的表单数据成功", user.getId(), comId);
        } catch (JSONException e) {
            log.error("上传表单数据失败：JSON格式无效", e);
            throw new RuntimeException("表单数据格式无效", e);
        } catch (Exception e) {
            log.error("上传表单数据失败", e);
            throw new RuntimeException("上传表单数据失败", e);
        }
    }

    // 报名比赛
    @Override
    public void signUpCom(User user, String jsonData) {
        // 实际应用中，这里应该处理比赛报名逻辑
        try {
            JSONObject data = JSONObject.parseObject(jsonData);
            Long comId = data.getLong("comId");
            // 执行报名逻辑...
            log.info("用户 {} 报名比赛 {} 成功", user.getId(), comId);
        } catch (Exception e) {
            log.error("报名比赛失败", e);
            throw new RuntimeException("报名比赛失败", e);
        }
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
