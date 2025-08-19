package fun.sast.service;

import fun.sast.entity.User;

public interface FileService {
    /**
     * 获取下载证书的url
     *
     * @param url 文件存储的url，如http://baiyaoshi.oss-cn-hangzhou.aliyuncs.com/文本.txt,在这里实现身份判断
     * @return 可以直接用于下载的凭证
     */
    String getDownloadCertificate(String url);

    /**
     * 处理提交文件
     *
     * @param user 提交者
     * @param comId 比赛的id
     * @param content 提交内容
     * @param title
     */
    void processSubmissionFiles(User user, Long comId, String content, String title);
}
