package com.spdpboss.config;

import com.spdpboss.model.Result;
import com.spdpboss.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private com.spdpboss.repository.FreeFixMarketRepository freeFixMarketRepository;

    @Override
    public void run(String... args) throws Exception {
        // This checks if the database is empty. If it is, it restores all 18 markets!
        if (resultRepository.count() == 0) {
            System.out.println("Database is empty. Auto-restoring the 18 live markets from DPRAJA...");

            List<Result> liveMarkets = Arrays.asList(
                createMarket("SRIDEVI", 1, "11:35 AM --- 12:35 PM"),
                createMarket("TIME BAZAR", 2, "01:00 PM --- 02:00 PM"),
                createMarket("PUNA BAZAR", 3, "01:15 PM --- 03:15 PM"),
                createMarket("MADHUR DAY", 4, "01:30 PM --- 02:30 PM"),
                createMarket("SRIDEVI DAY", 5, "01:35 PM --- 02:35 PM"),
                createMarket("TIRANGA DAY", 6, "02:15 PM --- 03:15 PM"),
                createMarket("MILAN DAY", 7, "03:00 PM --- 05:00 PM"),
                createMarket("RAJDHANI DAY", 8, "03:10 PM --- 05:10 PM"),
                createMarket("SUPREME DAY", 9, "03:35 PM --- 05:35 PM"),
                createMarket("KALYAN", 10, "04:00 PM --- 06:00 PM"),
                createMarket("SRIDEVI NIGHT", 11, "07:00 PM --- 08:00 PM"),
                createMarket("MADHUR NIGHT", 12, "08:30 PM --- 10:30 PM"),
                createMarket("TIRANGA NIGHT", 13, "08:30 PM --- 10:30 PM"),
                createMarket("SUPREME NIGHT", 14, "08:45 PM --- 10:45 PM"),
                createMarket("MILAN NIGHT", 15, "09:00 PM --- 11:00 PM"),
                createMarket("KALYAN NIGHT", 16, "09:20 PM --- 11:20 PM"),
                createMarket("RAJDHANI NIGHT", 17, "09:32 PM --- 11:45 PM"),
                createMarket("MAIN BAZAR", 18, "09:40 PM --- 12:05 AM")
            );
            
            resultRepository.saveAll(liveMarkets);
            System.out.println("Successfully restored all 18 markets!");
        }

        List<com.spdpboss.model.FreeFixMarket> defaultFixMarkets = Arrays.asList(
            new com.spdpboss.model.FreeFixMarket("ROSE BAZAR DAY", "1-5-8-4", "146-137-159-230-170-369-220-455-238-120", "15-85-41-81-37-13-91-79", "7-3-1-9"),
            new com.spdpboss.model.FreeFixMarket("PUNA BAZAR", "1-5-9", "128-245-159-258-135-289", "13-18-53-58-93-98", ""),
            new com.spdpboss.model.FreeFixMarket("SUPREME DAY", "2-4-7-8", "110-400-223-800", "21-26-41-46-71-76-81-86", ""),
            new com.spdpboss.model.FreeFixMarket("SRIDEVI NIGHT", "1-5-6-0", "119-799-330-668", "11-16-50-55-66-61-00-05", ""),
            new com.spdpboss.model.FreeFixMarket("KALYAN NIGHT", "1-5-8-4", "146-137-159-230-170-369-220-455-238-120", "15-85-41-81-37-13-91-79", "7-3-1-9"),
            new com.spdpboss.model.FreeFixMarket("MAIN BAZAR MORNING", "9-3-5-0", "450-670-113-127", "93-98-39-34-52-57-02-07", "3-8-4-9-2-7"),
            new com.spdpboss.model.FreeFixMarket("SUPREME NIGHT", "", "", "", ""),
            new com.spdpboss.model.FreeFixMarket("PADMAVATI", "1-4-7-9", "137-560-455-130-124-566-126-559", "12-11-44-42-76-73-92-96", "2-3-4-6"),
            new com.spdpboss.model.FreeFixMarket("BOMBAY DAY", "2-6-7-4", "300-160-250-237", "45-46-65-60-78-79-01-91", "4-5-8-9"),
            new com.spdpboss.model.FreeFixMarket("MADHUR DAY", "", "", "", ""),
            new com.spdpboss.model.FreeFixMarket("MADHUR NIGHT", "", "", "", ""),
            new com.spdpboss.model.FreeFixMarket("KARNATAKA DAY", "4-5-8", "130-235-456-890-350-478", "49-47-57-59-80-89", "0-7-9"),
            new com.spdpboss.model.FreeFixMarket("MAIN MORNING", "3-5-9-0", "346-258-478-569", "39-51-92-04-76-45", ""),
            new com.spdpboss.model.FreeFixMarket("SRIDEVI", "1-3-4-8", "13-18-34-38-45-40-83-88", "22-27-50-55-72-77-00-05", ""),
            new com.spdpboss.model.FreeFixMarket("SUPREME MORNING", "1-2-0", "290-390-490", "1-2-3", "3-2-1"),
            new com.spdpboss.model.FreeFixMarket("MUMBAI MORNING", "0-5-2-7", "127-280-140-230-147-246-160-340", "01-06-51-56-21-26-71-76", ""),
            new com.spdpboss.model.FreeFixMarket("MAIN KALYAN", "9-4-8-3", "378-239-170-346", "92-97-43-48-86-8134-39", "3-7-9"),
            new com.spdpboss.model.FreeFixMarket("PRABHAT", "1-2-3-4", "678-137-589-147-346-689-239-400", "12-16-23-27-32-37-45-40", ""),
            new com.spdpboss.model.FreeFixMarket("NEW TIME BAZAR", "7-2-8-3", "458-156-170-148-247", "75-70-25-20-83-88-33-38", ""),
            new com.spdpboss.model.FreeFixMarket("SRIDEVI MORNING", "1-6-4-9", "155-556-268-112-117", "14-19-64-69-41-46-91-96", ""),
            new com.spdpboss.model.FreeFixMarket("MAIN BAZAR DAY", "3-8-4-9", "139-189-559-400-900", "34-39-84-89-53-48-93-98", ""),
            new com.spdpboss.model.FreeFixMarket("MAIN MUMBAI NIGHT", "2-7-4-9", "237-377-179-400-900", "24-29-74-79-42-47-92-97", ""),
            new com.spdpboss.model.FreeFixMarket("MAIN MUMBAI RK", "1-6-3-8", "245-358-139-189-558", "13-18-63-68-31-36-81-86", ""),
            new com.spdpboss.model.FreeFixMarket("TIME BAZAR MORNING", "1-6-3-8", "227-125-247-150", "14-19-64-69-30-35-80-85", ""),
            new com.spdpboss.model.FreeFixMarket("OLD MAIN MUMBAI", "3-8-2-7", "139-189-200-700-890", "32-37-82-87-23-28-73-78", ""),
            new com.spdpboss.model.FreeFixMarket("BOMBAY NIGHT", "4-9-5-0", "347-379-122-118-280", "45-40-95-90-54-59-04-09", "")
        );

        for (com.spdpboss.model.FreeFixMarket ffm : defaultFixMarkets) {
            List<com.spdpboss.model.FreeFixMarket> existing = freeFixMarketRepository.findByMarketNameIgnoreCase(ffm.getMarketName());
            if (existing.isEmpty()) {
                freeFixMarketRepository.save(ffm);
            }
        }
        System.out.println("Successfully ensured all FreeFixMarket items are populated!");
    }

    private Result createMarket(String name, int serial, String time) {
        Result r = new Result();
        r.setGameName(name);
        r.setSerialNo(serial);
        r.setResultTime(time);
        r.setMarketColor("#9c27b0"); // Default purple accent color
        return r;
    }
}	