package fun.sast.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ReviewService {
    /**
     * 导出评审结果
     *
     * @param comId 比赛ID
     * @param response HTTP响应对象
     */
    void exportReviewResult(Integer comId, HttpServletResponse response);
}
