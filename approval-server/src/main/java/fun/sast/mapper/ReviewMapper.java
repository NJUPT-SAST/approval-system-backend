package fun.sast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.entity.Review;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewMapper extends BaseMapper<Review> {
    @Select("SELECT COUNT(*) FROM review WHERE com_id = #{comId} AND accept = 1")
    Long getReviewNum(@Param("comId") Integer comId);
}
