package com.cityapp.core.model;

import com.cityapp.core.enums.Government;
import com.cityapp.core.enums.StandardOfLiving;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Город в коллекции.
 * <p>Основной класс модели, содержащий всю информацию о городе:
 * название, координаты, площадь, население, правителя и другие характеристики.</p>
 *
 * <p>Реализует {@link Comparable} для сортировки по ID (требование задания)
 * и {@link Serializable} для сохранения в файл и передачи по сети.</p>
 *
 * @author Алина
 * @version 1.0
 */
public class City implements Serializable, Comparable<City> {

    private static final long serialVersionUID = 1L;  // ← Обязательно для Serializable!

    /** Уникальный идентификатор города (авто-генерация) */
    private int id;

    /** Название города (не может быть пустым) */
    private String name;

    /** Географические координаты города */
    private Coordinates coordinates;

    /** Дата создания города (авто-генерация при добавлении) */
    private LocalDate creationDate;

    /** Площадь города в квадратных километрах (должна быть > 0) */
    private Float area;

    /** Население города (должно быть > 0) */
    private Integer population;

    /** Высота над уровнем моря в метрах (может быть null) */
    private Double metersAboveSeaLevel;

    /** Плотность населения (может быть null, должна быть > 0 если установлена) */
    private Double populationDensity;

    /** Тип правления (может быть null) */
    private Government government;

    /** Уровень жизни (может быть null) */
    private StandardOfLiving standardOfLiving;

    /** Правитель города (не может быть null) */
    private Human governor;

    /**
     * Создаёт пустой объект города.
     * <p>Необходим для десериализации и фреймворков.</p>
     */
    public City() {}

    // === ГЕТТЕРЫ ===

    public int getId() { return id; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public LocalDate getCreationDate() { return creationDate; }
    public Float getArea() { return area; }
    public Integer getPopulation() { return population; }
    public Double getMetersAboveSeaLevel() { return metersAboveSeaLevel; }
    public Double getPopulationDensity() { return populationDensity; }
    public Government getGovernment() { return government; }
    public StandardOfLiving getStandardOfLiving() { return standardOfLiving; }
    public Human getGovernor() { return governor; }

    // === СЕТТЕРЫ С ВАЛИДАЦИЕЙ ===

    public void setId(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID должен быть > 0");
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        this.name = name.trim();
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("Coordinates не может быть null");
        this.coordinates = coordinates;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setArea(Float area) {
        if (area == null || area <= 0) throw new IllegalArgumentException("Площадь должна быть > 0");
        this.area = area;
    }

    public void setPopulation(Integer population) {
        if (population == null || population <= 0) throw new IllegalArgumentException("Население должно быть > 0");
        this.population = population;
    }

    public void setMetersAboveSeaLevel(Double metersAboveSeaLevel) {
        this.metersAboveSeaLevel = metersAboveSeaLevel;
    }

    public void setPopulationDensity(Double populationDensity) {
        if (populationDensity != null && populationDensity <= 0) {
            throw new IllegalArgumentException("Плотность должна быть > 0");
        }
        this.populationDensity = populationDensity;
    }

    public void setGovernment(Government government) {
        this.government = government;
    }

    public void setStandardOfLiving(StandardOfLiving standardOfLiving) {
        this.standardOfLiving = standardOfLiving;
    }

    public void setGovernor(Human governor) {
        if (governor == null) throw new IllegalArgumentException("Governor не может быть null");
        this.governor = governor;
    }

    // === ПЕРЕОПРЕДЕЛЁННЫЕ МЕТОДЫ ===

    @Override
    public String toString() {
        return String.format("City{id=%d, name='%s', coordinates=%s, area=%.2f, population=%d, density=%s, government=%s}",
                id, name, coordinates, area, population, populationDensity, government);
    }

    @Override
    public int compareTo(City o) {
        if (o == null) return 1;
        return Integer.compare(this.id, o.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof City)) return false;
        return id == ((City) obj).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}