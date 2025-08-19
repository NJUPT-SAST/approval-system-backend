package fun.sast.service;

import fun.sast.Exception.BaseException;

public interface CompetitionService {

    /**
     * 验证是否在提交时间段
     *
     * @param comId 比赛id
     * @throws BaseException 错误
     */
    void validateSubmissionPeriod(Long comId) throws BaseException;
}
