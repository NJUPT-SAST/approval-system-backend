package fun.sast.utils;

import java.util.*;

public class CommonUtil {

    private static final Set<String> DISALLOWED_TYPES = Set.of("exe", "html", "htm", "deb", "php");

    /**
     * 根据文件名获取文件类型
     *
     * @param filename 文件名
     * @return typename
     */
    public static String getTypeByFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex < 0 || lastDotIndex == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDotIndex);
    }

    /**
     * 获得八位的UUID
     *
     * @return UUID
     */
    public static String creatShortUUID() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 判断文件类型是否允许上传
     *
     * @param typeName 文件类型名
     * @return 是否允许上传
     */
    public static boolean isAllowUploadType(String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            return false;
        }
        return !DISALLOWED_TYPES.contains(typeName.toLowerCase());
    }
}
