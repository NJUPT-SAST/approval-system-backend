package fun.sast.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.sast.entity.Score;
import fun.sast.mapper.ScoreMapper;
import fun.sast.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {

}
