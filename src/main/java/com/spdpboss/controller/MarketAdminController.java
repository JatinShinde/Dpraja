package com.spdpboss.controller;

import com.spdpboss.model.Market;
import com.spdpboss.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/markets")
public class MarketAdminController {

    private final MarketService marketService;

    @Autowired
    public MarketAdminController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping
    public String listMarkets(Model model) {
        model.addAttribute("markets", marketService.getAllMarkets());
        return "market-list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("market", new Market());
        model.addAttribute("isNew", true);
        return "market-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Market market = marketService.getMarketById(id);
        if (market == null) {
            ra.addFlashAttribute("errorMessage", "Market not found with ID: " + id);
            return "redirect:/admin/markets";
        }
        model.addAttribute("market", market);
        model.addAttribute("isNew", false);
        return "market-form";
    }

    @PostMapping("/save")
    public String saveMarket(@ModelAttribute("market") Market market, RedirectAttributes ra) {
        marketService.saveMarket(market);
        ra.addFlashAttribute("successMessage", "Market '" + market.getMarketName() + "' saved successfully!");
        return "redirect:/admin/markets";
    }

    @PostMapping("/toggle-active/{id}")
    public String toggleActive(@PathVariable Long id, RedirectAttributes ra) {
        Market market = marketService.toggleActiveStatus(id);
        if (market != null) {
            ra.addFlashAttribute("successMessage", "Market status updated to " + (market.isActive() ? "Active" : "Inactive"));
        }
        return "redirect:/admin/markets";
    }

    @PostMapping("/declare-result")
    public String declareResult(@RequestParam("id") Long id,
                                @RequestParam("openPana") String openPana,
                                @RequestParam("closePana") String closePana,
                                RedirectAttributes ra) {
        Market market = marketService.declareResult(id, openPana, closePana);
        if (market != null) {
            ra.addFlashAttribute("successMessage", "Result declared for " + market.getMarketName() + ": " + market.getFormattedResultString());
        }
        return "redirect:/admin/markets";
    }

    @PostMapping("/delete/{id}")
    public String deleteMarket(@PathVariable Long id, RedirectAttributes ra) {
        marketService.deleteMarket(id);
        ra.addFlashAttribute("successMessage", "Market deleted successfully!");
        return "redirect:/admin/markets";
    }
}
