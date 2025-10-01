package rasterize;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private List<Point> vertices;

    public Polygon() {
        this.vertices = new ArrayList<>();
    }

    public void addVertex(Point point) {
        vertices.add(point);
    }

    public void addVertex(int x, int y) {
        vertices.add(new Point(x, y));
    }

    public List<Point> getVertices() {
        return vertices;
    }

    public void clear() {
        vertices.clear();
    }

    public int size() {
        return vertices.size();
    }

    public boolean isEmpty() {
        return vertices.isEmpty();
    }

    // Třída Point
    public static class Point {
        public int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}