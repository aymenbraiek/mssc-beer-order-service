package com.biat.msscbeerorderservice.services;

import com.biat.msscbeerorderservice.domain.BeerOrder;
import com.biat.msscbeerorderservice.domain.Customer;
import com.biat.msscbeerorderservice.domain.OrderStatusEnum;
import com.biat.msscbeerorderservice.repositories.BeerOrderRepository;
import com.biat.msscbeerorderservice.repositories.CustomerRepository;
import com.biat.msscbeerorderservice.web.mappers.BeerOrderMapper;
import com.biat.msscbeerorderservice.web.model.BeerOrderDto;
import com.biat.msscbeerorderservice.web.model.BeerOrderPagedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BeerOrderServiceImpl implements BeerOrderService{
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final ApplicationEventPublisher publisher;

    public BeerOrderServiceImpl(BeerOrderRepository beerOrderRepository,
                                CustomerRepository customerRepository,
                                BeerOrderMapper beerOrderMapper, ApplicationEventPublisher publisher) {
        this.beerOrderRepository = beerOrderRepository;
        this.customerRepository = customerRepository;
        this.beerOrderMapper = beerOrderMapper;
        this.publisher = publisher;
    }



    @Override
    public BeerOrderPagedList listOrders(UUID customerId, PageRequest pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Page<BeerOrder> beerOrderPage =
                    beerOrderRepository.findAllByCustomer(customerOptional.get(), (Pageable) pageable);

            return new BeerOrderPagedList(beerOrderPage
                    .stream()
                    .map(beerOrderMapper::beerOrderToDto)
                    .collect(Collectors.toList()), PageRequest.of(
                    beerOrderPage.getPageable().getPageNumber(),
                    beerOrderPage.getPageable().getPageSize()),
                    beerOrderPage.getTotalElements());
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            BeerOrder beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
            beerOrder.setId(null); //should not be set by outside client
            beerOrder.setCustomer(customerOptional.get());
            beerOrder.setOrderStatus(OrderStatusEnum.NEW);

            beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));

            BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);

            log.debug("Saved Beer Order: " + beerOrder.getId());

            //todo impl
            //  publisher.publishEvent(new NewBeerOrderEvent(savedBeerOrder));

            return beerOrderMapper.beerOrderToDto(savedBeerOrder);
        }
        //todo add exception type
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
        return beerOrderMapper.beerOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        BeerOrder beerOrder = getOrder(customerId, orderId);
        beerOrder.setOrderStatus(OrderStatusEnum.PICKED_UP);

        beerOrderRepository.save(beerOrder);
    }

    private BeerOrder getOrder(UUID customerId, UUID orderId){
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if(customerOptional.isPresent()){
            Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(orderId);

            if(beerOrderOptional.isPresent()){
                BeerOrder beerOrder = beerOrderOptional.get();

                // fall to exception if customer id's do not match - order not for customer
                if(beerOrder.getCustomer().getId().equals(customerId)){
                    return beerOrder;
                }
            }
            throw new RuntimeException("Beer Order Not Found");
        }
        throw new RuntimeException("Customer Not Found");
    }
}
