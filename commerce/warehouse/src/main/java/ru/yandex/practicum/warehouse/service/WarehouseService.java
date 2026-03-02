package ru.yandex.practicum.warehouse.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.interaction.api.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.AssemblyProductForOrderFromWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.repository.WarehouseProductRepository;

/**
 * Сервис бизнес-логики склада.
 */
@Service
@SuppressWarnings("null")
public class WarehouseService {

    private static final Logger log = LoggerFactory.getLogger(WarehouseService.class);

    private final WarehouseProductRepository warehouseProductRepository;

    public WarehouseService(WarehouseProductRepository warehouseProductRepository) {
        this.warehouseProductRepository = warehouseProductRepository;
    }

    /**
     * Варианты адресов склада.
     */
    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};

    /**
     * Текущий адрес склада — выбирается случайно при инициализации.
     */
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    /**
     * Добавить новый товар на склад (регистрация).
     */
    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest request) {
        log.info("Регистрация нового товара на складе: {}", request.getProductId());

        if (warehouseProductRepository.existsById(request.getProductId())) {
            throw new IllegalArgumentException(
                    "Товар с ID " + request.getProductId() + " уже зарегистрирован на складе");
        }

        WarehouseProduct product = WarehouseProduct.builder()
                .productId(request.getProductId())
                .fragile(request.isFragile())
                .width(request.getWidth())
                .height(request.getHeight())
                .depth(request.getDepth())
                .weight(request.getWeight())
                .quantity(0)
                .build();

        warehouseProductRepository.save(product);
        log.info("Товар {} успешно зарегистрирован на складе", request.getProductId());
    }

    /**
     * Пополнить количество товара на складе.
     */
    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        log.info("Пополнение товара {} на {} единиц",
                request.getProductId(), request.getQuantity());

        WarehouseProduct product = warehouseProductRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Товар с ID " + request.getProductId() + " не найден на складе"));

        product.setQuantity(product.getQuantity() + request.getQuantity());
        warehouseProductRepository.save(product);
        log.info("Количество товара {} обновлено: {}", request.getProductId(), product.getQuantity());
    }

    /**
     * Проверить доступность товаров на складе (по содержимому корзины).
     * Возвращает информацию о товарах для доставки (общий вес, объём, хрупкость).
     * Если товаров недостаточно — выбрасывает исключение с указанием недостающих товаров.
     */
    @Transactional(readOnly = true)
    public BookedProductsDto checkProductsAvailability(ShoppingCartDto shoppingCart) {
        log.info("Проверка наличия товаров для корзины пользователя: {}",
                shoppingCart.getUsername());

        Map<UUID, Long> insufficientProducts = new HashMap<>();
        double totalWeight = 0;
        double totalVolume = 0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Long> entry : shoppingCart.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            long requestedQty = entry.getValue();

            WarehouseProduct product = warehouseProductRepository.findById(productId)
                    .orElseThrow(() -> new NoSuchElementException(
                            "Товар с ID " + productId + " не найден на складе"));

            if (product.getQuantity() < requestedQty) {
                insufficientProducts.put(productId, requestedQty - product.getQuantity());
            } else {
                totalWeight += product.getWeight() * requestedQty;
                totalVolume += (product.getWidth() * product.getHeight() * product.getDepth())
                        * requestedQty;
                if (product.isFragile()) {
                    hasFragile = true;
                }
            }
        }

        if (!insufficientProducts.isEmpty()) {
            throw new InsufficientProductsException(
                    "Недостаточно товаров на складе", insufficientProducts);
        }

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .products(shoppingCart.getProducts())
                .build();
    }

    /**
     * Забрать товары со склада для сборки заказа.
     */
    @Transactional
    public BookedProductsDto assemblyProductsForOrder(
            AssemblyProductForOrderFromWarehouseRequest request) {
        log.info("Сборка товаров для заказа: {}", request.getOrderId());

        double totalWeight = 0;
        double totalVolume = 0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Long> entry : request.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            long qty = entry.getValue();

            WarehouseProduct product = warehouseProductRepository.findById(productId)
                    .orElseThrow(() -> new NoSuchElementException(
                            "Товар с ID " + productId + " не найден на складе"));

            if (product.getQuantity() < qty) {
                throw new IllegalStateException(
                        "Недостаточно товара " + productId + " на складе для сборки заказа");
            }

            product.setQuantity(product.getQuantity() - qty);
            warehouseProductRepository.save(product);

            totalWeight += product.getWeight() * qty;
            totalVolume += (product.getWidth() * product.getHeight() * product.getDepth()) * qty;
            if (product.isFragile()) {
                hasFragile = true;
            }
        }

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .products(request.getProducts())
                .build();
    }

    /**
     * Получить адрес склада в универсальном формате.
     */
    public AddressDto getWarehouseAddress() {
        log.info("Запрос адреса склада. Текущий адрес: {}", CURRENT_ADDRESS);
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }
}
