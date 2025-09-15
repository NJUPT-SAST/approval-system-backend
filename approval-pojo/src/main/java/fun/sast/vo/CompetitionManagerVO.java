package fun.sast.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
