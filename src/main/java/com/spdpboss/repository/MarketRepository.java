package com.spdpboss.repository;

import com.spdpboss.model.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {
    Optional<Market> findByMarketSlug(String marketSlug);
    List<Market> findByIsActiveTrueOrderBySortOrderAsc();
    List<Market> findAllByOrderBySortOrderAsc();
    List<Market> findByIsFeaturedInTickerTrueAndIsActiveTrue();
}
