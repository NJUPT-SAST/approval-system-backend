package fun.sast.vo;

import com.alibaba.fastjson2.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgramInfoForReview {
    private String teamName;
    private UserInfo captain;
    private Integer memberNum;
    private JSONArray memberList;
    private List<Accessory> accessories;
    private List<Text> texts;
}
