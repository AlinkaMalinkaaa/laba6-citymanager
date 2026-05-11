package com.cityapp.core.model;

import java.io.Serializable;

/**
 * Координаты города.
 * <p>Содержит географические координаты X и Y для местоположения города.
 * Координата X имеет ограничение максимум 201.0 (требование задания).</p>
 *
 * <p>Реализует {@link Comparable} для сортировки координат
 * и {@link Serializable} для сохранения в файл и передачи по сети.</p>
 *
 * @author Алина
 * @version 1.0
 */
public class Coordinates implements Serializable, Comparable<Coordinates> {

    private static final long serialVersionUID = 1L;  // ← Обязательно для Serializable!

    /** Максимально допустимое значение координаты X */
    public static final double MAX_X = 201.0;

    private Double x;  // Может быть null
    private double y;  // Примитив, не может быть null

    /**
     * Создаёт пустой объект координат.
     * <p>Необходим для десериализации и фреймворков.</p>
     */
    public Coordinates() {}

    /**
     * Создаёт координаты с заданными значениями.
     *
     * @param x координата X (должна быть <= MAX_X)
     * @param y координата Y
     */
    public Coordinates(Double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() { return x; }
    public double getY() { return y; }

    /**
     * Устанавливает координату X с валидацией.
     *
     * @param x координата X (должна быть <= MAX_X)
     * @throws IllegalArgumentException если x null или больше MAX_X
     */
    public void setX(Double x) {
        if (x == null || x > MAX_X) {
            throw new IllegalArgumentException("X должен быть <= " + MAX_X);
        }
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates{" + "x=" + x + ", y=" + y + "}";
    }

    @Override
    public int compareTo(Coordinates o) {
        if (o == null) return 1;
        if (this.x == null && o.x == null) return Double.compare(this.y, o.y);
        if (this.x == null) return -1;
        if (o.x == null) return 1;
        int xCompare = this.x.compareTo(o.x);
        if (xCompare != 0) {
            return xCompare;
        }
        return Double.compare(this.y, o.y);
    }
}