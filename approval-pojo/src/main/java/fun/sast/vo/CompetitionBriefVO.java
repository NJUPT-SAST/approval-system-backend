package fun.sast.vo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 比赛简要信息VO类 用于在列表中展示比赛的基本信息 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompetitionBriefVO implements Serializable {
    /** 比赛ID */
    private Long id;

    /** 比赛名称 */
    private String name;

    /** 封面图片URL */
    private String cover;

    /** 比赛状态（0未开始 1进行中 2已结束） */
    private Integer status;

    /** 报名开始时间 */
    private String regBeginTime;

    /** 报名结束时间 */
    private String regEndTime;

    /** 是否已报名 */
    private Boolean isSigned;
}
