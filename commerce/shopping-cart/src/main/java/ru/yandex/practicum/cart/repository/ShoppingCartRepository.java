package ru.yandex.practicum.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.cart.model.ShoppingCart;
import ru.yandex.practicum.interaction.api.enums.CartState;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий корзин покупателей.
 */
@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, UUID> {

    /**
     * Найти корзину пользователя по имени и состоянию.
     */
    Optional<ShoppingCart> findByUsernameAndCartState(String username, CartState cartState);

    /**
     * Найти корзину пользователя по имени.
     */
    Optional<ShoppingCart> findByUsername(String username);
}
