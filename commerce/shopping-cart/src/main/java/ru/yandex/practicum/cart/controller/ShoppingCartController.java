package ru.yandex.practicum.cart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.service.ShoppingCartService;
import ru.yandex.practicum.interaction.api.ShoppingCartOperations;
import ru.yandex.practicum.interaction.api.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

/**
 * REST-контроллер корзины покупателя.
 * Реализует интерфейс ShoppingCartOperations (а не ShoppingCartClient напрямую).
 */
@Validated
@RestController
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartOperations {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @Override
    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam("username") @NotBlank String username) {
        return shoppingCartService.getShoppingCart(username);
    }

    @Override
    @PutMapping
    public ShoppingCartDto addProducts(@RequestParam("username") @NotBlank String username,
                                       @RequestBody @NotNull Map<UUID, Long> products) {
        return shoppingCartService.addProducts(username, products);
    }

    @Override
    @DeleteMapping
    public void deactivateCart(@RequestParam("username") @NotBlank String username) {
        shoppingCartService.deactivateCart(username);
    }

    @Override
    @DeleteMapping("/remove")
    public ShoppingCartDto removeProduct(@RequestParam("username") @NotBlank String username,
                                         @RequestParam("productId") @NotNull UUID productId) {
        return shoppingCartService.removeProduct(username, productId);
    }

    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam("username") @NotBlank String username,
                                                 @RequestBody @Valid ChangeProductQuantityRequest request) {
        return shoppingCartService.changeProductQuantity(username, request);
    }
}
