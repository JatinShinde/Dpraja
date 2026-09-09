package com.spdpboss.repository;

import com.spdpboss.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    /**
     * Finds a specific game by its name (e.g., KALYAN).
     */
	Optional<Result> findByGameName(String gameName);
	Optional<Result> findByGameNameIgnoreCase(String gameName);

	/**
     * Returns all results sorted by serial_no in ascending order.
     * Use this in your controller to ensure markets appear in the correct sequence.
     */
    List<Result> findAllByOrderBySerialNoAsc();
}