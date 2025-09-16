package fun.sast.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.sast.Exception.BaseException;
import fun.sast.dto.UserLoginDTO;
import fun.sast.entity.Competition;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.mapper.UserMapper;
import fun.sast.service.UserService;
import fun.sast.utils.JwtUtil;
import fun.sast.utils.RedisUtil;
import fun.sast.vo.UserLoginVO;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            throw new BaseException(ErrorEnum.LOGIN_ERROR);
        }
        log.info("用户验证成功: 学号={}", code);
        return user;
    }

    // 分页查询所有已审批的比赛列表
    @Override
    public Map<String, Object> getAllComList(Integer cur, Integer limit) {
        try {
            if (cur == null || cur < 1) {
                cur = 1;
            }
            if (limit == null || limit < 1 || limit > 100) {
                limit = 10;
            }
            IPage<Competition> page = new Page<>(cur, limit);
            QueryWrapper<Competition> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_review", Competition.REVIEWED);
            queryWrapper.orderByDesc("id");
            IPage<Competition> competitionPage = competitionMapper.selectPage(page, queryWrapper);

            Map<String, Object> result = new HashMap<>();
            result.put("total", competitionPage.getTotal());
            result.put("list", competitionPage.getRecords());
            return result;
        } catch (Exception e) {
            log.error("分页查询所有已审批的比赛列表失败", e);
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }
    }

    // 分页查询用户已报名的比赛列表
    @Override
    public Map<String, Object> getSignedComList(User user, Integer cur, Integer limit) {
        // 参数校验
        if (user == null) {
            throw new BaseException(ErrorEnum.USER_NOT_EXIST);
        }
        if (cur == null || cur < 1) {
            cur = 1;
        }
        if (limit == null || limit < 1 || limit > 100) {
            limit = 10;
        }

        // 实际应用中，这里应该查询用户已报名的比赛
        // 假设我们有一个关联表user_competition记录用户报名信息
        IPage<Competition> page = new Page<>(cur, limit);

        // 假设存在user_competition表，关联userId和competitionId
        QueryWrapper<Competition> queryWrapper = new QueryWrapper<>();
        // 使用参数化查询避免SQL注入
        queryWrapper
                .lambda()
                .inSql(
                        Competition::getId,
                        String.format(
                                "SELECT com_id FROM user_competition WHERE user_id = %d",
                                user.getId()));
        queryWrapper.orderByDesc("id");

        try {
            IPage<Competition> competitionPage = competitionMapper.selectPage(page, queryWrapper);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("total", competitionPage.getTotal());
            result.put("list", competitionPage.getRecords());
            return result;
        } catch (BaseException e) {
            // 处理已定义的业务异常
            log.error("查询用户报名比赛列表失败: userId={}, 错误信息: {}", user.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            // 处理未定义的系统异常
            log.error("查询用户报名比赛列表失败: userId={}", user.getId(), e);
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }
    }

    // 查询比赛详情
    @Override
    public Map<String, Object> getComInfo(Long comId) {
        Competition competition = competitionMapper.selectById(comId);
        if (competition == null) {
            throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", competition);
        return result;
    }

    // 查询比赛报名信息
    @Override
    public Map<String, Object> getComSignUpInfo(Long comId) {
        Competition competition = competitionMapper.selectById(comId);
        if (competition == null) {
            throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("maxTeamMembers", competition.getMaxTeamMembers());
        result.put("minTeamMembers", competition.getMinTeamMembers());
        result.put("regBeginTime", competition.getRegBeginTime());
        result.put("regEndTime", competition.getRegEndTime());
        return result;
    }

    // 搜索比赛名称

    @Override
    public Map<String, Object> searchComName(String key, Integer cur, Integer limit) {
        try {
            // 参数校验
            if (cur == null || cur < 1) {
                cur = 1;
            }
            if (limit == null || limit < 1 || limit > 100) {
                limit = 10;
            }
            if (key == null) {
                key = "";
            }

            log.info("搜索比赛: 关键词={}, 当前页码={}, 每页条数={}", key, cur, limit);

            // 构建分页对象
            IPage<Competition> page = new Page<>(cur, limit);
            
            // 构建查询条件
            QueryWrapper<Competition> queryWrapper = new QueryWrapper<>();
            // 只查询已审批的比赛
            queryWrapper.eq("is_review", Competition.REVIEWED);
            
            // 如果有搜索关键词，则进行模糊搜索
            if (!key.isEmpty()) {
                final String searchKey = key; // 创建final副本用于lambda表达式
                queryWrapper.and(wrapper -> wrapper
                        .like("name", searchKey)        // 按比赛名称搜索
                        .or().like("introduce", searchKey)  // 按比赛介绍搜索
                );
            }
            
            // 按创建时间倒序排序，确保最新的比赛在前
            queryWrapper.orderByDesc("create_time");
            
            // 执行分页查询
            IPage<Competition> competitionPage = competitionMapper.selectPage(page, queryWrapper);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("total", competitionPage.getTotal());
            result.put("pageNum", cur);
            result.put("pageSize", limit);
            result.put("pages", competitionPage.getPages());
            result.put("list", competitionPage.getRecords());
            
            log.info("搜索比赛结果: 总条数={}, 总页数={}", competitionPage.getTotal(), competitionPage.getPages());
            return result;
        } catch (BaseException e) {
            // 处理已定义的业务异常
            log.error("搜索比赛失败: 关键词={}, 错误信息={}", key, e.getMessage());
            throw e;
        } catch (Exception e) {
            // 处理未定义的系统异常
            log.error("搜索比赛失败: 关键词={}", key, e);
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }
    }

    // 查询用户在指定比赛中的团队信息
    @Override
    public Map<String, Object> getTeamInfo(User user, Long comId) {
        // 实际应用中，这里应该查询用户在指定比赛中的团队信息
        // 此处改为抛出异常，而不是返回包含错误信息的Map
        throw new BaseException(ErrorEnum.NO_RESULT);
    }

    // 查询比赛报名表单模板
    @Override
    public JSONObject getComSchemaTemplate(Long comId) {
        if (comId == null || comId <= 0) {
            throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
        }
        // 检查比赛是否存在
        Competition competition = competitionMapper.selectById(comId);
        if (competition == null) {
            throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
        }
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
        if (user == null) {
            throw new BaseException(ErrorEnum.NO_LOGIN);
        }
        if (comId == null || comId <= 0) {
            throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
        }
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
            throw new BaseException(ErrorEnum.NO_LOGIN);
        }
        if (comId == null || comId <= 0) {
            throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
        }
        if (jsonData == null || jsonData.trim().isEmpty()) {
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }

        // 业务逻辑校验 - 检查比赛是否存在
        Competition competition = competitionMapper.selectById(comId);
        if (competition == null) {
            throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
        }
        // 可以添加更多业务校验，例如检查比赛是否已截止报名等

        try {
            // 校验JSON格式
            JSONObject data = JSONObject.parseObject(jsonData);
            if (data.isEmpty()) {
                throw new BaseException(ErrorEnum.COMMON_ERROR);
            }

            // 可以添加表单字段校验
            // 例如检查必填字段是否存在
            if (!data.containsKey("teamName")) {
                throw new BaseException(ErrorEnum.COMMON_ERROR);
            }
            // 保存数据到数据库...
            log.info("用户 {} 上传比赛 {} 的表单数据成功", user.getId(), comId);
        } catch (JSONException e) {
            log.error("上传表单数据失败：JSON格式无效", e);
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        } catch (Exception e) {
            log.error("上传表单数据失败", e);
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }
    }

    // 报名比赛
    @Override
    public void signUpCom(User user, String jsonData) {
        // 实际应用中，这里应该处理比赛报名逻辑
        try {
            if (user == null) {
                throw new BaseException(ErrorEnum.NO_LOGIN);
            }
            if (jsonData == null || jsonData.trim().isEmpty()) {
                throw new BaseException(ErrorEnum.COMMON_ERROR);
            }
            JSONObject data = JSONObject.parseObject(jsonData);
            Long comId = data.getLong("comId");
            if (comId == null || comId <= 0) {
                throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
            }
            // 检查比赛是否存在
            Competition competition = competitionMapper.selectById(comId);
            if (competition == null) {
                throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
            }
            // 执行报名逻辑
            log.info("用户 {} 报名比赛 {} 成功", user.getId(), comId);
        } catch (JSONException e) {
            log.error("报名比赛失败:JSON格式无效", e);
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("报名比赛失败", e);
            throw new BaseException(ErrorEnum.COMMON_ERROR);
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
