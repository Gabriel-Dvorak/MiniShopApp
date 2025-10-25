package at.spengergasse.Mini.viewmodel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

// DTO
// Data Transfer Object

// Immutable (nicht veränderbar, keine setter)
// getters, hashCode, equals, toString,

@Getter
@AllArgsConstructor
public class ProductRequest {

    @NotEmpty(message = "Produktname darf nicht leer sein")
    @Size(max = 50, message = "Produktname darf maximal 50 Zeichen haben")
    private String name;

    @Min(value = 0, message = "Preis darf nicht negativ sein")
    @Max(value = 500_000, message = "Preis zu hoch")
    private Double price;
}