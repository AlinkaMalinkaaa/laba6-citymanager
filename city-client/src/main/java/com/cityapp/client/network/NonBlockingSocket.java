package com.cityapp.client.network;

import com.cityapp.core.dto.CommandRequest;
import com.cityapp.core.dto.CommandResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

/**
 * Сетевой канал для обмена объектами с сервером.
 * Использует SocketChannel (NIO API), но в блокирующем режиме
 * для совместимости с ObjectOutputStream.
 */
public class NonBlockingSocket {
    private SocketChannel channel;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    /**
     * Устанавливает соединение с сервером с таймаутом.
     * @param host адрес сервера
     * @param port порт сервера
     * @param timeoutMs таймаут подключения в миллисекундах
     * @return true если успешно подключено
     */
    public boolean connect(String host, int port, long timeoutMs) throws IOException {
        channel = SocketChannel.open();

        // SocketChannel по умолчанию блокирующий — это то, что нам нужно
        // для работы ObjectOutputStream/ObjectInputStream

        // Устанавливаем таймаут на подключение
        channel.configureBlocking(true);

        // Подключаемся к серверу
        boolean connected = channel.connect(new InetSocketAddress(host, port));

        if (!connected) {
            // Если подключение не завершено сразу — ждём с таймаутом
            long start = System.currentTimeMillis();
            while (!channel.finishConnect()) {
                if (System.currentTimeMillis() - start > timeoutMs) {
                    channel.close();
                    return false;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Инициализация потоков сериализации поверх канала
        // Это работает только в блокирующем режиме!
        oos = new ObjectOutputStream(Channels.newOutputStream(channel));
        ois = new ObjectInputStream(Channels.newInputStream(channel));
        return true;
    }

    /**
     * Отправляет запрос на сервер
     */
    public void send(CommandRequest request) throws IOException {
        if (oos == null) throw new IOException("Not connected");
        oos.writeObject(request);
        oos.flush();
    }

    /**
     * Получает ответ от сервера
     */
    public CommandResponse receive() throws IOException, ClassNotFoundException {
        if (ois == null) throw new IOException("Not connected");
        return (CommandResponse) ois.readObject();
    }

    /**
     * Закрывает канал и потоки
     */
    public void close() {
        try { if (oos != null) oos.close(); } catch (IOException ignored) {}
        try { if (ois != null) ois.close(); } catch (IOException ignored) {}
        try { if (channel != null) channel.close(); } catch (IOException ignored) {}
    }
}