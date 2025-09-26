package fun.sast.service;

import fun.sast.dto.FileResponseDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface FileService {
    String getDownloadCertificate(String url);

    FileResponseDTO exportComInfo(Integer comId);

    void exportWork(HttpServletResponse response, Integer comId, String userCode);
}
