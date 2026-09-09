package com.spdpboss.repository;

import com.spdpboss.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByMobile(String mobile);
    List<Bid> findByMobileOrderByIdDesc(String mobile);
    List<Bid> findByStatus(String status);
    List<Bid> findByGameNameIgnoreCaseAndStatus(String gameName, String status);
}