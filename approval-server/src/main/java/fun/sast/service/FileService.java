package fun.sast.service;

import fun.sast.entity.User;

public interface FileService {
    String getDownloadCertificate(User user, String url);
}
