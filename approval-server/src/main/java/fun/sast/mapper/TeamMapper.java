package fun.sast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.entity.Team;
import java.util.List;

public interface TeamMapper extends BaseMapper<Team> {

    /**
     * 根据比赛ID和队长学号查询队伍信息
     *
     * @param comId 比赛ID
     * @param captain 队长学号
     * @return 队伍信息
     */
    Team selectByComIdAndCaptain(Long comId, String captain);

    /**
     * 根据用户ID查询已报名的比赛ID列表
     *
     * @param userId 用户ID
     * @return 已报名的比赛ID列表
     */
    List<Long> selectSignedComIdsByUserId(Integer userId);
}
