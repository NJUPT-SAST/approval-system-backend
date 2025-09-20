package fun.sast.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorEnum {
    COMMON_ERROR(1000, "错误"),
    JSON_DATA_EMPTY(1001, "报名表单数据不能为空"),
    JSON_FORMAT_ERROR(1002, "JSON格式错误"),
    TOKEN_ERROR(1003, "TOKEN错误"),
    NO_LOGIN(1004, "没有登录"),
    EXPIRED_LOGIN(1005, "登录过期"),
    NO_ROLE(1006, "无权限"),
    NO_TOKEN(1007, "无token"),
    LOGIN_ERROR(1008, "登录失败"),
    USERNAME_OR_PASSWORD_EMPTY(1009, "用户名或密码不能为空"),
    UNKNOWN_COMPETITION_ID(2001, "找不到相应的比赛"),
    UNKNOWN_TEAM_ID(2002, "找不到相应的队伍"),
    HAVE_NOT_SIGNED_COM(2003, "您还未报名该比赛"),
    HAVE_NOT_UPLOAD_WORK(2004, "您还未上传作品"),
    INVALID_FILE_TYPE_ERROR(3001, "文件类型不合法"),
    OSS_FAILED_UPLOAD_ERROR(3002, "文件上传至存储服务器时出错"),
    OSS_FAILED_DOWNLOAD_ERROR(3003, "文件下载至服务器时出错"),
    OSS_FAILED_DELETE_ERROR(3004, "删除存储服务器上的文件时出错"),
    OSS_BUCKET_NOT_EXIST(3005, "Bucket不存在"),
    OSS_FILE_NOT_EXIST(3006, "文件不存在"),
    INVALID_URL_ERROR(3007, "URL格式不合法"),
    TOO_MANY_REQUESTS(3008, "请求过于频繁"),
    SCORE_NOT_EXIST(4001, "评审结果不存在"),
    NO_RESULT(5002, "没有结果"),
    CONTEST_NOT_EXIST(6001, "比赛不存在"),
    CONTEST_ERROR(6002, "比赛操作失败"),
    // 时间相关错误细化
    SIGN_UP_TIME_NOT_STARTED(6003, "报名时间尚未开始"),
    SIGN_UP_TIME_EXPIRED(6004, "报名时间已过期"),
    DATE_FORMAT_ERROR(6005, "时间格式错误"),
    DEP_NOT_EXIST(6006, "该部门不存在"),
    LIMIT_ERROR(6007, "团队人数设置错误"),
    REVIEW_SETTINGS_ERROR(6008, "评审关系设置错误"),
    SCHEMA_ERROR(6009, "表单未设置"),
    WORK_NOT_EXIST(6010, "作品不存在"),
    ASSIGN_ERROR(6011, "无法分配评委，存在未审批或审批未通过的作品"),
    CONTEST_NOT_REVIEWED(6012, "比赛未审批"),
    ALREADY_SIGNED_UP_CONTEST(6013, "您已报名该比赛"),
    TEAM_NAME_EMPTY(6014, "团队名称不能为空"),
    TEAM_MEMBERS_EMPTY(6015, "团队成员不能为空"),
    TEAM_SAVE_FAILED(6016, "保存团队信息失败"),
    USER_NOT_EXIST(7001, "用户不存在"),
    USER_EXIST(7002, "用户已存在"),
    FILE_NOT_EXIST(8001, "文件不存在"),
    FILE_EXPIRED_ERROR(8002, "文件已过期，请重新提交"),
    NOTICE_ERROR(9001, "公告发布失败"),
    NOTICE_NOT_EXIST(9002, "公告不存在"),
    IMPORT_ERROR(10000, "导入失败"),
    INVALID_CAPTCHA(11001, "验证码过期"),
    INCORRECT_CAPTCHA(11002, "验证码错误"),
    CAPTCHA_NOT_EXIST(11003, "请输入验证码");

    private final Integer errCode;
    private final String errMsg;
}
