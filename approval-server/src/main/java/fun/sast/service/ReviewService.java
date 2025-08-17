package fun.sast.service;

import fun.sast.vo.AccountImportVO;
import fun.sast.vo.CompetitionListVO;
import java.util.List;

public interface ReviewService {
    List<AccountImportVO> importAccount(String comId);

    void uploadReview(String id, boolean accept, String opinion);

    CompetitionListVO getCompetitionList(int page);

    CompetitionListVO getPragramList(String comId, Integer page);
}
