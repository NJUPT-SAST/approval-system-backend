package fun.sast.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionManagerVO {

    private Long comId;

    private String fileName;

    private String userCode;

    // 0 未分配，1 分配
    private Integer isAssignJudge;

    private List<String> judges;
}