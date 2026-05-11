package com.cityapp.server.commands.handlers;

import com.cityapp.core.dto.CommandRequest;
import com.cityapp.core.dto.CommandResponse;
import com.cityapp.core.dto.CommandType;
import com.cityapp.core.model.City;
import com.cityapp.server.storage.CollectionRepository;

import java.util.List;

/**
 * Обработчик команд. Маршрутизирует запросы к хранилищу.
 */
public class CommandDispatcher {
    private final CollectionRepository repository;

    public CommandDispatcher(CollectionRepository repository) {
        this.repository = repository;
    }

    public CommandResponse execute(CommandRequest request) {
        try {
            // Проверка на "просроченный" запрос
            if (request.isExpired(60000)) {
                return new CommandResponse(false, "Запрос устарел", null);
            }

            return switch (request.getType()) {
                case ADD -> handleAdd(request);
                case UPDATE -> handleUpdate(request);
                case REMOVE_BY_ID -> handleRemove(request);
                case CLEAR -> handleClear();
                case SHOW -> handleShow();
                case INFO -> handleInfo();
                case SAVE -> handleSave();
                case GET_BY_ID -> handleGet(request);
                case MAX_BY_DENSITY -> handleMaxDensity();
                case FILTER_CONTAINS_NAME -> handleFilter(request);
                case COUNT_BY_GOVERNMENT -> handleCount(request);
                default -> new CommandResponse(false, "Неизвестная команда", null);
            };
        } catch (Exception e) {
            return new CommandResponse(false, "Ошибка сервера: " + e.getMessage(), null);
        }
    }

    // === Реализация команд ===

    private CommandResponse handleAdd(CommandRequest req) {
        if (!(req.getPayload() instanceof City city))
            return new CommandResponse(false, "Неверный формат данных (ожидается City)", null);

        repository.addCity(city);
        return new CommandResponse(true, "Город успешно добавлен (ID: " + city.getId() + ")", city);
    }

    private CommandResponse handleUpdate(CommandRequest req) {
        if (!(req.getPayload() instanceof City city))
            return new CommandResponse(false, "Неверный формат данных", null);

        boolean updated = repository.updateCity(city.getId(), city);
        return updated
                ? new CommandResponse(true, "Город обновлен", null)
                : new CommandResponse(false, "Город с таким ID не найден", null);
    }

    private CommandResponse handleRemove(CommandRequest req) {
        if (!(req.getPayload() instanceof Integer id))
            return new CommandResponse(false, "Неверный формат данных (ожидается Integer ID)", null);

        boolean removed = repository.removeById(id);
        return removed
                ? new CommandResponse(true, "Город удален", null)
                : new CommandResponse(false, "Город не найден", null);
    }

    private CommandResponse handleGet(CommandRequest req) {
        if (!(req.getPayload() instanceof Integer id))
            return new CommandResponse(false, "Неверный формат данных (ожидается Integer ID)", null);

        return repository.getById(id)
                .map(city -> new CommandResponse(true, "Город найден", city))
                .orElse(new CommandResponse(false, "Город не найден", null));
    }

    private CommandResponse handleClear() {
        repository.clear();
        return new CommandResponse(true, "Коллекция очищена", null);
    }

    private CommandResponse handleShow() {
        List<City> sorted = repository.getSortedByName();
        return new CommandResponse(true, "Получено " + sorted.size() + " элементов", sorted);
    }

    private CommandResponse handleInfo() {
        String info = "Тип: LinkedList\n" +
                "Дата инициализации: " + repository.getInitializationDate() + "\n" +
                "Количество элементов: " + repository.size();
        return new CommandResponse(true, info, null);
    }

    private CommandResponse handleSave() {
        repository.saveToFile();
        return new CommandResponse(true, "Сохранено в файл", null);
    }

    // Stream API команды
    private CommandResponse handleMaxDensity() {
        return repository.maxByPopulationDensity()
                .map(city -> new CommandResponse(true, "Максимальная плотность", city))
                .orElse(new CommandResponse(false, "Коллекция пуста или нет плотности", null));
    }

    private CommandResponse handleFilter(CommandRequest req) {
        if (!(req.getPayload() instanceof String name))
            return new CommandResponse(false, "Неверный формат данных (ожидается String)", null);

        List<City> filtered = repository.filterContainsName(name);
        return new CommandResponse(true, "Найдено: " + filtered.size(), filtered);
    }

    private CommandResponse handleCount(CommandRequest req) {
        if (!(req.getPayload() instanceof com.cityapp.core.enums.Government gov))
            return new CommandResponse(false, "Неверный формат данных", null);

        long count = repository.countGreaterThanGovernment(gov);
        return new CommandResponse(true, "Количество: " + count, count);
    }
}