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
import fun.sast.entity.Team;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.mapper.TeamMapper;
import fun.sast.mapper.UserMapper;
import fun.sast.service.UserService;
import fun.sast.utils.JwtUtil;
import fun.sast.utils.RedisUtil;
import fun.sast.vo.UserLoginVO;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private final TeamMapper teamMapper;

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
            result.put("records", competitionPage.getRecords());
            result.put("pageNum", cur);
            result.put("pageSize", limit);
            return result;
        } catch (Exception e) {
            log.error("分页查询所有已审批的比赛列表失败", e);
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }
    }

    // 修改比赛报名信息
    @Override
    public void updateComSignUpInfo(User user, String jsonData) {
        try {
            if (user == null) {
                throw new BaseException(ErrorEnum.NO_LOGIN);
            }
            if (jsonData == null || jsonData.trim().isEmpty()) {
                throw new BaseException(ErrorEnum.JSON_DATA_EMPTY);
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

            // 检查报名时间是否已结束
            long currentTimeMillis = System.currentTimeMillis();
            try {
                long regEndTimeMillis = Long.parseLong(competition.getRegEndTime());
                if (currentTimeMillis > regEndTimeMillis) {
                    throw new BaseException(ErrorEnum.SIGN_UP_TIME_EXPIRED);
                }
            } catch (NumberFormatException e) {
                log.error("时间格式错误: {}", e.getMessage());
                throw new BaseException(ErrorEnum.DATE_FORMAT_ERROR);
            }

            // 检查用户是否已经报名该比赛
            Team team =
                    teamMapper.selectOne(
                            new QueryWrapper<Team>()
                                    .eq("com_id", comId)
                                    .eq("captain", user.getCode()));
            if (team == null) {
                throw new BaseException(ErrorEnum.HAVE_NOT_SIGNED_COM);
            }

            // 如果是团队赛，修改队伍名称和成员信息
            if (Competition.TEAM.equals(competition.getType())) {
                String teamName = data.getString("teamName");
                if (teamName != null && !teamName.trim().isEmpty()) {
                    team.setName(teamName);
                }

                // 处理成员信息
                JSONArray teamMember = data.getJSONArray("teamMember");
                if (teamMember != null) {
                    team.setMember(teamMember.toJSONString());

                    // 检查团队人数是否符合要求
                    int teamSize = teamMember.size() + 1; // 加1是因为队长也算团队成员
                    if (teamSize < competition.getMinTeamMembers()
                            || teamSize > competition.getMaxTeamMembers()) {
                        throw new BaseException(ErrorEnum.LIMIT_ERROR);
                    }
                }
            }

            // 处理指导老师信息
            JSONArray teacherMember = data.getJSONArray("teacherMember");
            if (teacherMember != null) {
                team.setTeacher(teacherMember.toJSONString());
            }

            // 更新更新用户信息
            team.setUpdateUser(Long.valueOf(user.getId()));

            // 更新队伍信息
            int result = teamMapper.updateById(team);
            if (result <= 0) {
                throw new BaseException(ErrorEnum.CONTEST_ERROR);
            }

            log.info("用户 {} 修改比赛 {} 报名信息成功", user.getId(), comId);
        } catch (JSONException e) {
            log.error("修改比赛报名信息失败:JSON格式无效", e);
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("修改比赛报名信息失败", e);
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

        IPage<Competition> page = new Page<>(cur, limit);
        QueryWrapper<Competition> queryWrapper = new QueryWrapper<>();

        try {
            // 查询用户已报名的比赛ID列表
            List<Long> signedComIds = teamMapper.selectSignedComIdsByUserId(user.getId());

            if (signedComIds != null && !signedComIds.isEmpty()) {
                queryWrapper.in("id", signedComIds);
            } else {
                // 如果用户没有报名任何比赛，返回空结果
                Map<String, Object> emptyResult = new HashMap<>();
                emptyResult.put("total", 0);
                emptyResult.put("records", new ArrayList<>());
                emptyResult.put("pageNum", cur);
                emptyResult.put("pageSize", limit);
                return emptyResult;
            }

            queryWrapper.orderByDesc("id");
            IPage<Competition> competitionPage = competitionMapper.selectPage(page, queryWrapper);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("total", competitionPage.getTotal());
            result.put("records", competitionPage.getRecords());
            result.put("pageNum", cur);
            result.put("pageSize", limit);
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
        try {
            // 1. 参数校验
            if (comId == null || comId <= 0) {
                throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
            }

            log.info("查询比赛详情: 比赛ID={}", comId);

            // 2. 查询比赛信息
            Competition competition = competitionMapper.selectById(comId);
            if (competition == null) {
                log.warn("查询比赛详情失败: 比赛不存在，比赛ID={}", comId);
                throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
            }

            // 3. 计算比赛状态（0未开始 1进行中 2已结束）
            Integer status = calculateCompetitionStatus(competition);

            // 4. 构建符合接口规范的data对象
            Map<String, Object> data = new HashMap<>();
            data.put("name", competition.getName());
            data.put("cover", competition.getCover());
            data.put("introduce", competition.getIntroduce());
            data.put("status", status);
            data.put("regBegin", competition.getRegBeginTime());
            data.put("regEnd", competition.getRegEndTime());
            data.put("submitBegin", competition.getSubmitBeginTime());
            data.put("submitEnd", competition.getSubmitEndTime());
            data.put("reviewBegin", competition.getReviewBeginTime());
            data.put("reviewEnd", competition.getReviewEndTime());

            log.info("查询比赛详情成功: 比赛ID={}, 比赛名称={}", comId, competition.getName());
            return data;
        } catch (BaseException e) {
            // 处理已定义的业务异常
            log.error("查询比赛详情失败: 比赛ID={}, 错误信息={}", comId, e.getMessage());
            throw e;
        } catch (Exception e) {
            // 处理未定义的系统异常
            log.error("查询比赛详情失败: 比赛ID={}", comId, e);
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }
    }

    /**
     * 计算比赛状态
     *
     * @param competition 比赛信息
     * @return 比赛状态（0未开始 1进行中 2已结束）
     */
    private Integer calculateCompetitionStatus(Competition competition) {
        try {
            Date now = new Date();
            // 检查评审截止时间
            if (competition.getReviewEndTime() != null) {
                Date reviewEnd = parseDate(competition.getReviewEndTime());
                if (reviewEnd != null && now.after(reviewEnd)) {
                    return 2; // 已结束
                }
            }
            // 检查提交截止时间
            if (competition.getSubmitEndTime() != null) {
                Date submitEnd = parseDate(competition.getSubmitEndTime());
                if (submitEnd != null && now.after(submitEnd)) {
                    return 1; // 进行中（评审阶段）
                }
            }
            // 检查报名截止时间
            if (competition.getRegEndTime() != null) {
                Date regEnd = parseDate(competition.getRegEndTime());
                if (regEnd != null && now.after(regEnd)) {
                    return 1; // 进行中（提交阶段）
                }
            }
            // 检查报名开始时间
            if (competition.getRegBeginTime() != null) {
                Date regBegin = parseDate(competition.getRegBeginTime());
                if (regBegin != null && now.after(regBegin)) {
                    return 1; // 进行中（报名阶段）
                }
            }
            // 以上条件都不满足，即为未开始
            return 0;
        } catch (Exception e) {
            log.error("计算比赛状态失败", e);
            return 0; // 默认返回未开始
        }
    }

    /**
     * 解析日期字符串
     *
     * @param dateStr 日期字符串
     * @return Date对象
     */
    private Date parseDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            log.error("解析日期失败: {}", dateStr, e);
            return null;
        }
    }

    // 查询用户在指定比赛中的报名信息
    @Override
    public Map<String, Object> getComSignUpInfo(User user, Long comId) {
        try {
            // 参数校验
            if (user == null) {
                throw new BaseException(ErrorEnum.NO_LOGIN);
            }
            if (comId == null || comId <= 0) {
                throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
            }

            // 检查比赛是否存在
            Competition competition = competitionMapper.selectById(comId);
            if (competition == null) {
                throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
            }

            // 查询用户在该比赛中的团队信息
            Team team =
                    teamMapper.selectOne(
                            new QueryWrapper<Team>()
                                    .eq("com_id", comId)
                                    .eq("captain", user.getCode()));

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("comId", comId);
            result.put("comName", competition.getName());
            result.put("type", competition.getType());
            result.put("isTeam", Competition.TEAM.equals(competition.getType()));
            result.put("isReviewed", competition.getIsReview());
            result.put("maxTeamMembers", competition.getMaxTeamMembers());
            result.put("minTeamMembers", competition.getMinTeamMembers());
            result.put("regBeginTime", competition.getRegBeginTime());
            result.put("regEndTime", competition.getRegEndTime());
            result.put("submitBeginTime", competition.getSubmitBeginTime());
            result.put("submitEndTime", competition.getSubmitEndTime());

            // 如果用户已报名该比赛，添加报名详情
            if (team != null) {
                result.put("hasSignedUp", true);
                result.put("teamId", team.getId());
                result.put("teamName", team.getName());
                result.put("captain", team.getCaptain());

                // 处理成员信息
                if (team.getMember() != null && !team.getMember().trim().isEmpty()) {
                    try {
                        JSONArray teamMember = JSONArray.parseArray(team.getMember());
                        result.put("teamMember", teamMember);
                    } catch (JSONException e) {
                        log.warn("解析团队成员信息失败", e);
                        result.put("teamMember", new JSONArray());
                    }
                }

                // 处理指导老师信息
                if (team.getTeacher() != null && !team.getTeacher().trim().isEmpty()) {
                    try {
                        JSONArray teacherMember = JSONArray.parseArray(team.getTeacher());
                        result.put("teacherMember", teacherMember);
                    } catch (JSONException e) {
                        log.warn("解析指导老师信息失败", e);
                        result.put("teacherMember", new JSONArray());
                    }
                }

                result.put("createTime", team.getCreateTime());
                result.put("updateTime", team.getUpdateTime());
            } else {
                result.put("hasSignedUp", false);
            }

            log.info("用户 {} 查询比赛 {} 的报名信息成功", user.getId(), comId);
            return result;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询比赛报名信息失败", e);
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }
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
                queryWrapper.and(
                        wrapper ->
                                wrapper.like("name", searchKey) // 按比赛名称搜索
                                        .or()
                                        .like("introduce", searchKey) // 按比赛介绍搜索
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
            result.put("records", competitionPage.getRecords());

            log.info(
                    "搜索比赛结果: 总条数={}, 总页数={}",
                    competitionPage.getTotal(),
                    competitionPage.getPages());
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
        try {
            // 参数校验
            if (user == null) {
                throw new BaseException(ErrorEnum.NO_LOGIN);
            }
            if (comId == null || comId <= 0) {
                throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
            }

            // 检查比赛是否存在
            Competition competition = competitionMapper.selectById(comId);
            if (competition == null) {
                throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
            }

            // 查询用户在该比赛中的团队信息
            Team team =
                    teamMapper.selectOne(
                            new QueryWrapper<Team>()
                                    .eq("com_id", comId)
                                    .eq("captain", user.getCode()));
            if (team == null) {
                throw new BaseException(ErrorEnum.HAVE_NOT_SIGNED_COM);
            }

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("teamId", team.getId());
            result.put("teamName", team.getName());
            result.put("captain", team.getCaptain());

            // 处理成员信息
            if (team.getMember() != null && !team.getMember().trim().isEmpty()) {
                try {
                    JSONArray teamMember = JSONArray.parseArray(team.getMember());
                    result.put("teamMember", teamMember);
                } catch (JSONException e) {
                    log.warn("解析团队成员信息失败", e);
                    result.put("teamMember", new JSONArray());
                }
            } else {
                result.put("teamMember", new JSONArray());
            }

            // 处理指导老师信息
            if (team.getTeacher() != null && !team.getTeacher().trim().isEmpty()) {
                try {
                    JSONArray teacherMember = JSONArray.parseArray(team.getTeacher());
                    result.put("teacherMember", teacherMember);
                } catch (JSONException e) {
                    log.warn("解析指导老师信息失败", e);
                    result.put("teacherMember", new JSONArray());
                }
            } else {
                result.put("teacherMember", new JSONArray());
            }

            log.info("用户 {} 查询比赛 {} 的团队信息成功", user.getId(), comId);
            return result;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询团队信息失败", e);
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }
    }

    // 报名比赛
    @Override
    public void signUpCom(User user, String jsonData) {
        try {
            if (user == null) {
                throw new BaseException(ErrorEnum.NO_LOGIN);
            }
            if (jsonData == null || jsonData.trim().isEmpty()) {
                throw new BaseException(ErrorEnum.JSON_DATA_EMPTY);
            }
            JSONObject data = JSONObject.parseObject(jsonData);
            // 从URL路径获取的comId会在UserController中传入service方法
            // 这里不需要再从jsonData中解析comId
            Long comId = UserInterceptor.competitionIdHolder.get();
            if (comId == null || comId <= 0) {
                throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
            }

            // 检查比赛是否存在
            Competition competition = competitionMapper.selectById(comId);
            if (competition == null) {
                throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
            }

            // 检查比赛是否已审批
            if (Competition.NOT_REVIEWED.equals(competition.getIsReview())) {
                throw new BaseException(ErrorEnum.CONTEST_NOT_REVIEWED);
            }

            // 检查报名时间是否在有效期内
            long currentTimeMillis = System.currentTimeMillis();
            // 将字符串时间按照yyyy-MM-dd HH:mm:ss格式解析
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date regBeginDate = dateFormat.parse(competition.getRegBeginTime());
                Date regEndDate = dateFormat.parse(competition.getRegEndTime());
                long regBeginTimeMillis = regBeginDate.getTime();
                long regEndTimeMillis = regEndDate.getTime();

                if (currentTimeMillis < regBeginTimeMillis) {
                    throw new BaseException(ErrorEnum.SIGN_UP_TIME_NOT_STARTED);
                }
                if (currentTimeMillis > regEndTimeMillis) {
                    throw new BaseException(ErrorEnum.SIGN_UP_TIME_EXPIRED);
                }
            } catch (ParseException e) {
                log.error("时间格式错误: {}", e.getMessage());
                throw new BaseException(ErrorEnum.DATE_FORMAT_ERROR);
            }

            // 检查用户是否已经报名该比赛
            Team existingTeam =
                    teamMapper.selectOne(
                            new QueryWrapper<Team>()
                                    .eq("com_id", comId)
                                    .eq("captain", user.getCode()));
            if (existingTeam != null) {
                throw new BaseException(ErrorEnum.ALREADY_SIGNED_UP_CONTEST);
            }

            // 创建队伍信息
            Team team = new Team();
            team.setComId(comId);
            team.setCaptain(user.getCode());

            // 如果是团队赛，设置队伍名称和成员信息
            if (Competition.TEAM.equals(competition.getType())) {
                String teamName = data.getString("teamName");
                if (teamName == null || teamName.trim().isEmpty()) {
                    throw new BaseException(ErrorEnum.TEAM_NAME_EMPTY);
                }
                team.setName(teamName);

                // 处理成员信息
                JSONArray teamMember = data.getJSONArray("teamMember");
                // 必须检查teamMember是否为空，团队赛需要成员信息
                if (teamMember == null || teamMember.isEmpty()) {
                    throw new BaseException(ErrorEnum.TEAM_MEMBERS_EMPTY);
                }
                team.setMember(teamMember.toJSONString());

                // 检查团队人数是否符合要求
                int teamSize = teamMember.size() + 1; // 加1是因为队长也算团队成员
                if (teamSize < competition.getMinTeamMembers()
                        || teamSize > competition.getMaxTeamMembers()) {
                    throw new BaseException(ErrorEnum.LIMIT_ERROR);
                }
            }

            // 处理指导老师信息
            JSONArray teacherMember = data.getJSONArray("teacherMember");
            if (teacherMember != null) {
                team.setTeacher(teacherMember.toJSONString());
            }

            // 设置创建和更新信息
            team.setCreateUser(Long.valueOf(user.getId()));
            team.setUpdateUser(Long.valueOf(user.getId()));

            // 保存队伍信息
            int result = teamMapper.insert(team);
            if (result <= 0) {
                throw new BaseException(ErrorEnum.TEAM_SAVE_FAILED);
            }

            log.info("用户 {} 报名比赛 {} 成功", user.getId(), comId);
        } catch (JSONException e) {
            log.error("报名比赛失败:JSON格式无效", e);
            throw new BaseException(ErrorEnum.JSON_FORMAT_ERROR);
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
