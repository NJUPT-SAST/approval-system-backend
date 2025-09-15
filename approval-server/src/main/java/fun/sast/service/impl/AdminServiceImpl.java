package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.sast.Exception.BaseException;
import fun.sast.entity.*;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.*;
import fun.sast.service.AdminService;
import fun.sast.utils.FileUtil;
import fun.sast.vo.CompetitionDetailVO;
import fun.sast.vo.CompetitionManagerVO;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.baomidou.mybatisplus.extension.ddl.DdlScriptErrorHandler.PrintlnLogErrorHandler.log;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CompetitionMapper competitionMapper;
    private final WorkMapper workMapper;
    private final TeamMapper teamMapper;
    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final JudgeMapper judgeMapper;
    private final FileUtil fileUtil;

    /**
     * 创建比赛
     *
     * @param competition 比赛信息
     * @param cover 比赛封面
     */
    @Override
    public void createCompetition(Competition competition, MultipartFile cover) {
        // 比较时间设置是否正确
        validateCompetitionDates(competition);

        // 校验审批关系数据是否正确
        validateReviewSettings(competition.getReviewSettings());

        // 判断活动负责人是否存在
        if (!userIsExist(competition.getUserCode())) {
            throw new BaseException(ErrorEnum.USER_NOT_EXIST);
        }

        // 判断比赛团队人数限制是否正确
        if (competition.getMinTeamMembers() > competition.getMaxTeamMembers()) {
            throw new BaseException(ErrorEnum.LIMIT_ERROR);
        }

        // 判断比赛表单是否为空
        if (competition.getTable() == null) {
            throw new BaseException(ErrorEnum.SCHEMA_ERROR);
        }

        int result = competitionMapper.insert(competition);
        // 是否成功插入到数据库
        if (result <= 0) {
            throw new BaseException(ErrorEnum.CONTEST_ERROR);
        }
        if (cover != null && !cover.isEmpty()) {
            String url = writeUploadImage(cover, competition.getId());
            competition.setCover(url);
            competitionMapper.updateById(competition);
        }
    }

    /**
     * 修改比赛信息
     *
     * @param competition 比赛信息
     * @param cover 封面
     */
    @Override
    public void editCompetition(Competition competition, MultipartFile cover) {
        // 比较时间设置是否正确
        validateCompetitionDates(competition);

        // 检查比赛是否存在
        QueryWrapper<Competition> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", competition.getId());
        Competition temCompetition = competitionMapper.selectOne(queryWrapper);
        if (temCompetition == null) {
            throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
        }

        // 校验审批关系数据是否正确
        validateReviewSettings(competition.getReviewSettings());

        // 判断活动负责人是否存在
        if (!userIsExist(competition.getUserCode())) {
            throw new BaseException(ErrorEnum.USER_NOT_EXIST);
        }

        // 判断比赛团队人数限制是否正确
        if (competition.getMinTeamMembers() > competition.getMaxTeamMembers()) {
            throw new BaseException(ErrorEnum.LIMIT_ERROR);
        }

        // 判断比赛表单是否为空
        if (competition.getTable() == null) {
            throw new BaseException(ErrorEnum.SCHEMA_ERROR);
        }

        if (cover != null && !cover.isEmpty()) {
            String url = writeUploadImage(cover, competition.getId());
            competition.setCover(url);
        }

        int result = competitionMapper.updateById(competition);
        if (result <= 0) {
            throw new BaseException(ErrorEnum.CONTEST_ERROR);
        }
    }

    /**
     * 删除比赛
     *
     * @param id 比赛id
     */
    @Override
    public void deleteCompetition(Long id) {
        // 检查比赛是否存在
        Competition competition = competitionMapper.selectById(id);
        if (competition == null) {
            throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
        }
        competitionMapper.deleteById(id);
    }

    /**
     * 获取比赛信息
     *
     * @param id 比赛id
     * @return 比赛信息
     */
    @Override
    public CompetitionDetailVO getCompetitionInfo(Long id) {
        Competition competition = competitionMapper.selectOne(new QueryWrapper<Competition>().eq("id", id));
        if (competition == null) {
            throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
        }
        return CompetitionDetailVO.builder()
                .cover(competition.getCover())
                .id(competition.getId())
                .userCode(competition.getUserCode())
                .introduce(competition.getIntroduce())
                .isReview(competition.getIsReview())
                .maxTeamMembers(competition.getMaxTeamMembers())
                .minTeamMembers(competition.getMinTeamMembers())
                .name(competition.getName())
                .regBeginTime(competition.getRegBeginTime())
                .regEndTime(competition.getRegEndTime())
                .table(competition.getTable())
                .submitBeginTime(competition.getSubmitBeginTime())
                .submitEndTime(competition.getSubmitEndTime())
                .type(competition.getType())
                .reviewBeginTime(competition.getReviewBeginTime())
                .reviewEndTime(competition.getReviewEndTime())
                .reviewSettings(competition.getReviewSettings())
                .build();
    }

    /**
     * 获取比赛管理员信息
     *
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param comId 比赛id
     * @return 管理员信息
     */
    @Override
    public Map<String, Object> getComMangerInfo(Integer pageNum, Integer pageSize, Long comId) {
        // 检查比赛是否存在
        Competition competition = competitionMapper.selectById(comId);
        if (competition == null) {
            throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
        }

        // 结果集从 0 开始，所以这里要减一
        List<Work> works = workMapper.getWorks((pageNum - 1) * pageSize, pageSize, comId);

        // 注册数
        Long regNum = teamMapper.selectCount(new QueryWrapper<Team>().eq("com_id", comId));

        // 提交材料数
        Long subNum = workMapper.selectCount(new QueryWrapper<Work>().eq("com_id", comId));

        // 已审批数
        Long revNum = reviewMapper.selectCount(new QueryWrapper<Review>().isNotNull("accept"));

        ArrayList<CompetitionManagerVO> resList = new ArrayList<>();

        works.forEach(work -> {
            ArrayList<String> judges = new ArrayList<>();
            CompetitionManagerVO comMangerVo = new CompetitionManagerVO();
            String workName = work.getWorkName();
            String userCode = work.getUserCode();

            List<Judge> judgeList = judgeMapper.selectList(new QueryWrapper<Judge>().eq("com_id", comId).eq("user_code", userCode));

            // 判断是否分配评委
            if (judgeList.isEmpty()) {
                comMangerVo.setIsAssignJudge(0);
            } else {
                judgeList.forEach(judge -> {
                    if (!userIsExist(judge.getJudgeCode())) {
                        throw new BaseException(ErrorEnum.USER_NOT_EXIST);
                    }
                    String judgeName = userMapper.selectById(judge.getJudgeCode()).getName();
                    judges.add(judgeName);
                });
                comMangerVo.setIsAssignJudge(1);
            }

            comMangerVo.setUserCode(userCode);
            comMangerVo.setJudges(judges);
            comMangerVo.setComId(comId);
            comMangerVo.setFileName(workName);
            resList.add(comMangerVo);
        });

        QueryWrapper<Competition> competitionQueryWrapper = new QueryWrapper<>();
        competitionQueryWrapper.eq("id", comId);
        String comName = competitionMapper.selectOne(competitionQueryWrapper).getName();

        // 返回结果集、提交作品数量、报名数、提交材料数、评审数
        return getComMangerMap(resList, Math.toIntExact(subNum), pageNum, pageSize, regNum, subNum, revNum, comName);
    }


    /**
     * 判断是否存在这个部门
     *
     * @param id 部门 id
     * @return 判断结果
     */
    public boolean depIsExist(Integer id) {
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        return departmentMapper.exists(wrapper);
    }

    /**
     * 判断是否存在这个用户
     *
     * @param userCode 用户学号
     * @return 判断结果
     */
    public boolean userIsExist(String userCode) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("code", userCode);
        return userMapper.exists(wrapper);
    }

    /**
     * 上传比赛封面图
     *
     * @param cover 比赛封面
     * @param comId 比赛id
     * @return 封面在COS里的url
     */
    public String writeUploadImage(MultipartFile cover, Long comId) {
        return fileUtil.uploadCover(cover, comId);
    }

    /**
     * 格式是否正确
     *
     * @param typeName 文件格式名
     */
    public boolean isImage(@NotNull String typeName) {
        return switch (typeName) {
            case "jpg", "jpeg", "png" -> true;
            default -> false;
        };
    }

    /**
     * 验证比赛时间设置是否正确
     * @param competition 比赛信息
     */
    private void validateCompetitionDates(Competition competition) {
        if (competition.getRegBeginTime().isAfter(competition.getSubmitBeginTime()) ||  // 提交开始时间不早于报名开始时间
                competition.getSubmitBeginTime().isAfter(competition.getReviewBeginTime()) ||  // 评审开始时间不早于提交开始时间
                competition.getRegBeginTime().isAfter(competition.getRegEndTime()) ||  // 报名截止时间不早于报名开始时间
                competition.getRegEndTime().isAfter(competition.getSubmitEndTime()) ||  // 提交截止时间不早于报名截止时间
                competition.getSubmitEndTime().isAfter(competition.getReviewEndTime())) {  // 评审截止时间不早于提交截止时间
            throw new BaseException(ErrorEnum.DATE_ERROR);
        }
    }

    /**
     * 校验审批关系数据是否正确
     * 主要是判断部门跟用户是否存在
     * @param settings 审批关系设置
     */
    private void validateReviewSettings(Map<String, String> settings) {
        if (settings == null) {
            throw new BaseException(ErrorEnum.REVIEW_SETTINGS_ERROR);
        }
        settings.forEach((departmentId, userCode) -> {
            // 验证用户是否存在
            if (!userIsExist(userCode)) {
                throw new BaseException(ErrorEnum.USER_NOT_EXIST);
            }

            // 如果部门ID不为"0"，验证部门是否存在
            if (!"0".equals(departmentId)) {
                try {
                    Integer depId = Integer.valueOf(departmentId);
                    if (!depIsExist(depId)) {
                        throw new BaseException(ErrorEnum.DEP_NOT_EXIST);
                    }
                } catch (NumberFormatException e) {
                    // 如果部门ID不是有效整数，抛出部门不存在异常
                    throw new BaseException(ErrorEnum.DEP_NOT_EXIST);
                }
            }
        });
    }

    /**
     * 获取比赛管理员信息
     * @param comMangerVo 比赛管理员信息
     * @param num 总数
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param regNum 报名数
     * @param subNum 提交数
     * @param revNum 审批数
     * @param comName 比赛名称
     * @return 比赛管理员信息
     */
    public Map<String, Object> getComMangerMap(List<CompetitionManagerVO> comMangerVo, int num, Integer pageNum,
                                               Integer pageSize, Long regNum, Long subNum, Long revNum, String comName) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("records", comMangerVo);
        resultMap.put("total", num);
        resultMap.put("pageNum", pageNum);
        resultMap.put("pageSize", pageSize);
        resultMap.put("regNum", regNum);
        resultMap.put("subNum", subNum);
        resultMap.put("revNum", revNum);
        // 如果不为空就添加comId
        if(!comMangerVo.isEmpty()) {
            resultMap.put("comId", comMangerVo.get(0).getComId());
        }
        resultMap.put("comName", comName);
        return resultMap;
    }
}
