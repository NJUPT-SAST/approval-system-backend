package fun.sast.service;

import fun.sast.entity.Team;
import java.util.List;

public interface TeamService {

    /**
     * 根据比赛ID统计队伍数量
     *
     * @param comId 比赛ID
     * @return 队伍数量
     */
    Long countTeamsByComId(Long comId);

    /**
     * 根据比赛ID获取所有队伍信息
     *
     * @param comId 比赛ID
     * @return 队伍列表
     */
    List<Team> getTeamsByComId(Long comId);

    /**
     * 根据比赛ID统计总参与人数
     *
     * @param comId 比赛ID
     * @param competitionType 比赛类型 (0: 团队赛, 1: 个人赛)
     * @return 总参与人数
     */
    Integer countParticipantsByComId(Long comId, Integer competitionType);
}
