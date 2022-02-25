package com.digitalinnovation.beerstock.beerstock.service;

import com.digitalinnovation.beerstock.beerstock.dto.request.BeerDTO;
import com.digitalinnovation.beerstock.beerstock.entity.Beer;
import com.digitalinnovation.beerstock.beerstock.exception.BeerAlreadyRegisteredException;
import com.digitalinnovation.beerstock.beerstock.exception.BeerNegativeStockException;
import com.digitalinnovation.beerstock.beerstock.exception.BeerNotFoundException;
import com.digitalinnovation.beerstock.beerstock.exception.BeerStockExcededException;
import com.digitalinnovation.beerstock.beerstock.mapper.BeerMapper;
import com.digitalinnovation.beerstock.beerstock.repository.BeerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper = BeerMapper.INSTANCE;

    public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
        verifyIfAlreadyRegistrered(beerDTO.getName());
        Beer beer = beerMapper.toModel(beerDTO);
        Beer savedBeer = beerRepository.save(beer);

        return beerMapper.toDTO(savedBeer);
    }

    public BeerDTO findByName(String name) throws BeerNotFoundException {
        Beer foundBeer = beerRepository.findByName(name)
                .orElseThrow(() -> new BeerNotFoundException(name));
        return beerMapper.toDTO(foundBeer);
    }

    public BeerDTO listAll() {
        return (BeerDTO) beerRepository.findAll()
                .stream()
                .map(beerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws BeerNotFoundException {
        verifyIfExists(id);
        beerRepository.deleteById(id);
    }

    public void verifyIfAlreadyRegistrered(String name) throws BeerAlreadyRegisteredException {
        Optional<Beer> optSavedBeer = beerRepository.findByName(name);
        if (optSavedBeer.isPresent()) {
            throw new BeerAlreadyRegisteredException(name);
        }
    }

    private Beer verifyIfExists(Long id) throws BeerNotFoundException {
        return beerRepository.findById(id)
                .orElseThrow(() -> new BeerNotFoundException(id));
    }

    public BeerDTO increment(Long id, int quantityToIncrement) throws BeerNotFoundException, BeerStockExcededException {
        Beer beerToIncrementStock = verifyIfExists(id);
        int quantityAfterIncrement = quantityToIncrement + beerToIncrementStock.getQuantity();
        if (quantityAfterIncrement <= beerToIncrementStock.getMax()) {
            beerToIncrementStock.setQuantity(beerToIncrementStock.getQuantity() + quantityToIncrement);
            Beer incrementedBeerStock = beerRepository.save(beerToIncrementStock);
            return beerMapper.toDTO(incrementedBeerStock);
        }
        throw new BeerStockExcededException(id, quantityToIncrement);
    }

    public BeerDTO decrement(Long id, Integer quantityToDecrement) throws BeerNotFoundException, BeerNegativeStockException {
        Beer beerToDecrementStock = verifyIfExists(id);

        if(quantityToDecrement > beerToDecrementStock.getQuantity()) {
            throw new BeerNegativeStockException(id, quantityToDecrement);
        }

        int quantityAfterDecrement = beerToDecrementStock.getQuantity() - quantityToDecrement;

        beerToDecrementStock.setQuantity(quantityAfterDecrement);
        Beer incrementedBeerStock = beerRepository.save(beerToDecrementStock);
        return beerMapper.toDTO(incrementedBeerStock);
    }
}
