package ru.yandex.practicum.warehouse;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.yandex.practicum.interaction.api.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.AssemblyProductForOrderFromWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.interaction.api.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.enums.CartState;

/**
 * Интеграционные тесты для сервиса склада.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("null")
class WarehouseApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void добавитьНовыйТоварНаСклад_Успешно() throws Exception {
        UUID productId = UUID.randomUUID();
        NewProductInWarehouseRequest request = NewProductInWarehouseRequest.builder()
                .productId(productId)
                .fragile(true)
                .width(10.0)
                .height(20.0)
                .depth(5.0)
                .weight(0.5)
                .build();

        mockMvc.perform(post("/api/v1/warehouse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void добавитьДубликатТовара_ВозвращаетОшибку() throws Exception {
        UUID productId = UUID.randomUUID();
        NewProductInWarehouseRequest request = NewProductInWarehouseRequest.builder()
                .productId(productId)
                .fragile(false)
                .width(5.0)
                .height(10.0)
                .depth(3.0)
                .weight(1.0)
                .build();

        // Первый раз
        mockMvc.perform(post("/api/v1/warehouse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Дубликат
        mockMvc.perform(post("/api/v1/warehouse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void пополнитьКоличествоТовара_Успешно() throws Exception {
        UUID productId = UUID.randomUUID();

        // Сначала регистрируем
        NewProductInWarehouseRequest newProduct = NewProductInWarehouseRequest.builder()
                .productId(productId)
                .fragile(false)
                .width(15.0)
                .height(15.0)
                .depth(15.0)
                .weight(2.0)
                .build();

        mockMvc.perform(post("/api/v1/warehouse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk());

        // Пополняем
        AddProductToWarehouseRequest addRequest = AddProductToWarehouseRequest.builder()
                .productId(productId)
                .quantity(50)
                .build();

        mockMvc.perform(post("/api/v1/warehouse/add-quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void проверитьНаличиеТоваров_ДостаточноТовара_Успешно() throws Exception {
        UUID productId = UUID.randomUUID();

        // Регистрируем и пополняем товар
        NewProductInWarehouseRequest newProduct = NewProductInWarehouseRequest.builder()
                .productId(productId)
                .fragile(true)
                .width(5.0)
                .height(10.0)
                .depth(3.0)
                .weight(0.3)
                .build();

        mockMvc.perform(post("/api/v1/warehouse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk());

        AddProductToWarehouseRequest addRequest = AddProductToWarehouseRequest.builder()
                .productId(productId)
                .quantity(100)
                .build();

        mockMvc.perform(post("/api/v1/warehouse/add-quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        // Проверяем наличие
        ShoppingCartDto cart = ShoppingCartDto.builder()
                .shoppingCartId(UUID.randomUUID())
                .username("testuser")
                .products(Map.of(productId, 5L))
                .cartState(CartState.ACTIVE)
                .build();

        mockMvc.perform(post("/api/v1/warehouse/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cart)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryWeight", is(1.5)))
                .andExpect(jsonPath("$.deliveryVolume", is(750.0)))
                .andExpect(jsonPath("$.fragile", is(true)));
    }

    @Test
    void проверитьНаличиеТоваров_НедостаточноТовара_ВозвращаетОшибку() throws Exception {
        UUID productId = UUID.randomUUID();

        // Регистрируем товар без пополнения (количество = 0)
        NewProductInWarehouseRequest newProduct = NewProductInWarehouseRequest.builder()
                .productId(productId)
                .fragile(false)
                .width(5.0)
                .height(10.0)
                .depth(3.0)
                .weight(1.0)
                .build();

        mockMvc.perform(post("/api/v1/warehouse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk());

        // Проверяем наличие (запрос 10, на складе 0)
        ShoppingCartDto cart = ShoppingCartDto.builder()
                .shoppingCartId(UUID.randomUUID())
                .username("testuser")
                .products(Map.of(productId, 10L))
                .cartState(CartState.ACTIVE)
                .build();

        mockMvc.perform(post("/api/v1/warehouse/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cart)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Недостаточно")));
    }

    @Test
    void получитьАдресСклада_ВозвращаетАдрес() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country", anyOf(is("ADDRESS_1"), is("ADDRESS_2"))))
                .andExpect(jsonPath("$.city", anyOf(is("ADDRESS_1"), is("ADDRESS_2"))))
                .andExpect(jsonPath("$.street", anyOf(is("ADDRESS_1"), is("ADDRESS_2"))))
                .andExpect(jsonPath("$.house", anyOf(is("ADDRESS_1"), is("ADDRESS_2"))))
                .andExpect(jsonPath("$.flat", anyOf(is("ADDRESS_1"), is("ADDRESS_2"))));
    }

    @Test
    void собратьТоварыДляЗаказа_Успешно() throws Exception {
        UUID productId = UUID.randomUUID();

        // Регистрируем и пополняем товар
        NewProductInWarehouseRequest newProduct = NewProductInWarehouseRequest.builder()
                .productId(productId)
                .fragile(false)
                .width(10.0)
                .height(10.0)
                .depth(10.0)
                .weight(2.0)
                .build();

        mockMvc.perform(post("/api/v1/warehouse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk());

        AddProductToWarehouseRequest addRequest = AddProductToWarehouseRequest.builder()
                .productId(productId)
                .quantity(50)
                .build();

        mockMvc.perform(post("/api/v1/warehouse/add-quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        // Собираем заказ
        AssemblyProductForOrderFromWarehouseRequest assemblyRequest =
                AssemblyProductForOrderFromWarehouseRequest.builder()
                        .orderId(UUID.randomUUID())
                        .products(Map.of(productId, 3L))
                        .build();

        mockMvc.perform(post("/api/v1/warehouse/assembly")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assemblyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deliveryWeight", is(6.0)))
                .andExpect(jsonPath("$.deliveryVolume", is(3000.0)))
                .andExpect(jsonPath("$.fragile", is(false)));
    }
}
