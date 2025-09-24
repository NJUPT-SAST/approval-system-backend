package fun.sast.service.impl;

import fun.sast.entity.Competition;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.service.CompetitionService;
import fun.sast.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionMapper competitionMapper;
    private final TeamService teamService;

    @Override
    public Competition getById(Long id) {
        return competitionMapper.selectById(id);
    }

    @Override
    public Integer getTotalParticipants(Long comId) {
        // 获取比赛信息以确定比赛类型
        Competition competition = getById(comId);
        if (competition == null) {
            return 0;
        }

        // 通过 TeamService 获取总参与人数
        return teamService.countParticipantsByComId(comId, competition.getType());
    }

    @Override
    public Long getTotalTeams(Long comId) {
        return teamService.countTeamsByComId(comId);
    }
}
