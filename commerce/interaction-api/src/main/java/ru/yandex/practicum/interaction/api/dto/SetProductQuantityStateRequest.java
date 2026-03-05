package ru.yandex.practicum.interaction.api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.interaction.api.enums.QuantityState;

/**
 * Запрос на изменение состояния количества товара.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetProductQuantityStateRequest {

    @NotNull(message = "ID товара обязателен")
    private UUID productId;

    @NotNull(message = "Состояние количества обязательно")
    private QuantityState quantityState;
}
