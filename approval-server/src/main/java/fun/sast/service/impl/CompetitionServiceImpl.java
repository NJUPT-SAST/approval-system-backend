package fun.sast.service.impl;

import fun.sast.Exception.BaseException;
import fun.sast.entity.Competition;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.service.CompetitionService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionMapper competitionMapper;

    /**
     * 验证是否在提交时间
     *
     * @param comId 比赛id
     * @throws BaseException 错误信息
     */
    @Override
    public void validateSubmissionPeriod(Long comId) throws BaseException {
        Competition competition = competitionMapper.selectById(comId);
        if (competition == null) {
            throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
        }
        if (competition.getSubmitEndTime().isBefore(LocalDateTime.now())) {
            throw new BaseException(ErrorEnum.WORK_SUBMIT_END);
        }
        if (competition.getSubmitBeginTime().isAfter(LocalDateTime.now())) {
            throw new BaseException(ErrorEnum.WORK_SUBMIT_NOT_START);
        }
    }
}
