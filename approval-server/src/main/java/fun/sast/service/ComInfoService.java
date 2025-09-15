package fun.sast.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ComInfoService {
    void exportComInfo(HttpServletResponse response, Long comId);
}
