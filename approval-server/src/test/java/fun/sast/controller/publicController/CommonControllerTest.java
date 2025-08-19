package fun.sast.controller.publicController;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fun.sast.entity.Notice;
import fun.sast.service.NoticeService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CommonControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private NoticeService noticeService;

    @Test
    void testNoticeList_withValidId_shouldReturnNoticeList() throws Exception {
        // Given
        String comId = "1";
        Notice notice1 = new Notice();
        notice1.setId(1);
        notice1.setTitle("Notice 1");
        notice1.setContent("Content 1");

        Notice notice2 = new Notice();
        notice2.setId(2);
        notice2.setTitle("Notice 2");
        notice2.setContent("Content 2");

        List<Notice> notices = Arrays.asList(notice1, notice2);

        when(noticeService.getNotices(comId)).thenReturn(notices);

        // When & Then
        mockMvc.perform(get("/com/notice/list").param("id", comId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].title").value("Notice 1"))
                .andExpect(jsonPath("$.data[1].title").value("Notice 2"));

        verify(noticeService).getNotices(comId);
    }

    @Test
    void testNoticeList_withMissingId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/com/notice/list"))
                .andExpect(status().isOk()) // 全局异常处理后返回200
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errCode").value(1000));
    }

    @Test
    void testNoticeList_withEmptyId_shouldReturnSuccessWithEmptyList() throws Exception {
        when(noticeService.getNotices("")).thenReturn(List.of());

        mockMvc.perform(get("/com/notice/list").param("id", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}
