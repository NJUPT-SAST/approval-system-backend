package fun.sast.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComListForReview {

        private Integer id;
        private String title;
        private Integer totalNum;
        private Integer completedNum;
        private String startDate;
        private String endDate;
}
