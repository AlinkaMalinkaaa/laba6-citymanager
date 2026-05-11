package com.cityapp.core.dto;

import com.cityapp.core.model.City;

import java.io.Serializable;
import java.util.List;

/**
 * Ответ сервера клиенту.
 * <p>Инкапсулирует результат выполнения команды для передачи по сети.</p>
 *
 * <p>Структура ответа:
 * <ul>
 *   <li><b>success:</b> флаг успешности выполнения</li>
 *   <li><b>message:</b> текстовое сообщение для вывода в консоль</li>
 *   <li><b>data:</b> произвольные данные (список городов, ID, строка и т.д.)</li>
 *   <li><b>serverTime:</b> время формирования ответа (для отладки)</li>
 * </ul>
 * </p>
 *
 * @author Алина
 * @version 1.0
 */
public class CommandResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Флаг успешности выполнения команды */
    private boolean success;

    /** Текстовое сообщение для пользователя */
    private String message;

    /** Данные ответа (зависят от типа команды) */
    private Object data;

    /** Время формирования ответа на сервере */
    private long serverTime;

    /**
     * Создаёт пустой ответ (для десериализации).
     */
    public CommandResponse() {}

    /**
     * Создаёт ответ с указанными параметрами.
     *
     * @param success флаг успешности
     * @param message сообщение для вывода
     * @param data полезные данные (может быть null)
     */
    public CommandResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.serverTime = System.currentTimeMillis();
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
    public long getServerTime() { return serverTime; }

    /**
     * Безопасное приведение данных ответа к списку городов.
     * <p>Используется клиентом для команд SHOW, FILTER, MAX_BY_DENSITY и т.д.</p>
     *
     * @return список городов или пустой список, если данных нет
     */
    @SuppressWarnings("unchecked")
    public List<City> getCities() {
        if (data instanceof List) {
            return (List<City>) data;
        }
        return List.of();
    }

    /**
     * Возвращает строковое представление ответа (для логирования).
     * @return информация об ответе
     */
    @Override
    public String toString() {
        return "CommandResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", dataType=" + (data != null ? data.getClass().getSimpleName() : "null") +
                ", serverTime=" + serverTime +
                '}';
    }
}