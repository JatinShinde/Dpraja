package com.spdpboss.controller;

import com.spdpboss.model.Bid;
import com.spdpboss.repository.BidRepository;
import com.spdpboss.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bids")
public class BidController {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private BidService bidService;

    @PostMapping("/placeBid")
    public ResponseEntity<?> placeBid(@RequestBody Map<String, Object> bidRequest) {
        try {
            String mobile = (String) bidRequest.get("mobile");
            String gameName = (String) bidRequest.get("gameName");
            String bidDate = (String) bidRequest.get("bidDate");
            String gameSession = (String) bidRequest.get("gameSession");
            String bidType = (String) bidRequest.get("bidType");
            String bidNumber = (String) bidRequest.get("bidNumber");
            int amount = Integer.parseInt(bidRequest.get("amount").toString());

            Map<String, Object> result = bidService.placeBid(mobile, gameName, bidDate, gameSession, bidType, bidNumber, amount);

            if ("SUCCESS".equals(result.get("status"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "FAILURE",
                "message", "Error placing bid: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getBidHistory(@RequestParam String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "FAILURE", "message", "Mobile number is required"));
        }
        List<Bid> history = bidRepository.findByMobileOrderByIdDesc(mobile.trim());
        return ResponseEntity.ok(history);
    }
}