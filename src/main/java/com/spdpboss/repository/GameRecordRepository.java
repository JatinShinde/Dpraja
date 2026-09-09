package com.spdpboss.repository;

import com.spdpboss.model.GameRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface GameRecordRepository extends JpaRepository<GameRecord, Long> {
    // This custom method allows you to find history for a specific game (e.g., KALYAN)
    List<GameRecord> findByGameNameOrderByDateDesc(String gameName);
    
    // Find record by game name and specific date
    GameRecord findByGameNameAndDate(String gameName, LocalDate date);
}