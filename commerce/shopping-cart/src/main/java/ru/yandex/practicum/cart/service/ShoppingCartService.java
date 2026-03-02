package ru.yandex.practicum.cart.service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import ru.yandex.practicum.cart.model.ShoppingCart;
import ru.yandex.practicum.cart.repository.ShoppingCartRepository;
import ru.yandex.practicum.interaction.api.client.WarehouseClient;
import ru.yandex.practicum.interaction.api.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.enums.CartState;

/**
 * Сервис бизнес-логики корзины покупателя.
 */
@Service
@SuppressWarnings("null")
public class ShoppingCartService {

    private static final Logger log = LoggerFactory.getLogger(ShoppingCartService.class);

    private final ShoppingCartRepository cartRepository;
    private final WarehouseClient warehouseClient;

    public ShoppingCartService(ShoppingCartRepository cartRepository, WarehouseClient warehouseClient) {
        this.cartRepository = cartRepository;
        this.warehouseClient = warehouseClient;
    }

    /**
     * Получить корзину пользователя. Если активной корзины нет — создаётся новая.
     */
    @Transactional
    public ShoppingCartDto getShoppingCart(String username) {
        log.info("Получение корзины пользователя: {}", username);
        ShoppingCart cart = getOrCreateActiveCart(username);
        return toDto(cart);
    }

    /**
     * Добавить товары в корзину пользователя.
     * Перед добавлением проверяется наличие товаров на складе через Feign-вызов к warehouse.
     */
    @Transactional
    @CircuitBreaker(name = "warehouseBreaker", fallbackMethod = "addProductsFallback")
    public ShoppingCartDto addProducts(String username, Map<UUID, Long> products) {
        log.info("Добавление товаров в корзину пользователя: {}", username);
        ShoppingCart cart = getOrCreateActiveCart(username);

        if (cart.getCartState() == CartState.DEACTIVATED) {
            throw new IllegalStateException("Корзина деактивирована. Нельзя добавлять товары.");
        }

        // Формируем временный DTO корзины с новыми товарами для проверки на складе
        Map<UUID, Long> checkProducts = new HashMap<>(cart.getProducts());
        products.forEach((productId, qty) ->
                checkProducts.merge(productId, qty, Long::sum));

        ShoppingCartDto checkDto = ShoppingCartDto.builder()
                .shoppingCartId(cart.getShoppingCartId())
                .products(checkProducts)
                .username(username)
                .cartState(CartState.ACTIVE)
                .build();

        // Проверяем наличие на складе
        warehouseClient.checkProductsAvailability(checkDto);
        log.info("Склад подтвердил наличие товаров для пользователя: {}", username);

        // Добавляем товары в корзину
        products.forEach((productId, qty) ->
                cart.getProducts().merge(productId, qty, Long::sum));

        ShoppingCart saved = cartRepository.save(cart);
        return toDto(saved);
    }

    /**
     * Фолбэк при недоступности склада.
     */
    public ShoppingCartDto addProductsFallback(String username, Map<UUID, Long> products,
                                                Throwable throwable) {
        log.error("Сервис склада недоступен. Невозможно проверить наличие товаров: {}",
                throwable.getMessage());
        throw new RuntimeException(
                "Сервис склада временно недоступен. Попробуйте позже.", throwable);
    }

    /**
     * Деактивировать корзину пользователя.
     */
    @Transactional
    public void deactivateCart(String username) {
        log.info("Деактивация корзины пользователя: {}", username);
        ShoppingCart cart = cartRepository.findByUsernameAndCartState(username, CartState.ACTIVE)
                .orElseThrow(() -> new NoSuchElementException(
                        "Активная корзина пользователя " + username + " не найдена"));
        cart.setCartState(CartState.DEACTIVATED);
        cartRepository.save(cart);
    }

    /**
     * Удалить товар из корзины.
     */
    @Transactional
    public ShoppingCartDto removeProduct(String username, UUID productId) {
        log.info("Удаление товара {} из корзины пользователя: {}", productId, username);
        ShoppingCart cart = getOrCreateActiveCart(username);
        cart.getProducts().remove(productId);
        ShoppingCart saved = cartRepository.save(cart);
        return toDto(saved);
    }

    /**
     * Изменить количество товара в корзине.
     */
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username,
                                                  ChangeProductQuantityRequest request) {
        log.info("Изменение количества товара {} в корзине пользователя: {}",
                request.getProductId(), username);
        ShoppingCart cart = getOrCreateActiveCart(username);

        if (!cart.getProducts().containsKey(request.getProductId())) {
            throw new NoSuchElementException(
                    "Товар " + request.getProductId() + " не найден в корзине");
        }

        if (request.getNewQuantity() <= 0) {
            cart.getProducts().remove(request.getProductId());
        } else {
            cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        }

        ShoppingCart saved = cartRepository.save(cart);
        return toDto(saved);
    }

    /**
     * Получить или создать активную корзину пользователя.
     */
    private ShoppingCart getOrCreateActiveCart(String username) {
        return cartRepository.findByUsernameAndCartState(username, CartState.ACTIVE)
                .orElseGet(() -> {
                    log.info("Создание новой корзины для пользователя: {}", username);
                    ShoppingCart newCart = ShoppingCart.builder()
                            .username(username)
                            .products(new HashMap<>())
                            .cartState(CartState.ACTIVE)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Конвертация сущности в DTO.
     */
    private ShoppingCartDto toDto(ShoppingCart cart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(cart.getShoppingCartId())
                .products(cart.getProducts())
                .username(cart.getUsername())
                .cartState(cart.getCartState())
                .build();
    }
}
