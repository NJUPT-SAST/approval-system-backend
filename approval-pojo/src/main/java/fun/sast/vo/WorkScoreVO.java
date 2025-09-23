package fun.sast.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class WorkScoreVO {
    private int id;
    private String title;
    private Object score;
    private String opinion;
}