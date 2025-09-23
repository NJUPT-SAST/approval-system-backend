package fun.sast.service.impl;

import fun.sast.service.ScoreService;
import fun.sast.vo.CompetitionListVO;
import org.springframework.stereotype.Service;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Override
    public CompetitionListVO getCompetitionList(int page) {
        return null;
    }

    @Override
    public CompetitionListVO getProgramList(String comId, Integer page) {
        return null;
    }
}
