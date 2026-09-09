package com.spdpboss.service;

import com.spdpboss.model.GameHistory;
import com.spdpboss.model.Result;
import com.spdpboss.repository.GameHistoryRepository;
import com.spdpboss.repository.ResultRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScraperService {

    @Autowired 
    private ResultRepository resultRepository; 

    // Core major markets to auto-update
    private static final List<String> AUTO_MARKETS = Arrays.asList(
        "SRIDEVI", "TIME BAZAR", "MADHUR DAY", "SRIDEVI DAY", "MILAN DAY", 
        "KALYAN", "RAJDHANI DAY", "SUPREME DAY", "SRIDEVI NIGHT", "KALYAN NIGHT", 
        "MADHUR NIGHT", "RAJDHANI NIGHT", "SUPREME NIGHT", "MILAN NIGHT", "MAIN BAZAR"
    );

    @Async 
   // @Scheduled(fixedDelay = 60000) 
    public void runScraper() {
        try {
            System.out.println("Running background data sync from dpbossnets...");
            
            Document doc = Jsoup.connect("https://dpbossnets.co.in/")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            System.out.println("DEBUG: Found " + doc.select("div").size() + " total div elements.");
            System.out.println("DEBUG: Page snippet: " + doc.text().substring(0, 500));

            // Target the result container cards on the page
            Elements marketElements = doc.select("div.market-box, div.result-box, tr, div"); 

            for (Element element : marketElements) {
                String text = element.text().toUpperCase();
                
                for (String marketName : AUTO_MARKETS) {
                    // Exact match boundary check so "MILAN DAY" doesn't accidentally trip "MILAN NIGHT"
                    if (text.contains(marketName)) {
                        parseAndSaveLiveResult(marketName, element.text());
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ GLOBAL SCRAPER FAILURE: " + e.getMessage());
        }
    }

    @Autowired
    private GameHistoryRepository historyRepository; // Add this dependency

    private void parseAndSaveLiveResult(String marketName, String rawText) {
        Pattern pattern = Pattern.compile("([0-9\\*]{3})-([0-9\\*]{2})-([0-9\\*]{3})");
        Matcher matcher = pattern.matcher(rawText);

        if (matcher.find()) {
            String openPanel = matcher.group(1);
            String jodi = matcher.group(2);
            String closePanel = matcher.group(3);
            LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Kolkata"));

            if (openPanel.contains("*") || jodi.contains("*") || closePanel.contains("*")) {
                System.out.println("⚠️ Scraper found incomplete data. Skipping update.");
                return; 
            }
            
         // Change line 83 to this:
            Optional<Result> gameOpt = resultRepository.findByGameName(marketName);

            // 2. Check if the game exists inside the Optional
            if (gameOpt.isPresent()) {
                Result game = gameOpt.get(); // Unwrap the Result object from the Optional

                // 3. Only update if the result is actually different
                if (!jodi.equals(game.getJodi()) || !openPanel.equals(game.getOpenPanel()) || !closePanel.equals(game.getClosePanel())) {
                    game.setOpenPanel(openPanel);
                    game.setJodi(jodi);
                    game.setClosePanel(closePanel);
                    if (jodi != null && jodi.length() == 2) {
                        game.setOpenAnk(jodi.substring(0, 1));
                        game.setCloseAnk(jodi.substring(1, 2));
                    }
                    resultRepository.save(game);
                    System.out.println("✅ Scraper updated " + marketName);

                    // Also save to GameHistory (Thymeleaf template charts)
                    String normGameName = marketName.toUpperCase().trim();
                    GameHistory gameHistory = historyRepository.findByGameNameAndResultDate(normGameName, today);
                    if (gameHistory == null) {
                        gameHistory = new GameHistory();
                        gameHistory.setGameName(normGameName);
                        gameHistory.setResultDate(today);
                    }
                    gameHistory.setOpenPanel(openPanel);
                    if (jodi != null && jodi.length() >= 2) {
                        gameHistory.setOpenAnk(jodi.substring(0, 1));
                        gameHistory.setCloseAnk(jodi.substring(1, 2));
                    } else {
                        gameHistory.setOpenAnk("");
                        gameHistory.setCloseAnk("");
                    }
                    gameHistory.setClosePanel(closePanel);
                    gameHistory.setJodi(jodi);
                    historyRepository.save(gameHistory);
                }
            }
        }
    }
}