package com.sast.approval.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review implements Serializable {
    /**
     * 是否通过
     */
    private boolean accept;
    /**
     * 活动id
     */
    private Integer comId;
    private String id;
    /**
     * 审批人id
     */
    private Integer judgeId;
    /**
     * 审批意见
     */
    private String opinion;
    /**
     * 队长id
     */
    private Integer userId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
