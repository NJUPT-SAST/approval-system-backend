package fun.sast.service;

import jakarta.servlet.http.HttpServletResponse;

public interface FileService {
    String getDownloadCertificate(String url);

    void exportComInfo(HttpServletResponse response, Long comId);

    void exportWork(HttpServletResponse response, Long comId, String userCode);
}
