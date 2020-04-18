package com.biat.msscbeerorderservice.services.Beer;

import com.biat.msscbeerorderservice.web.model.BeerDto;
import com.biat.msscbeerorderservice.web.model.BeerOrderDto;

import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    Optional<BeerDto> getBeerById(UUID uuid);

    Optional<BeerDto> getBeerByUpc(String upc);
}
