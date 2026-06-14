package com.example.lt_web2.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sku_code", nullable = false, unique = true)
    private String skuCode;

    private String color;

    private String size;

    @Column(name = "import_price")
    private BigDecimal importPrice;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    private String barcode;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // Getter Setter
}