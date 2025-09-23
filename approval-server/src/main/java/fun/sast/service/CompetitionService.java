package fun.sast.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fun.sast.entity.Competition;

public interface CompetitionService extends IService<Competition> {

    public Boolean reviewPoint();
}
