package com.digitalinnovation.beerstock.beerstock.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerStockExcededException extends Throwable {
    public BeerStockExcededException(Long id, int quantityToIncrement) {
            super(String.format("Beer with id %d has exceeded max stock when tried to increment by %d", id, quantityToIncrement));
    }
}
