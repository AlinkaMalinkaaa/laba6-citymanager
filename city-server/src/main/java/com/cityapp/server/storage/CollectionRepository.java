package com.cityapp.server.storage;

import com.cityapp.core.model.City;

import java.io.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Хранилище коллекции городов.
 */
public class CollectionRepository {
    private final LinkedList<City> collection;
    private final AtomicInteger idCounter;
    private final LocalDateTime initializationDate;
    private final String dataFilePath;

    public CollectionRepository(String dataFilePath) {
        this.dataFilePath = dataFilePath;
        this.collection = new LinkedList<>();
        this.idCounter = new AtomicInteger(1);
        this.initializationDate = LocalDateTime.now();
        loadFromFile();
    }

    // === Основные операции ===

    public void addCity(City city) {
        if (city.getId() <= 0) {
            city.setId(idCounter.getAndIncrement());
        } else {
            if (city.getId() >= idCounter.get()) {
                idCounter.set(city.getId() + 1);
            }
        }
        collection.add(city);
    }

    public boolean updateCity(int id, City updatedCity) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getId() == id) {
                updatedCity.setId(id);
                collection.set(i, updatedCity);
                return true;
            }
        }
        return false;
    }

    public boolean removeById(int id) {
        return collection.removeIf(city -> city.getId() == id);
    }

    /**
     * Получить город по ID.
     */
    public Optional<City> getById(int id) {
        return collection.stream()
                .filter(city -> city.getId() == id)
                .findFirst();
    }

    public void clear() {
        collection.clear();
    }

    // === Запросы (Stream API) ===

    public List<City> getSortedByName() {
        return collection.stream()
                .sorted((c1, c2) -> c1.getName().compareTo(c2.getName()))
                .collect(Collectors.toList());
    }

    public Optional<City> maxByPopulationDensity() {
        return collection.stream()
                .filter(c -> c.getPopulationDensity() != null)
                .max((c1, c2) -> Double.compare(c1.getPopulationDensity(), c2.getPopulationDensity()));
    }

    public List<City> filterContainsName(String substring) {
        return collection.stream()
                .filter(c -> c.getName().toLowerCase().contains(substring.toLowerCase()))
                .collect(Collectors.toList());
    }

    public long countGreaterThanGovernment(com.cityapp.core.enums.Government gov) {
        return collection.stream()
                .filter(c -> c.getGovernment().compareTo(gov) > 0)
                .count();
    }

    // === Мета-информация ===

    public int size() {
        return collection.size();
    }

    public LocalDateTime getInitializationDate() {
        return initializationDate;
    }

    // === Сохранение/загрузка ===

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        if (dataFilePath == null || dataFilePath.isEmpty()) return;

        File file = new File(dataFilePath);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof LinkedList<?>) {
                collection.addAll((LinkedList<City>) obj);
                int maxId = collection.stream()
                        .mapToInt(City::getId)
                        .max()
                        .orElse(0);
                idCounter.set(maxId + 1);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[REPOSITORY] Ошибка загрузки: " + e.getMessage());
        }
    }

    public void saveToFile() {
        if (dataFilePath == null || dataFilePath.isEmpty()) return;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFilePath))) {
            oos.writeObject(collection);
        } catch (IOException e) {
            System.err.println("[REPOSITORY] Ошибка сохранения: " + e.getMessage());
        }
    }
}