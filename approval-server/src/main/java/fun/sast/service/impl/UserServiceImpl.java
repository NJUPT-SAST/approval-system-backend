package fun.sast.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import fun.sast.Exception.BaseException;
import fun.sast.constant.RedisKeyConstant;
import fun.sast.dto.UserLoginDTO;
import fun.sast.dto.WorkSchemaDTO;
import fun.sast.entity.*;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.*;
import fun.sast.service.CompetitionService;
import fun.sast.service.FileService;
import fun.sast.service.ReviewService;
import fun.sast.service.UserService;
import fun.sast.utils.FileUtil;
import fun.sast.utils.JwtUtil;
import fun.sast.utils.RedisUtil;
import fun.sast.vo.UserLoginVO;
import fun.sast.vo.UserProfileVO;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final JwtUtil jwtUtil;
    private final FileUtil fileUtil;
    private final RedisUtil redisUtil;
    private final ReviewService reviewService;
    private final CompetitionService competitionService;
    private final WorkMapper workMapper;
    private final UserMapper userMapper;
    private final FileService fileService;
    private final DepartmentMapper departmentMapper;
    private final CompetitionMapper competitionMapper;
    private final TeamMapper teamMapper;

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
     * @param id 比赛id
     * @param input 输入框名
     * @param filename 文件名
     * @return 上传凭证
     */
    @Override
    public Map<String, String> getUploadCertificate(
            User user, Long id, String input, String filename) {
        Team team =
                teamMapper.selectOne(
                        new LambdaQueryWrapper<Team>()
                                .eq(Team::getCaptain, user.getCode())
                                .eq(Team::getComId, id));
        if (team == null) {
            throw new BaseException(ErrorEnum.UNKNOWN_TEAM_ID);
        }

        // 检查redis缓存
        String key = RedisKeyConstant.getWorkFileCacheKey(user.getCode(), input);
        if (redisUtil.hasKey(key)) {
            FileUploadCache cache =
                    JSON.parseObject((String) redisUtil.get(key), FileUploadCache.class);
            fileUtil.deleteFileOSS(cache.getUrl(), FileUtil.PRIVATE_FOLDER);
            redisUtil.delete(key);
        }
        Map<String, String> urlMap =
                fileUtil.getUploadCertificate(filename, id, team.getId(), input);
        FileUploadCache uploadFile = new FileUploadCache();
        uploadFile.setComId(id);
        uploadFile.setUserCode(user.getCode());
        uploadFile.setInput(input);
        uploadFile.setUrl(urlMap.get("clearUrl"));
        uploadFile.setDate(LocalDateTime.now());
        redisUtil.set(key, JSON.toJSONString(uploadFile));
        return urlMap;
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

    /**
     * 上传比赛表单
     *
     * @param user 用户
     * @param comId 比赛id
     * @param workSchemaDTOLinkedList 表单内容
     */
    @Transactional
    @Override
    public void uploadComSchema(
            User user, Long comId, LinkedList<WorkSchemaDTO> workSchemaDTOLinkedList) {

        competitionService.validateSubmissionPeriod(comId);

        Work work = processWorkSchema(user, comId, workSchemaDTOLinkedList);

        Work workDB =
                workMapper.selectOne(
                        new LambdaQueryWrapper<Work>()
                                .eq(Work::getComId, comId)
                                .eq(Work::getUserCode, user.getCode()));

        if (workDB != null) {
            workDB.setWorkName(work.getWorkName());
            workDB.setSchemaContent(work.getSchemaContent());
            workMapper.updateById(workDB);
        } else {
            workMapper.insert(work);
        }

        reviewService.updateReviewStatus(comId, user.getCode());
    }

    /**
     * 处理作品表单
     *
     * @param user 用户
     * @param comId 比赛id
     * @param workSchemaDTOLinkedList 表单内容
     * @return 处理后的作品表单
     */
    private Work processWorkSchema(
            User user, Long comId, List<WorkSchemaDTO> workSchemaDTOLinkedList) {
        Work work = new Work();

        work.setUserCode(user.getCode());
        work.setComId(comId);

        List<WorkSchemaDTO> workSchemas = new LinkedList<>();
        for (WorkSchemaDTO workSchemaDTO : workSchemaDTOLinkedList) {
            String title = workSchemaDTO.getInput();
            String content = workSchemaDTO.getContent();

            // 获取作品名称，这里很不优雅 todo
            if (title.equals("作品名称") || title.equals("作品名") || title.equals("项目名称"))
                work.setWorkName(content);

            WorkSchemaDTO workSchema = new WorkSchemaDTO();
            workSchema.setInput(title);
            workSchema.setContent(content);
            workSchema.setIsFile(false);
            // 单独处理文件
            if (fileUtil.isOSSBucketURL(content)) {
                fileService.processSubmissionFiles(user, comId, content, title);
                workSchema.setIsFile(true);
            }
            workSchemas.add(workSchema);
        }
        work.setSchemaContent(JSON.toJSONString(workSchemas));
        return work;
    }
}
