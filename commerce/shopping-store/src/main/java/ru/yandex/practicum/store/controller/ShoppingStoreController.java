package ru.yandex.practicum.store.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.interaction.api.client.ShoppingStoreClient;
import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.api.enums.ProductCategory;
import ru.yandex.practicum.store.service.ShoppingStoreService;

/**
 * REST-контроллер витрины товаров.
 * Реализует Feign-интерфейс ShoppingStoreClient.
 */
@Validated
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ShoppingStoreService shoppingStoreService;

    @Override
    @GetMapping
    public List<ProductDto> getProducts(@RequestParam("category") ProductCategory category) {
        return shoppingStoreService.getProducts(category);
    }

    @Override
    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable("productId") UUID productId) {
        return shoppingStoreService.getProduct(productId);
    }

    @Override
    @PostMapping
    public ProductDto createProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.createProduct(productDto);
    }

    @Override
    @PutMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.updateProduct(productDto);
    }

    @Override
    @DeleteMapping("/{productId}")
    public boolean deleteProduct(@PathVariable("productId") UUID productId) {
        return shoppingStoreService.deleteProduct(productId);
    }

    @Override
    @PostMapping("/quantityState")
    public boolean setQuantityState(@RequestBody SetProductQuantityStateRequest request) {
        return shoppingStoreService.setQuantityState(request);
    }
}
