package fun.sast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.entity.Review;
import fun.sast.vo.ReviewExportVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    @Select(
            "SELECT r.id, r.judge_id, r.user_id, r.com_id, r.score, r.option,r.accept,r.create_time,r.update_time,r.create_user,r.update_user "
                    + "FROM review r "
                    + "WHERE r.com_id = #{comId}")
    List<ReviewExportVO> selectReviewsForExport(Integer comId);
}
