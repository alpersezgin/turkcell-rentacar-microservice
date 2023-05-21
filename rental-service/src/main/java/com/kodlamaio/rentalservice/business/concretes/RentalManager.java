package com.kodlamaio.rentalservice.business.concretes;


import com.kodlamaio.commonpackage.configuration.mappers.ModelMapperService;
import com.kodlamaio.commonpackage.events.rental.RentalCreatedEvent;
import com.kodlamaio.commonpackage.events.rental.RentalDeletedEvent;
import com.kodlamaio.commonpackage.events.rental.RentalUpdatedEvent;
import com.kodlamaio.commonpackage.kafka.producer.KafkaProducer;
import com.kodlamaio.commonpackage.utils.dto.ClientResponse;
import com.kodlamaio.commonpackage.utils.dto.ProcessPaymentRequest;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import com.kodlamaio.rentalservice.api.clients.inventory.CarClient;
import com.kodlamaio.rentalservice.api.clients.payment.PaymentClient;
import com.kodlamaio.rentalservice.business.abstracts.RentalService;
import com.kodlamaio.rentalservice.business.dto.requests.create.CreateRentalRequest;
import com.kodlamaio.rentalservice.business.dto.requests.update.UpdateRentalRequest;
import com.kodlamaio.rentalservice.business.dto.responses.create.CreateRentalResponse;
import com.kodlamaio.rentalservice.business.dto.responses.get.GetAllRentalsResponse;
import com.kodlamaio.rentalservice.business.dto.responses.get.GetRentalResponse;
import com.kodlamaio.rentalservice.business.dto.responses.update.UpdateRentalResponse;
import com.kodlamaio.rentalservice.business.rules.RentalBusinessRules;
import com.kodlamaio.rentalservice.entities.Rental;
import com.kodlamaio.rentalservice.repository.RentalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RentalManager implements RentalService {
    private final RentalRepository repository;
    private final ModelMapperService mapper;
    private final RentalBusinessRules rules;
    private final KafkaProducer producer;
    private final PaymentClient paymentClient;
    private final CarClient carClient;

    @Override
    public List<GetAllRentalsResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(rental -> mapper.forResponse().map(rental, GetAllRentalsResponse.class))
                .toList();
    }

    @Override
    public GetRentalResponse get(UUID rentalId) {
        rules.checkIfRentalExists(rentalId);
        return mapper.forResponse().map(repository.findById(rentalId).orElseThrow(), GetRentalResponse.class);
    }

    @Override
    public CreateRentalResponse add(CreateRentalRequest request) {
        rules.ensureCarIsAvailable(request.getCarId());
        //rules.checkCarNotUnderRental(request.getCarId());

        final double carDailyPrice;
        try {
            carDailyPrice = carClient.getDailyPrice(request.getCarId());
        } catch (BusinessException businessException) {
            throw new BusinessException("Renting could not be done: " + businessException.getMessage());
        }


        final double totalPrice = carDailyPrice * request.getRentedForDays();
        final Rental rental = mapper.forRequest().map(request, Rental.class);
        rental.setId(UUID.randomUUID());
        rental.setTotalPrice(totalPrice);

        var payment = ProcessPaymentRequest.builder()
                .paymentId(request.getPaymentId())
                .price(totalPrice)
                .build();
        ClientResponse response = paymentClient.processPaymentRequest(payment);
        if (!response.isSuccess()) {
            throw new BusinessException("Renting could not be done: " + response.getMessage());
        }

        rental.setRentedAt(LocalDate.now());

        // payment al

        final Rental createdRental = repository.save(rental);
        sendKafkaRentalCreatedEvent(createdRental);
        return mapper.forResponse().map(createdRental, CreateRentalResponse.class);
    }

    @Override
    public UpdateRentalResponse update(UUID rentalId, UpdateRentalRequest request) {
        rules.checkIfRentalExists(rentalId);
        final Rental oldRental = repository.findById(rentalId).orElseThrow();

        final Rental rental = mapper.forRequest().map(request, Rental.class);
        rental.setId(rentalId);

        final Rental updatedRental = repository.save(rental);
        sendKafkaRentalUpdatedEvent(oldRental.getCarId(), updatedRental);
        return mapper.forResponse().map(updatedRental, UpdateRentalResponse.class);
    }

    @Override
    public void delete(UUID rentalId) {
        rules.checkIfRentalExists(rentalId);
        var carId = repository.findById(rentalId).orElseThrow().getCarId();
        repository.deleteById(rentalId);
        sendKafkaRentalDeletedEvent(carId);
    }

    private void sendKafkaRentalCreatedEvent(Rental createdRental) {
        var event = mapper.forResponse().map(createdRental, RentalCreatedEvent.class);
        producer.sendMessage(event, "rental-created");
    }

    private void sendKafkaRentalUpdatedEvent(UUID oldCarId, Rental updatedRental) {
        final boolean carChanged = !oldCarId.equals(updatedRental.getCarId());

        var event = mapper.forResponse().map(updatedRental, RentalUpdatedEvent.class);
        event.setOldCarId(oldCarId);
        event.setCarChanged(carChanged);
        producer.sendMessage(event, "rental-updated");
    }

    private void sendKafkaRentalDeletedEvent(UUID carId) {
        var event = new RentalDeletedEvent(carId);
        producer.sendMessage(event, "rental-deleted");
    }
}
