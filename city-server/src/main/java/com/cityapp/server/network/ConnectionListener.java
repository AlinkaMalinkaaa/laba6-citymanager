package com.cityapp.server.network;

import com.cityapp.core.dto.CommandRequest;
import com.cityapp.core.dto.CommandResponse;
import com.cityapp.server.commands.handlers.CommandDispatcher;
import com.cityapp.server.storage.CollectionRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Слушатель подключений.
 * Работает в однопоточном режиме (по заданию).
 */
public class ConnectionListener {
    private final int port;
    private final CollectionRepository repository;
    private final CommandDispatcher dispatcher;

    public ConnectionListener(int port, CollectionRepository repository) {
        this.port = port;
        this.repository = repository;
        this.dispatcher = new CommandDispatcher(repository);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[SERVER] Запущен на порту " + port);

            while (true) {
                // Ожидаем подключения
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] Клиент подключился: " + clientSocket.getInetAddress());

                try {
                    handleClient(clientSocket);
                } catch (Exception e) {
                    System.err.println("[SERVER] Ошибка при работе с клиентом: " + e.getMessage());
                    e.printStackTrace();  
                } finally {
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Ошибка сервера: " + e.getMessage());
        }
    }
    private void handleClient(Socket socket) {
        try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {

            // Цикл для обработки нескольких команд от одного клиента
            while (true) {
                try {
                    // Читаем запрос
                    CommandRequest request = (CommandRequest) input.readObject();
                    System.out.println("[SERVER] Получена команда: " + request.getType());

                    // Обрабатываем
                    CommandResponse response = dispatcher.execute(request);
                    System.out.println("[SERVER] Ответ: " + response.isSuccess() + " - " + response.getMessage());

                    // Отправляем ответ
                    output.writeObject(response);
                    output.flush();
                    System.out.println("[SERVER] Ответ отправлен клиенту");

                } catch (java.io.EOFException e) {
                    // Клиент закрыл соединение — нормальное завершение
                    System.out.println("[SERVER] Клиент отключился");
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("[SERVER] Ошибка десериализации: " + e.getMessage());
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    System.err.println("[SERVER] Ошибка при обработке команды: " + e.getMessage());
                    e.printStackTrace();
                    // Продолжаем работать, не закрываем соединение
                }
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Ошибка потоков: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Игнорируем
            }
        }

    }
}
