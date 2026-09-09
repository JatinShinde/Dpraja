package com.spdpboss.controller;

import com.spdpboss.model.Result;
import com.spdpboss.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class SitemapController {

    @Autowired
    private ResultRepository resultRepository;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String getSitemap() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // 1. Homepage (Highest Priority, Changes Always)
        xml.append(createUrlEntry("https://dpraja.com/", today, "always", "1.0"));

        // 2. Static Game Zone Subpages
        String[] staticPages = {
            "/all-market-free-fix",
            "/matka-jodi-count",
            "/dhanvarsha-daily-fix",
            "/matka-jodi-family",
            "/penal-count-chart",
            "/penal-total-chart",
            "/all-220-card-list",
            "/matka-final-number-trick",
            "/contact"
        };

        for (String page : staticPages) {
            xml.append(createUrlEntry("https://dpraja.com" + page, today, "daily", "0.8"));
        }

        // 3. Dynamic Jodi & Panel Chart Pages for ALL Markets
        List<Result> allGames = resultRepository.findAllByOrderBySerialNoAsc();
        for (Result game : allGames) {
            if (game.getGameName() != null && !game.getGameName().trim().isEmpty()) {
                String marketName = game.getGameName().trim();
                String encodedMarket = URLEncoder.encode(marketName, StandardCharsets.UTF_8);
                xml.append(createUrlEntry("https://dpraja.com/jodi-chart?name=" + encodedMarket, today, "daily", "0.7"));
                xml.append(createUrlEntry("https://dpraja.com/panel-chart?name=" + encodedMarket, today, "daily", "0.7"));
            }
        }

        xml.append("</urlset>");
        return xml.toString();
    }

    private String createUrlEntry(String loc, String lastMod, String changeFreq, String priority) {
        return "  <url>\n" +
               "    <loc>" + loc + "</loc>\n" +
               "    <lastmod>" + lastMod + "</lastmod>\n" +
               "    <changefreq>" + changeFreq + "</changefreq>\n" +
               "    <priority>" + priority + "</priority>\n" +
               "  </url>\n";
    }
}