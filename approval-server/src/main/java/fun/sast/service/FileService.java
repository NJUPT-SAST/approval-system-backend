package fun.sast.service;

import fun.sast.vo.WorkOutPutVO;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface FileService {
    String getDownloadCertificate(String url);

    List<WorkOutPutVO> exportWork(Long comId);

    void exportWorkExcel(Long comId, HttpServletResponse response);
}
