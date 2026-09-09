package com.spdpboss.service;

import com.spdpboss.model.GameHistory;
import com.spdpboss.model.GameRecord;
import com.spdpboss.model.Result;
import com.spdpboss.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class CsvImportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResultRepository resultRepository;

    @Transactional
    public Map<String, Integer> importCsv(InputStream inputStream) throws Exception {
        List<GameHistory> historyList = new ArrayList<>();
        List<GameRecord> recordList = new ArrayList<>();
        Set<String> marketsToClear = new HashSet<>();
        Map<String, GameHistory> latestHistoryMap = new HashMap<>();

        DateTimeFormatter dateRangeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine(); // Skip header row
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] columns = line.split(",", -1);
                if (columns.length < 6) {
                    continue; // Skip malformed rows
                }

                String marketName = columns[0].trim().toUpperCase();
                String dateRange = columns[1].trim(); // e.g., "17/04/2023to22/04/2023"
                String day = columns[2].trim();       // e.g., "Mon"
                String openPanel = columns[3].trim();
                String jodi = columns[4].trim();
                String closePanel = columns[5].trim();

                if (marketName.isEmpty() || dateRange.isEmpty() || day.isEmpty()) {
                    continue;
                }

                // Extract Monday start date from "dd/MM/yyyyto..."
                String[] dateParts = dateRange.split("to");
                if (dateParts.length < 1) {
                    continue;
                }
                String startDateStr = dateParts[0].trim();
                if (startDateStr.length() < 10) {
                    continue;
                }

                LocalDate startDate;
                try {
                    startDate = LocalDate.parse(startDateStr, dateRangeFormatter);
                } catch (Exception e) {
                    continue; // Skip rows with invalid date format
                }

                // Align start date to the correct Monday
                LocalDate monday = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

                int dayOffset = getDayOffset(day);
                if (dayOffset == -1) {
                    continue; // Skip if invalid day name
                }
                LocalDate resultDate = monday.plusDays(dayOffset);

                // Add to markets to clear
                marketsToClear.add(marketName);

                // Extract Open Ank and Close Ank from Jodi string
                String openAnk = "";
                String closeAnk = "";
                if (jodi.length() >= 1) {
                    openAnk = String.valueOf(jodi.charAt(0));
                }
                if (jodi.length() >= 2) {
                    closeAnk = String.valueOf(jodi.charAt(1));
                }

                // Construct GameHistory
                GameHistory history = new GameHistory();
                history.setGameName(marketName);
                history.setResultDate(resultDate);
                history.setOpenPanel(openPanel);
                history.setOpenAnk(openAnk);
                history.setCloseAnk(closeAnk);
                history.setClosePanel(closePanel);
                history.setJodi(jodi);
                historyList.add(history);

                // Check and update latest record for live Result update
                GameHistory currentLatest = latestHistoryMap.get(marketName);
                if (currentLatest == null || resultDate.isAfter(currentLatest.getResultDate())) {
                    latestHistoryMap.put(marketName, history);
                }

                // Construct GameRecord
                GameRecord record = new GameRecord();
                record.setGameName(marketName);
                record.setDate(resultDate);
                record.setOpenPanel(openPanel);
                record.setClosePanel(closePanel);
                record.setJodi(jodi);
                record.setColor(record.isRedJodi() ? "RED" : "BLACK");

                LocalDate sunday = monday.plusDays(6);
                record.setStartDate(monday.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                record.setEndDate(sunday.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

                if (openPanel.length() == 3) {
                    record.setOpen1(openPanel.substring(0, 1));
                    record.setOpen2(openPanel.substring(1, 2));
                    record.setOpen3(openPanel.substring(2, 3));
                } else {
                    record.setOpen1("");
                    record.setOpen2("");
                    record.setOpen3("");
                }

                if (closePanel.length() == 3) {
                    record.setClose1(closePanel.substring(0, 1));
                    record.setClose2(closePanel.substring(1, 2));
                    record.setClose3(closePanel.substring(2, 3));
                } else {
                    record.setClose1("");
                    record.setClose2("");
                    record.setClose3("");
                }
                recordList.add(record);
            }
        }

        // 1. Delete existing records for imported markets to prevent duplicate entries
        for (String market : marketsToClear) {
            jdbcTemplate.update("DELETE FROM game_history WHERE game_name = ?", market);
            jdbcTemplate.update("DELETE FROM game_records WHERE game_name = ?", market);
        }

        // 2. Batch Insert into game_history
        String historySql = "INSERT INTO game_history (game_name, result_date, open_panel, open_ank, jodi, close_ank, close_panel) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(historySql, historyList, 1000, (ps, history) -> {
            ps.setString(1, history.getGameName());
            ps.setObject(2, history.getResultDate());
            ps.setString(3, history.getOpenPanel());
            ps.setString(4, history.getOpenAnk());
            ps.setString(5, history.getJodi());
            ps.setString(6, history.getCloseAnk());
            ps.setString(7, history.getClosePanel());
        });

        // 3. Batch Insert into game_records
        String recordSql = "INSERT INTO game_records (game_name, date, open_panel, close_panel, jodi, color, start_date, end_date, open1, open2, open3, close1, close2, close3) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(recordSql, recordList, 1000, (ps, record) -> {
            ps.setString(1, record.getGameName());
            ps.setObject(2, record.getDate());
            ps.setString(3, record.getOpenPanel());
            ps.setString(4, record.getClosePanel());
            ps.setString(5, record.getJodi());
            ps.setString(6, record.getColor());
            ps.setString(7, record.getStartDate());
            ps.setString(8, record.getEndDate());
            ps.setString(9, record.getOpen1());
            ps.setString(10, record.getOpen2());
            ps.setString(11, record.getOpen3());
            ps.setString(12, record.getClose1());
            ps.setString(13, record.getClose2());
            ps.setString(14, record.getClose3());
        });

        // 4. Update the live Results table with the latest values from the CSV
        for (Map.Entry<String, GameHistory> entry : latestHistoryMap.entrySet()) {
            String marketName = entry.getKey();
            GameHistory latest = entry.getValue();

            Optional<Result> gameOpt = resultRepository.findByGameName(marketName);
            Result game;
            if (gameOpt.isPresent()) {
                game = gameOpt.get();
            } else {
                game = new Result();
                game.setGameName(marketName);
                game.setOpenTime("12:00 PM");
                game.setCloseTime("01:00 PM");
                game.setSerialNo(0);
                game.setMarketDays("Mon,Tue,Wed,Thu,Fri,Sat,Sun");
                game.setDaysOfWeek("Mon,Tue,Wed,Thu,Fri,Sat,Sun");
                game.setMarketColor("BLACK");
                game.setResultTime("PENDING");
            }

            game.setOpenPanel(latest.getOpenPanel());
            game.setJodi(latest.getJodi());
            game.setClosePanel(latest.getClosePanel());
            game.setOpenAnk(latest.getOpenAnk());
            game.setCloseAnk(latest.getCloseAnk());
            game.setChartDate(latest.getResultDate().toString());

            resultRepository.save(game);
        }

        Map<String, Integer> counts = new HashMap<>();
        counts.put("historyCount", historyList.size());
        counts.put("recordsCount", recordList.size());
        counts.put("marketsCount", marketsToClear.size());
        return counts;
    }

    private int getDayOffset(String day) {
        switch (day.toLowerCase()) {
            case "mon":
            case "monday":
                return 0;
            case "tue":
            case "tuesday":
                return 1;
            case "wed":
            case "wednesday":
                return 2;
            case "thu":
            case "thursday":
                return 3;
            case "fri":
            case "friday":
                return 4;
            case "sat":
            case "saturday":
                return 5;
            case "sun":
            case "sunday":
                return 6;
            default:
                return -1;
        }
    }
}
