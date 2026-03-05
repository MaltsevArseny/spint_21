package ru.yandex.practicum.interaction.api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Запрос на изменение количества товара в корзине.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProductQuantityRequest {

    @NotNull(message = "ID товара обязателен")
    private UUID productId;

    @Positive(message = "Новое количество должно быть больше нуля")
    private long newQuantity;
}
