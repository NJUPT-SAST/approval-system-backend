package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.sast.Exception.BaseException;
import fun.sast.entity.Competition;
import fun.sast.entity.User;
import fun.sast.enums.ErrorEnum;
import fun.sast.enums.ReviewStatusEnum;
import fun.sast.enums.UserRoleEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.service.CompetitionService;
import fun.sast.service.ReviewService;
import fun.sast.service.WorkService;
import fun.sast.vo.AccountImportVO;
import fun.sast.vo.CompetitionList;
import fun.sast.vo.CompetitionListVO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final CompetitionMapper competitionMapper;
    private final WorkService workService;
    private final CompetitionService competitionService;
    private static final int PAGE_SIZE = 10;

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
        User user = UserInterceptor.userHolder.get();
        if (user == null
                || !(UserRoleEnum.JUDGE.getRole().equals(user.getRole())
                        || UserRoleEnum.ADMIN.getRole().equals(user.getRole()))) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }

        Page<Competition> pageObj = new Page<>(page, PAGE_SIZE);
        LambdaQueryWrapper<Competition> queryWrapper =
                new LambdaQueryWrapper<Competition>()
                        .eq(Competition::getIsReview, ReviewStatusEnum.PENDING.getCode())
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
    public CompetitionListVO getPragramList(String comId, Integer page) {
        return null;
    }
}
