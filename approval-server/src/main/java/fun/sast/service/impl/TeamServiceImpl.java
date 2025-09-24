package fun.sast.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fun.sast.entity.Team;
import fun.sast.mapper.TeamMapper;
import fun.sast.service.TeamService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamMapper teamMapper;

    @Override
    public Long countTeamsByComId(Long comId) {
        return teamMapper.selectCount(new LambdaQueryWrapper<Team>().eq(Team::getComId, comId));
    }

    @Override
    public List<Team> getTeamsByComId(Long comId) {
        return teamMapper.selectList(new LambdaQueryWrapper<Team>().eq(Team::getComId, comId));
    }

    @Override
    public Integer countParticipantsByComId(Long comId, Integer competitionType) {
        List<Team> teams = getTeamsByComId(comId);

        if (teams == null || teams.isEmpty()) {
            return 0;
        }

        int totalParticipants = 0;

        for (Team team : teams) {
            if (competitionType == 1) {
                // 个人赛：每个队伍就是一个参与者
                totalParticipants += 1;
            } else {
                // 团队赛：队长 + 成员
                totalParticipants += 1; // 队长

                // 统计成员数量
                if (StringUtils.hasText(team.getMember())) {
                    try {
                        JSONArray members = JSON.parseArray(team.getMember());
                        if (members != null) {
                            totalParticipants += members.size();
                        }
                    } catch (Exception e) {
                        // JSON解析失败，忽略成员数量
                    }
                }
            }
        }

        return totalParticipants;
    }
}
