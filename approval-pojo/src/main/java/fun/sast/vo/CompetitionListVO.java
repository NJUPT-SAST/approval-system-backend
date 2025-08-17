package fun.sast.vo;

import java.util.List;

record CompetitionList(
        int id,
        String title,
        int totalNum,
        String completedNum,
        String startTime,
        String endTime) {}

public record CompetitionListVO(
        int total,
        List<CompetitionList> list,
        int pageNum,
        String pageSize,
        int pages,
        boolean isFirstPage,
        boolean isLastPage) {}
