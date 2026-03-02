package ru.yandex.practicum.cart;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.yandex.practicum.interaction.api.client.WarehouseClient;
import ru.yandex.practicum.interaction.api.dto.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;

/**
 * Интеграционные тесты для сервиса корзины покупателя.
 * WarehouseClient мокируется, так как warehouse — внешний сервис.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("null")
class ShoppingCartApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WarehouseClient warehouseClient;

    @Test
    void contextLoads() {
    }

    @Test
    void получитьКорзину_СоздаётНовуюДляПользователя() throws Exception {
        mockMvc.perform(get("/api/v1/shopping-cart")
                        .param("username", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("user1")))
                .andExpect(jsonPath("$.cartState", is("ACTIVE")))
                .andExpect(jsonPath("$.shoppingCartId", notNullValue()));
    }

    @Test
    void добавитьТоварыВКорзину_Успешно() throws Exception {
        UUID productId = UUID.randomUUID();

        // Мокируем ответ склада — товар доступен
        BookedProductsDto bookedResponse = BookedProductsDto.builder()
                .deliveryWeight(1.0)
                .deliveryVolume(100.0)
                .fragile(false)
                .products(Map.of(productId, 3L))
                .build();
        when(warehouseClient.checkProductsAvailability(any(ShoppingCartDto.class)))
                .thenReturn(bookedResponse);

        Map<UUID, Long> products = Map.of(productId, 3L);

        mockMvc.perform(put("/api/v1/shopping-cart")
                        .param("username", "user2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(products)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("user2")))
                .andExpect(jsonPath("$.cartState", is("ACTIVE")));
    }

    @Test
    void деактивироватьКорзину_Успешно() throws Exception {
        // Сначала создаём корзину
        mockMvc.perform(get("/api/v1/shopping-cart")
                        .param("username", "user3"))
                .andExpect(status().isOk());

        // Деактивируем
        mockMvc.perform(delete("/api/v1/shopping-cart")
                        .param("username", "user3"))
                .andExpect(status().isOk());
    }

    @Test
    void удалитьТоварИзКорзины_Успешно() throws Exception {
        UUID productId = UUID.randomUUID();

        // Мокируем склад
        BookedProductsDto bookedResponse = BookedProductsDto.builder()
                .deliveryWeight(1.0)
                .deliveryVolume(100.0)
                .fragile(false)
                .products(Map.of(productId, 2L))
                .build();
        when(warehouseClient.checkProductsAvailability(any(ShoppingCartDto.class)))
                .thenReturn(bookedResponse);

        // Добавляем товар
        Map<UUID, Long> products = Map.of(productId, 2L);
        mockMvc.perform(put("/api/v1/shopping-cart")
                        .param("username", "user4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(products)))
                .andExpect(status().isOk());

        // Удаляем из корзины
        mockMvc.perform(delete("/api/v1/shopping-cart/remove")
                        .param("username", "user4")
                        .param("productId", productId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("user4")));
    }

    @Test
    void изменитьКоличествоТовара_Успешно() throws Exception {
        UUID productId = UUID.randomUUID();

        // Мокируем склад
        BookedProductsDto bookedResponse = BookedProductsDto.builder()
                .deliveryWeight(2.0)
                .deliveryVolume(200.0)
                .fragile(false)
                .products(Map.of(productId, 5L))
                .build();
        when(warehouseClient.checkProductsAvailability(any(ShoppingCartDto.class)))
                .thenReturn(bookedResponse);

        // Добавляем товар
        Map<UUID, Long> products = Map.of(productId, 5L);
        mockMvc.perform(put("/api/v1/shopping-cart")
                        .param("username", "user5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(products)))
                .andExpect(status().isOk());

        // Изменяем количество
        ChangeProductQuantityRequest changeRequest = ChangeProductQuantityRequest.builder()
                .productId(productId)
                .newQuantity(10L)
                .build();

        mockMvc.perform(post("/api/v1/shopping-cart/change-quantity")
                        .param("username", "user5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("user5")));
    }
}
