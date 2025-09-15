package fun.sast.vo;

import lombok.Data;

@Data
public class CompetitionVO {
    private Integer id;
    private String name;
    private String beginTime;
    private String endTime;
    private String introduce;
    private String reviewer;
    private String status;
    private Integer regNum;
    private Integer subNum;
    private Integer revNum;
}
