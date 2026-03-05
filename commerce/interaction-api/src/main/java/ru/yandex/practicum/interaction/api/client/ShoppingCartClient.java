package ru.yandex.practicum.interaction.api.client;

import org.springframework.cloud.openfeign.FeignClient;

import ru.yandex.practicum.interaction.api.ShoppingCartOperations;

/**
 * Feign-клиент для сервиса корзины покупателя (shopping-cart).
 * Наследует контракт из ShoppingCartOperations.
 */
@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient extends ShoppingCartOperations {
}
