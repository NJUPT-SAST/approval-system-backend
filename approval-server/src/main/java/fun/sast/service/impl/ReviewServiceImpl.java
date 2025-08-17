package fun.sast.service.impl;

import fun.sast.service.ReviewService;
import fun.sast.vo.AccountImportVO;
import fun.sast.vo.CompetitionListVO;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Override
    public List<AccountImportVO> importAccount(String comId) {
        return null;
    }

    @Override
    public void uploadReview(String id, boolean accept, String opinion) {
        return;
    }

    @Override
    public CompetitionListVO getCompetitionList(int page) {
        return null;
    }

    @Override
    public CompetitionListVO getPragramList(String comId, Integer page) {
        return null;
    }
}
