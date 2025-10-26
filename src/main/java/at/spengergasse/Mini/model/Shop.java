package at.spengergasse.Mini.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true, exclude = "products")
@Getter
@Entity
@Table(name = "shop")
public class Shop extends BaseEntity {

    @Column(name = "shopName", unique = true, nullable = false, length = 50)
    @NotEmpty
    @Size(max = 50)
    private String shopName;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    public Shop() {}

    public Shop(String shopName) {
        super();
        this.shopName = shopName;
    }

    public void addProduct(Product product) {
        if (!products.contains(product)) {
            products.add(product);
            if (product.getShop() != this) {
                product.setShop(this);
            }
        }
    }

    public void removeProduct(Product product) {
        products.remove(product);
        if (product.getShop() == this) {
            product.setShop(null);
        }
    }
}