package fun.sast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.sast.entity.Notice;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.*;

@Repository
public interface NoticeMapper extends BaseMapper<Notice> {
    @Insert("INSERT INTO notice (com_id, title, content, role, time) " +
            "VALUES (#{comId}, #{title}, #{content}, #{role}, #{time})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notice notice);

    @Update("UPDATE notice SET title = #{title}, content = #{content}, " +
            "role = #{role}, time = #{time} WHERE id = #{id}")
    int update(Notice notice);

    @Delete("DELETE FROM notice WHERE id = #{id}")
    int delete(@Param("id") Integer id);
}
