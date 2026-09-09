package com.spdpboss.service;

import com.spdpboss.model.Bid;
import com.spdpboss.model.UserWallet;
import com.spdpboss.repository.BidRepository;
import com.spdpboss.repository.UserWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private UserWalletRepository userWalletRepository;

    @Transactional
    public Map<String, Object> placeBid(String mobile, String gameName, String bidDate,
                                       String gameSession, String bidType, String bidNumber, int amount) {
        Map<String, Object> response = new HashMap<>();

        if (mobile == null || mobile.trim().isEmpty()) {
            response.put("status", "FAILURE");
            response.put("message", "Mobile number is required");
            return response;
        }

        if (amount <= 0) {
            response.put("status", "FAILURE");
            response.put("message", "Bid amount must be greater than ₹0");
            return response;
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

        if (wallet.getBalance() < amount) {
            response.put("status", "FAILURE");
            response.put("message", "Insufficient wallet balance! Current balance: ₹" + wallet.getBalance() + ", Bid amount: ₹" + amount);
            response.put("currentBalance", wallet.getBalance());
            return response;
        }

        wallet.setBalance(wallet.getBalance() - amount);
        userWalletRepository.save(wallet);

        Bid bid = new Bid();
        bid.setMobile(cleanMobile);
        bid.setGameName(gameName != null ? gameName.trim().toUpperCase() : "");
        bid.setBidDate(bidDate);
        bid.setGameSession(gameSession != null ? gameSession.trim().toUpperCase() : "OPEN");
        bid.setBidType(bidType != null ? bidType.trim().toUpperCase() : "SINGLE");
        bid.setBidNumber(bidNumber != null ? bidNumber.trim() : "");
        bid.setAmount(amount);
        bid.setStatus("PENDING");
        bid.setPayoutAmount(0.0);

        bidRepository.save(bid);

        response.put("status", "SUCCESS");
        response.put("message", "Bid placed successfully!");
        response.put("bidId", bid.getId());
        response.put("newBalance", wallet.getBalance());
        return response;
    }

    @Transactional
    public void settleBidsForMarket(String gameName, String openPanna, String openAnk, String closeAnk, String closePanna) {
        if (gameName == null) return;

        String normGameName = gameName.trim().toUpperCase();
        List<Bid> pendingBids = bidRepository.findByGameNameIgnoreCaseAndStatus(normGameName, "PENDING");

        if (pendingBids.isEmpty()) {
            return;
        }

        String fullJodi = (openAnk != null && closeAnk != null && !openAnk.isEmpty() && !closeAnk.isEmpty())
                ? (openAnk.trim() + closeAnk.trim()) : "";

        for (Bid bid : pendingBids) {
            String bType = bid.getBidType() != null ? bid.getBidType().toUpperCase().trim() : "SINGLE";
            String bSession = bid.getGameSession() != null ? bid.getGameSession().toUpperCase().trim() : "OPEN";
            String targetNum = bid.getBidNumber() != null ? bid.getBidNumber().trim() : "";

            boolean evaluated = false;
            boolean isWinner = false;
            double multiplier = 10.0;

            if ("SINGLE".equals(bType) || "SINGLE_ANK".equals(bType)) {
                multiplier = 9.5;
                if ("OPEN".equals(bSession) && openAnk != null && !openAnk.trim().isEmpty() && !"*".equals(openAnk.trim())) {
                    evaluated = true;
                    isWinner = targetNum.equals(openAnk.trim());
                } else if ("CLOSE".equals(bSession) && closeAnk != null && !closeAnk.trim().isEmpty() && !"*".equals(closeAnk.trim())) {
                    evaluated = true;
                    isWinner = targetNum.equals(closeAnk.trim());
                }
            } else if ("JODI".equals(bType)) {
                multiplier = 90.0;
                if (!fullJodi.isEmpty() && !fullJodi.contains("*")) {
                    evaluated = true;
                    isWinner = targetNum.equals(fullJodi);
                }
            } else if (bType.contains("PANNA") || bType.contains("PATTI")) {
                if (bType.contains("TRIPLE")) multiplier = 600.0;
                else if (bType.contains("DOUBLE")) multiplier = 280.0;
                else multiplier = 140.0;

                if ("OPEN".equals(bSession) && openPanna != null && !openPanna.trim().isEmpty() && !"***".equals(openPanna.trim())) {
                    evaluated = true;
                    isWinner = targetNum.equals(openPanna.trim());
                } else if ("CLOSE".equals(bSession) && closePanna != null && !closePanna.trim().isEmpty() && !"***".equals(closePanna.trim())) {
                    evaluated = true;
                    isWinner = targetNum.equals(closePanna.trim());
                }
            }

            if (evaluated) {
                if (isWinner) {
                    bid.setStatus("WON");
                    double payout = bid.getAmount() * multiplier;
                    bid.setPayoutAmount(payout);

                    Optional<UserWallet> walletOpt = userWalletRepository.findByMobile(bid.getMobile());
                    if (walletOpt.isPresent()) {
                        UserWallet wallet = walletOpt.get();
                        wallet.setBalance(wallet.getBalance() + payout);
                        userWalletRepository.save(wallet);
                    }
                } else {
                    bid.setStatus("LOST");
                    bid.setPayoutAmount(0.0);
                }
                bidRepository.save(bid);
            }
        }
    }
}
