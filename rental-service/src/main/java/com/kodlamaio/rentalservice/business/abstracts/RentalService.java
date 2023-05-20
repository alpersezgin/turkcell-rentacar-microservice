package com.kodlamaio.rentalservice.business.abstracts;

import com.kodlamaio.rentalservice.business.dto.requests.create.CreateRentalRequest;
import com.kodlamaio.rentalservice.business.dto.requests.update.UpdateRentalRequest;
import com.kodlamaio.rentalservice.business.dto.responses.create.CreateRentalResponse;
import com.kodlamaio.rentalservice.business.dto.responses.get.GetAllRentalsResponse;
import com.kodlamaio.rentalservice.business.dto.responses.get.GetRentalResponse;
import com.kodlamaio.rentalservice.business.dto.responses.update.UpdateRentalResponse;

import java.util.List;
import java.util.UUID;

public interface RentalService {
    List<GetAllRentalsResponse> getAll();

    GetRentalResponse get(UUID rentalId);

    CreateRentalResponse add(CreateRentalRequest request);

    UpdateRentalResponse update(UUID rentalId, UpdateRentalRequest request);

    void delete(UUID rentalId);

}
