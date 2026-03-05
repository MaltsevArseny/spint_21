package ru.yandex.practicum.interaction.api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.interaction.api.enums.ProductCategory;
import ru.yandex.practicum.interaction.api.enums.ProductState;
import ru.yandex.practicum.interaction.api.enums.QuantityState;

/**
 * DTO товара витрины.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private UUID productId;

    @NotBlank(message = "Название товара не может быть пустым")
    private String productName;

    private String description;
    private String imageSrc;
    private QuantityState quantityState;
    private ProductState productState;

    @NotNull(message = "Категория товара обязательна")
    private ProductCategory productCategory;

    @Positive(message = "Цена должна быть больше нуля")
    private double price;
}
