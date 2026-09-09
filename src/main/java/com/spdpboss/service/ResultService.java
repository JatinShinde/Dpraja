package com.spdpboss.service;

import com.spdpboss.model.Result;
import com.spdpboss.model.ChartData;
import com.spdpboss.model.GameRecord;
import com.spdpboss.model.GameHistory;
import com.spdpboss.repository.ResultRepository;
import com.spdpboss.repository.ChartDataRepository;
import com.spdpboss.repository.GameRecordRepository;
import com.spdpboss.repository.GameHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private GameRecordRepository gameRecordRepository;

    @Autowired
    private GameHistoryRepository historyRepository;

    @Autowired
    private ChartDataRepository chartDataRepository;

    @Autowired
    private BidService bidService;

    // --- Scraper Update Method ---

    /**
     * This method is called by the AutoUpdateService.
     * It syncs results from the external site to your local DB.
     */
    @Transactional
    public void updateFromScraper(String gameName, String openPanel, String openAnk, String closeAnk,
            String closePanel) {
        // 1. Get the Optional result
        Optional<Result> gameOpt = resultRepository.findByGameName(gameName);

        // 2. Extract the Result object (or get null if it doesn't exist)
        Result game = gameOpt.orElse(null);
        String jodi = openAnk + closeAnk;

        if (game != null) {
            // Only update if the result is actually different (saves database performance)
            if (!jodi.equals(game.getJodi()) || !openPanel.equals(game.getOpenPanel())) {

                // A. Update Live Result for Home Page
                game.setOpenPanel(openPanel);
                game.setJodi(jodi);
                game.setClosePanel(closePanel);
                resultRepository.save(game);

                // B. Save to History Table for Jodi/Panel Charts (API/Vite feed)
                GameRecord history = new GameRecord();
                history.setGameName(gameName.toUpperCase().trim());
                LocalDate todayIST = LocalDate.now(ZoneId.of("Asia/Kolkata"));
                history.setDate(todayIST);
                history.setOpenPanel(openPanel);
                history.setJodi(jodi);
                history.setClosePanel(closePanel);

                gameRecordRepository.save(history);

                // C. Save to GameHistory Table (Thymeleaf web template charts)
                String normGameName = gameName.toUpperCase().trim();
                GameHistory gameHistory = historyRepository.findByGameNameAndResultDate(normGameName, todayIST);
                if (gameHistory == null) {
                    gameHistory = new GameHistory();
                    gameHistory.setGameName(normGameName);
                    gameHistory.setResultDate(todayIST);
                }
                gameHistory.setOpenPanel(openPanel);
                gameHistory.setOpenAnk(openAnk);
                gameHistory.setCloseAnk(closeAnk);
                gameHistory.setClosePanel(closePanel);
                gameHistory.setJodi(jodi);
                historyRepository.save(gameHistory);

                // Settle Bids for Customer App
                bidService.settleBidsForMarket(gameName, openPanel, openAnk, closeAnk, closePanel);

                System.out.println("🔄 AUTO-SYNC SUCCESS: " + gameName + " updated to " + openPanel + "-" + jodi + "-"
                        + closePanel);
            }
        } else {
            System.out.println(
                    "⚠️ SCRAPER WARNING: Market '" + gameName + "' found on site but doesn't exist in your DB.");
        }
    }

    /**
     * Syncs results directly from Result Hub Cloud API.
     */
    @Transactional
    public void updateFromResultHub(String marketName, String resultDisplay, String openTime, String closeTime) {
        if (marketName == null || marketName.trim().isEmpty()) {
            return;
        }

        String normName = marketName.trim();
        Optional<Result> gameOpt = resultRepository.findByGameNameIgnoreCase(normName);
        Result game = gameOpt.orElseGet(() -> {
            Result newGame = new Result();
            newGame.setGameName(normName.toUpperCase());
            return newGame;
        });

        // Set times if present
        if (openTime != null && !openTime.trim().isEmpty() && (game.getOpenTime() == null || game.getOpenTime().trim().isEmpty())) {
            game.setOpenTime(openTime.trim());
        }
        if (closeTime != null && !closeTime.trim().isEmpty() && (game.getCloseTime() == null || game.getCloseTime().trim().isEmpty())) {
            game.setCloseTime(closeTime.trim());
        }

        // Check if resultDisplay has actual digits
        if (resultDisplay == null || resultDisplay.trim().isEmpty()
                || resultDisplay.trim().equals("***-**-***")
                || resultDisplay.trim().equalsIgnoreCase("loading...")
                || resultDisplay.trim().equals("-")) {
            if (game.getId() == null) {
                resultRepository.save(game);
            }
            return;
        }

        // Parse resultDisplay (e.g. "679-28-990", "450-9*", "450-9*-***", "259-67-133")
        String raw = resultDisplay.trim();
        String openPanel = "";
        String jodi = "";
        String openAnk = "";
        String closeAnk = "";
        String closePanel = "";

        String[] parts = raw.split("-");
        if (parts.length >= 1) {
            String p0 = parts[0].trim();
            if (!p0.equals("***") && !p0.contains("*")) {
                openPanel = p0;
            }
        }
        if (parts.length >= 2) {
            String p1 = parts[1].trim();
            if (!p1.equals("**") && !p1.equals("*")) {
                String cleanedP1 = p1.replace("*", "").trim();
                if (cleanedP1.length() >= 2) {
                    openAnk = String.valueOf(cleanedP1.charAt(0));
                    closeAnk = String.valueOf(cleanedP1.charAt(1));
                    jodi = openAnk + closeAnk;
                } else if (cleanedP1.length() == 1) {
                    if (p1.startsWith("*")) {
                        closeAnk = String.valueOf(cleanedP1.charAt(0));
                        jodi = "*" + closeAnk;
                    } else {
                        openAnk = String.valueOf(cleanedP1.charAt(0));
                        jodi = openAnk + "*";
                    }
                } else if (!p1.isEmpty()) {
                    jodi = p1;
                }
            }
        }
        if (parts.length >= 3) {
            String p2 = parts[2].trim();
            if (!p2.equals("***") && !p2.contains("*")) {
                closePanel = p2;
            }
        }

        // Check if anything meaningful changed or if DB needs filling
        boolean changed = false;

        if (!openPanel.isEmpty() && !openPanel.equals(game.getOpenPanel())) {
            game.setOpenPanel(openPanel);
            changed = true;
        }
        if (!jodi.isEmpty() && !jodi.equals(game.getJodi())) {
            if (!jodi.contains("*") || game.getJodi() == null || game.getJodi().trim().isEmpty() || game.getJodi().equals("**")) {
                game.setJodi(jodi);
                changed = true;
            }
        }
        if (!openAnk.isEmpty() && !openAnk.equals(game.getOpenAnk())) {
            game.setOpenAnk(openAnk);
            changed = true;
        }
        if (!closeAnk.isEmpty() && !closeAnk.equals(game.getCloseAnk())) {
            game.setCloseAnk(closeAnk);
            changed = true;
        }
        if (!closePanel.isEmpty() && !closePanel.equals(game.getClosePanel())) {
            game.setClosePanel(closePanel);
            changed = true;
        }

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        String upperName = (game.getGameName() != null) ? game.getGameName().toUpperCase().trim() : normName.toUpperCase();

        GameHistory existingHistory = historyRepository.findByGameNameIgnoreCaseAndResultDate(upperName, today);
        boolean historyMissing = (existingHistory == null) || (existingHistory.getJodi() == null || existingHistory.getJodi().trim().isEmpty());

        if (changed || game.getId() == null || historyMissing) {
            Result savedGame = resultRepository.saveAndFlush(game);

            // Update GameHistory (for web charts) only if we have an open result for today
            if (savedGame.getOpenPanel() != null && !savedGame.getOpenPanel().trim().isEmpty() && !savedGame.getOpenPanel().equals("***")) {
                GameHistory history = existingHistory;
                if (history == null) {
                    history = new GameHistory();
                    history.setGameName(upperName);
                    history.setResultDate(today);
                }

                if (savedGame.getOpenPanel() != null) history.setOpenPanel(savedGame.getOpenPanel());
                if (savedGame.getOpenAnk() != null) history.setOpenAnk(savedGame.getOpenAnk());
                if (savedGame.getCloseAnk() != null) history.setCloseAnk(savedGame.getCloseAnk());
                if (savedGame.getClosePanel() != null) history.setClosePanel(savedGame.getClosePanel());
                if (savedGame.getJodi() != null) history.setJodi(savedGame.getJodi());

                historyRepository.save(history);

                // Update GameRecord (for mobile app API)
                GameRecord record = gameRecordRepository.findByGameNameAndDate(upperName, today);
                if (record == null) {
                    record = new GameRecord();
                    record.setGameName(upperName);
                    record.setDate(today);
                }
                if (savedGame.getOpenPanel() != null) record.setOpenPanel(savedGame.getOpenPanel());
                if (savedGame.getClosePanel() != null) record.setClosePanel(savedGame.getClosePanel());
                if (savedGame.getJodi() != null) record.setJodi(savedGame.getJodi());

                String oP = savedGame.getOpenPanel();
                if (oP != null && oP.length() == 3) {
                    record.setOpen1(oP.substring(0, 1));
                    record.setOpen2(oP.substring(1, 2));
                    record.setOpen3(oP.substring(2, 3));
                }
                String cP = savedGame.getClosePanel();
                if (cP != null && cP.length() == 3) {
                    record.setClose1(cP.substring(0, 1));
                    record.setClose2(cP.substring(1, 2));
                    record.setClose3(cP.substring(2, 3));
                }
                gameRecordRepository.save(record);
            }

            // Settle Bids
            if (!openAnk.isEmpty() || !closeAnk.isEmpty()) {
                try {
                    bidService.settleBidsForMarket(upperName, savedGame.getOpenPanel(), savedGame.getOpenAnk(), savedGame.getCloseAnk(), savedGame.getClosePanel());
                } catch (Exception e) {
                    System.err.println("Bid settle warning: " + e.getMessage());
                }
            }

            System.out.println("✅ API SYNC UPDATED FOR TODAY (" + today + "): [" + upperName + "] -> " + savedGame.getOpenPanel() + "-" + savedGame.getJodi() + "-" + savedGame.getClosePanel());
        }
    }

    /**
     * Ensures all active market results with valid open panels are saved under today's Indian date (Asia/Kolkata) in history charts.
     */
    @Transactional
    public void syncCurrentResultsToHistory() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        List<Result> allResults = resultRepository.findAll();

        for (Result game : allResults) {
            String openPanel = game.getOpenPanel();
            if (openPanel != null && !openPanel.trim().isEmpty() && !openPanel.equals("***")) {
                String upperName = game.getGameName().toUpperCase().trim();

                GameHistory history = historyRepository.findByGameNameIgnoreCaseAndResultDate(upperName, today);
                if (history == null) {
                    history = new GameHistory();
                    history.setGameName(upperName);
                    history.setResultDate(today);
                }
                history.setOpenPanel(game.getOpenPanel());
                history.setOpenAnk(game.getOpenAnk());
                history.setCloseAnk(game.getCloseAnk());
                history.setClosePanel(game.getClosePanel());
                history.setJodi(game.getJodi());
                historyRepository.save(history);
            }
        }
    }

    // --- Market Management Methods ---

    public void deleteResultById(Long id) {
        System.out.println(">>> SERVICE LAYER: Calling delete for ID: " + id);
        resultRepository.deleteById(id);
    }

    // Change the return type from List<Result> to Optional<Result>
    public Optional<Result> getResultsByGameName(String gameName) {
        return resultRepository.findByGameName(gameName);
    }

    public List<ChartData> getAllChartsByCategory(String category) {
        return chartDataRepository.findByCategoryOrderByOrderNoAsc(category);
    }

    public void deleteChart(Long id) {
        chartDataRepository.deleteById(id);
    }

    // --- Live Result Methods (Today's Games) ---

 // --- Live Result Methods (Today's Games) ---

    public List<Result> getAllResults() {
        return resultRepository.findAllByOrderBySerialNoAsc();
    }

    public Result getResultById(Long id) {
        return resultRepository.findById(id).orElse(null);
    }

    public void saveResult(Result result) {
        resultRepository.save(result);
    }

    // --- Historical Chart Methods (Panel/Jodi Charts) ---

    public List<GameRecord> getAllGameRecords() {
        return gameRecordRepository.findAll();
    }

    public List<GameRecord> getHistoryByGame(String gameName) {
        return gameRecordRepository.findByGameNameOrderByDateDesc(gameName);
    }

    public void saveRecord(GameRecord record) {
        gameRecordRepository.save(record);
    }

    public void saveOrUpdateResult(String marketName, LocalDate resultDate, String openPana, String openAnk,
            String closeAnk, String closePana) {
        Optional<Result> gameOpt = resultRepository.findByGameName(marketName);
        Result game = gameOpt.orElse(new Result());
        game.setGameName(marketName);
        game.setOpenPanel(openPana);
        game.setOpenAnk(openAnk);
        game.setCloseAnk(closeAnk);
        game.setClosePanel(closePana);
        if (openAnk != null && closeAnk != null) {
            game.setJodi(openAnk + closeAnk);
        }
        resultRepository.save(game);

        String normGameName = marketName.toUpperCase().trim();
        LocalDate targetDate = (resultDate != null) ? resultDate : LocalDate.now(ZoneId.of("Asia/Kolkata"));
        GameHistory gameHistory = historyRepository.findByGameNameAndResultDate(normGameName, targetDate);
        if (gameHistory == null) {
            gameHistory = new GameHistory();
            gameHistory.setGameName(normGameName);
            gameHistory.setResultDate(targetDate);
        }
        gameHistory.setOpenPanel(openPana);
        gameHistory.setOpenAnk(openAnk);
        gameHistory.setCloseAnk(closeAnk);
        gameHistory.setClosePanel(closePana);
        if (openAnk != null && closeAnk != null) {
            gameHistory.setJodi(openAnk + closeAnk);
        }
        historyRepository.save(gameHistory);
    }

}