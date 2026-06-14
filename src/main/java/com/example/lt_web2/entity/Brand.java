package com.example.lt_web2.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "brands")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    private String logo;

    private String description;

    @OneToMany(mappedBy = "brand")
    private List<Product> products;

    // Getter Setter
}