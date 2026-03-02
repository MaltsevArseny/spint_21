package ru.yandex.practicum.interaction.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.dto.*;

/**
 * Feign-клиент для сервиса склада (warehouse).
 */
@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {

    /**
     * Добавить новый товар на склад (регистрация товара).
     */
    @PostMapping
    void addNewProduct(@RequestBody NewProductInWarehouseRequest request);

    /**
     * Пополнить количество существующего товара на складе.
     */
    @PostMapping("/add-quantity")
    void addProductQuantity(@RequestBody AddProductToWarehouseRequest request);

    /**
     * Проверить доступность товаров на складе (по содержимому корзины).
     */
    @PostMapping("/check")
    BookedProductsDto checkProductsAvailability(@RequestBody ShoppingCartDto shoppingCart);

    /**
     * Забрать товары со склада для сборки заказа.
     */
    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(
            @RequestBody AssemblyProductForOrderFromWarehouseRequest request);

    /**
     * Получить адрес склада.
     */
    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}
