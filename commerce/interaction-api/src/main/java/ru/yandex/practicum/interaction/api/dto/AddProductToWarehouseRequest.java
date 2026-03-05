package ru.yandex.practicum.interaction.api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Запрос на добавление количества товара на склад.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductToWarehouseRequest {

    @NotNull(message = "ID товара обязателен")
    private UUID productId;

    @Positive(message = "Количество должно быть больше нуля")
    private long quantity;
}
