package fun.sast.service;

import fun.sast.vo.AccountImportVO;
import fun.sast.vo.CompetitionListVO;
import fun.sast.vo.WorkReviewListVO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ReviewService {
    List<AccountImportVO> importAccount(String depId, MultipartFile file);

    void uploadReview(String id, boolean accept, String opinion);

    CompetitionListVO getCompetitionList(int page);

    WorkReviewListVO getPragramList(String comId, Integer page);
}
