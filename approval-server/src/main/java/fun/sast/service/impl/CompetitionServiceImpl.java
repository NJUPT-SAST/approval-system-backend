package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.sast.entity.Competition;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements CompetitionService {

    private final CompetitionMapper competitionMapper;


    //审核红点
    @Override
    public Boolean reviewPoint() {
        QueryWrapper<Competition> wrapper = new QueryWrapper<Competition>()
                .select("isReview")
                .eq("isReview","1");
        Long count= competitionMapper.selectCount(wrapper);
        return count == 0;
    }
}
