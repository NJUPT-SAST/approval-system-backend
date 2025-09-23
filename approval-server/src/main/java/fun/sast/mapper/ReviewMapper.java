package fun.sast.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.sast.entity.Review;
import fun.sast.vo.ComListForReview;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewMapper extends BaseMapper<Review> {
    Integer getReviewCount();

    Integer getComCount();

    String getContents(@Param("comId") Integer comId, @Param("userCode") String userCode);

    String getJMember(@Param("comId") Integer comId, @Param("captainId") String captainId);

    IPage<ComListForReview> getComInfo(Page<ComListForReview> page, @Param("code") String code, @Param("dep_id") Integer depId);

    List<String> getAccessories(@Param("comId") Integer comId, @Param("userCode") String userCode);

    String getTeamName(@Param("comId") Integer comId, @Param("captainId") String captainId);

    String getCaptainName(@Param("code") String captainId);

    String getCaptainIdByProId(Integer proId);

    Integer getComIdByProId(Integer proId);
}
