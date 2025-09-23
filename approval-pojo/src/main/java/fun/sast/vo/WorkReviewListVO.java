package fun.sast.vo;

import java.util.List;

public record WorkReviewListVO(
        int total,
        List<WorkReviewVO> list,
        int pageNum,
        String pageSize,
        int pages,
        boolean isFirstPage,
        boolean isLastPage) {}