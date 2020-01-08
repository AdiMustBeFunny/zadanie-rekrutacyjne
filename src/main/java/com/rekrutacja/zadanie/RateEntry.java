package com.rekrutacja.zadanie;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RateEntry {

    private String no;
    private LocalDate effectiveDate;
    private Double bid;
    private Double ask;


}
