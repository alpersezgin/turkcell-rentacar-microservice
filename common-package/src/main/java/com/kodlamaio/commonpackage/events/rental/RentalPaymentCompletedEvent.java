package com.kodlamaio.commonpackage.events.rental;

import com.kodlamaio.commonpackage.events.Event;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalPaymentCompletedEvent implements Event {
    private UUID carId;
    private String cardHolder;
    private int rentedForDays;
    private double totalPrice;
    private LocalDateTime rentedAt;
}
