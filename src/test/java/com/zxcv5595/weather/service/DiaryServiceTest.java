package com.zxcv5595.weather.service;

import com.zxcv5595.weather.Exception.DiaryException;
import com.zxcv5595.weather.domain.DateWeather;
import com.zxcv5595.weather.domain.Diary;
import com.zxcv5595.weather.dto.DiaryDto;
import com.zxcv5595.weather.repository.DateWeatherRepository;
import com.zxcv5595.weather.repository.DiaryRepository;
import com.zxcv5595.weather.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiaryServiceTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private DateWeatherRepository dateWeatherRepository;


    @InjectMocks
    private DiaryService diaryService;

    @Captor
    private ArgumentCaptor<Diary> diaryCaptor;

    @Test
    @DisplayName("새로운 다이어리 생성")
    void createDiaryTest() {
        // given
        String text = "오늘은 좋은 날씨예요.";
        LocalDate today = LocalDate.now();

        // when
        diaryService.createDiary(today, text);

        // then
        verify(diaryRepository, times(1)).save(diaryCaptor.capture());
        Diary capturedDiary = diaryCaptor.getValue();
        assertEquals(today, capturedDiary.getDate());
        assertEquals(text, capturedDiary.getText());
    }

    @Test
    @DisplayName("새로운 다이어리 생성 - 너무 먼 미래의 날짜")
    void createDiaryInvalidDate() {
        // given
        String text = "오늘은 좋은 날씨예요.";
        LocalDate invalidDate = LocalDate.of(5000, 1, 1);

        // when
        DiaryException exception = assertThrows(DiaryException.class,
                () -> diaryService.createDiary(invalidDate, text));
        // then
        assertEquals(ErrorCode.INVALID_DATE, exception.getErrorCode());
    }

    @Test
    @DisplayName("DB에서 값 찾아오기")
    void testGetDateWeatherFromDB() {
        DateWeather expected = DateWeather.builder()
                .date(LocalDate.of(2022, 4, 28))
                .icon("01d")
                .weather("Clear")
                .temperature(17.0)
                .build();

        when(dateWeatherRepository.findAllByDate(
                LocalDate.of(2022, 4, 28)))
                .thenReturn(Collections.singletonList(expected));
        DateWeather result = diaryService.getDateWeather(
                LocalDate.of(2022, 4, 28));
        assertEquals(expected, result);
    }


    @Test
    @DisplayName("json 파싱")
    void testParseWeatherSuccess() {
        String jsonString = "{\"weather\":[{\"main\":\"Clear\",\"icon\":\"01d\"}],\"main\":{\"temp\":17.0}}";
        Map<String, Object> expected = new HashMap<>();
        expected.put("main", "Clear");
        expected.put("icon", "01d");
        expected.put("temp", 17.0);

        Map<String, Object> result = diaryService.parseWeather(jsonString);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("해당 날짜 다이어리 불러오기")
    void readDiaryTest() {
        // given
        LocalDate date = LocalDate.now();
        List<Diary> diaries = new ArrayList<>();
        diaries.add(Diary.builder().date(date).text("Some text").build());
        given(diaryRepository.findAllByDate(date)).willReturn(diaries);

        // when
        List<DiaryDto> diaryDtos = diaryService.readDiary(date);

        // then
        assertEquals("Some text", diaryDtos.get(0).getText());
    }

    @Test
    @DisplayName("시작날짜 - 끝날짜, 다이어리 불러오기")
    void readDiariesTest() {
        // given
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 1, 31);
        List<Diary> diaries = new ArrayList<>();
        diaries.add(Diary.builder().date(startDate).text("Some text 1").build());
        diaries.add(Diary.builder().date(endDate).text("Some text 2").build());
        given(diaryRepository.findAllByDateBetween(startDate, endDate)).willReturn(diaries);

        // when
        List<DiaryDto> diaryDtos = diaryService.readDiaries(startDate, endDate);

        // then
        assertEquals(2,diaryDtos.size());
        assertEquals("Some text 1", diaryDtos.get(0).getText());
        assertEquals("Some text 2", diaryDtos.get(1).getText());
    }

    @Test
    @DisplayName("다이어리 수정하기")
    void updateDiaryTest() {
        // given
        LocalDate date = LocalDate.of(2022, 1, 1);
        Diary diary = Diary.builder().date(date).text("Some text").build();
        given(diaryRepository.findFirstByDate(date)).willReturn(Optional.of(diary));
        given(diaryRepository.save(diary)).willReturn(diary);

        // when
        diaryService.updateDiary(date, "New text");

        // then
        assertEquals("New text",diary.getText());
    }

    @Test
    @DisplayName("해당 날짜 다이어리 삭제하기")
    void deleteDiaryTest() {
        // given
        LocalDate date = LocalDate.of(2022, 1, 1);

        // when
        diaryService.deleteDiary(date);

        // then
        verify(diaryRepository, times(1)).deleteAllByDate(date);
    }

}

