package com.kodlamaio.inventoryservice.api.controllers;

import com.kodlamaio.commonpackage.utils.dto.responses.ClientResponse;
import com.kodlamaio.commonpackage.utils.dto.responses.GetCarDetailsResponse;
import com.kodlamaio.commonpackage.utils.security.KeycloakJwtRoleConverter;
import com.kodlamaio.inventoryservice.business.abstracts.CarService;
import com.kodlamaio.inventoryservice.business.dto.requests.create.CreateCarRequest;
import com.kodlamaio.inventoryservice.business.dto.requests.update.UpdateCarRequest;
import com.kodlamaio.inventoryservice.business.dto.responses.create.CreateCarResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetAllCarsResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetCarResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.update.UpdateCarResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cars")
public class CarsController {
    private final CarService service;

    @GetMapping
    // Secured, PreAuthorize, PostAuthroize, PostFilter, PreFilter
    /// @Secured("ROLE_admin")
    /// @PreAuthorize("hasRole('user') or hasRole('admin')")
    @PreAuthorize("hasAnyRole('user', 'admin')")
    public List<GetAllCarsResponse> getAll(@AuthenticationPrincipal Jwt jwt) {
        var cars = service.getAll();

        var roles = KeycloakJwtRoleConverter.getRolesFromJwt(jwt);
        if (roles.contains("admin")) return cars;

        int currentYear = LocalDate.now().getYear();
        return cars.stream().filter(car -> car.getModelYear() >= currentYear - 1).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCarResponse add(@Valid @RequestBody CreateCarRequest request) {
        return service.add(request);
    }

    @GetMapping("/{id}")
    @PostAuthorize(
            "hasRole('admin') || " +
                    "returnObject.modelYear>=T(java.time.LocalDateTime).now().minusYear(1).getYear()"
    )
    public GetCarResponse getById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("Jwt: " + jwt);
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public UpdateCarResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateCarRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping(value = "/check-car-available/{id}")
    public ClientResponse checkIfCarAvailable(@PathVariable UUID id) {
        return service.checkIfCarAvailable(id);
    }

    @GetMapping("/daily-price/{id}")
    public double getDailyPrice(@PathVariable UUID id) {
        return service.getDailyPrice(id);
    }

    @GetMapping("/details/{id}")
    public GetCarDetailsResponse getDetails(@PathVariable UUID id) {
        return service.getDetails(id);
    }
}
