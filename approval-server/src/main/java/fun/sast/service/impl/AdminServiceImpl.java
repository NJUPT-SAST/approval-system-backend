package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.sast.entity.*;
import fun.sast.mapper.*;
import fun.sast.service.AdminService;
import fun.sast.vo.CompetitionListVO;
import fun.sast.vo.CompetitionVO;
import fun.sast.vo.UserInfoVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final CompetitionMapper competitionMapper;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final JudgeMapper judgeMapper;
    private final WorkMapper workMapper;
    private final TeamMapper teamMapper;
    private final ReviewMapper reviewMapper;

    @Override
    public CompetitionListVO getCompetitionList(Integer pageNum, Integer pageSize) {
        // 分页查询比赛列表
        Page<Competition> competitionPage =
                competitionMapper.selectPage(new Page<>(pageNum, pageSize), new QueryWrapper<>());

        List<CompetitionVO> resultList = new ArrayList<>();

        for (Competition competition : competitionPage.getRecords()) {
            Integer comId = competition.getId();

            CompetitionVO competitionVO = new CompetitionVO();
            competitionVO.setId(comId);
            competitionVO.setName(competition.getName());
            competitionVO.setBeginTime(String.valueOf(competition.getRegBeginTime()));
            competitionVO.setEndTime(String.valueOf(competition.getRegEndTime()));
            competitionVO.setIntroduce(competition.getIntroduce());

            // 获取评委
            QueryWrapper<Judge> judgeQueryWrapper = new QueryWrapper<>();
            judgeQueryWrapper.eq("com_id", comId);
            List<Judge> judges = judgeMapper.selectList(judgeQueryWrapper);
            // 提取judgeCode
            List<String> reviewerCodes = new ArrayList<>();
            for (Judge judge : judges) {
                reviewerCodes.add(judge.getJudgeCode());
            }
            // 拼接成逗号分隔的字符串
            String reviewers = String.join(",", reviewerCodes);

            competitionVO.setReviewer(reviewers);

            // 比赛状态判断
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime beginTime = competition.getRegBeginTime();
            LocalDateTime endTime = competition.getReviewEndTime();
            String status;
            if (now.isBefore(beginTime)) {
                status = "未开始";
            } else if (now.isAfter(endTime)) {
                status = "已结束";
            } else {
                status = "进行中";
            }
            competitionVO.setStatus(status);

            // 注册数
            Long regNum = teamMapper.selectCount(new QueryWrapper<Team>().eq("com_id", comId));
            // 提交材料数
            Long subNum = workMapper.selectCount(new QueryWrapper<Work>().eq("com_id", comId));
            // 已审批数
            Long revNum = reviewMapper.getReviewNum(comId);

            competitionVO.setRegNum(Math.toIntExact(regNum));
            competitionVO.setSubNum(Math.toIntExact(subNum));
            competitionVO.setRevNum(Math.toIntExact(revNum));

            resultList.add(competitionVO);
        }

        // 组装 VO
        CompetitionListVO competitionListVO = new CompetitionListVO();
        competitionListVO.setTotal((int) competitionPage.getTotal());
        competitionListVO.setPageNum(pageNum);
        competitionListVO.setPageSize(pageSize);
        competitionListVO.setRecords(resultList);

        return competitionListVO;
    }

    @Override
    public UserInfoVO getUserInfo(String userCode) {
        // 查询用户信息
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("code", userCode);
        User user = userMapper.selectOne(wrapper);
        // 查询部门信息
        Department department = departmentMapper.selectById(user.getDepId());

        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setName(user.getName());
        vo.setRole(user.getRole());
        vo.setNum(userCode);
        vo.setDep_id(departmentMapper.selectById(department.getId()));

        return vo;
    }
}
