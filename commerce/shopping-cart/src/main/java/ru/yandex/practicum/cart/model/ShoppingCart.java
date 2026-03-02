package ru.yandex.practicum.cart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.interaction.api.enums.CartState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Сущность корзины покупателя.
 */
@Entity
@Table(name = "shopping_carts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shopping_cart_id")
    private UUID shoppingCartId;

    @Column(name = "username", nullable = false)
    private String username;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cart_products",
            joinColumns = @JoinColumn(name = "shopping_cart_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    @Builder.Default
    private Map<UUID, Long> products = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "cart_state")
    private CartState cartState;
}
