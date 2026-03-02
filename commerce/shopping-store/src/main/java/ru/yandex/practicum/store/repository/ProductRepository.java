package ru.yandex.practicum.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.interaction.api.enums.ProductCategory;
import ru.yandex.practicum.interaction.api.enums.ProductState;
import ru.yandex.practicum.store.model.Product;

import java.util.UUID;

/**
 * Репозиторий товаров витрины.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Поиск товаров по категории и состоянию с пагинацией.
     */
    Page<Product> findByProductCategoryAndProductState(ProductCategory category,
                                                       ProductState state,
                                                       Pageable pageable);
}
