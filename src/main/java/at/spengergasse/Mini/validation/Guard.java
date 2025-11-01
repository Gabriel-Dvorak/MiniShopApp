package at.spengergasse.Mini.validation;

import at.spengergasse.Mini.exceptions.ServiceException;
import at.spengergasse.Mini.persistence.ProductRepository;
import at.spengergasse.Mini.viewmodel.ProductRequest;
import at.spengergasse.Mini.viewmodel.ShopRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Guard Pattern (Defensive Programming)
// ---------------------------------
// Centralized validation logic
// Throws IllegalArgumentException on failure
// Returns validated/normalized value on success
// Used by value objects to enforce invariants

@Component
@RequiredArgsConstructor
public class Guard {

    private final ProductRepository productRepository;

    public void validateProduct(ProductRequest request, Long productId) {
        if (request == null) throw new IllegalArgumentException("ProductRequest darf nicht null sein!");
        if (request.name() == null || request.name().isBlank()) throw new IllegalArgumentException("Produktname darf nicht leer sein!");
        if (request.price() == null || request.price() < 0) throw new IllegalArgumentException("Preis darf nicht negativ oder null sein!");
        if (request.price() > 500000) throw new IllegalArgumentException("Preis darf 500.000 (Cent) nicht überschreiten!");

        // Nur prüfen, ob anderer Datensatz denselben Namen hat
        productRepository.findByName(request.name()).ifPresent(existing -> {
            if (!existing.getId().equals(productId)) {
                throw new ServiceException("Produktname '" + request.name() + "' existiert bereits!");
            }
        });
    }

    public void validateShop(ShopRequest request) {
        if (request == null) throw new IllegalArgumentException("ShopRequest darf nicht null sein!");
        if (request.shopName() == null || request.shopName().isBlank()) throw new IllegalArgumentException("Shopname darf nicht leer sein!");
        if (request.shopName().length() > 50) throw new IllegalArgumentException("Shopname darf maximal 50 Zeichen lang sein!");
    }

}