package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.sast.entity.Competition;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.service.CompetitionService;
import fun.sast.service.ScoreService;
import fun.sast.service.WorkService;
import fun.sast.vo.CompetitionList;
import fun.sast.vo.CompetitionListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final CompetitionMapper competitionMapper;
    private final WorkService workService;
    private final CompetitionService competitionService;
    private static final int PAGE_SIZE = 10;

    @Override
    public CompetitionListVO getCompetitionList(int page) {
        Page<Competition> pageObj = new Page<>(page, PAGE_SIZE);
        LambdaQueryWrapper<Competition> queryWrapper =
                new LambdaQueryWrapper<Competition>()
                        .orderByDesc(Competition::getCreateTime);

        IPage<Competition> competitionPage = competitionMapper.selectPage(pageObj, queryWrapper);

        List<CompetitionList> list =
                competitionPage.getRecords().stream()
                        .map(
                                competition -> {
                                    return new CompetitionList()
                                            .setId(competition.getId())
                                            .setTitle(competition.getName())
                                            .setTotalNum(
                                                    competitionService.getTotalParticipants(
                                                            Long.valueOf(competition.getId())))
                                            .setCompletedNum(
                                                    workService
                                                            .countByComId(
                                                                    Long.valueOf(
                                                                            competition.getId()))
                                                            .toString())
                                            .setStartDate(competition.getRegBeginTime())
                                            .setEndDate(competition.getRegEndTime());
                                })
                        .collect(Collectors.toCollection(ArrayList::new));

        int pages = (int) competitionPage.getPages();
        boolean isFirst = page <= 1;
        boolean isLast = pages == 0 || page >= pages;

        return new CompetitionListVO(
                (int) competitionPage.getTotal(),
                list,
                page,
                String.valueOf(PAGE_SIZE),
                pages,
                isFirst,
                isLast);
    }

    @Override
    public CompetitionListVO getProgramList(String comId, Integer page) {
        return null;
    }
}
