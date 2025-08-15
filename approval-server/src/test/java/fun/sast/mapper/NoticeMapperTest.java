package fun.sast.mapper;

import fun.sast.entity.Notice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class NoticeMapperTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NoticeMapper noticeMapper;

    @Test
    void testSaveAndFindNotice() {
        // Given
        Notice notice = new Notice();
        notice.setTitle("Test Notice");
        notice.setContent("Test Content");
        notice.setComId(1);

        // When
        Notice savedNotice = noticeMapper.save(notice);
        Optional<Notice> foundNotice = noticeMapper.findById(savedNotice.getId());

        // Then
        assertThat(foundNotice).isPresent();
        assertThat(foundNotice.get().getTitle()).isEqualTo("Test Notice");
        assertThat(foundNotice.get().getContent()).isEqualTo("Test Content");
    }

    @Test
    void testFindByComId() {
        // Given
        Notice notice1 = new Notice();
        notice1.setTitle("Notice 1");
        notice1.setComId(1);

        Notice notice2 = new Notice();
        notice2.setTitle("Notice 2");
        notice2.setComId(1);

        Notice notice3 = new Notice();
        notice3.setTitle("Notice 3");
        notice3.setComId(2);

        entityManager.persistAndFlush(notice1);
        entityManager.persistAndFlush(notice2);
        entityManager.persistAndFlush(notice3);

        // When
        List<Notice> notices = noticeMapper.findByComId(1);

        // Then
        assertThat(notices).hasSize(2);
        assertThat(notices).extracting(Notice::getTitle)
                .containsExactlyInAnyOrder("Notice 1", "Notice 2");
    }
}
