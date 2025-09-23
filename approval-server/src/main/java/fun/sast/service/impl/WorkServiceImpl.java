package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fun.sast.entity.Work;
import fun.sast.mapper.WorkMapper;
import fun.sast.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService {

    private final WorkMapper workMapper;

    @Override
    public Long countByComId(Long comId) {
        return workMapper.selectCount(new LambdaQueryWrapper<Work>().eq(Work::getComId, comId));
    }
}
