package fun.sast.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CompetitionList {
    private int id;
    private String title;
    private int totalNum;
    private String completedNum;
    private String startDate;
    private String endDate;
}
