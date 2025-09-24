package fun.sast.service;

import fun.sast.entity.File;
import jakarta.servlet.http.HttpServletResponse;

public interface FileService {
    String getDownloadCertificate(String url);

    File exportComInfo(HttpServletResponse response,Long comId);

    File exportWork(HttpServletResponse response,Long comId,String userCode);

}
