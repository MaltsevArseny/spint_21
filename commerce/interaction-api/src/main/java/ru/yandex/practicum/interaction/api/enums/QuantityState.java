package ru.yandex.practicum.interaction.api.enums;

/**
 * Степень доступности товара (описание количества).
 */
public enum QuantityState {
    /** Товар закончился */
    ENDED,
    /** Осталось меньше 10 единиц */
    FEW,
    /** Осталось от 10 до 100 единиц */
    ENOUGH,
    /** Осталось больше 100 единиц */
    MANY
}
