package fun.sast.service;

import fun.sast.entity.Competition;
import fun.sast.vo.CompetitionDetailVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface AdminService {
    /**
     * 创建比赛
     *
     * @param Com   比赛信息
     * @param cover      比赛封面
     */
    void createCompetition(Competition Com, MultipartFile cover);

    /**
     * 修改比赛
     *
     * @param parseCom   比赛信息
     * @param cover      比赛封面
     */
    void editCompetition(Competition parseCom, MultipartFile cover);

    /**
     * 删除比赛
     *
     * @param comId   比赛id
     */
    void deleteCompetition(Long comId);

    /**
     * 获取比赛信息
     *
     * @param comId   比赛id
     * @return CompetitionDetailVO 返回比赛信息
     */
    CompetitionDetailVO getCompetitionInfo(Long comId);

    /**
     * 获取比赛管理信息
     *
     * @param pageNum   页码
     * @param pageSize   每页数量
     * @param comId   比赛id
     * @return Map<String, Object> 返回比赛管理信息
     */
    Map<String, Object> getComMangerInfo(Integer pageNum, Integer pageSize, Long comId);
}
