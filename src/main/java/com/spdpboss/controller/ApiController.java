package com.spdpboss.controller;

import com.spdpboss.model.*;
import com.spdpboss.repository.*;
import com.spdpboss.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private TodayFinalRepository todayFinalRepository;

    @Autowired
    private GameRecordRepository gameRecordRepository;

    @Autowired
    private GoldenAnkRepository goldenAnkRepository;

    @Autowired
    private PaperRepository paperRepository;

    // 1. HOME SCREEN INITIALIZATION FEED
    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> getHomeFeed() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        // Calculate Weekly Range
        LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Kolkata"));
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);
        DateTimeFormatter dateForm = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String weeklyRange = monday.format(dateForm) + " To " + sunday.format(dateForm);

        TodayFinal homeNotice = todayFinalRepository.findById(4L).orElse(new TodayFinal());
        String noticeText = homeNotice.getContent() != null ? homeNotice.getContent() : "WELCOME TO DP RAJA";

        data.put("noticeText", noticeText);
        data.put("weeklyRange", weeklyRange);
        
        GoldenAnk goldenAnkObj = goldenAnkRepository.findById(1L).orElse(new GoldenAnk());
        data.put("goldenAnk", goldenAnkObj.getValue() != null ? goldenAnkObj.getValue() : "5");

        Map<String, String> anchors = new HashMap<>();
        anchors.put("punaFinal", todayFinalRepository.findById(1L).map(TodayFinal::getContent).orElse("."));
        anchors.put("kalyanFinal", todayFinalRepository.findById(2L).map(TodayFinal::getContent).orElse("."));
        anchors.put("mainFinal", todayFinalRepository.findById(3L).map(TodayFinal::getContent).orElse("."));
        data.put("anchors", anchors);

        Map<String, String> guessingSection = new HashMap<>();
        guessingSection.put("weeklyJodiText", todayFinalRepository.findById(5L).map(TodayFinal::getContent).orElse(""));
        guessingSection.put("weeklyPattiText", todayFinalRepository.findById(6L).map(TodayFinal::getContent).orElse(""));
        guessingSection.put("weeklyLineText", todayFinalRepository.findById(7L).map(TodayFinal::getContent).orElse(""));
        data.put("guessingSection", guessingSection);

        response.put("status", "success");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    // 2. DASHBOARD MARKETS ARRAY FEED
    @GetMapping("/markets")
    public ResponseEntity<List<Result>> getMarketsFeed() {
        return ResponseEntity.ok(resultService.getAllResults());
    }

    // 3. JODI HISTORICAL RECORDS CHART
    @GetMapping("/charts/jodi")
    public ResponseEntity<Map<String, Object>> getJodiChart(@RequestParam("name") String gameName) {
        String cleanedName = gameName.trim().toUpperCase();
        List<GameRecord> records = gameRecordRepository.findByGameNameOrderByDateDesc(cleanedName);
        
        TreeMap<LocalDate, List<GameRecord>> groupedByWeek = records.stream()
            .collect(Collectors.groupingBy(record -> 
                record.getDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                () -> new TreeMap<>(Collections.reverseOrder()), Collectors.toList()));

        Optional<Result> gameOpt = resultService.getResultsByGameName(cleanedName);
        String daysOfWeek = gameOpt.isPresent() && gameOpt.get().getDaysOfWeek() != null ? gameOpt.get().getDaysOfWeek() : "Sun,Mon,Tue,Wed,Thu,Fri,Sat";

        Map<String, Object> response = new HashMap<>();
        response.put("gameName", cleanedName);
        response.put("daysOfWeek", daysOfWeek);
        response.put("weeks", groupedByWeek);
        return ResponseEntity.ok(response);
    }

    // 4. PANEL HISTORICAL RECORDS CHART
    @GetMapping("/charts/panel")
    public ResponseEntity<Map<String, Object>> getPanelChart(@RequestParam("name") String gameName) {
        return getJodiChart(gameName); // Shares matching logical formatting
    }

    // 5. ANALYSIS MEDIA OVERLAYS (PAPERS)
    @GetMapping("/papers")
    public ResponseEntity<List<Paper>> getPapersFeed(@RequestParam(value = "type", defaultValue = "WEEKLY") String type) {
        return ResponseEntity.ok(paperRepository.findByType(type.toUpperCase().trim()));
    }
}