package fun.sast.vo;

import java.util.List;

public record CompetitionListVO(
        int total,
        List<CompetitionList> list,
        int pageNum,
        String pageSize,
        int pages,
        boolean isFirstPage,
        boolean isLastPage) {}
