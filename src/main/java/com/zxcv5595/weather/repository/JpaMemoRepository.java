package com.zxcv5595.weather.repository;

import com.zxcv5595.weather.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaMemoRepository extends JpaRepository<Memo, Integer> {
}
