package fun.sast.service.impl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import fun.sast.Exception.BaseException;
import fun.sast.entity.User;
import fun.sast.entity.WhiteList;
import fun.sast.enums.ErrorEnum;
import fun.sast.mapper.UserMapper;
import fun.sast.mapper.WhiteListMapper;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** 用于解析白名单Excel的工具类 */
@Slf4j
@Component
public class WhiteListUtilForImpl extends AnalysisEventListener<Map<Integer, String>> {

    // 批量存储：每25条数据存储一次
    private static final int BATCH_COUNT = 25;

    // 存储合法学号
    private List<String> validUserCodes = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    private Long comId;
    private Boolean isWhiteList; // 是否启用白名单

    private WhiteListMapper whiteListMapper;
    private UserMapper userMapper;

    @Autowired
    public WhiteListUtilForImpl(WhiteListMapper whiteListMapper, UserMapper userMapper) {
        this.whiteListMapper = whiteListMapper;
        this.userMapper = userMapper;
        log.info(
                "WhiteListUtilForImpl初始化：whiteListMapper={}, userMapper={}",
                whiteListMapper != null ? "注入成功" : "注入失败（null）",
                userMapper != null ? "注入成功" : "注入失败（null）");
    }

    public void setParams(Long comId, Boolean isWhiteList) {
        this.comId = comId;
        this.isWhiteList = isWhiteList;
        log.info("工具类参数设置：comId={}, isWhiteList={}", comId, isWhiteList);
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
        if (!isWhiteList) {
            return;
        }

        String userCode = rowData.get(0); // 学号在第一列（索引0）
        if (userCode != null) {
            userCode = userCode.trim();
        }

        int rowNum = context.readRowHolder().getRowIndex() + 1; // 获取当前行号（从0开始，需要加1）

        log.info("解析Excel第{}行：原始数据={}, 处理后学号={}", rowNum, rowData, userCode);

        if (userCode == null || userCode.isEmpty()) {
            WhiteListUtilForImpl.log.warn("第{}行学号为空，已跳过", rowNum);
            return;
        }

        log.info("第{}行：开始校验学号{}是否存在", rowNum, userCode);
        if (!userIsExist(userCode)) {
            log.error("第{}行：学号{}不存在于user表，抛出异常", rowNum, userCode);
            throw new BaseException(ErrorEnum.USER_NOT_EXIST);
        }
        log.info("第{}行：学号{}校验通过，加入缓存", rowNum, userCode);

        validUserCodes.add(userCode);
        if (validUserCodes.size() >= BATCH_COUNT) {
            log.info("缓存达到批量阈值（{}条），准备入库", BATCH_COUNT);
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
        try {
            log.info("开始批量入库：比赛ID={}, 待入库学号数量={}", comId, validUserCodes.size());
            // 删除该比赛旧的白名单
            QueryWrapper<WhiteList> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("com_id", comId);
            int deleteCount = whiteListMapper.delete(deleteWrapper);
            log.info("删除比赛{}旧白名单数据{}条", comId, deleteCount);

            // 插入新的白名单
            WhiteList whiteList = new WhiteList();
            whiteList.setComId(comId);
            whiteList.setUserCodes(validUserCodes);
            WhiteListUtilForImpl.log.info("已保存{}条有效学号到数据库", validUserCodes.size());
            int insertCount = whiteListMapper.insert(whiteList);
            log.info("插入新白名单数据{}条（实际插入行数={}）", validUserCodes.size(), insertCount);
        } catch (Exception e) {
            log.error("批量入库失败：比赛ID={}, 学号列表={}", comId, validUserCodes, e);
            throw e;
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 存储剩余不足BATCH_COUNT的学号
        if (isWhiteList && !validUserCodes.isEmpty()) {
            log.info("Excel解析完成，处理剩余{}条学号", validUserCodes.size());
            saveBatchToDB();
            validUserCodes.clear();
            log.info("所有数据解析完成，已处理剩余{}条有效学号到数据库", validUserCodes.size());
        } else {
            log.info("Excel解析完成，无剩余数据需处理");
        }
    }
}
