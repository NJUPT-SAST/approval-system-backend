package com.sast.approval.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRoleEnum {

    TOURIST(-1, "游客"),
    STUNDENT(0, "学生"),
    REVIEW(1, "审批"),
    JUDGE(2, "评委"),
    ADMIN(3, "管理员");

    private final Integer role;
    private final String roleName;
}
