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
     * Endpoint untuk sync data dari API dan paparkan hasil rolling.
     * Contoh: /api/rain/sync?loc=Klang&lat=3.04&lon=101.44&date=2026-02-01
     */
    @GetMapping("/sync")
    public RainSummaryDTO syncAndShow(
            @RequestParam String loc,
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(required = false) String date) {
    	
    	LocalDate targetDate;
        try {
            // Jika date dari frontend "null" atau "", gunakan hari ini
            if (date == null || date.trim().isEmpty() || date.equals("undefined")) {
                targetDate = LocalDate.now();
            } else {
                targetDate = LocalDate.parse(date);
            }
        } catch (Exception e) {
            targetDate = LocalDate.now(); // Fallback jika format tarikh pelik
        }

        // 1. Tentukan tarikh sasaran (Guna hari ini jika tiada input)
         targetDate = (date != null && !date.isEmpty()) 
                               ? LocalDate.parse(date) 
                               : LocalDate.now();

        // 2. Bina URL API Open-Meteo
        // Kita tarik 31 hari ke belakang dari tarikh sasaran untuk pastikan data cukup
        String url = UriComponentsBuilder.fromHttpUrl("https://api.open-meteo.com/v1/forecast")
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("daily", "precipitation_sum")
                .queryParam("past_days", 31) 
                .queryParam("timezone", "Asia/Kuala_Lumpur")
                .toUriString();

        // 3. Panggil API dan dapatkan respons
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("daily")) {
                Map<String, Object> daily = (Map<String, Object>) response.get("daily");
                List<String> dates = (List<String>) daily.get("time");
                List<Object> rains = (List<Object>) daily.get("precipitation_sum");

                // 4. Simpan ke Database (Check Duplicate)
                for (int i = 0; i < dates.size(); i++) {
                    LocalDate d = LocalDate.parse(dates.get(i));
                    // Handle casting dari Number (Double/Integer) ke Double
                    Double r = (rains.get(i) != null) ? Double.valueOf(rains.get(i).toString()) : 0.0;

                    if (!repository.existsByLocationNameAndRecordDate(loc, d)) {
                        RainData data = new RainData();
                        data.setLocationName(loc);
                        data.setRecordDate(d);
                        data.setPrecipitation(r);
                        repository.save(data);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("API Error: " + e.getMessage());
            // Kita teruskan ke pengiraan walaupun API gagal, mungkin data dah ada dalam DB
        }

        // 5. Pengiraan Rolling menggunakan Helper Method
        return new RainSummaryDTO(
                loc,
                targetDate,
                getSum(loc, targetDate, targetDate) + " mm",
                getSum(loc, targetDate.minusDays(2), targetDate) + " mm",
                getSum(loc, targetDate.minusDays(29), targetDate) + " mm"
        );
    }

    /**
     * Helper method untuk mengira jumlah hujan dalam julat tarikh dari database.
     */
    private double getSum(String loc, LocalDate start, LocalDate end) {
        List<RainData> dataList = repository.findAllByLocationNameAndRecordDateBetween(loc, start, end);
        return dataList.stream()
                .mapToDouble(RainData::getPrecipitation)
                .sum();
    }
}