package com.zxcv5595.weather.controller;

import com.zxcv5595.weather.dto.DiaryDto;
import com.zxcv5595.weather.service.DiaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiaryController.class)
class DiaryControllerTest {
    @MockBean
    private DiaryService diaryService;
    @Autowired
    private MockMvc mockMvc;


    @Test
    public void createDiaryTest() throws Exception {
        // given
        LocalDate date = LocalDate.now();
        String text = "오늘의 일기";

        // when
        mockMvc.perform(post("/create/diary")
                        .param("date", date.toString())
                        .content(text)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        verify(diaryService, times(1)).createDiary(date, text);

    }

    @Test
    public void readDiaryTest() throws Exception {
        // given
        LocalDate date = LocalDate.now();
        List<DiaryDto> diaries = Arrays.asList(DiaryDto.builder()
                .date(date)
                .text("오늘의 일기")
                .build());

        when(diaryService.readDiary(date)).thenReturn(diaries);

        // when
        mockMvc.perform(get("/read/diary")
                        .param("date", date.toString()))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value(diaries.get(0).getDate().toString()))
                .andExpect(jsonPath("$[0].text").value(diaries.get(0).getText()));

        verify(diaryService, times(1)).readDiary(date);
    }

    @Test
    public void readDiariesTest() throws Exception {
        // given
        LocalDate date = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<DiaryDto> diaries = Arrays.asList(DiaryDto.builder()
                .date(date)
                .text("지난 일주일 동안의 일기")
                .build());


        when(diaryService.readDiaries(startDate, endDate)).thenReturn(diaries);

        // when
        mockMvc.perform(get("/read/diaries")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value(diaries.get(0).getDate().toString()))
                .andExpect(jsonPath("$[0].text").value(diaries.get(0).getText()));

        verify(diaryService, times(1)).readDiaries(startDate, endDate);
    }

    @Test
    public void updateDiaryTest() throws Exception {
        // given
        LocalDate date = LocalDate.now();
        String updatedText = "수정된 일기";

        // when
        mockMvc.perform(put("/update/diary")
                        .param("date", date.toString())
                        .content(updatedText)
                        .contentType(MediaType.TEXT_PLAIN))

                // then
                .andExpect(status().isOk())
                .andDo(print());

        verify(diaryService, times(1)).updateDiary(date, updatedText);
    }

    @Test
    public void deleteDiaryTest() throws Exception {
        // given
        LocalDate date = LocalDate.now();

        // when
        mockMvc.perform(delete("/delete/diary")
                        .param("date", date.toString()))

                // then
                .andExpect(status().isOk());

        verify(diaryService, times(1)).deleteDiary(date);
    }


}