package com.cityapp.core.dto;

import java.io.Serializable;

/**
 * Запрос от клиента к серверу.
 * <p>Инкапсулирует команду и её аргументы для передачи по сети.</p>
 *
 * <p>Примеры использования:
 * <ul>
 *   <li><b>ADD:</b> payload = {@link com.cityapp.core.model.City}</li>
 *   <li><b>REMOVE_BY_ID:</b> payload = {@link Integer} (ID города)</li>
 *   <li><b>FILTER_CONTAINS_NAME:</b> payload = {@link String} (подстрока)</li>
 *   <li><b>SHOW:</b> payload = null</li>
 * </ul>
 * </p>
 *
 * @author Алина
 * @version 1.0
 */
public class CommandRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Тип выполняемой команды */
    private CommandType type;

    /** Данные команды (город, ID, строка и т.д.) */
    private Object payload;

    /** Уникальный идентификатор клиента (для отладки) */
    private String clientId;

    /** Время создания запроса (для таймаутов) */
    private long timestamp;

    /**
     * Создаёт пустой запрос (для десериализации).
     */
    public CommandRequest() {}

    /**
     * Создаёт запрос с указанным типом команды и данными.
     *
     * @param type тип команды из {@link CommandType}
     * @param payload данные команды (объект или null)
     */
    public CommandRequest(CommandType type, Object payload) {
        this.type = type;
        this.payload = payload;
        this.clientId = "client-" + System.nanoTime();
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Возвращает тип команды.
     * @return тип команды
     */
    public CommandType getType() {
        return type;
    }

    /**
     * Возвращает данные команды.
     * <p>Тип данных зависит от команды (см. документацию {@link CommandType}).</p>
     * @return объект с данными или null
     */
    public Object getPayload() {
        return payload;
    }

    /**
     * Возвращает уникальный идентификатор клиента.
     * @return ID клиента в формате "client-XXXXX"
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Возвращает время создания запроса.
     * @return timestamp в миллисекундах
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Проверяет, не истёк ли срок действия запроса.
     * <p>Используется на сервере для защиты от "зависших" запросов.</p>
     *
     * @param maxAgeMs максимальный возраст запроса в миллисекундах
     * @return true если запрос просрочен
     */
    public boolean isExpired(long maxAgeMs) {
        return (System.currentTimeMillis() - timestamp) > maxAgeMs;
    }

    /**
     * Возвращает строковое представление запроса (для логирования).
     * @return информация о запросе
     */
    @Override
    public String toString() {
        return "CommandRequest{" +
                "type=" + type +
                ", payload=" + (payload != null ? payload.getClass().getSimpleName() : "null") +
                ", clientId='" + clientId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
