package ru.yandex.practicum.store;

import java.util.UUID;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.yandex.practicum.interaction.api.dto.ProductDto;
import ru.yandex.practicum.interaction.api.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.api.enums.ProductCategory;
import ru.yandex.practicum.interaction.api.enums.ProductState;
import ru.yandex.practicum.interaction.api.enums.QuantityState;

/**
 * Интеграционные тесты для сервиса витрины товаров.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("null")
class ShoppingStoreApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final ProductDto testProduct = ProductDto.builder()
            .productName("Умная лампа")
            .description("LED лампа с управлением через приложение")
            .imageSrc("/images/smart-lamp.jpg")
            .quantityState(QuantityState.MANY)
            .productState(ProductState.ACTIVE)
            .productCategory(ProductCategory.LIGHTING)
            .price(1500.0)
            .build();

    @Test
    void создатьТовар_УспешноВозвращаетТовар() throws Exception {
        mockMvc.perform(post("/api/v1/shopping-store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName", is("Умная лампа")))
                .andExpect(jsonPath("$.productCategory", is("LIGHTING")))
                .andExpect(jsonPath("$.productState", is("ACTIVE")))
                .andExpect(jsonPath("$.price", is(1500.0)))
                .andExpect(jsonPath("$.productId", notNullValue()));
    }

    @Test
    void получитьТовар_ПоID_УспешноВозвращаетТовар() throws Exception {
        // Создаём товар
        String response = mockMvc.perform(post("/api/v1/shopping-store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andReturn().getResponse().getContentAsString();

        ProductDto created = objectMapper.readValue(response, ProductDto.class);

        // Получаем товар по ID
        mockMvc.perform(get("/api/v1/shopping-store/{productId}", created.getProductId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName", is("Умная лампа")))
                .andExpect(jsonPath("$.productId", is(created.getProductId().toString())));
    }

    @Test
    void получитьТоварыПоКатегории_ВозвращаетСписок() throws Exception {
        // Создаём товар
        mockMvc.perform(post("/api/v1/shopping-store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk());

        // Получаем по категории
        mockMvc.perform(get("/api/v1/shopping-store")
                        .param("category", "LIGHTING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void обновитьТовар_УспешноОбновляетДанные() throws Exception {
        // Создаём товар
        String response = mockMvc.perform(post("/api/v1/shopping-store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andReturn().getResponse().getContentAsString();

        ProductDto created = objectMapper.readValue(response, ProductDto.class);

        // Обновляем
        created.setProductName("Обновлённая умная лампа");
        created.setPrice(2000.0);

        mockMvc.perform(put("/api/v1/shopping-store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName", is("Обновлённая умная лампа")))
                .andExpect(jsonPath("$.price", is(2000.0)));
    }

@Test
void удалитьТовар_ДеактивируетТовар() throws Exception {
        // Создаём товар
        String response = mockMvc.perform(post("/api/v1/shopping-store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andReturn().getResponse().getContentAsString();

        ProductDto created = objectMapper.readValue(response, ProductDto.class);

        // Удаляем (деактивируем)
        mockMvc.perform(delete("/api/v1/shopping-store/{productId}", created.getProductId()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // Проверяем, что товар деактивирован
        mockMvc.perform(get("/api/v1/shopping-store/{productId}", created.getProductId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productState", is("DEACTIVATE")));
    }

    @Test
    void установитьСостояниеКоличества_УспешноОбновляет() throws Exception {
        // Создаём товар
        String response = mockMvc.perform(post("/api/v1/shopping-store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andReturn().getResponse().getContentAsString();

        ProductDto created = objectMapper.readValue(response, ProductDto.class);

        // Устанавливаем состояние количества
        SetProductQuantityStateRequest request = SetProductQuantityStateRequest.builder()
                .productId(created.getProductId())
                .quantityState(QuantityState.FEW)
                .build();

        mockMvc.perform(post("/api/v1/shopping-store/quantityState")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void получитьТовар_НесуществующийID_Возвращает404() throws Exception {
        mockMvc.perform(get("/api/v1/shopping-store/{productId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
