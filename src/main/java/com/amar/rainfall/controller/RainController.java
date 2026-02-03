package com.amar.rainfall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.amar.rainfall.dao.RainData;
import com.amar.rainfall.dao.RainRepository;
import com.amar.rainfall.dto.RainSummaryDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rain")
public class RainController {

    @Autowired
    private RainRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * VERSI DATABASE: Simpan ke DB dan kira dari DB.
     */
    @GetMapping("/sync")
    public RainSummaryDTO syncAndShow(
            @RequestParam String loc,
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(required = false) String date) {
        
        LocalDate targetDate = parseDate(date);
        fetchAndSaveToDb(loc, lat, lon);

        return new RainSummaryDTO(
                loc,
                targetDate,
                getSumFromDb(loc, targetDate, targetDate) + " mm",
                getSumFromDb(loc, targetDate.minusDays(2), targetDate) + " mm",
                getSumFromDb(loc, targetDate.minusDays(29), targetDate) + " mm"
        );
    }

    /**
     * VERSI NO-DB (LIVE): Terus kira dari API response tanpa simpan ke MySQL.
     */
    @GetMapping("/live")
    public RainSummaryDTO livePreview(
            @RequestParam String loc,
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(required = false) String date) {
        
        LocalDate targetDate = parseDate(date);
        String url = buildUrl(lat, lon);
        
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, Object> daily = (Map<String, Object>) response.get("daily");
            List<String> dates = (List<String>) daily.get("time");
            List<Object> rains = (List<Object>) daily.get("precipitation_sum");

            double sum1 = 0, sum3 = 0, sum30 = 0;

            for (int i = 0; i < dates.size(); i++) {
                LocalDate d = LocalDate.parse(dates.get(i));
                double r = Double.parseDouble(rains.get(i).toString());

                // Logik matematik rolling dalam memori
                if (d.isEqual(targetDate)) sum1 = r;
                if (!d.isAfter(targetDate) && d.isAfter(targetDate.minusDays(3))) sum3 += r;
                if (!d.isAfter(targetDate) && d.isAfter(targetDate.minusDays(30))) sum30 += r;
            }

            return new RainSummaryDTO(loc, targetDate, sum1 + " mm", 
                                      String.format("%.2f mm", sum3), 
                                      String.format("%.2f mm", sum30));
        } catch (Exception e) {
            return new RainSummaryDTO(loc, targetDate, "Error API", "0 mm", "0 mm");
        }
    }

    // --- HELPER METHODS ---

    private LocalDate parseDate(String date) {
        if (date == null || date.trim().isEmpty() || date.equals("undefined")) {
            return LocalDate.now();
        }
        return LocalDate.parse(date);
    }

    private String buildUrl(double lat, double lon) {
        return UriComponentsBuilder.fromHttpUrl("https://api.open-meteo.com/v1/forecast")
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("daily", "precipitation_sum")
                .queryParam("past_days", 31)
                .queryParam("timezone", "Asia/Kuala_Lumpur")
                .toUriString();
    }

    private void fetchAndSaveToDb(String loc, double lat, double lon) {
        try {
            Map<String, Object> response = restTemplate.getForObject(buildUrl(lat, lon), Map.class);
            Map<String, Object> daily = (Map<String, Object>) response.get("daily");
            List<String> dates = (List<String>) daily.get("time");
            List<Object> rains = (List<Object>) daily.get("precipitation_sum");

            for (int i = 0; i < dates.size(); i++) {
                LocalDate d = LocalDate.parse(dates.get(i));
                Double r = Double.valueOf(rains.get(i).toString());
                if (!repository.existsByLocationNameAndRecordDate(loc, d)) {
                    RainData data = new RainData(loc, d, r);
                    repository.save(data);
                }
            }
        } catch (Exception e) { System.err.println("DB Sync Error: " + e.getMessage()); }
    }

    private double getSumFromDb(String loc, LocalDate start, LocalDate end) {
        return repository.findAllByLocationNameAndRecordDateBetween(loc, start, end)
                .stream().mapToDouble(RainData::getPrecipitation).sum();
    }
}