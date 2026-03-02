package ru.yandex.practicum.interaction.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.api.enums.ProductCategory;

import java.util.List;
import java.util.UUID;

/**
 * Feign-клиент для сервиса витрины товаров (shopping-store).
 */
@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {

    /**
     * Получить список товаров по категории.
     */
    @GetMapping
    List<ProductDto> getProducts(@RequestParam("category") ProductCategory category);

    /**
     * Получить товар по идентификатору.
     */
    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable("productId") UUID productId);

    /**
     * Создать новый товар.
     */
    @PostMapping
    ProductDto createProduct(@RequestBody ProductDto productDto);

    /**
     * Обновить товар.
     */
    @PutMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    /**
     * Удалить товар (мягкое удаление — деактивация).
     */
    @DeleteMapping("/{productId}")
    boolean deleteProduct(@PathVariable("productId") UUID productId);

    /**
     * Установить состояние количества товара.
     */
    @PostMapping("/quantityState")
    boolean setQuantityState(@RequestBody SetProductQuantityStateRequest request);
}
