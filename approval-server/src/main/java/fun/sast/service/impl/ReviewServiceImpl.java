package fun.sast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import fun.sast.Exception.BaseException;
import fun.sast.entity.Competition;
import fun.sast.entity.Review;
import fun.sast.entity.User;
import fun.sast.entity.Work;
import fun.sast.enums.ErrorEnum;
import fun.sast.enums.ReviewStatusEnum;
import fun.sast.enums.UserRoleEnum;
import fun.sast.interceptor.UserInterceptor;
import fun.sast.mapper.CompetitionMapper;
import fun.sast.mapper.ReviewMapper;
import fun.sast.mapper.UserMapper;
import fun.sast.mapper.WorkMapper;
import fun.sast.service.CompetitionService;
import fun.sast.service.ReviewService;
import fun.sast.service.WorkService;
import fun.sast.vo.AccountImportVO;
import fun.sast.vo.CompetitionList;
import fun.sast.vo.CompetitionListVO;
import fun.sast.vo.WorkReviewListVO;
import fun.sast.vo.WorkReviewVO;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final CompetitionMapper competitionMapper;
    private final WorkMapper workMapper;
    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;
    private final WorkService workService;
    private final CompetitionService competitionService;
    private static final int PAGE_SIZE = 10;

    @Override
    public List<AccountImportVO> importAccount(String depId, MultipartFile file) {
        List<AccountImportVO> accounts = new ArrayList<>();

        if (file != null && !file.isEmpty()) {
            try {
                List<StudentInfo> students = parseExcelFile(file);
                accounts = createUsersAndGenerateAccounts(students, depId);
            } catch (IOException e) {
                throw new BaseException(ErrorEnum.IMPORT_ERROR);
            }
        }

        return accounts;
    }

    private static class StudentInfo {
        String code;
        String name;

        StudentInfo(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    private List<StudentInfo> parseExcelFile(MultipartFile file) throws IOException {
        List<StudentInfo> students = new ArrayList<>();

        Workbook workbook = null;
        try {
            String fileName = file.getOriginalFilename();
            if (fileName != null && fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else if (fileName != null && fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else {
                throw new BaseException(ErrorEnum.INVALID_FILE_TYPE_ERROR);
            }

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell codeCell = row.getCell(0);
                    Cell nameCell = row.getCell(1);

                    if (codeCell != null && nameCell != null) {
                        String studentCode = getCellValueAsString(codeCell);
                        String studentName = getCellValueAsString(nameCell);

                        if (studentCode != null
                                && !studentCode.trim().isEmpty()
                                && studentName != null
                                && !studentName.trim().isEmpty()) {
                            students.add(new StudentInfo(studentCode.trim(), studentName.trim()));
                        }
                    }
                }
            }
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }

        return students;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private List<AccountImportVO> createUsersAndGenerateAccounts(
            List<StudentInfo> students, String depId) {
        List<AccountImportVO> accounts = new ArrayList<>();
        User currentUser = UserInterceptor.userHolder.get();
        Long operatorId = currentUser != null ? currentUser.getId().longValue() : 0L;
        Integer departmentId = depId != null ? Integer.parseInt(depId) : null;

        for (StudentInfo student : students) {
            String randomSuffix = generateRandomPassword();
            String password = student.code + randomSuffix;
            String salt = generateSalt();
            String encryptedPassword = encryptPassword(password, salt);

            User user = new User();
            user.setDepId(departmentId);
            user.setName(student.name);
            user.setCode(student.code);
            user.setPassword(encryptedPassword);
            user.setSalt(salt);
            user.setRole(UserRoleEnum.STUDENT.getRole());
            user.setMajor("");
            user.setContact("");
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setCreateUser(operatorId);
            user.setUpdateUser(operatorId);

            userMapper.insert(user);
            accounts.add(new AccountImportVO(password, student.code));
        }

        return accounts;
    }

    private String generateSalt() {
        return generateRandomString(16);
    }

    private String encryptPassword(String password, String salt) {
        return DigestUtils.md5DigestAsHex((password + salt).getBytes());
    }

    private String generateRandomPassword() {
        return generateRandomString(6);
    }

    private String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(chars.charAt(random.nextInt(chars.length())));
        }

        return result.toString();
    }

    @Override
    public void uploadReview(String id, boolean accept, String opinion) {
        User user = UserInterceptor.userHolder.get();
        if (user == null
                || !(UserRoleEnum.JUDGE.getRole().equals(user.getRole())
                        || UserRoleEnum.ADMIN.getRole().equals(user.getRole()))) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }

        Work work = workMapper.selectById(id);
        if (work == null) {
            throw new BaseException(ErrorEnum.WORK_NOT_EXIST);
        }

        LambdaQueryWrapper<Review> queryWrapper =
                new LambdaQueryWrapper<Review>().eq(Review::getId, id);
        Review existingReview = reviewMapper.selectOne(queryWrapper);

        if (existingReview != null) {
            existingReview.setAccept(accept);
            existingReview.setOpinion(opinion);
            existingReview.setJudgeId(user.getId());
            existingReview.setUpdateTime(LocalDateTime.now());
            existingReview.setUpdateUser(user.getId().longValue());
            reviewMapper.updateById(existingReview);
        } else {
            Review review = new Review();
            review.setId(id);
            review.setAccept(accept);
            review.setOpinion(opinion);
            review.setJudgeId(user.getId());
            review.setComId(work.getComId().intValue());
            review.setCode(work.getUserCode());
            review.setUserId(getUserIdByUserCode(work.getUserCode()));
            review.setCreateTime(LocalDateTime.now());
            review.setUpdateTime(LocalDateTime.now());
            review.setCreateUser(user.getId().longValue());
            review.setUpdateUser(user.getId().longValue());
            reviewMapper.insert(review);
        }
    }

    @Override
    public CompetitionListVO getCompetitionList(int page) {
        User user = UserInterceptor.userHolder.get();
        if (user == null
                || !(UserRoleEnum.JUDGE.getRole().equals(user.getRole())
                        || UserRoleEnum.ADMIN.getRole().equals(user.getRole()))) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }

        Page<Competition> pageObj = new Page<>(page, PAGE_SIZE);
        LambdaQueryWrapper<Competition> queryWrapper =
                new LambdaQueryWrapper<Competition>()
                        .eq(Competition::getIsReview, ReviewStatusEnum.PENDING.getCode())
                        .orderByDesc(Competition::getCreateTime);

        IPage<Competition> competitionPage = competitionMapper.selectPage(pageObj, queryWrapper);

        List<CompetitionList> list =
                competitionPage.getRecords().stream()
                        .map(
                                competition -> {
                                    return new CompetitionList()
                                            .setId(competition.getId())
                                            .setTitle(competition.getName())
                                            .setTotalNum(
                                                    competitionService.getTotalParticipants(
                                                            Long.valueOf(competition.getId())))
                                            .setCompletedNum(
                                                    workService
                                                            .countByComId(
                                                                    Long.valueOf(
                                                                            competition.getId()))
                                                            .toString())
                                            .setStartDate(competition.getRegBeginTime())
                                            .setEndDate(competition.getRegEndTime());
                                })
                        .collect(Collectors.toCollection(ArrayList::new));

        int pages = (int) competitionPage.getPages();
        boolean isFirst = page <= 1;
        boolean isLast = pages == 0 || page >= pages;

        return new CompetitionListVO(
                (int) competitionPage.getTotal(),
                list,
                page,
                String.valueOf(PAGE_SIZE),
                pages,
                isFirst,
                isLast);
    }

    @Override
    public WorkReviewListVO getPragramList(String comId, Integer page) {
        User user = UserInterceptor.userHolder.get();
        if (user == null
                || !(UserRoleEnum.JUDGE.getRole().equals(user.getRole())
                        || UserRoleEnum.ADMIN.getRole().equals(user.getRole()))) {
            throw new BaseException(ErrorEnum.NO_ROLE);
        }

        if (page == null) {
            page = 1;
        }

        Page<Work> pageObj = new Page<>(page, PAGE_SIZE);
        LambdaQueryWrapper<Work> queryWrapper =
                new LambdaQueryWrapper<Work>()
                        .eq(Work::getComId, Long.valueOf(comId))
                        .orderByDesc(Work::getCreateTime);

        IPage<Work> workPage = workMapper.selectPage(pageObj, queryWrapper);

        List<WorkReviewVO> list =
                workPage.getRecords().stream()
                        .map(
                                work -> {
                                    Review review =
                                            reviewMapper.selectOne(
                                                    new LambdaQueryWrapper<Review>()
                                                            .eq(
                                                                    Review::getId,
                                                                    work.getId().toString()));

                                    return new WorkReviewVO()
                                            .setId(work.getId().intValue())
                                            .setTitle(work.getWorkName())
                                            .setPass(review != null ? review.isAccept() : false)
                                            .setOpinion(review != null ? review.getOpinion() : "");
                                })
                        .collect(Collectors.toCollection(ArrayList::new));

        int pages = (int) workPage.getPages();
        boolean isFirst = page <= 1;
        boolean isLast = pages == 0 || page >= pages;

        return new WorkReviewListVO(
                (int) workPage.getTotal(),
                list,
                page,
                String.valueOf(PAGE_SIZE),
                pages,
                isFirst,
                isLast);
    }

    private Integer getUserIdByUserCode(String userCode) {
        LambdaQueryWrapper<User> queryWrapper =
                new LambdaQueryWrapper<User>().eq(User::getCode, userCode);
        User user = userMapper.selectOne(queryWrapper);
        return user != null ? user.getId() : null;
    }
}
