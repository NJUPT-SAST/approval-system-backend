package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.sast.entity.Competition;
import fun.sast.entity.Department;
import fun.sast.entity.User;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.mapper.DepartmentMapper;
import fun.sast.mapper.UserMapper;
import fun.sast.service.AdminService;
import fun.sast.vo.CompetitionListVO;
import fun.sast.vo.CompetitionVO;
import fun.sast.vo.UserInfoVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final CompetitionMapper competitionMapper;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;

    @Override
    public CompetitionListVO getCompetitionList(Integer pageNum, Integer pageSize) {
        // 分页查询比赛列表
        Page<Competition> competitionPage =
                competitionMapper.selectPage(new Page<>(pageNum, pageSize), new QueryWrapper<>());

        List<CompetitionVO> resultList = new ArrayList<>();

        for (Competition competition : competitionPage.getRecords()) {
            CompetitionVO competitionVO = new CompetitionVO();
            competitionVO.setId(competition.getId());
            competitionVO.setName(competition.getName());
            competitionVO.setBeginTime(String.valueOf(competition.getRegBeginTime()));
            competitionVO.setEndTime(String.valueOf(competition.getRegEndTime()));
            competitionVO.setIntroduce(competition.getIntroduce());

            System.out.println(competitionVO);

            // 解析 Object 类型的 reviewSettings
            Map<String, String> settings = new HashMap<>();
            Object obj = competition.getReviewSettings();
            if (obj != null && obj instanceof Map) {
                settings = (Map<String, String>) obj;
            }

            // 直接取默认评委 "0"
            String reviewer = settings.get("0");
            competitionVO.setReviewer(reviewer);

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
