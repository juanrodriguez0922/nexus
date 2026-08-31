package nexusmarket.domain.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import nexusmarket.domain.valueobjects.ProductStatus;
import nexusmarket.domain.valueobjects.ProductType;

/**
 * Represents a physical or digital good offered through the marketplace catalog.
 *
 * <p>Physical products require inventory management and dispatch. Digital products
 * are delivered immediately after payment and do not require warehouse stock.</p>
 */
public class Product {

    private final String productId;
    private String name;
    private final ProductType productType;
    private final List<ProductVariant> variants = new ArrayList<>();
    private ProductStatus status;
    private final Seller seller;
    private BigDecimal price;

    public Product(String productId, String name, ProductType productType,
                   ProductStatus status, Seller seller, BigDecimal price) {
        Objects.requireNonNull(productId, "Product id is required");
        Objects.requireNonNull(name, "Product name is required");
        this.productId = productId;
        this.name = name;
        this.productType = productType == null ? ProductType.PHYSICAL : productType;
        this.status = status == null ? ProductStatus.PUBLISHED : status;
        this.seller = Objects.requireNonNull(seller, "Product seller is required");
        setPrice(price);
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Objects.requireNonNull(name, "Product name is required");
        this.name = name;
    }

    public ProductType getProductType() {
        return productType;
    }

    public List<ProductVariant> getVariants() {
        return Collections.unmodifiableList(variants);
    }

    /** Adds a variation (color, size, model, etc.) to this product. */
    public void addVariant(ProductVariant variant) {
        Objects.requireNonNull(variant, "Variant is required");
        variants.add(variant);
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status == null ? ProductStatus.PUBLISHED : status;
    }

    public void publish() {
        this.status = ProductStatus.PUBLISHED;
    }

    public void suspend() {
        this.status = ProductStatus.SUSPENDED;
    }

    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
    }

    public Seller getSeller() {
        return seller;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Product price must be a non-negative amount");
        }
        this.price = price;
    }

    /** Only physical products require inventory management. */
    public boolean isPhysical() {
        return productType == ProductType.PHYSICAL;
    }

    /** Ensures the product is visible and available in the public catalog. */
    public void ensureAvailable() {
        if (status != ProductStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Product " + productId + " is not available for sale (status: "
                            + status.getName() + ")");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Product product)) {
            return false;
        }
        return productId.equals(product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "Product{productId='" + productId + "', name='" + name
                + "', type=" + productType + ", status=" + status + "}";
    }
}