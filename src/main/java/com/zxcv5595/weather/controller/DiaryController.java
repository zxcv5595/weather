package com.zxcv5595.weather.controller;

import com.zxcv5595.weather.dto.CreatDiary;
import com.zxcv5595.weather.dto.DiaryDto;
import com.zxcv5595.weather.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "DiaryController", description = "날씨 일기를 CRUD 관련 Controller 입니다.")
@RestController
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @Operation(summary = "일기 텍스트와 날씨를 이용해 DB에 일기 저장")
    @PostMapping("/create/diary")
    public void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
            ,@RequestBody @Valid  String text) {

        diaryService.createDiary(date,text);

    }

    @Operation(summary = "선택한 날짜의 모든 일기를 가져옵니다.")
    @GetMapping("/read/diary")
    public List<DiaryDto> readDiary(@RequestParam
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                    @Parameter(description = "선택할 날짜", example = "2023-01-01") LocalDate date) {
        return diaryService.readDiary(date);
    }

    @Operation(summary = "선택한 기간중의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diaries")
    public List<DiaryDto> readDiaries(@RequestParam
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                      @Parameter(description = "조회할 기간의 첫번째 날", example = "2023-01-01") LocalDate startDate,
                                      @RequestParam
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                      @Parameter(description = "조회할 기간의 마지막 날", example = "2023-02-01") LocalDate endDate) {

        return diaryService.readDiaries(startDate, endDate);
    }

    @Operation(summary = "선택한 날짜의 첫번째 작성된 일기 내용을 수정합니다.")
    @PutMapping("update/diary")
    public void updateDiary(@RequestParam
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            @Parameter(description = "수정할 날짜", example = "2023-01-01") LocalDate date,
                            @RequestBody
                            @Parameter(description = "일기 내용", example = "일기를 작성합니다.") String text) {
        diaryService.updateDiary(date, text);
    }

    @Operation(summary = "선택한 날짜의 모든 일기를 삭제합니다.")
    @DeleteMapping("delete/diary")
    public void updateDiary(@RequestParam
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            @Parameter(description = "선택할 날짜", example = "2023-01-01") LocalDate date) {
        diaryService.deleteDiary(date);
    }
}
