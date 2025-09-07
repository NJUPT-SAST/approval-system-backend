package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import fun.sast.entity.Review;
import fun.sast.mapper.ReviewMapper;
import fun.sast.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    private final ReviewMapper reviewMapper;


    @Override
    public Integer reviewTotal(String code, Integer comId) {
        QueryWrapper<Review> wrapper = new QueryWrapper<Review>()
                .eq("judge_id",code)
                .eq("com_id",comId);
        Integer count = Math.toIntExact(reviewMapper.selectCount(wrapper));
        return count;
    }
}
