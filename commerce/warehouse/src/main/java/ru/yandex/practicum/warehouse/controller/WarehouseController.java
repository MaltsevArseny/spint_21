package ru.yandex.practicum.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.api.client.WarehouseClient;
import ru.yandex.practicum.interaction.api.dto.*;
import ru.yandex.practicum.warehouse.service.WarehouseService;

/**
 * REST-контроллер склада.
 * Реализует Feign-интерфейс WarehouseClient.
 */
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {

    private final WarehouseService warehouseService;

    @Override
    @PostMapping
    public void addNewProduct(@RequestBody NewProductInWarehouseRequest request) {
        warehouseService.addNewProduct(request);
    }

    @Override
    @PostMapping("/add-quantity")
    public void addProductQuantity(@RequestBody AddProductToWarehouseRequest request) {
        warehouseService.addProductQuantity(request);
    }

    @Override
    @PostMapping("/check")
    public BookedProductsDto checkProductsAvailability(@RequestBody ShoppingCartDto shoppingCart) {
        return warehouseService.checkProductsAvailability(shoppingCart);
    }

    @Override
    @PostMapping("/assembly")
    public BookedProductsDto assemblyProductsForOrder(
            @RequestBody AssemblyProductForOrderFromWarehouseRequest request) {
        return warehouseService.assemblyProductsForOrder(request);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }
}
