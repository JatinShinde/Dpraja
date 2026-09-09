package com.spdpboss.repository;

import com.spdpboss.model.MarketAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketAgentRepository extends JpaRepository<MarketAgent, Long> {
    // This interface allows you to Save, Find, and Delete agents automatically
}