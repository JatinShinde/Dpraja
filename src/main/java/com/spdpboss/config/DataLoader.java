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