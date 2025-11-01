package at.spengergasse.Mini.viewmodel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

// DTO
// Data Transfer Object

// Immutable (nicht veränderbar, keine setter)
// getters, hashCode, equals, toString,

public record ProductRequest(
        @NotEmpty(message = "Produktname darf nicht leer sein")
        @Size(max = 50, message = "Produktname darf maximal 50 Zeichen haben")
        String name,

        @Min(value = 0, message = "Preis darf nicht negativ sein")
        @Max(value = 500_000, message = "Preis zu hoch")
        Double price
) {}