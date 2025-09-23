package fun.sast.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import fun.sast.entity.Review;
import fun.sast.mapper.ReviewMapper;
import fun.sast.service.ReviewService;
import fun.sast.utils.FileUtil;
import fun.sast.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public ProgramInfoForReview getProgramInfo(Integer proId) {
        Integer comId = reviewMapper.getComIdByProId(proId);
        String captainId = reviewMapper.getCaptainIdByProId(proId);
        if (comId == null || captainId == null) {
            return null;
        }
        //获取文字信息
        List<Text> texts = new ArrayList<>();
        String SContents = reviewMapper.getContents(comId, captainId);
        if (SContents != null) {
            List<Content> contents = JSON.parseArray(SContents).toJavaList(Content.class);
            for (Content content : contents) {
                if (!content.getIsFile()) {
                    texts.add(new Text(content.getInput(), content.getContent()));
                }
            }
        }
        //获取附件
        List<String> urls = reviewMapper.getAccessories(comId, captainId);
        List<Accessory> accessories = new ArrayList<>();
        if (urls != null) {
            for (Object url : urls) {
                String sUrl = url.toString();
                accessories.add(new Accessory(FileUtil.getFileName(sUrl), sUrl));
            }
        }
        String teamName = reviewMapper.getTeamName(comId, captainId);
        //成员信息
        String jMembers = reviewMapper.getJMember(comId, captainId);
        //获取成员数量
        JSONArray array = JSON.parseArray(jMembers);
        Integer memberNum = array.size();

        //包装返回
        return new ProgramInfoForReview(teamName,new UserInfo(captainId, reviewMapper.getCaptainName(captainId)) , memberNum, array, accessories, texts);
    }

}
