package com.cityapp.client;

import com.cityapp.client.console.TerminalInterface;
import com.cityapp.client.network.NonBlockingSocket;

import java.io.IOException;

/**
 * Точка входа клиентского приложения.
 */
public class ClientApplication {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;
    private static final long CONNECTION_TIMEOUT_MS = 5000;

    public static void main(String[] args) {
        System.out.println("CITY CLIENT STARTING");

        // 1. Разбор аргументов
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;

        if (args.length >= 2) {
            host = args[0];
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Некорректный порт. Используем: " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        } else if (args.length == 1) {
            host = args[0];
        }

        System.out.println("Подключение к " + host + ":" + port + "...");

        // 2. Создаём сокет (без аргументов!)
        NonBlockingSocket socket = new NonBlockingSocket();

        // 3. Подключаемся с таймаутом (3 аргумента!)
        try {
            if (!socket.connect(host, port, CONNECTION_TIMEOUT_MS)) {
                System.err.println(" Не удалось подключиться к серверу.");
                System.err.println("Проверьте, запущен ли ServerApplication.");
                return;
            }
            System.out.println(" Соединение установлено.");
        } catch (IOException e) {
            System.err.println("Ошибка сети: " + e.getMessage());
            return;
        }

        // 4. Запускаем интерфейс
        TerminalInterface ui = new TerminalInterface(socket);
        ui.run();

        // 5. Закрываем соединение
        socket.close();
        System.out.println(" Клиент отключен.");
    }
}