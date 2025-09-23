package fun.sast.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fun.sast.entity.Review;
import fun.sast.vo.ProgramInfoForReview;


public interface ReviewService extends IService<Review> {

    public ProgramInfoForReview getProgramInfo(Integer proId);
    Integer reviewTotal(String code, Integer comId);
}
