package fun.sast.controller.reviewController;

import fun.sast.annotation.ResponseResult;
import fun.sast.service.ReviewService;
import fun.sast.vo.AccountImportVO;
import fun.sast.vo.CompetitionListVO;
import fun.sast.vo.WorkReviewListVO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 导入学生账号并导出账号 excel
     *
     * @param depId 学院 ID
     * @return List<Account> 账号列表
     */
    @ResponseResult
    @PostMapping("/import")
    public List<AccountImportVO> importAccount(@RequestParam String depId) {
        return reviewService.importAccount(depId);
    }

    /**
     * 提交作品审核信息
     *
     * @param id 所审核作品的 ID
     * @param accept 是否审核通过
     * @param opinion 对于通过情况的说明
     */
    @ResponseResult
    @PostMapping("/upload")
    public Map<String, Object> uploadReview(
            @RequestParam String id,
            @RequestParam boolean accept,
            @RequestParam(required = false) String opinion) {
        reviewService.uploadReview(id, accept, opinion);
        return new HashMap<>();
    }

    /**
     * 获取审核比赛列表
     *
     * @param page 页码
     * @return CompetitionListVO 比赛列表
     */
    @ResponseResult
    @GetMapping("/competition-list")
    public CompetitionListVO getCompetitionList(
            @RequestParam(required = false, defaultValue = "1") int page) {
        return reviewService.getCompetitionList(page);
    }

    /**
     * 获取审核作品列表
     *
     * @param comId 比赛 Id
     * @param page 页码
     * @return WorkReviewListVO 作品列表
     */
    @ResponseResult
    @GetMapping("/program-list")
    public WorkReviewListVO getPragramList(
            @RequestParam String comId, @RequestParam(required = false) Integer page) {
        return reviewService.getPragramList(comId, page);
    }
}
