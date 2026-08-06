package com.ecommerce.sb_ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "categories")
public class Category {

    @Id
    private Long categoryId;
    private String categoryName;

}
