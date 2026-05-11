package com.cityapp.core.enums;

import java.io.Serializable;

/**
 * Типы правления города.
 * <p>Перечисление содержит все допустимые формы правления,
 * которые могут быть установлены для города в коллекции.</p>
 *
 * <ul>
 *   <li>KRITARCHY - критархия</li>
 *   <li>PLUTOCRACY - плутократия</li>
 *   <li>STRATOCRACY - стратократия</li>
 *   <li>THEOCRACY - теократия</li>
 *   <li>TECHNOCRACY - технократия</li>
 * </ul>
 *
 * @author Алина
 * @version 1.0
 */
public enum Government implements Serializable {
    KRITARCHY,
    PLUTOCRACY,
    STRATOCRACY,
    THEOCRACY,
    TECHNOCRACY;

    /**
     * Выводит список всех доступных значений правления.
     * <p>Используется на стороне клиента при интерактивном вводе
     * для подсказки пользователю.</p>
     */
    public static void printValues() {
        System.out.println("Доступные значения:");
        for (Government g : values()) {
            System.out.println(" - " + g.name());
        }
    }
}