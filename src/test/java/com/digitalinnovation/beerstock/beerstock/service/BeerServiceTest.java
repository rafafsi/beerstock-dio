package com.digitalinnovation.beerstock.beerstock.service;

import com.digitalinnovation.beerstock.beerstock.builder.BeerDTOBuilder;
import com.digitalinnovation.beerstock.beerstock.dto.request.BeerDTO;
import com.digitalinnovation.beerstock.beerstock.entity.Beer;
import com.digitalinnovation.beerstock.beerstock.exception.BeerAlreadyRegisteredException;
import com.digitalinnovation.beerstock.beerstock.exception.BeerNegativeStockException;
import com.digitalinnovation.beerstock.beerstock.exception.BeerNotFoundException;
import com.digitalinnovation.beerstock.beerstock.exception.BeerStockExcededException;
import com.digitalinnovation.beerstock.beerstock.mapper.BeerMapper;
import com.digitalinnovation.beerstock.beerstock.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    private static final long INVALID_BEER_ID = 1L;

    private BeerMapper beerMapper = BeerMapper.INSTANCE;

    @Mock
    private BeerRepository beerRepository;

    @InjectMocks
    private BeerService beerService;

    @Test
    void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
        //given
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();//dados de entrada
        Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO); //tenho uma cerveja salva convertida de DTO p beer

        //when
        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
        when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

        //then
        BeerDTO createdBeerDTO = beerService.createBeer(expectedBeerDTO);

        //MatcherAssert.assertThat(createdBeerDTO.getId(), Matchers.is(Matchers.equalTo(expectedBeerDTO.getId())));
        assertThat(createdBeerDTO, is(expectedBeerDTO));


    };

    @Test
    void whenAlreadyRegistredBeerInformedThenAnExceptionShouldBeThrown() throws BeerAlreadyRegisteredException {
        //given
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer duplicatedBeer = beerMapper.toModel(expectedBeerDTO);

        //when
        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

        //then
        assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(expectedBeerDTO));
    }

    @Test
    void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
        //given
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

        //when
        when(beerRepository.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));

        //then
        BeerDTO foundBeerDTO = beerService.findByName(expectedFoundBeerDTO.getName());

        assertThat(foundBeerDTO, is(equalTo(expectedFoundBeerDTO)));
    }

    @Test
    void whenNotRegistredBeerNameIsGivenThenThrowAnException() {
        //given
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        //when
        when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.empty());

        //then
        assertThrows(BeerNotFoundException.class, () -> beerService.findByName(expectedFoundBeerDTO.getName()));
    }

    @Test
    void whenListBeerIsCalledThenReturnAListOfBeers() {
        //given
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

        //when
        when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));

        //then
        List<BeerDTO> foundListBeerDTO= (List<BeerDTO>) beerService.listAll();

        assertThat(foundListBeerDTO, is(not(empty())));
        assertThat(foundListBeerDTO.get(0), is(equalTo(expectedFoundBeerDTO)));
    }

    @Test
    void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {
        //when
        when(beerRepository.findAll()).thenReturn(Collections.emptyList());

        //then
        List<BeerDTO> foundListBeerDTO= (List<BeerDTO>) beerService.listAll();

        assertThat(foundListBeerDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithIdVallidThenABeerShouldBeDeleted() throws BeerNotFoundException {
        //given
        BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);

        //when
        when(beerRepository.findById(expectedDeletedBeerDTO.getId())).thenReturn((Optional.of(expectedDeletedBeer)));
        doNothing().when(beerRepository).deleteById(expectedDeletedBeerDTO.getId());

        //then
        beerService.deleteById(expectedDeletedBeerDTO.getId());

        verify(beerRepository, times(1)).findById(expectedDeletedBeerDTO.getId()); //cause it's a void class
        verify(beerRepository, times(1)).deleteById(expectedDeletedBeerDTO.getId());
    }


    //using TDD to develop an increment function
    @Test
    void whenIncrementIsCalledThenIncrementBeerStock() throws BeerNotFoundException, BeerStockExcededException {
        //given
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        //when
        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedBeerDTO.getQuantity() + quantityToIncrement;

        //then
        BeerDTO incrementedBeerDTO = beerService.increment(expectedBeerDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedBeerDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedBeerDTO.getMax()));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        //given
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        int quantityToIncrement = 80;
        assertThrows(BeerStockExcededException.class, () -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        //given
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        int quantityToIncrement = 45;
        assertThrows(BeerStockExcededException.class, () -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidThenThrowException() {
        int quantityToIncrement = 10;

        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class, () -> beerService.increment(INVALID_BEER_ID, quantityToIncrement));
    }

    @Test
    void whenDecrementIsCalledThenDecrementBeerStock() throws BeerNotFoundException, BeerNegativeStockException {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        int quantityToDecrement = 6;
        int expectedQuantityAfterDecrement = expectedBeerDTO.getQuantity() - quantityToDecrement;
        BeerDTO decrementedBeerDTO = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, is(decrementedBeerDTO.getQuantity()));
        assertThat(expectedQuantityAfterDecrement, is(greaterThanOrEqualTo(0)));
    }

    @Test
    void whenDecrementIsGreaterThanCurrentAmountIsCalledThenItShouldThrowAnException() {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        int quantityToDecrement = 41;

        assertThrows(BeerNegativeStockException.class, () -> beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement));
    }

    @Test
    void whenDecrementIsCalledWithBeerInvalidIdThenItShouldThrowAnException() {

        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        int quantityToDecrement = 10;

        assertThrows(BeerNotFoundException.class, () -> beerService.decrement(INVALID_BEER_ID, quantityToDecrement));
    }

}




















