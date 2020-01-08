package com.rekrutacja.zadanie;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BNPResponseDTO {

    private String table;
    private String currency;
    private String code;
    private List<RateEntry> rates;

}
