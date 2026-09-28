package com.spdpboss.config;

import com.spdpboss.model.GameHistory;
import com.spdpboss.model.GameRecord;
import com.spdpboss.model.Result;
import com.spdpboss.repository.GameHistoryRepository;
import com.spdpboss.repository.GameRecordRepository;
import com.spdpboss.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Component
public class HistoryDataSeeder implements CommandLineRunner {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private GameHistoryRepository historyRepository;

    @Autowired
    private GameRecordRepository recordRepository;

    // Selected markets specified by the user
    private static final List<String> TARGET_MARKETS = Arrays.asList(
        "RAJA MORNING",
        "TIRANGA DAY",
        "TIRANGA NIGHT",
        "PUNA NIGHT",
        "RAJA NIGHT",
        "RAJA"
    );

    // Authentic Matka panel pools for each Ank (0 to 9)
    private static final Map<Integer, List<String>> ANK_PANEL_MAP = new HashMap<>();

    static {
        ANK_PANEL_MAP.put(0, Arrays.asList("127", "136", "145", "190", "235", "280", "370", "460", "550", "118", "226", "334", "442"));
        ANK_PANEL_MAP.put(1, Arrays.asList("128", "137", "146", "155", "236", "245", "290", "380", "470", "560", "678", "119", "227"));
        ANK_PANEL_MAP.put(2, Arrays.asList("129", "138", "147", "156", "237", "246", "255", "345", "390", "480", "570", "688", "110"));
        ANK_PANEL_MAP.put(3, Arrays.asList("120", "139", "148", "157", "166", "238", "247", "256", "346", "355", "490", "580", "670"));
        ANK_PANEL_MAP.put(4, Arrays.asList("130", "149", "158", "167", "239", "248", "257", "266", "347", "356", "455", "590", "680"));
        ANK_PANEL_MAP.put(5, Arrays.asList("140", "159", "168", "177", "230", "249", "258", "267", "348", "357", "366", "456", "690"));
        ANK_PANEL_MAP.put(6, Arrays.asList("123", "150", "169", "178", "240", "259", "268", "277", "349", "358", "367", "457", "466"));
        ANK_PANEL_MAP.put(7, Arrays.asList("124", "160", "179", "188", "250", "269", "278", "340", "359", "368", "377", "458", "467"));
        ANK_PANEL_MAP.put(8, Arrays.asList("125", "134", "170", "189", "260", "279", "288", "350", "369", "378", "459", "468", "477"));
        ANK_PANEL_MAP.put(9, Arrays.asList("126", "135", "144", "180", "199", "270", "289", "360", "379", "388", "450", "469", "478"));
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Checking historical chart data for target markets...");

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        LocalDate startDate = today.minusWeeks(52); // Past 1 year (52 weeks)

        for (String marketName : TARGET_MARKETS) {
            String normName = marketName.toUpperCase().trim();

            // 1. Fetch market settings (active days)
            Optional<Result> resultOpt = resultRepository.findByGameNameIgnoreCase(normName);
            Result marketResult = null;

            if (resultOpt.isPresent()) {
                marketResult = resultOpt.get();
            } else {
                // If market doesn't exist in Result DB table, create it so charts work seamlessly
                marketResult = new Result();
                marketResult.setGameName(normName);
                marketResult.setSerialNo(99);
                marketResult.setMarketDays("Mon,Tue,Wed,Thu,Fri,Sat,Sun");
                marketResult.setDaysOfWeek("Mon,Tue,Wed,Thu,Fri,Sat,Sun");
                marketResult.setMarketColor("#9c27b0");
                resultRepository.save(marketResult);
            }

            // Determine active days for this market
            List<String> activeDays = getActiveDaysList(marketResult);

            // Loop day by day for the past 52 weeks
            LocalDate currDate = startDate;
            int addedCount = 0;

            while (!currDate.isAfter(today)) {
                String dayName = getDayAbbreviation(currDate.getDayOfWeek());

                // Skip if market is closed on this day of week
                if (!activeDays.contains(dayName)) {
                    currDate = currDate.plusDays(1);
                    continue;
                }

                // Check if history record ALREADY exists for this market and date
                List<GameHistory> existingHistories = historyRepository.findAllByGameNameIgnoreCaseAndResultDate(normName, currDate);

                if (!existingHistories.isEmpty()) {
                    boolean hasValidResult = existingHistories.stream().anyMatch(h -> 
                        h.getJodi() != null && !h.getJodi().trim().isEmpty() && !h.getJodi().equals("**")
                    );
                    if (hasValidResult) {
                        // USER HAS MANUALLY ADDED DATA OR DATA ALREADY EXISTS - SKIP TO PRESERVE MANUALLY ADDED RESULTS!
                        currDate = currDate.plusDays(1);
                        continue;
                    }
                }

                // Generate deterministic result for missing date
                long seed = (normName + currDate.toString()).hashCode();
                Random rng = new Random(seed);

                int openAnkVal = rng.nextInt(10);
                int closeAnkVal = rng.nextInt(10);

                List<String> openPanelPool = ANK_PANEL_MAP.get(openAnkVal);
                List<String> closePanelPool = ANK_PANEL_MAP.get(closeAnkVal);

                String openPanel = openPanelPool.get(rng.nextInt(openPanelPool.size()));
                String closePanel = closePanelPool.get(rng.nextInt(closePanelPool.size()));
                String jodi = String.valueOf(openAnkVal) + String.valueOf(closeAnkVal);

                // Create or update GameHistory
                GameHistory existingHistory = existingHistories.isEmpty() ? new GameHistory() : existingHistories.get(0);
                existingHistory.setGameName(normName);
                existingHistory.setResultDate(currDate);
                existingHistory.setOpenPanel(openPanel);
                existingHistory.setOpenAnk(String.valueOf(openAnkVal));
                existingHistory.setCloseAnk(String.valueOf(closeAnkVal));
                existingHistory.setClosePanel(closePanel);
                existingHistory.setJodi(jodi);
                historyRepository.save(existingHistory);

                // Create or update GameRecord (for API)
                List<GameRecord> existingRecords = recordRepository.findAllByGameNameAndDate(normName, currDate);
                GameRecord record = existingRecords.isEmpty() ? new GameRecord() : existingRecords.get(0);
                record.setGameName(normName);
                record.setDate(currDate);
                record.setOpenPanel(openPanel);
                record.setClosePanel(closePanel);
                record.setJodi(jodi);
                record.setColor(record.isRedJodi() ? "RED" : "BLACK");

                LocalDate monday = currDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate sunday = monday.plusDays(6);
                record.setStartDate(monday.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                record.setEndDate(sunday.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

                if (openPanel.length() == 3) {
                    record.setOpen1(openPanel.substring(0, 1));
                    record.setOpen2(openPanel.substring(1, 2));
                    record.setOpen3(openPanel.substring(2, 3));
                }
                if (closePanel.length() == 3) {
                    record.setClose1(closePanel.substring(0, 1));
                    record.setClose2(closePanel.substring(1, 2));
                    record.setClose3(closePanel.substring(2, 3));
                }
                recordRepository.save(record);

                addedCount++;
                currDate = currDate.plusDays(1);
            }

            if (addedCount > 0) {
                System.out.println("✅ Seeded " + addedCount + " missing 1-year historical chart records for " + normName);
            }
        }
    }

    private List<String> getActiveDaysList(Result game) {
        String daysStr = null;
        if (game.getMarketDays() != null && !game.getMarketDays().trim().isEmpty()) {
            daysStr = game.getMarketDays();
        } else if (game.getDaysOfWeek() != null && !game.getDaysOfWeek().trim().isEmpty()) {
            daysStr = game.getDaysOfWeek();
        }

        if (daysStr != null && !daysStr.trim().isEmpty()) {
            List<String> list = new ArrayList<>();
            for (String d : daysStr.split(",")) {
                list.add(d.trim());
            }
            return list;
        }
        return Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
    }

    private String getDayAbbreviation(DayOfWeek day) {
        switch (day) {
            case MONDAY: return "Mon";
            case TUESDAY: return "Tue";
            case WEDNESDAY: return "Wed";
            case THURSDAY: return "Thu";
            case FRIDAY: return "Fri";
            case SATURDAY: return "Sat";
            case SUNDAY: return "Sun";
            default: return "";
        }
    }
}
