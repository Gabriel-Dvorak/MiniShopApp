package at.spengergasse.Mini.service;

// Service Layer (Business Logic Layer)
// ---------------------------------
// Business Logic Layer
// Handles Business Rules, Complex Business Logic
// Orchestrates between Controller and Repository
// Manages Transactions
// Throws Business Exceptions (ServiceException)


// Transaction Management Best Practice
// ---------------------------------
// CLASS LEVEL:  @Transactional(readOnly = true)  -> Default for all methods
//   - Most service methods are reads (queries)
//   - Safe by default (read-only)
//   - Less code repetition
//
// METHOD LEVEL: @Transactional -> Overrides class-level for write operations
//   - Explicitly marks write operations (INSERT, UPDATE, DELETE)
//   - Clear intent: "this method modifies data"
//   - Only applied where needed


// JPA Dirty Checking
// ---------------------------------
// - JPA automatically tracks changes to managed entities within a transaction
// - No need to explicitly call save() after modifying an entity
// - Changes are flushed to the database when the transaction commits


// Logging Best Practices
// ---------------------------------
// DEBUG level:
//   - For development and debugging
//   - Method entry/exit with parameters
//   - Detailed step-by-step execution
//
// INFO level:
//   - For production logging
//   - Important business events (created, updated, deleted)
//   - Keep concise (performance!)
//
// WARN level:
//   - Business rule violations
//   - Handled exceptions
//
// ERROR level:
//   - Unexpected failures

import at.spengergasse.Mini.exceptions.ServiceException;
import at.spengergasse.Mini.model.Product;
import at.spengergasse.Mini.model.Shop;
import at.spengergasse.Mini.persistence.ProductRepository;
import at.spengergasse.Mini.persistence.ShopRepository;
import at.spengergasse.Mini.validation.Guard;
import at.spengergasse.Mini.viewmodel.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final Guard guard;

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ServiceException.ofNotFound(productId));

        if (product.getShop() != null) {
            throw new ServiceException("Produkt gehört zu einem Shop und kann nur über den Shop gelöscht werden!");
        }

        productRepository.delete(product);
        LOGGER.info("Deleted product {} (not associated with any shop)", productId);
    }

    @Transactional
    public Product createProduct(ProductRequest request, Long shopId) {
        Shop shop = null;
        if (shopId != null) {
            shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> ServiceException.ofNotFound(shopId));
        }

        Product product = new Product(request.getName(), request.getPrice(), shop);

        // Wenn Shop existiert, Bidirektionalität pflegen
        if (shop != null) {
            shop.getProducts().add(product);
        }

        productRepository.save(product);
        LOGGER.info("Created product '{}'", product.getName());
        return product;
    }

    @Transactional
    public Product updateProduct(Long productId, ProductRequest request) {
        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> ServiceException.ofNotFound(productId));

        guard.validateProduct(request, productId);

        Product updated = existing.withUpdatedValues(
                request.getName(),
                request.getPrice()
        );

        LOGGER.info("Updated product '{}' (id={})", existing.getName(), productId);
        return productRepository.save(updated);
    }
}