package at.spengergasse.Mini.viewmodel;

// DTO
// Data Transfer Object

// Immutable (nicht veränderbar, keine setter)
// getters, hashCode, equals, toString,

import jakarta.validation.constraints.NotBlank;

public record ShopRequest(
        @NotBlank(message = "shoptitle should not be blank")
        String shopName
) {}