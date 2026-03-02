package ru.yandex.practicum.interaction.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

/**
 * Feign-клиент для сервиса корзины покупателя (shopping-cart).
 */
@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {

    /**
     * Получить корзину пользователя.
     */
    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam("username") String username);

    /**
     * Добавить товары в корзину.
     */
    @PutMapping
    ShoppingCartDto addProducts(@RequestParam("username") String username,
                                @RequestBody Map<UUID, Long> products);

    /**
     * Деактивировать корзину.
     */
    @DeleteMapping
    void deactivateCart(@RequestParam("username") String username);

    /**
     * Удалить товар из корзины.
     */
    @DeleteMapping("/remove")
    ShoppingCartDto removeProduct(@RequestParam("username") String username,
                                  @RequestParam("productId") UUID productId);

    /**
     * Изменить количество товара в корзине.
     */
    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam("username") String username,
                                          @RequestBody ChangeProductQuantityRequest request);
}
