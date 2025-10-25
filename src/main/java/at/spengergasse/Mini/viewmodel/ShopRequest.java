package at.spengergasse.Mini.viewmodel;

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
public class ShopRequest {

    @NotEmpty(message = "Shopname darf nicht leer sein")
    @Size(max = 50, message = "Shopname darf maximal 50 Zeichen haben")
    private String shopName;
}