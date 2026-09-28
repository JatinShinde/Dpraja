package com.spdpboss.repository;

import com.spdpboss.model.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {
    // Find specific result(s) for a game on a specific day
    GameHistory findByGameNameAndResultDate(String gameName, LocalDate resultDate);
    GameHistory findByGameNameIgnoreCaseAndResultDate(String gameName, LocalDate resultDate);
    List<GameHistory> findAllByGameNameIgnoreCaseAndResultDate(String gameName, LocalDate resultDate);
    
    // Fetch whole chart for a game
    List<GameHistory> findByGameNameOrderByResultDateDesc(String gameName);
    List<GameHistory> findByGameNameIgnoreCaseOrderByResultDateDesc(String gameName);
}