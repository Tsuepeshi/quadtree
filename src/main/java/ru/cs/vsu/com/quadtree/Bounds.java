package ru.cs.vsu.com.quadtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Bounds {
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public Bounds(double x, double y, double width, double height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Ширина и высота должны быть положительными");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public boolean contains(Point point) {
        return point.getX() >= x &&
                point.getX() <= x + width &&
                point.getY() >= y &&
                point.getY() <= y + height;
    }

    public boolean intersects(Bounds other) {
        return !(other.x > x + width ||
                other.x + other.width < x ||
                other.y > y + height ||
                other.y + other.height < y);
    }

    public List<Bounds> subdivide() {
        List<Bounds> subdivisions = new ArrayList<>();
        double halfWidth = width / 2;
        double halfHeight = height / 2;

        subdivisions.add(new Bounds(x, y, halfWidth, halfHeight));
        subdivisions.add(new Bounds(x + halfWidth, y, halfWidth, halfHeight));
        subdivisions.add(new Bounds(x, y + halfHeight, halfWidth, halfHeight));
        subdivisions.add(new Bounds(x + halfWidth, y + halfHeight, halfWidth, halfHeight));

        return subdivisions;
    }

    public Point getCenter() {
        return new Point(x + width / 2, y + height / 2);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Bounds bounds = (Bounds) obj;
        return Double.compare(bounds.x, x) == 0 &&
                Double.compare(bounds.y, y) == 0 &&
                Double.compare(bounds.width, width) == 0 &&
                Double.compare(bounds.height, height) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(x, y, width, height); }

    @Override
    public String toString() {
        return String.format("Bounds(%.1f, %.1f, %.1f, %.1f)", x, y, width, height);
    }
}