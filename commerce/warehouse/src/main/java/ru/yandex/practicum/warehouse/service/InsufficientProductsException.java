package ru.yandex.practicum.warehouse.service;

import lombok.Getter;

import java.util.Map;
import java.util.UUID;

/**
 * Исключение, выбрасываемое при недостаточном количестве товаров на складе.
 */
@Getter
public class InsufficientProductsException extends RuntimeException {

    /**
     * Карта недостающих товаров: productId → недостающее количество.
     */
    private final Map<UUID, Long> insufficientProducts;

    public InsufficientProductsException(String message, Map<UUID, Long> insufficientProducts) {
        super(message);
        this.insufficientProducts = insufficientProducts;
    }
}
