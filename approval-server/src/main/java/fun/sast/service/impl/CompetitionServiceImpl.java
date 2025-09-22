package fun.sast.service.impl;

import com.alibaba.fastjson2.JSONObject;
import fun.sast.Exception.BaseException;
import fun.sast.entity.Competition;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {
    private final CompetitionMapper competitionMapper;

    @Override
    public JSONObject getSchema(Long comId) {
        if (comId == null) {
            throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
        }
        Competition competition = competitionMapper.selectById(comId);
        if (competition == null) {
            throw new BaseException(ErrorEnum.CONTEST_NOT_EXIST);
        }

        JSONObject resultData = new JSONObject();
        resultData.put("table", competition.getTableSchema().toString());

        return resultData;
    }
}
