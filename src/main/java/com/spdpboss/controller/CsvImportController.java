package com.spdpboss.controller;

import com.spdpboss.service.CsvImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class CsvImportController {

    @Autowired
    private CsvImportService csvImportService;

    @PostMapping("/admin/import-csv")
    public String importCsv(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a CSV file to upload.");
            return "redirect:/admin?section=import-csv";
        }

        try {
            Map<String, Integer> counts = csvImportService.importCsv(file.getInputStream());
            redirectAttributes.addFlashAttribute("successMessage", 
                String.format("Successfully imported historical data! Markets updated: %d, GameHistory records: %d, GameRecord records: %d", 
                    counts.get("marketsCount"), counts.get("historyCount"), counts.get("recordsCount")));
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error importing CSV: " + e.getMessage());
        }

        return "redirect:/admin?section=import-csv";
    }
}
