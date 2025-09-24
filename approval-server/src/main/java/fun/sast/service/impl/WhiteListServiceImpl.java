package fun.sast.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fun.sast.Exception.BaseException;
import fun.sast.entity.Competition;
import fun.sast.entity.WhiteList;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.mapper.UserMapper;
import fun.sast.mapper.WhiteListMapper;
import fun.sast.service.WhiteListService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class WhiteListServiceImpl implements WhiteListService {
    @Autowired private WhiteListMapper whiteListMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private CompetitionMapper competitionMapper;
    @Autowired private WhiteListUtilForImpl excelUtil;

    @Override
    public void operateWhiteList(Long comId, Boolean isWhiteList, MultipartFile file) {
        log.info("=== 开始处理白名单操作 ===");
        log.info("入参详情：comId={}, isWhiteList={}", comId, isWhiteList);
        if (file != null) {
            log.info(
                    "文件信息：文件名={}, 文件大小={}字节, 文件类型={}",
                    file.getOriginalFilename(),
                    file.getSize(),
                    file.getContentType());
        } else {
            log.warn("文件参数为null"); // 重点关注：若此处打印，说明前端未传文件
        }

        Competition competition = competitionMapper.selectById(comId);
        if (comId == null || competition == null) {
            log.error("比赛校验失败：comId={}，对应的比赛不存在", comId);
            throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
        }
        if (isWhiteList) {
            log.info("当前操作：启用白名单，开始校验文件");

            if (file == null || file.isEmpty()) {
                log.error(
                        "文件校验失败：文件不存在或为空（file={}, isEmpty={}",
                        file,
                        file == null ? "null" : file.isEmpty());
                throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
            }
            // 校验文件格式
            String fileName = file.getOriginalFilename();
            if (fileName == null || !(fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
                log.error("文件格式校验失败：文件名={}，不支持的格式", fileName);
                throw new BaseException(ErrorEnum.INVALID_FILE_TYPE_ERROR);
            }
            log.info("文件校验通过，准备解析Excel");
        } else {
            log.info("当前操作：取消白名单，无需文件");
        }

        try {
            // 取消白名单
            if (!isWhiteList) {
                // 删除白名单
                QueryWrapper<WhiteList> deleteWrapper = new QueryWrapper<>();
                deleteWrapper.eq("com_id", comId);
                whiteListMapper.delete(deleteWrapper);
                log.info("已取消比赛{}的白名单", comId);

                // 更新比赛表白名单
                competition.setIsWhiteList(false);
                competitionMapper.updateById(competition);
                return;
            }

            // 启用白名单
            excelUtil.setParams(comId, isWhiteList);

            EasyExcel.read(file.getInputStream(), excelUtil)
                    .sheet()
                    .headRowNumber(0) // 0表示没有表头，从第1行开始读
                    .doRead();
            // 更新比赛表白名单
            competition.setIsWhiteList(true);
            competitionMapper.updateById(competition);
            log.info("已启用比赛{}的白名单", comId);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("解析Excel文件失败", e);
            throw new BaseException(ErrorEnum.EXCEL_FAILED);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("处理白名单失败", e);
            throw new BaseException(ErrorEnum.WHITELIST_FAILED);
        }
    }
}
