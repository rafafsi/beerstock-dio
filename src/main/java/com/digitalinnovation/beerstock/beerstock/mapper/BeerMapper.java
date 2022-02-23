package com.digitalinnovation.beerstock.beerstock.mapper;

import com.digitalinnovation.beerstock.beerstock.dto.request.BeerDTO;
import com.digitalinnovation.beerstock.beerstock.entity.Beer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BeerMapper {

    BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);

    Beer toModel(BeerDTO beerDTO);

    BeerDTO toDTO(Beer beer);
}
