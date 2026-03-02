package ru.yandex.practicum.store.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.api.enums.ProductCategory;
import ru.yandex.practicum.interaction.api.enums.ProductState;
import ru.yandex.practicum.store.model.Product;
import ru.yandex.practicum.store.repository.ProductRepository;

/**
 * Сервис бизнес-логики витрины товаров.
 */
@Service
@SuppressWarnings("null")
public class ShoppingStoreService {

    private static final Logger log = LoggerFactory.getLogger(ShoppingStoreService.class);

    private final ProductRepository productRepository;

    public ShoppingStoreService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Получить список активных товаров по категории.
     */
    @Transactional(readOnly = true)
    public List<ProductDto> getProducts(ProductCategory category) {
        log.info("Получение товаров категории: {}", category);
        Page<Product> products = productRepository.findByProductCategoryAndProductState(
                category, ProductState.ACTIVE,
                PageRequest.of(0, 100, Sort.by("productName"))
        );
        return products.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить товар по идентификатору.
     */
    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        log.info("Получение товара по ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Товар с ID " + productId + " не найден"));
        return toDto(product);
    }

    /**
     * Создать новый товар.
     */
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Создание нового товара: {}", productDto.getProductName());
        Product product = Product.builder()
                .productName(productDto.getProductName())
                .description(productDto.getDescription())
                .imageSrc(productDto.getImageSrc())
                .quantityState(productDto.getQuantityState())
                .productState(ProductState.ACTIVE)
                .productCategory(productDto.getProductCategory())
                .price(productDto.getPrice())
                .build();
        product = productRepository.save(product);
        return toDto(product);
    }

    /**
     * Обновить существующий товар.
     */
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("Обновление товара с ID: {}", productDto.getProductId());
        Product product = productRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Товар с ID " + productDto.getProductId() + " не найден"));

        product.setProductName(productDto.getProductName());
        product.setDescription(productDto.getDescription());
        product.setImageSrc(productDto.getImageSrc());
        product.setQuantityState(productDto.getQuantityState());
        product.setProductCategory(productDto.getProductCategory());
        product.setPrice(productDto.getPrice());

        product = productRepository.save(product);
        return toDto(product);
    }

    /**
     * Удалить (деактивировать) товар.
     */
    @Transactional
    public boolean deleteProduct(UUID productId) {
        log.info("Деактивация товара с ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Товар с ID " + productId + " не найден"));
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        return true;
    }

    /**
     * Установить состояние доступного количества товара.
     */
    @Transactional
    public boolean setQuantityState(SetProductQuantityStateRequest request) {
        log.info("Установка количества для товара {}: {}", request.getProductId(), request.getQuantityState());
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Товар с ID " + request.getProductId() + " не найден"));
        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);
        return true;
    }

    /**
     * Конвертация сущности в DTO.
     */
    private ProductDto toDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageSrc(product.getImageSrc())
                .quantityState(product.getQuantityState())
                .productState(product.getProductState())
                .productCategory(product.getProductCategory())
                .price(product.getPrice())
                .build();
    }
}
