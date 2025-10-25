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
        if (request.getName() == null || request.getName().isBlank()) throw new IllegalArgumentException("Produktname darf nicht leer sein!");
        if (request.getPrice() == null || request.getPrice() < 0) throw new IllegalArgumentException("Preis darf nicht negativ oder null sein!");
        if (request.getPrice() > 500000) throw new IllegalArgumentException("Preis darf 500.000 (Cent) nicht überschreiten!");

        // Nur prüfen, ob anderer Datensatz denselben Namen hat
        productRepository.findByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(productId)) {
                throw new ServiceException("Produktname '" + request.getName() + "' existiert bereits!");
            }
        });
    }

    public void validateShop(ShopRequest request) {
        if (request == null) throw new IllegalArgumentException("ShopRequest darf nicht null sein!");
        if (request.getShopName() == null || request.getShopName().isBlank()) throw new IllegalArgumentException("Shopname darf nicht leer sein!");
        if (request.getShopName().length() > 50) throw new IllegalArgumentException("Shopname darf maximal 50 Zeichen lang sein!");
    }

}