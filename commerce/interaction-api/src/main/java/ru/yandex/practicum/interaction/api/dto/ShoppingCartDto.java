package ru.yandex.practicum.interaction.api.dto;

import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.interaction.api.enums.CartState;

/**
 * DTO корзины покупателя.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDto {

    @NotNull(message = "ID корзины обязателен")
    private UUID shoppingCartId;

    private Map<UUID, Long> products;

    @NotBlank(message = "Имя пользователя обязательно")
    private String username;

    private CartState cartState;
}
