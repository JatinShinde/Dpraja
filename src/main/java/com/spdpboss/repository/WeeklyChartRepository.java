package com.spdpboss.repository;

import com.spdpboss.model.WeeklyChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyChartRepository extends JpaRepository<WeeklyChart, Long> {
}