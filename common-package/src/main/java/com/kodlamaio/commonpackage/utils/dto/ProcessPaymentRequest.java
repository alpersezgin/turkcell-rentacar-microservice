package com.kodlamaio.commonpackage.utils.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessPaymentRequest {
    @NotNull
    private UUID paymentId;

    @Min(value = 1)
    private double price;
}
