package fun.sast.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fun.sast.Exception.BaseException;
import fun.sast.entity.Competition;
import fun.sast.entity.User;
import fun.sast.entity.WhiteList;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.mapper.UserMapper;
import fun.sast.mapper.WhiteListMapper;
import fun.sast.service.WhiteListService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class WhiteListServiceImpl implements WhiteListService {
    @Autowired private WhiteListMapper whiteListMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private CompetitionMapper competitionMapper;

    @Override
    public void operateWhiteList(Long comId, Boolean isEnable, MultipartFile excelFile) {
        Competition competition = competitionMapper.selectById(comId);
        if (comId == null || competition == null) {
            throw new BaseException(ErrorEnum.UNKNOWN_COMPETITION_ID);
        }
        if (isEnable) {
            if (excelFile == null || excelFile.isEmpty()) {
                throw new BaseException(ErrorEnum.FILE_NOT_EXIST);
            }
            // 校验文件格式
            String fileName = excelFile.getOriginalFilename();
            if (fileName == null || !(fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
                throw new BaseException(ErrorEnum.INVALID_FILE_TYPE_ERROR);
            }
        }

        try {
            // 取消白名单
            if (!isEnable) {
                // 删除白名单
                QueryWrapper<WhiteList> deleteWrapper = new QueryWrapper<>();
                deleteWrapper.eq("com_id", comId);
                whiteListMapper.delete(deleteWrapper);
                log.info("已取消比赛{}的白名单", comId);

                // 更新比赛表白名单
                competition.setIsWhiteList(false);
                competitionMapper.updateById(competition);
            }

            // 启用白名单
            WhiteListExcelUtil excelUtil = new WhiteListExcelUtil();
            excelUtil.setParams(comId, isEnable);

            EasyExcel.read(excelFile.getInputStream(), Map.class, excelUtil)
                    .sheet()
                    .headRowNumber(0) // 0表示没有表头，从第1行开始读
                    .doRead();
            // 更新比赛表白名单
            competition.setIsWhiteList(true);
            competitionMapper.updateById(competition);
            log.info("已启用比赛{}的白名单", comId);
        } catch (IOException e) {
            log.error("解析Excel文件失败", e);
            throw new RuntimeException("解析Excel文件失败" + e.getMessage());
        } catch (Exception e) {
            log.error("处理白名单失败", e);
            throw new RuntimeException("处理白名单失败" + e.getMessage());
        }
    }

    /** 用于解析白名单Excel的工具类 */
    @Slf4j
    @Component
    public static class WhiteListExcelUtil extends AnalysisEventListener<Map<Integer, String>> {

        // 批量存储：每25条数据存储一次
        private static final int BATCH_COUNT = 25;

        // 存储合法学号
        private List<String> validUserCodes = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

        private Long comId;
        private Boolean isEnableWhiteList; // 是否启用白名单

        @Autowired private WhiteListMapper whiteListMapper;
        @Autowired private UserMapper userMapper;

        public void setParams(Long comId, Boolean isEnableWhiteList) {
            this.comId = comId;
            this.isEnableWhiteList = isEnableWhiteList;
        }

        /**
         * 每解析一行Excel数据，就会调用一次
         *
         * @param rowData 一行数据：key=列索引（0表示第1列），value=单元格值（学号）
         * @param context 解析上下文（包含行号等信息）
         */
        @Override
        public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
            // 如果是取消白名单，无需解析Excel，直接返回
            if (!isEnableWhiteList) {
                return;
            }

            String userCode = rowData.get(0); // 学号在第一列（索引0）
            if (userCode != null) {
                userCode = userCode.trim();
            }

            int rowNum = context.readRowHolder().getRowIndex() + 1; // 获取当前行号（从0开始，需要加1）
            if (userCode == null || userCode.isEmpty()) {
                WhiteListExcelUtil.log.warn("第{}行学号为空，已跳过", rowNum);
                return;
            }

            if (!userIsExist(userCode)) {
                throw new BaseException(ErrorEnum.USER_NOT_EXIST);
            }

            validUserCodes.add(userCode);
            if (validUserCodes.size() >= BATCH_COUNT) {
                saveBatchToDB();
                validUserCodes.clear();
            }
        }

        // 检查用户是否存在
        private boolean userIsExist(String userCode) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", userCode);
            return userMapper.exists(queryWrapper);
        }

        // 批量保存到数据库
        private void saveBatchToDB() {
            // 删除该比赛旧的白名单
            QueryWrapper<WhiteList> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("com_id", comId);
            whiteListMapper.delete(deleteWrapper);

            // 插入新的白名单
            WhiteList whiteList = new WhiteList();
            whiteList.setComId(comId);
            whiteList.setUserCodes(validUserCodes);
            whiteListMapper.insert(whiteList);
            WhiteListExcelUtil.log.info("已保存{}条有效学号到数据库", validUserCodes.size());
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            // 存储剩余不足BATCH_COUNT的学号
            if (isEnableWhiteList && !validUserCodes.isEmpty()) {
                saveBatchToDB();
                validUserCodes.clear();
                log.info("所有数据解析完成，已处理剩余{}条有效学号到数据库", validUserCodes.size());
            }
        }
    }
}
