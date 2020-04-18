package com.biat.msscbeerorderservice.web.mappers;

import com.biat.msscbeerorderservice.domain.BeerOrderLine;
import com.biat.msscbeerorderservice.web.model.BeerOrderLineDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
@DecoratedWith(BeerOrderLineMapperDecorator.class)
@Mapper(uses = {DateMapper.class})
public interface BeerOrderLineMapper {
    BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line);

    BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto);
}
