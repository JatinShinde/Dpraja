package com.spdpboss.repository;

import com.spdpboss.model.GameRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface GameRecordRepository extends JpaRepository<GameRecord, Long> {
    List<GameRecord> findByGameNameOrderByDateDesc(String gameName);
    GameRecord findByGameNameAndDate(String gameName, LocalDate date);
    List<GameRecord> findAllByGameNameAndDate(String gameName, LocalDate date);
}