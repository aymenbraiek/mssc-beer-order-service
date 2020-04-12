package com.biat.msscbeerorderservice.repositories;

import com.biat.msscbeerorderservice.domain.BeerOrder;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.UUID;

public interface BeerOrderLineRepository extends JpaRepository<BeerOrder, UUID> {
}
