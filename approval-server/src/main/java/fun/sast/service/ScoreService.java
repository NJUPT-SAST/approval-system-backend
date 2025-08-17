package fun.sast.service;

import fun.sast.vo.CompetitionListVO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public interface ScoreService {
    public CompetitionListVO getCompetitionList(@RequestParam int page);

    public CompetitionListVO getProgramList(
            @RequestParam(required = false) String comId,
            @RequestParam(required = false) Integer page);
}
