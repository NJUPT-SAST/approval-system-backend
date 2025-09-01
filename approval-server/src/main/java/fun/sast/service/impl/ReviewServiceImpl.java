package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import fun.sast.entity.Review;
import fun.sast.mapper.ReviewMapper;
import fun.sast.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    /**
     * 更新审核状态
     *
     * @param comId 比赛id
     * @param userCode 用户学号
     */
    @Override
    public void updateReviewStatus(Long comId, String userCode) {
        // 修改作品信息后重置审核状态
        Review review =
                reviewMapper.selectOne(
                        new LambdaQueryWrapper<Review>()
                                .eq(Review::getComId, comId)
                                .eq(Review::getCode, userCode));
        if (review == null) {
            review = new Review();
            review.setComId(comId);
            review.setCode(userCode);
            reviewMapper.insert(review);
        } else {
            reviewMapper.update(
                    null,
                    new LambdaUpdateWrapper<Review>()
                            .eq(Review::getComId, comId)
                            .eq(Review::getCode, userCode)
                            .set(Review::isAccept, false)
                            .set(Review::getOpinion, null));
        }
    }
}
