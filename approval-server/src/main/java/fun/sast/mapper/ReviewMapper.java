package fun.sast.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.sast.entity.Review;
import fun.sast.vo.ComListForReview;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewMapper extends BaseMapper<Review> {
    Integer getReviewCount();

    Integer getComCount();

    IPage<ComListForReview> getComInfo(Page<ComListForReview> page, @Param("code") String code, @Param("dep_id") Integer depId);
}
