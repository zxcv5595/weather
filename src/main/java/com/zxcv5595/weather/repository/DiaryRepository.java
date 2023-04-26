package com.zxcv5595.weather.repository;

import com.zxcv5595.weather.domain.Diary;
import com.zxcv5595.weather.dto.DiaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary,Integer> {
    List<Diary> findAllByDate(LocalDate date);
    List<Diary> findAllByDateBetween(LocalDate startDate,LocalDate endDate);

    Optional<Diary>  findFirstByDate(LocalDate date);
    @Transactional
    void deleteAllByDate(LocalDate date);
}
