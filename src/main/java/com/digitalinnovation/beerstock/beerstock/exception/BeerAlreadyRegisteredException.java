package com.digitalinnovation.beerstock.beerstock.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerAlreadyRegisteredException extends Exception {
    public BeerAlreadyRegisteredException(String beerName) {
        super(String.format("Beer with name %s is already registered in the system.", beerName));
    }
}
