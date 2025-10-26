package at.spengergasse.Mini.persistence;

import at.spengergasse.Mini.model.Product;
import at.spengergasse.Mini.model.Shop;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest  // Läuft mit H2 In-Memory DB
class ShopRepositoryTest {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testShopAndProductsPersisted() {
        Shop shop = new Shop("SuperShop");
        shopRepository.save(shop);

        Product product1 = new Product("Apple", 1.5, shop);
        Product product2 = new Product("Banana", 2.0, shop);

        productRepository.saveAll(List.of(product1, product2));

        // Prüfen, dass Shop gespeichert wurde
        assertThat(shopRepository.findById(shop.getId())).isPresent();

        // Prüfen 1:n Beziehung
        Shop savedShop = shopRepository.findById(shop.getId()).get();
        assertThat(savedShop.getProducts()).hasSize(2);
    }

    @Test
    void testSaveAndFind() {
        Shop shop = new Shop("TestShop");
        shopRepository.save(shop);

        assertTrue(shopRepository.findByShopName("TestShop").isPresent());
    }
}
