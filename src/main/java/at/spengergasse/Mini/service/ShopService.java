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
import at.spengergasse.Mini.viewmodel.ShopRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopService.class);

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final Guard guard;

    @Transactional
    public Shop createShop(ShopRequest request) {
        guard.validateShop(request);

        shopRepository.findByShopName(request.getShopName()).ifPresent(existing -> {
            throw new ServiceException("Shopname '" + request.getShopName() + "' existiert bereits!");
        });

        Shop shop = new Shop(request.getShopName());

        shopRepository.save(shop);

        LOGGER.info("Created shop '{}'", shop.getShopName());
        return shop;
    }

    public Shop getShopById(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> ServiceException.ofNotFound(shopId));
    }

    @Transactional
    public Product addProductToShop(Long shopId, ProductRequest request) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> ServiceException.ofNotFound(shopId));

        guard.validateProduct(request, null); // null, da es ein neues Produkt ist

        Product product = new Product(request.getName(), request.getPrice(), shop);

        shop.getProducts().add(product);

        productRepository.save(product);

        LOGGER.info("Added product '{}' to shop '{}'", product.getName(), shop.getShopName());
        return product;
    }

    @Transactional
    public void removeProductFromShop(Long shopId, Long productId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> ServiceException.ofNotFound(shopId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ServiceException.ofNotFound(productId));

        if (!shop.getProducts().contains(product)) {
            throw new ServiceException("Produkt gehört nicht zu diesem Shop!");
        }

        shop.getProducts().remove(product);
        product = product.withUpdatedValues(product.getName(), product.getPrice(), null);

        productRepository.save(product);
        LOGGER.info("Removed product '{}' from shop '{}'", product.getName(), shop.getShopName());
    }

    @Transactional
    public Product updateProductInShop(Long shopId, Long productId, ProductRequest request) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> ServiceException.ofNotFound(shopId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ServiceException.ofNotFound(productId));

        if (!shop.getProducts().contains(product)) {
            throw new ServiceException("Produkt gehört nicht zu diesem Shop!");
        }

        guard.validateProduct(request, productId);

        LOGGER.info("Updated product '{}' (id={}) in shop '{}'", product.getName(), productId, shop.getShopName());
        return productService.updateProduct(productId, request);
    }

    public List<Product> getAllProductsFromShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> ServiceException.ofNotFound(shopId));
        return shop.getProducts();
    }

    @Transactional
    public void deleteShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> ServiceException.ofNotFound(shopId));

        // Produkte vom Shop trennen, bevor der Shop gelöscht wird
        for (Product product : shop.getProducts()) {
            product = product.withUpdatedValues(product.getName(), product.getPrice(), null);
            productRepository.save(product);
        }

        shopRepository.delete(shop);
        LOGGER.info("Deleted shop '{}' (id={})", shop.getShopName(), shopId);
    }
}