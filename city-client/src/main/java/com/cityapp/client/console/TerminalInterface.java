package com.cityapp.client.console;

import com.cityapp.client.network.NonBlockingSocket;
import com.cityapp.core.dto.CommandRequest;
import com.cityapp.core.dto.CommandResponse;
import com.cityapp.core.dto.CommandType;
import com.cityapp.core.enums.Government;
import com.cityapp.core.enums.StandardOfLiving;
import com.cityapp.core.model.*;
import com.cityapp.core.util.ValidationHelper;

import java.io.IOException;
import java.util.Scanner;

/**
 * Интерактивный интерфейс клиента.
 * Реализует строгий ввод данных с указанием типов и мгновенной валидацией.
 */
public class TerminalInterface {
    private final NonBlockingSocket socket;
    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    public TerminalInterface(NonBlockingSocket socket) {
        this.socket = socket;
    }

    public void run() {
        System.out.println("Клиент запущен. Для справки введите 'help'.");
        while (running) {
            System.out.print("\n> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            try {
                processCommand(line);
            } catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void processCommand(String line) throws IOException, ClassNotFoundException {
        String[] parts = line.split("\\s+", 2);
        String cmdStr = parts[0].toUpperCase();

        // Маппинг коротких команд
        cmdStr = switch (cmdStr) {
            case "REMOVE" -> "REMOVE_BY_ID";
            case "GET" -> "GET_BY_ID";
            case "FILTER" -> "FILTER_CONTAINS_NAME";
            case "MAX_DENSITY" -> "MAX_BY_DENSITY";
            case "COUNT_GOV" -> "COUNT_BY_GOVERNMENT";
            default -> cmdStr;
        };

        CommandType type;

        try {
            type = CommandType.valueOf(cmdStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Неизвестная команда: " + parts[0].toUpperCase());
            return;
        }

        // Локальные команды
        if (type == CommandType.EXIT) {
            running = false;
            System.out.println("Завершение работы.");
            return;
        }
        if (type == CommandType.HELP) {
            printHelp();
            return;
        }
        if (type == CommandType.SAVE) {
            System.out.println("Команда save доступна только на сервере.");
            return;
        }

        // Обработка команд с аргументами
        Object payload = null;
        switch (type) {
            case ADD -> payload = readCity();
            case UPDATE -> payload = readCityForUpdate();

            case REMOVE_BY_ID, GET_BY_ID -> {
                if (parts.length > 1) {
                    try {
                        payload = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: некорректный ID. Ожидается число.");
                        return;
                    }
                } else {
                    payload = readInteger("Введите ID (Integer): ", null);
                }
            }

            case FILTER_CONTAINS_NAME -> {
                if (parts.length > 1) {
                    payload = parts[1].trim();
                } else {
                    System.out.print("Введите подстроку (String): ");
                    payload = scanner.nextLine().trim();
                }
            }

            case COUNT_BY_GOVERNMENT -> payload = readGovernment();
            default -> {}
        }

        if (payload != null || type == CommandType.CLEAR || type == CommandType.SHOW ||
                type == CommandType.INFO || type == CommandType.MAX_BY_DENSITY) {
            sendRequest(new CommandRequest(type, payload));
        }
    }

    private void sendRequest(CommandRequest request) {
        try {
            socket.send(request);
            CommandResponse response = socket.receive();
            formatResponse(response);
        } catch (IOException e) {
            System.err.println("Сетевая ошибка: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка данных: " + e.getMessage());
        }
    }

    private void formatResponse(CommandResponse resp) {
        if (resp.isSuccess()) {
            System.out.println(resp.getMessage());
            if (resp.getData() != null) {
                if (resp.getData() instanceof java.util.List<?> list) {
                    if (list.isEmpty()) {
                        System.out.println("(Коллекция пуста или ничего не найдено)");
                    } else {
                        list.forEach(item -> System.out.println(" - " + item));
                    }
                } else {
                    System.out.println(resp.getData());
                }
            }
        } else {
            System.err.println("Ошибка сервера: " + resp.getMessage());
        }
    }

    //  Методы ввода с явным указанием типов

    private City readCity() {
        City city = new City();
        System.out.println("\n Создание нового объекта City ");

        city.setName(readRequiredString("Название (String): ", "Значение не может быть пустым"));

        System.out.println("Координаты:");
        Double x = readDouble("Координата X (Double, <= 201): ", 201.0, "Координата X должна быть <= 201");
        double y = readDouble("Координата Y (Double): ", null, null);
        city.setCoordinates(new Coordinates(x, y));

        city.setArea(readFloat("Площадь (Float, > 0): ", "Площадь должна быть положительным числом"));
        city.setPopulation(readInteger("Население (Integer, > 0): ", "Население должно быть положительным числом"));

        city.setMetersAboveSeaLevel(readOptionalDouble("Высота над уровнем моря (Double, необязательно): "));
        city.setPopulationDensity(readOptionalDouble("Плотность населения (Double, необязательно): "));

        // Enum с подсказкой доступных значений
        city.setGovernment(readEnum("Тип правления (Government): " + getEnumValues(Government.class) + ": ", Government.class));
        city.setStandardOfLiving(readEnum("Уровень жизни (StandardOfLiving): " + getEnumValues(StandardOfLiving.class) + ": ", StandardOfLiving.class));

        System.out.println("Правитель:");
        Human gov = new Human();
        gov.setName(readRequiredString("Имя правителя (String): ", "Имя не может быть пустым"));
        gov.setAge(readLong("Возраст правителя (Long, > 0): ", "Возраст должен быть положительным числом"));
        city.setGovernor(gov);

        var result = ValidationHelper.validateCity(city);
        if (!result.isValid()) {
            throw new IllegalArgumentException("Ошибка валидации: " + result.getMessage());
        }
        return city;
    }

    private City readCityForUpdate() {
        Integer id = readInteger("Введите ID города для обновления (Integer): ", null);
        if (id == null) throw new IllegalArgumentException("ID обязателен");

        City city = readCity();
        city.setId(id);
        return city;
    }

    private Integer readInteger(String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty() && errorMessage == null) return null;

            try {
                int value = Integer.parseInt(input);
                if (errorMessage != null && value <= 0) {
                    System.out.println("Ошибка: " + errorMessage);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Ожидается значение типа Integer.");
            }
        }
    }

    private Long readLong(String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            try {
                long value = Long.parseLong(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Ошибка: " + errorMessage);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Ожидается значение типа Long.");
            }
        }
    }

    private Float readFloat(String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            try {
                float value = Float.parseFloat(scanner.nextLine().trim().replace(',', '.'));
                if (value > 0) return value;
                System.out.println("Ошибка: " + errorMessage);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Ожидается значение типа Float.");
            }
        }
    }

    private Double readDouble(String prompt, Double maxValue, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
                if (maxValue != null && value > maxValue) {
                    System.out.println("Ошибка: " + errorMessage);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Ожидается значение типа Double.");
            }
        }
    }

    private Double readOptionalDouble(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            return Double.parseDouble(input.replace(',', '.'));
        } catch (NumberFormatException e) {
            System.out.println("Ошибка ввода Double. Поле пропущено.");
            return null;
        }
    }

    private String readRequiredString(String prompt, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("Ошибка: " + errorMessage);
        }
    }

    private <E extends Enum<E>> E readEnum(String prompt, Class<E> enumClass) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Enum.valueOf(enumClass, input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: Недопустимое значение. Доступные: " + getEnumValues(enumClass));
            }
        }
    }

    private Government readGovernment() {
        return readEnum("Правительство для сравнения (Government): " + getEnumValues(Government.class) + ": ", Government.class);
    }

    private <E extends Enum<E>> String getEnumValues(Class<E> enumClass) {
        StringBuilder sb = new StringBuilder();
        for (E e : enumClass.getEnumConstants()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(e.name().toLowerCase());
        }
        return sb.toString();
    }

    private void printHelp() {
        System.out.println("Список команд:");
        System.out.println("  add               - Добавить элемент");
        System.out.println("  update            - Обновить элемент по ID");
        System.out.println("  remove <id>       - Удалить по ID");
        System.out.println("  clear             - Очистить коллекцию");
        System.out.println("  show              - Вывести элементы");
        System.out.println("  info              - Информация о коллекции");
        System.out.println("  get <id>          - Получить по ID");
        System.out.println("  filter <name>     - Фильтр по названию");
        System.out.println("  max_density       - Максимальная плотность");
        System.out.println("  count_gov         - Подсчет по правительству");
        System.out.println("  exit              - Выход");
        System.out.println("  help              - Эта справка");
    }
}