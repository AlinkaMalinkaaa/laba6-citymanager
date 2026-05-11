package com.cityapp.core.enums;

import java.io.Serializable;

/**
 * Уровни жизни населения города.
 * <p>Перечисление содержит все допустимые уровни жизни,
 * которые могут быть установлены для города в коллекции.</p>
 *
 * <ul>
 *   <li>VERY_HIGH - очень высокий</li>
 *   <li>MEDIUM - средний</li>
 *   <li>LOW - низкий</li>
 *   <li>VERY_LOW - очень низкий</li>
 * </ul>
 *
 * @author Алина
 * @version 1.0
 */
public enum StandardOfLiving implements Serializable {
    VERY_HIGH,
    MEDIUM,
    LOW,
    VERY_LOW;

    /**
     * Выводит список всех доступных уровней жизни.
     * <p>Используется на стороне клиента при интерактивном вводе
     * для подсказки пользователю.</p>
     */
    public static void printValues() {
        System.out.println("Доступные значения:");
        for (StandardOfLiving s : values()) {
            System.out.println(" - " + s.name());
        }
    }
}
