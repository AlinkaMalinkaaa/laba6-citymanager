package com.cityapp.server;

import com.cityapp.server.network.ConnectionListener;
import com.cityapp.server.storage.CollectionRepository;

/**
 * Точка входа серверного приложения.
 */
public class ServerApplication {

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_DATA_FILE = "data.dat";

    public static void main(String[] args) {
        System.out.println("=== CITY SERVER STARTING ===");

        // Чтение конфигурации
        int port = DEFAULT_PORT;
        String dataFile = System.getenv("CITY_FILE");
        if (dataFile == null || dataFile.isEmpty()) {
            dataFile = DEFAULT_DATA_FILE;
            System.out.println("Переменная CITY_FILE не задана. Используем " + DEFAULT_DATA_FILE);
        } else {
            System.out.println("Используем файл данных: " + dataFile);
        }

        // Чтение порта из аргументов
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Некорректный порт. Используем: " + DEFAULT_PORT);
            }
        }

        // Инициализация хранилища (загрузка из файла происходит в конструкторе)
        CollectionRepository repository = new CollectionRepository(dataFile);
        System.out.println("[INFO] Коллекция загружена. Элементов: " + repository.size());

        // Запуск сервера
        ConnectionListener listener = new ConnectionListener(port, repository);
        listener.start();
    }
}