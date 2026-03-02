package ru.yandex.practicum.interaction.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * DTO забронированных (или недостающих) товаров.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookedProductsDto {
    private double deliveryWeight;
    private double deliveryVolume;
    private boolean fragile;
    private Map<UUID, Long> products;
}
