package fun.sast.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fun.sast.Exception.BaseException;
import fun.sast.entity.File;
import fun.sast.entity.User;
import fun.sast.entity.Work;
import fun.sast.enums.ErrorEnum;
import fun.sast.enums.UserRoleEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.mapper.FileMapper;
import fun.sast.mapper.WorkMapper;
import fun.sast.service.FileService;
import fun.sast.utils.COSUtil;
import fun.sast.vo.WorkOutPutVO;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileMapper fileMapper;
    private final WorkMapper workMapper;
    private final COSUtil cosUtil;

    /**
     * @param url
     *     文件存储的url，如https://mock-bucket.cos.ap-nanjing.myqcloud.com//list/list2/text2.txt,在这里实现身份判断
     * @return 可以直接用于下载的凭证
     */
    @Override
    public String getDownloadCertificate(String url) {

        // 获取user身份信息
        User user = UserInterceptor.userHolder.get();

        if (user == null) {
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }

        // 解码
        url = URLDecoder.decode(url, StandardCharsets.UTF_8);

        // 判断url是否合法
        if (!cosUtil.isLegalCOSUrl(url)) {
            throw new BaseException(ErrorEnum.INVALID_URL_ERROR);
        }
        String cosCert = cosUtil.getDownloadCertificate(url);
        // 提取 objectKey 对比数据库看有没有文件
        String objectKey = cosUtil.extractObjectKey(cosCert);
        if (objectKey == null) {
            throw new BaseException(ErrorEnum.INVALID_URL_ERROR);
        }

        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", objectKey);
        File file = fileMapper.selectOne(queryWrapper);

        if (file == null) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }

        // 权限控制
        if (user.getRole().equals(UserRoleEnum.TOURIST.getRole())) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }

        if (user.getRole().equals(UserRoleEnum.STUDENT.getRole())
                && !user.getCode().equals(file.getUserCode())) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }

        return cosUtil.getDownloadCertificate(url);
    }

    @Override
    public List<WorkOutPutVO> exportWork(Long comId) {
        QueryWrapper<Work> workQueryWrapper = new QueryWrapper<>();
        workQueryWrapper.select("id", "work_name").eq("com_id", comId);
        List<Work> workList = workMapper.selectList(workQueryWrapper);
        ArrayList<WorkOutPutVO> outputs = new ArrayList<>();
        if (workList.isEmpty()) {
            throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
        }
        for (Work work : workList) {
            WorkOutPutVO workOutput = new WorkOutPutVO();
            workOutput.setName(work.getWorkName());
            workOutput.setId(work.getId());
            outputs.add(workOutput);
        }

        return outputs;
    }

    @Override
    public void exportWorkExcel(Long comId, HttpServletResponse response) {
        List<WorkOutPutVO> dataList = exportWork(comId);

        List<List<String>> head = new ArrayList<>();
        head.add(Collections.singletonList("作品ID"));
        head.add(Collections.singletonList("作品名称"));
        head.add(Collections.singletonList("评委学号(在此列及以后填写)"));

        List<List<Object>> data = new ArrayList<>();
        for (WorkOutPutVO vo : dataList) {
            List<Object> row = new ArrayList<>();
            row.add(vo.getId());
            row.add(vo.getName());
            row.add(""); // 评委学号列留空
            data.add(row);
        }

        try {
            EasyExcel.write(response.getOutputStream()).head(head).sheet("作品数据").doWrite(data);
        } catch (IOException e) {
            throw new RuntimeException("导出作品数据失败", e);
        }
    }
}
