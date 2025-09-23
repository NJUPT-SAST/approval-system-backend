package fun.sast.vo;

import java.util.List;

public record WorkScoreListVO(
        int total,
        List<WorkScoreVO> list,
        int pageNum,
        String pageSize,
        int pages,
        boolean isFirstPage,
        boolean isLastPage) {}