package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Сущность товара на складе.
 * Хранит физические характеристики товара и его текущее количество.
 */
@Entity
@Table(name = "warehouse_products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseProduct {

    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "fragile")
    private boolean fragile;

    @Column(name = "width")
    private double width;

    @Column(name = "height")
    private double height;

    @Column(name = "depth")
    private double depth;

    @Column(name = "weight")
    private double weight;

    @Column(name = "quantity")
    private long quantity;
}
