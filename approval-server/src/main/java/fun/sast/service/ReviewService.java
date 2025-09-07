package fun.sast.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fun.sast.entity.Review;


public interface ReviewService extends IService<Review> {


    Integer reviewTotal(String code, Integer comId);
}
