package fun.sast.service;

public interface ReviewService {

    /**
     * 更新审核状态
     *
     * @param comId 比赛id
     * @param userCode 用户学号
     */
    void updateReviewStatus(Long comId, String userCode);
}
