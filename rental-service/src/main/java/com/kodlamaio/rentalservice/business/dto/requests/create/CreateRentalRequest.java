package com.kodlamaio.rentalservice.business.dto.requests.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRentalRequest {
    @NotNull
    private UUID carId;
    @NotNull
    private UUID paymentId;
    @Min(1)
    private int rentedForDays;
}

