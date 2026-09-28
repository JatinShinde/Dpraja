package com.spdpboss.repository;

import com.spdpboss.model.FreeFixMarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreeFixMarketRepository extends JpaRepository<FreeFixMarket, Long> {
    List<FreeFixMarket> findAllByOrderByIdAsc();
    List<FreeFixMarket> findByMarketNameIgnoreCase(String marketName);
}
