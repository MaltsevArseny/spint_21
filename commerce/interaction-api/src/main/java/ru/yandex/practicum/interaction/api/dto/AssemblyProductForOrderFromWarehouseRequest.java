package ru.yandex.practicum.interaction.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Запрос на сборку товаров для заказа со склада.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssemblyProductForOrderFromWarehouseRequest {
    private Map<UUID, Long> products;
    private UUID orderId;
}
