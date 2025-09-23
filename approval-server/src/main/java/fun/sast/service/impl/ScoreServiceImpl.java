package fun.sast.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.sast.entity.Competition;
import fun.sast.entity.Review;
import fun.sast.entity.Score;
import fun.sast.mapper.ReviewMapper;
import fun.sast.mapper.ScoreMapper;
import fun.sast.service.ScoreService;
import fun.sast.utils.FileUtil;
import fun.sast.vo.Accessory;
import fun.sast.vo.ProgramInfoForScore;
import fun.sast.vo.Text;
import fun.sast.vo.UserInfo;
import lombok.RequiredArgsConstructor;
import org.jdom2.Content;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor

public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {

    private final ScoreMapper scoreMapper;
    ReviewMapper reviewMapper;
    @Override
    public Boolean scorePoint() {
        QueryWrapper<Score> wrapper = new QueryWrapper<Score>()
                .select("score")
                .eq("score",null);
        Long count = scoreMapper.selectCount(wrapper);
        return count==0 ;
    }

    @Override
    public Integer scoreTatal(String code,Integer comId) {
        QueryWrapper<Score> wrapper = new QueryWrapper<Score>()
                .eq("judge_id",code)
                .eq("com_id",comId);
        Integer count = Math.toIntExact(scoreMapper.selectCount(wrapper));
        return count;
    }

    @Override
    public boolean scoreUpload(Integer id, Integer score, String opinion) {
        QueryWrapper<Score> wrapper=new QueryWrapper<Score>()
                .select("com_id","user_id")
                .eq("id",id);

        Score score1=scoreMapper.selectById(wrapper);
        String comId = score1.getComId();
        String userId = score1.getUserId();

        Score score2 = new Score();
        score2.setScore(score);
        score2.setOption(opinion);

        if (userId != null && comId != null) {
           scoreMapper.update(score2,wrapper);
           return true;
        }
        return false;

    }

    @Override
    public Boolean confirmPro(String code, Integer id) {
        return scoreMapper.getJCount(code, id) > 0;
    }

    @Override
    public ProgramInfoForScore getProgramInfo(Integer proId, String judgeCode) {
        Integer comId = scoreMapper.getComIdByProId(proId);
        String captainId = scoreMapper.getUserCode(proId);
        if (comId == null || captainId == null) {
            return null;
        }
        //成员信息

        String jMembers = reviewMapper.getJMember(comId, captainId);
        //获取成员数量
        JSONArray array = JSON.parseArray(jMembers);
        Integer memberNum = array.size();
        //获取文字信息
        List<Text> texts = new ArrayList<>();
        String SContents = reviewMapper.getContents(comId, captainId);
        if (SContents != null) {
            List<fun.sast.vo.Content> contents = JSON.parseArray(SContents).toJavaList(fun.sast.vo.Content.class);
            for (fun.sast.vo.Content content : contents) {
                if (!content.getIsFile()) {
                    texts.add(new Text(content.getInput(), content.getContent()));
                }
            }
        }
        //获取评分
        Integer score = scoreMapper.getScoreInfo(comId, captainId, judgeCode);
        String opinion = scoreMapper.getOpinionInfo(comId, captainId, judgeCode);
        //获取附件
        List<String> urls = reviewMapper.getAccessories(comId, captainId);
        List<Accessory> accessories = new ArrayList<>();
        for (Object url : urls) {
            String sUrl = url.toString();
            accessories.add(new Accessory(FileUtil.getFileName(sUrl), sUrl));
        }
        //获取队伍名
        String teamName = reviewMapper.getTeamName(comId, captainId);
        //包装返回
    return new ProgramInfoForScore(teamName, new UserInfo(captainId, reviewMapper.getCaptainName(captainId)),
                memberNum, array, accessories, texts, score, opinion);
    }

}
