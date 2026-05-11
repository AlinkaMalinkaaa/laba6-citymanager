package com.cityapp.core.util;

import com.cityapp.core.model.City;
import com.cityapp.core.model.Human;

/**
 * Утилитный класс для проверки корректности данных.
 * <p>Используется на стороне клиента перед формированием запроса.</p>
 *
 * @author Алина
 * @version 1.0
 */
public class ValidationHelper {

    // Приватный конструктор, чтобы нельзя было создать экземпляр класса
    private ValidationHelper() {}

    /**
     * Проверка имени (не null и не пустое).
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Проверка координаты X (не null и <= 201.0).
     */
    public static boolean isValidCoordinatesX(Double x) {
        return x != null && x <= 201.0;
    }

    /**
     * Проверка площади (не null и > 0).
     */
    public static boolean isValidArea(Float area) {
        return area != null && area > 0;
    }

    /**
     * Проверка населения (не null и > 0).
     */
    public static boolean isValidPopulation(Integer population) {
        return population != null && population > 0;
    }

    /**
     * Полная валидация города перед отправкой на сервер.
     *
     * @param city город для проверки
     * @return результат валидации
     */
    public static ValidationResult validateCity(City city) {
        if (city == null) {
            return new ValidationResult(false, "Город не может быть null");
        }

        if (!isValidName(city.getName())) {
            return new ValidationResult(false, "Некорректное название города");
        }

        if (city.getCoordinates() == null) {
            return new ValidationResult(false, "Координаты не могут быть null");
        }

        if (!isValidCoordinatesX(city.getCoordinates().getX())) {
            return new ValidationResult(false, "Координата X должна быть <= 201.0");
        }

        if (!isValidArea(city.getArea())) {
            return new ValidationResult(false, "Площадь должна быть > 0");
        }

        if (!isValidPopulation(city.getPopulation())) {
            return new ValidationResult(false, "Население должно быть > 0");
        }

        // Валидация правителя (если требуется, что он не null)
        if (city.getGovernor() != null) {
            Human gov = city.getGovernor();
            if (!isValidName(gov.getName())) {
                return new ValidationResult(false, "Некорректное имя правителя");
            }
            if (gov.getAge() == null || gov.getAge() <= 0) {
                return new ValidationResult(false, "Возраст правителя должен быть > 0");
            }
        }

        return new ValidationResult(true, "Валидация пройдена успешно");
    }

    /**
     * Класс-обёртка для результата проверки.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}