package fun.sast.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ScoreService {
    /**
     * 导出评审结果
     *
     * @param comId 比赛ID
     * @param response HTTP响应对象
     */
    void exportScore(Integer comId, HttpServletResponse response);
}
