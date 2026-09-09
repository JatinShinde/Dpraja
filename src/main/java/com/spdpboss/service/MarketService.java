package com.spdpboss.service;

import com.spdpboss.model.Market;
import com.spdpboss.repository.MarketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketService {

    private final MarketRepository marketRepository;

    @Autowired
    public MarketService(MarketRepository marketRepository) {
        this.marketRepository = marketRepository;
    }

    public List<Market> getAllMarkets() {
        return marketRepository.findAllByOrderBySortOrderAsc();
    }

    public List<Market> getActiveMarkets() {
        return marketRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    public List<Market> getFeaturedTickerMarkets() {
        return marketRepository.findByIsFeaturedInTickerTrueAndIsActiveTrue();
    }

    public Market getMarketById(Long id) {
        return marketRepository.findById(id).orElse(null);
    }

    public Market saveMarket(Market market) {
        if (market.getMarketSlug() == null || market.getMarketSlug().isBlank()) {
            market.setMarketSlug(slugify(market.getMarketName()));
        }
        market.updateCalculatedResults();
        return marketRepository.save(market);
    }

    public Market toggleActiveStatus(Long id) {
        Market market = getMarketById(id);
        if (market != null) {
            market.setActive(!market.isActive());
            return marketRepository.save(market);
        }
        return null;
    }

    public Market declareResult(Long id, String openPana, String closePana) {
        Market market = getMarketById(id);
        if (market != null) {
            if (openPana != null && !openPana.isBlank()) {
                market.setOpenPana(openPana.trim());
            }
            if (closePana != null && !closePana.isBlank()) {
                market.setClosePana(closePana.trim());
            }
            market.updateCalculatedResults();
            return marketRepository.save(market);
        }
        return null;
    }

    public void deleteMarket(Long id) {
        marketRepository.deleteById(id);
    }

    private String slugify(String input) {
        if (input == null) return "";
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}
