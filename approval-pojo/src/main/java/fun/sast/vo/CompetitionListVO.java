package fun.sast.vo;

import java.util.List;
import lombok.Data;

@Data
public class CompetitionListVO {
    private Integer total;
    private List<CompetitionVO> records;
    private Integer pageSize;
    private Integer pageNum;
}
