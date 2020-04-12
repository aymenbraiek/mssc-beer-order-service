package com.biat.msscbeerorderservice.services;

import com.biat.msscbeerorderservice.web.model.BeerOrderDto;
import com.biat.msscbeerorderservice.web.model.BeerOrderPagedList;

import java.awt.print.Pageable;
import java.util.UUID;

public interface BeerOrderService {

    BeerOrderPagedList listOrders(UUID customerId, Pageable pageable);

    BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto);

    BeerOrderDto getOrderById(UUID customerId, UUID orderId);

    void pickupOrder(UUID customerId, UUID orderId);
}
