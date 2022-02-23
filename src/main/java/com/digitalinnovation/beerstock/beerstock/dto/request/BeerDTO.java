package com.digitalinnovation.beerstock.beerstock.dto.request;

import com.digitalinnovation.beerstock.beerstock.enums.BeerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class BeerDTO {

    private Long id;

    @NotEmpty
    @Size(min = 2, max = 100)
    private String name;

    @NotEmpty
    @Size(min = 2, max = 100)
    private String brand;

    @NotNull
    private int max;

    @NotNull
    private int quantity;

    @Enumerated(EnumType.STRING)
    @NotNull
    private BeerType type;
}

