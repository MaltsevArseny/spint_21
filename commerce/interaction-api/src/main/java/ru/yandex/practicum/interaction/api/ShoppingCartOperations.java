package ru.yandex.practicum.interaction.api;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.interaction.api.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

/**
 * Контракт операций корзины покупателя.
 * Реализуется и {@link ru.yandex.practicum.interaction.api.client.ShoppingCartClient},
 * и ShoppingCartController сервиса shopping-cart.
 */
public interface ShoppingCartOperations {

    /**
     * Получить корзину пользователя.
     */
    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam("username") @NotBlank String username);

    /**
     * Добавить товары в корзину.
     */
    @PutMapping
    ShoppingCartDto addProducts(@RequestParam("username") @NotBlank String username,
                                @RequestBody @NotNull Map<UUID, Long> products);

    /**
     * Деактивировать корзину.
     */
    @DeleteMapping
    void deactivateCart(@RequestParam("username") @NotBlank String username);

    /**
     * Удалить товар из корзины.
     */
    @DeleteMapping("/remove")
    ShoppingCartDto removeProduct(@RequestParam("username") @NotBlank String username,
                                  @RequestParam("productId") @NotNull UUID productId);

    /**
     * Изменить количество товара в корзине.
     */
    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam("username") @NotBlank String username,
                                          @RequestBody @Valid ChangeProductQuantityRequest request);
}
