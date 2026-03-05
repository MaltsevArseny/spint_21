package ru.yandex.practicum.interaction.api.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.api.enums.ProductCategory;

/**
 * Feign-клиент для сервиса витрины товаров (shopping-store).
 */
@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {

    /**
     * Получить список товаров по категории.
     */
    @GetMapping
    List<ProductDto> getProducts(@RequestParam("category") @NotNull ProductCategory category);

    /**
     * Получить товар по идентификатору.
     */
    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable("productId") @NotNull UUID productId);

    /**
     * Создать новый товар.
     */
    @PostMapping
    ProductDto createProduct(@RequestBody @Valid ProductDto productDto);

    /**
     * Обновить товар.
     */
    @PutMapping
    ProductDto updateProduct(@RequestBody @Valid ProductDto productDto);

    /**
     * Удалить товар (мягкое удаление — деактивация).
     */
    @DeleteMapping("/{productId}")
    boolean deleteProduct(@PathVariable("productId") @NotNull UUID productId);

    @PostMapping("/quantityState")
    boolean setQuantityState(@RequestBody @Valid SetProductQuantityStateRequest request);
}
