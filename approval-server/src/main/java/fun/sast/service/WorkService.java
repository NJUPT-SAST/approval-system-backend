package fun.sast.service;

public interface WorkService {

    /**
     * 根据比赛ID统计提交作品数量
     *
     * @param comId 比赛ID
     * @return 作品数量
     */
    Long countByComId(Long comId);
}
