package ru.yandex.practicum.interaction.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.interaction.api.enums.CartState;

import java.util.Map;
import java.util.UUID;

/**
 * DTO корзины покупателя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDto {
    private UUID shoppingCartId;
    private Map<UUID, Long> products;
    private String username;
    private CartState cartState;
}
