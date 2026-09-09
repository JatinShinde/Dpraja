package com.spdpboss.repository;

import com.spdpboss.model.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {
    // This finds a specific result for a specific game on a specific day
    GameHistory findByGameNameAndResultDate(String gameName, LocalDate resultDate);
    GameHistory findByGameNameIgnoreCaseAndResultDate(String gameName, LocalDate resultDate);
    
    // This fetches the whole chart for a game to show in your HTML
    List<GameHistory> findByGameNameOrderByResultDateDesc(String gameName);
    List<GameHistory> findByGameNameIgnoreCaseOrderByResultDateDesc(String gameName);
}