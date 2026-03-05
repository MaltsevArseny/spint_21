package ru.yandex.practicum.interaction.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import ru.yandex.practicum.interaction.api.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.AssemblyProductForOrderFromWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

/**
 * Feign-клиент для сервиса склада (warehouse).
 */
@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {

    /**
     * Добавить новый товар на склад (регистрация товара).
     */
    @PostMapping
    void addNewProduct(@RequestBody @Valid NewProductInWarehouseRequest request);

    /**
     * Пополнить количество существующего товара на складе.
     */
    @PostMapping("/add-quantity")
    void addProductQuantity(@RequestBody @Valid AddProductToWarehouseRequest request);

    /**
     * Проверить доступность товаров на складе (по содержимому корзины).
     */
    @PostMapping("/check")
    BookedProductsDto checkProductsAvailability(@RequestBody @Valid ShoppingCartDto shoppingCart);

    /**
     * Забрать товары со склада для сборки заказа.
     */
    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(
            @RequestBody @Valid AssemblyProductForOrderFromWarehouseRequest request);

    /**
     * Получить адрес склада.
     */
    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}
