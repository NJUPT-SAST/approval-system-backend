package fun.sast.service;

import fun.sast.entity.Competition;

public interface CompetitionService {

    /**
     * 根据ID获取比赛信息
     *
     * @param id 比赛ID
     * @return 比赛信息
     */
    Competition getById(Long id);

    /**
     * 获取比赛的总参与人数
     *
     * @param comId 比赛ID
     * @return 总参与人数
     */
    Integer getTotalParticipants(Long comId);

    /**
     * 获取比赛的队伍数量
     *
     * @param comId 比赛ID
     * @return 队伍数量
     */
    Long getTotalTeams(Long comId);
}
