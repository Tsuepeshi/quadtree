package ru.cs.vsu.com.quadtree;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private static final int DEFAULT_CAPACITY = 4;
    private static final int MAX_DEPTH = 12;

    private final int capacity;
    private final int depth;
    private final Bounds bounds;
    private final List<Point> points;
    private boolean divided;

    private QuadTree northwest;
    private QuadTree northeast;
    private QuadTree southwest;
    private QuadTree southeast;

    public QuadTree(Bounds bounds) {
        this(bounds, DEFAULT_CAPACITY, 0);
    }

    public QuadTree(Bounds bounds, int capacity) {
        this(bounds, capacity, 0);
    }

    private QuadTree(Bounds bounds, int capacity, int depth) {
        if (bounds == null) throw new IllegalArgumentException("Границы не могут быть null");
        if (capacity <= 0) throw new IllegalArgumentException("Емкость должна быть положительной");

        this.bounds = bounds;
        this.capacity = capacity;
        this.depth = depth;
        this.points = new ArrayList<>();
        this.divided = false;
    }

    //Вставляет точку в дерево

    public boolean insert(Point point) {
        if (point == null || !bounds.contains(point)) {
            return false;
        }

        if (!divided && (points.size() < capacity || depth >= MAX_DEPTH)) {
            points.add(point);
            return true;
        }

        if (!divided) {
            subdivide();
        }


        if (northwest.insert(point)) return true;
        if (northeast.insert(point)) return true;
        if (southwest.insert(point)) return true;
        if (southeast.insert(point)) return true;


        points.add(point);
        return true;
    }

    //Ищет все точки в указанной области

    public List<Point> query(Bounds range) {
        return query(range, new ArrayList<>());
    }

    public List<Point> query(Bounds range, List<Point> found) {
        if (range == null) throw new IllegalArgumentException("Область поиска не может быть null");

        if (!bounds.intersects(range)) {
            return found;
        }

        for (Point point : points) {
            if (range.contains(point)) {
                found.add(point);
            }
        }

        if (divided) {
            northwest.query(range, found);
            northeast.query(range, found);
            southwest.query(range, found);
            southeast.query(range, found);
        }

        return found;
    }

    //Очищает дерево

    public void clear() {
        points.clear();
        divided = false;
        northwest = null;
        northeast = null;
        southwest = null;
        southeast = null;
    }

    //Возвращает общее количество точек в дереве
    public int size() {
        int count = points.size();
        if (divided) {
            count += northwest.size();
            count += northeast.size();
            count += southwest.size();
            count += southeast.size();
        }
        return count;
    }

    //Возвращает true если дерево пустое

    public boolean isEmpty() {
        return size() == 0;
    }

    //Возвращает глубину дерева

    public int getDepth() {
        if (!divided) return depth;

        return Math.max(
                Math.max(northwest.getDepth(), northeast.getDepth()),
                Math.max(southwest.getDepth(), southeast.getDepth())
        );
    }

    //Разделяет текущий узел на 4 подузла
    private void subdivide() {
        List<Bounds> subdivisions = bounds.subdivide();

        northwest = new QuadTree(subdivisions.get(0), capacity, depth + 1);
        northeast = new QuadTree(subdivisions.get(1), capacity, depth + 1);
        southwest = new QuadTree(subdivisions.get(2), capacity, depth + 1);
        southeast = new QuadTree(subdivisions.get(3), capacity, depth + 1);

        divided = true;

        // Перераспределяем точки по дочерним узлам
        List<Point> pointsToRedistribute = new ArrayList<>(points);
        points.clear();  // Очищаем текущий узел

        for (Point point : pointsToRedistribute) {
            boolean inserted = northwest.insert(point) ||
                    northeast.insert(point) ||
                    southwest.insert(point) ||
                    southeast.insert(point);

            if (!inserted) {
                northwest.insert(point); // если пограничная ни в 1 из 4 не попала закидываем в первую
            }
        }
    }

    public Bounds getBounds() { return bounds; }
    public boolean isDivided() { return divided; }

    @Override
    public String toString() {
        return String.format("QuadTree[bounds=%s, points=%d, divided=%s, depth=%d]",
                bounds, points.size(), divided, depth);
    }
}