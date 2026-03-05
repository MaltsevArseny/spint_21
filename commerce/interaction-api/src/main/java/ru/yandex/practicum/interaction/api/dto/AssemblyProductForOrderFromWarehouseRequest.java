package ru.yandex.practicum.interaction.api.dto;

import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Запрос на сборку товаров для заказа со склада.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssemblyProductForOrderFromWarehouseRequest {

    @NotNull(message = "Список товаров обязателен")
    @NotEmpty(message = "Список товаров не может быть пустым")
    private Map<UUID, Long> products;

    @NotNull(message = "ID заказа обязателен")
    private UUID orderId;
}
