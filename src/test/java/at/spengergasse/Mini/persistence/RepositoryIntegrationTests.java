package at.spengergasse.Mini.persistence;

import at.spengergasse.Mini.model.Product;
import at.spengergasse.Mini.model.Shop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepositoryIntegrationTests {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    // ===== Shop-bezogene Tests =====
    @Nested
    @DisplayName("Shop Repository Tests")
    class ShopTests {

        @Test
        @DisplayName("Shop speichern und abrufen")
        void testSaveAndFindShop() {
            Shop shop = new Shop("TestShop");
            Shop saved = shopRepository.save(shop);
            Optional<Shop> found = shopRepository.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getShopName()).isEqualTo("TestShop");
        }

        @Test
        @DisplayName("Shop löschen")
        void testDeleteShop() {
            Shop shop = shopRepository.save(new Shop("DeleteShop"));
            shopRepository.delete(shop);
            Optional<Shop> found = shopRepository.findById(shop.getId());
            assertThat(found).isNotPresent();
        }

        @Test
        @DisplayName("Shop nach Name suchen")
        void testFindShopByName() {
            shopRepository.save(new Shop("UniqueShop"));
            Optional<Shop> found = shopRepository.findByShopName("UniqueShop");
            assertThat(found).isPresent();
            assertThat(found.get().getShopName()).isEqualTo("UniqueShop");
        }

        @Test
        @DisplayName("1:n Beziehung Shop → Products prüfen")
        void testShopProductsRelationship() {
            Shop shop = new Shop("WeaponShop");
            Product p1 = new Product("Axe", 50.0);
            Product p2 = new Product("Hammer", 75.0);

            shop.addProduct(p1);
            shop.addProduct(p2);
            shopRepository.saveAndFlush(shop); // <-- wichtig

            Optional<Shop> foundShop = shopRepository.findById(shop.getId());
            assertThat(foundShop).isPresent();
            assertThat(foundShop.get().getProducts())
                    .hasSize(2)
                    .extracting("name")
                    .containsExactlyInAnyOrder("Axe", "Hammer");

            foundShop.get().removeProduct(p1);
            shopRepository.saveAndFlush(foundShop.get()); // <-- wichtig

            foundShop = shopRepository.findById(shop.getId());
            assertThat(foundShop.get().getProducts())
                    .hasSize(1)
                    .extracting("name")
                    .containsExactly("Hammer");
        }

        @Test
        @DisplayName("Orphan Removal prüfen")
        void testOrphanRemoval() {
            Shop shop = new Shop("OrphanShop");
            Product p = new Product("Potion", 10.0);
            shop.addProduct(p);
            shopRepository.saveAndFlush(shop);

            shop.removeProduct(p);
            shopRepository.saveAndFlush(shop);

            assertThat(productRepository.findById(p.getId())).isNotPresent();
        }

        @Test
        @DisplayName("Doppelte Produkte verhindern")
        void testPreventDuplicateProducts() {
            Shop shop = new Shop("DuplicateShop");
            Product p = new Product("Elixir", 20.0);
            shop.addProduct(p);
            shop.addProduct(p); // nochmal hinzufügen
            shopRepository.saveAndFlush(shop);

            Optional<Shop> foundShop = shopRepository.findById(shop.getId());
            assertThat(foundShop.get().getProducts()).hasSize(1);
        }

        @Test
        @DisplayName("Cascade Delete prüfen")
        void testCascadeDelete() {
            Shop shop = new Shop("CascadeShop");
            Product p1 = new Product("Sword", 50.0);
            Product p2 = new Product("Shield", 60.0);
            shop.addProduct(p1);
            shop.addProduct(p2);
            shopRepository.saveAndFlush(shop);

            shopRepository.delete(shop);
            shopRepository.flush();

            assertThat(shopRepository.findById(shop.getId())).isNotPresent();
            assertThat(productRepository.findById(p1.getId())).isNotPresent();
            assertThat(productRepository.findById(p2.getId())).isNotPresent();
        }
    }

    // ===== Product-bezogene Tests =====
    @Nested
    @DisplayName("Product Repository Tests")
    class ProductTests {

        @Test
        @DisplayName("Product speichern und abrufen")
        void testSaveAndFindProduct() {
            Product product = new Product("Sword", 99.99, null);
            Product saved = productRepository.save(product);
            Optional<Product> found = productRepository.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Sword");
            assertThat(found.get().getPrice()).isEqualTo(99.99);
        }

        @Test
        @DisplayName("Product löschen")
        void testDeleteProduct() {
            Product product = productRepository.save(new Product("Shield", 99.99, null));
            productRepository.delete(product);
            Optional<Product> found = productRepository.findById(product.getId());
            assertThat(found).isNotPresent();
        }

        @Test
        @DisplayName("existsByName prüfen")
        void testExistsByName() {
            productRepository.save(new Product("Potion", 99.99, null));
            assertThat(productRepository.existsByName("Potion")).isTrue();
            assertThat(productRepository.existsByName("Elixir")).isFalse();
        }

        @Test
        @DisplayName("findByName prüfen")
        void testFindByName() {
            productRepository.save(new Product("Bow", 99.99, null));
            Optional<Product> found = productRepository.findByName("Bow");
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Bow");
        }
    }
}
