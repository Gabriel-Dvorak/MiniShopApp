package at.spengergasse.Mini.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true, exclude = "shop")
@Getter
@Entity
@Table(name = "product")
public class Product extends BaseEntity {

    @Column(name = "name", unique = true, nullable = false, length = 50)
    @NotEmpty @Size(max = 50)
    private String name;

    @Column(name = "price", nullable = false)
    @Min(0)
    @Max(500000) // In Cent
    private Double price;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    public Product() {}

    public Product(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public Product(String name, Double price, Shop shop) {
        super();
        this.name = name;
        this.price = price;
        if (shop != null) {
            shop.addProduct(this); // synchronisiert beide Seiten
        }
    }

    public Product withUpdatedValues(String newName, Double newPrice) {
        return new Product(newName, newPrice, this.shop);
    }

    public Product withUpdatedValues(String newName, Double newPrice, Shop newShop) {
        return new Product(newName, newPrice, newShop);
    }

    public void setShop(Shop shop) {
        if (this.shop != null && this.shop != shop) {
            this.shop.getProducts().remove(this);
        }
        this.shop = shop;
        if (shop != null && !shop.getProducts().contains(this)) {
            shop.getProducts().add(this);
        }
    }
}