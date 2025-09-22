package fun.sast.vo;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 搜索比赛结果VO类 用于替代Map<String, Object>作为searchComName方法的返回类型 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SearchCompetitionResultVO implements Serializable {
    /** 总记录数 */
    private Long total;

    /** 搜索到的比赛列表 */
    private List<CompetitionBriefVO> records;

    /** 当前页码 */
    private Integer pageNum;

    /** 每页记录数 */
    private Integer pageSize;
}
