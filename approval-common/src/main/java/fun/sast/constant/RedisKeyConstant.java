package fun.sast.constant;

public class RedisKeyConstant {

    public static final String TOKEN = "TOKEN:";
    public static final String WORK_FILE = "WORK_FILE:";

    /**
     * 获取TOKEN的Key
     *
     * @param userCode 学号
     * @return key
     */
    public static String getTokenKey(String userCode) {
        return TOKEN + userCode;
    }

    /**
     * 获取文件缓存数据的Key 不包含前缀
     *
     * @param userCode 学号
     * @param input 输入框名
     * @return key
     */
    public static String getWorkFileCacheKey(String userCode, String input) {
        return WORK_FILE + userCode + ":" + input;
    }
}
