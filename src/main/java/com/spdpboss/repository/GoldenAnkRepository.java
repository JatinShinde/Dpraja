package com.spdpboss.repository;

import com.spdpboss.model.GoldenAnk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoldenAnkRepository extends JpaRepository<GoldenAnk, Long> {
    // Spring Data JPA will automatically handle the SQL for you
}