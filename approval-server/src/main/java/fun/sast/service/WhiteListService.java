package fun.sast.service;

import org.springframework.web.multipart.MultipartFile;

public interface WhiteListService {
    /**
     * @param comId 比赛id
     * @param isWhiteList 是否启用白名单
     * @param file 白名单Excel文件
     */
    void operateWhiteList(Long comId, Boolean isWhiteList, MultipartFile file);
}
