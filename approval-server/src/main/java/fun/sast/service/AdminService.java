package fun.sast.service;

import fun.sast.vo.CompetitionListVO;
import fun.sast.vo.UserInfoVO;

public interface AdminService {
    CompetitionListVO getCompetitionList(Integer pageNo, Integer pageSize);

    UserInfoVO getUserInfo(String userCode);
}
