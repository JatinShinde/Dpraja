package com.spdpboss.spdpboss;

import com.spdpboss.service.CsvImportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.File;
import java.util.Map;

@SpringBootTest
public class CsvImportTest {

    @Autowired
    private CsvImportService csvImportService;

    @Test
    public void testImport() throws Exception {
        File csvFile = new File("c:/Users/Lenovo/.gemini/antigravity/scratch/matka_data.csv");
        if (csvFile.exists()) {
            System.out.println("Starting import from local path: " + csvFile.getAbsolutePath());
            Map<String, Integer> results = csvImportService.importCsv(new FileInputStream(csvFile));
            System.out.println("Import results: " + results);
        } else {
            System.out.println("CSV File not found at: " + csvFile.getAbsolutePath());
        }
    }
}
