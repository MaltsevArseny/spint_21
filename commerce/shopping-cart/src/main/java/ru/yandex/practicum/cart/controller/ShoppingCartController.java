package ru.yandex.practicum.cart.controller;


import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.cart.service.ShoppingCartService;
import ru.yandex.practicum.interaction.api.client.ShoppingCartClient;
import ru.yandex.practicum.interaction.api.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

/**
 * REST-контроллер корзины покупателя.
 * Реализует Feign-интерфейс ShoppingCartClient.
 */
@RestController
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartClient {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @Override
    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam("username") String username) {
        return shoppingCartService.getShoppingCart(username);
    }

    @Override
    @PutMapping
    public ShoppingCartDto addProducts(@RequestParam("username") String username,
                                       @RequestBody Map<UUID, Long> products) {
        return shoppingCartService.addProducts(username, products);
    }

    @Override
    @DeleteMapping
    public void deactivateCart(@RequestParam("username") String username) {
        shoppingCartService.deactivateCart(username);
    }

    @Override
    @DeleteMapping("/remove")
    public ShoppingCartDto removeProduct(@RequestParam("username") String username,
                                         @RequestParam("productId") UUID productId) {
        return shoppingCartService.removeProduct(username, productId);
    }

    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam("username") String username,
                                                 @RequestBody ChangeProductQuantityRequest request) {
        return shoppingCartService.changeProductQuantity(username, request);
    }
}
