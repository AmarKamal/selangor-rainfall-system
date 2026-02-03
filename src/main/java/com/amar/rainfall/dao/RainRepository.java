package com.amar.rainfall.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RainRepository extends JpaRepository<RainData, Long> {

    // Untuk pengiraan rolling 3-hari dan 30-hari
    List<RainData> findAllByLocationNameAndRecordDateBetween(
        String locationName, 
        LocalDate startDate, 
        LocalDate endDate
    );

    // Untuk semak sebelum simpan (elak duplicate)
    boolean existsByLocationNameAndRecordDate(String locationName, LocalDate recordDate);
}