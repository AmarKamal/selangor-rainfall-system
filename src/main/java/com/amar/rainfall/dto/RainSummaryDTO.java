package com.amar.rainfall.dto;

import java.time.LocalDate;

public class RainSummaryDTO {
    public String location;
    public LocalDate date;
    public String daily;    // 1-day
    public String rolling3; // 3-day
    public String rolling30;// 30-day

    // Constructor kosong
    public RainSummaryDTO() {}

    // Constructor penuh untuk mudahkan logic nanti
    public RainSummaryDTO(String location, LocalDate date, String daily, String rolling3, String rolling30) {
        this.location = location;
        this.date = date;
        this.daily = daily;
        this.rolling3 = rolling3;
        this.rolling30 = rolling30;
    }
}