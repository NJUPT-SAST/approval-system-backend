package fun.sast.service;

import org.springframework.web.multipart.MultipartFile;

public interface WhiteListService {
    /**
     * @param comId 比赛id
     * @param isEnable 是否启用白名单
     * @param excelFile 白名单Excel文件
     */
    void operateWhiteList(Long comId, Boolean isEnable, MultipartFile excelFile);
}
