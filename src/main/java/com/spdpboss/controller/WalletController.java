package com.spdpboss.controller;

import com.spdpboss.model.UserWallet;
import com.spdpboss.repository.UserWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    @Autowired
    private UserWalletRepository userWalletRepository;

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@RequestParam String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "FAILURE", "message", "Mobile number is required"));
        }

        String cleanMobile = mobile.trim();
        Optional<UserWallet> walletOpt = userWalletRepository.findByMobile(cleanMobile);
        UserWallet wallet;

        if (walletOpt.isPresent()) {
            wallet = walletOpt.get();
        } else {
            wallet = new UserWallet(cleanMobile, "User-" + cleanMobile, 500.0);
            userWalletRepository.save(wallet);
        }

        return ResponseEntity.ok(Map.of(
            "status", "SUCCESS",
            "mobile", wallet.getMobile(),
            "name", wallet.getName() != null ? wallet.getName() : "Customer",
            "balance", wallet.getBalance()
        ));
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> depositFunds(@RequestBody Map<String, Object> request) {
        try {
            String mobile = (String) request.get("mobile");
            if (mobile == null || mobile.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("status", "FAILURE", "message", "Mobile number is required"));
            }

            double amount = Double.parseDouble(request.get("amount").toString());
            if (amount <= 0) {
                return ResponseEntity.badRequest().body(Map.of("status", "FAILURE", "message", "Amount must be greater than 0"));
            }

            String cleanMobile = mobile.trim();
            UserWallet wallet = userWalletRepository.findByMobile(cleanMobile)
                .orElseGet(() -> new UserWallet(cleanMobile, "User-" + cleanMobile, 0.0));

            wallet.setBalance(wallet.getBalance() + amount);
            userWalletRepository.save(wallet);

            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Deposit successful! Added ₹" + amount,
                "newBalance", wallet.getBalance()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "FAILURE",
                "message", "Deposit error: " + e.getMessage()
            ));
        }
    }
}
