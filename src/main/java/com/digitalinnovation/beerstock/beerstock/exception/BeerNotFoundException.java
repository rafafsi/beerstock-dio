package com.digitalinnovation.beerstock.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BeerNotFoundException extends Exception {
    public BeerNotFoundException(String beerName) {
        super(String.format("Beer not found with name: " + beerName));
    }

    public BeerNotFoundException(Long id) {
        super(String.format("Beer not found with ID: " + id));
    }
}
