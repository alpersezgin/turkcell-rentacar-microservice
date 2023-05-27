package com.kodlamaio.commonpackage.utils.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCarDetailsResponse {
    private UUID id;
    private String modelName;
    private String modelBrandName;
    private String plate;
    private int modelYear;
    private double dailyPrice;
}
