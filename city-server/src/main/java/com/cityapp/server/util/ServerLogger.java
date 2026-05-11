package com.cityapp.server.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Простой консольный логгер для серверного приложения.
 * <p>Форматирует сообщения с указанием времени и уровня важности.
 * Используется вместо прямых вызовов System.out для единообразия вывода.</p>
 *
 * @author Алина
 * @version 1.0
 */
public class ServerLogger {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Приватный конструктор, чтобы нельзя было создать экземпляр класса
    private ServerLogger() {}

    /**
     * Вывод информационного сообщения.
     */
    public static void info(String message) {
        System.out.println(format("INFO", message));
    }

    /**
     * Вывод предупреждения.
     */
    public static void warn(String message) {
        System.out.println(format("WARN", message));
    }

    /**
     * Вывод ошибки (в stderr).
     */
    public static void error(String message) {
        System.err.println(format("ERROR", message));
    }

    /**
     * Вывод отладочной информации (можно отключить в продакшене).
     */
    public static void debug(String message) {
        System.out.println(format("DEBUG", message));
    }

    /**
     * Вывод события, связанного с конкретным клиентом.
     */
    public static void client(String clientId, String message) {
        System.out.println(format("CLIENT", "[" + clientId + "] " + message));
    }

    /**
     * Форматирование строки лога.
     */
    private static String format(String level, String message) {
        return "[" + level + "] " + LocalDateTime.now().format(FORMATTER) + " | " + message;
    }
}
