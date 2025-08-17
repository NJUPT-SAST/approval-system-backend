package fun.sast.controller.scoreController;

import fun.sast.annotation.ResponseResult;
import fun.sast.service.ScoreService;
import fun.sast.vo.CompetitionListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/score")
public class ScoreController {

    private final ScoreService scoreService;

    /**
     * 获取评分比赛列表
     *
     * @param page 页码
     * @return CompetitionListVO 比赛列表
     */
    @ResponseResult
    @GetMapping("/competition-list")
    public CompetitionListVO getCompetitionList(@RequestParam int page) {
        return scoreService.getCompetitionList(page);
    }

    /**
     * 获取评分作品列表
     *
     * @param comId 比赛 Id
     * @param page 页码
     * @return CompetitionListVO 作品列表
     */
    @ResponseResult
    @GetMapping("/program-list")
    public CompetitionListVO getProgramList(
            @RequestParam(required = false) String comId,
            @RequestParam(required = false) Integer page) {
        return scoreService.getProgramList(comId, page);
    }
}
