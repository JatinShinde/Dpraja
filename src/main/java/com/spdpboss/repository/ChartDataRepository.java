package com.spdpboss.repository;

import com.spdpboss.model.ChartData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChartDataRepository extends JpaRepository<ChartData, Long> {
    List<ChartData> findByCategoryOrderByOrderNoAsc(String category);
}