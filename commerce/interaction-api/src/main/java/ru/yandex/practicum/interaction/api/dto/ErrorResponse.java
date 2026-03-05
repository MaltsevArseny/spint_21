package ru.yandex.practicum.interaction.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Унифицированный DTO ответа об ошибке.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Краткое описание типа ошибки.
     */
    private String error;

    /**
     * Детальное сообщение об ошибке.
     */
    private String message;

    /**
     * Список нарушений валидации (заполняется при ошибках валидации).
     */
    private List<String> violations;

    /**
     * Дополнительные данные (например, insufficientProducts для склада).
     */
    private Object details;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
