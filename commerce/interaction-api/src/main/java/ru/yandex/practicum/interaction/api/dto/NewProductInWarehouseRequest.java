package ru.yandex.practicum.interaction.api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Запрос на добавление нового товара на склад.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProductInWarehouseRequest {

    @NotNull(message = "ID товара обязателен")
    private UUID productId;

    private boolean fragile;

    @Positive(message = "Ширина должна быть больше нуля")
    private double width;

    @Positive(message = "Высота должна быть больше нуля")
    private double height;

    @Positive(message = "Глубина должна быть больше нуля")
    private double depth;

    @Positive(message = "Вес должен быть больше нуля")
    private double weight;
}
