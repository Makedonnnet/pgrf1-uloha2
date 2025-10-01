package controller;

import rasterize.FilledLineRasterizer;
import rasterize.LineRasterizer;
import rasterize.RasterBufferedImage;
import view.Panel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Controller2D {
    private final Panel panel;
    private final RasterBufferedImage raster;
    private final LineRasterizer lineRasterizer;

    // Proměnné pro interaktivní kreslení
    private int startX, startY; // Počáteční bod úsečky
    private boolean drawing = false; // Režim kreslení
    private List<Point> polygonVertices = new ArrayList<>(); // Vrcholy polygonu
    private boolean shiftPressed = false; // Režim přichytávání

    // Proměnné pro gradient
    private boolean gradientMode = false;
    private int currentColor1 = 0xff0000; // Červená
    private int currentColor2 = 0x0000ff; // Modrá

    // Třída pro reprezentaci bodu
    private static class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.raster = panel.getRaster();
        this.lineRasterizer = new FilledLineRasterizer(raster);

        initListeners();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // Levá myš
                    if (!drawing) {
                        // Začátek kreslení - uložit první bod
                        startX = e.getX();
                        startY = e.getY();
                        drawing = true;

                        // Přidat vrchol do polygonu
                        polygonVertices.add(new Point(startX, startY));
                    } else {
                        // Konec kreslení - potvrdit úsečku
                        int endX = e.getX();
                        int endY = e.getY();

                        if (shiftPressed) {
                            // Režim s přichytáváním
                            Point snapped = getSnappedPoint(startX, startY, endX, endY);
                            endX = snapped.x;
                            endY = snapped.y;
                        }

                        // Vykreslit finální úsečku
                        drawLine(startX, startY, endX, endY);

                        // Přidat další vrchol polygonu
                        polygonVertices.add(new Point(endX, endY));

                        // Připravit se na další úsečku
                        startX = endX;
                        startY = endY;
                    }
                    panel.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Můžeme přidat další funkcionalitu při uvolnění
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawing) {
                    // "Pružná" čára - dočasné vykreslení
                    raster.clear();

                    // Vykreslit již potvrzené úsečky polygonu
                    drawPolygon();

                    // Vykreslit aktuální "pružnou" úsečku
                    int currentX = e.getX();
                    int currentY = e.getY();

                    if (shiftPressed) {
                        Point snapped = getSnappedPoint(startX, startY, currentX, currentY);
                        currentX = snapped.x;
                        currentY = snapped.y;
                    }

                    drawLine(startX, startY, currentX, currentY);
                    panel.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // Pohyb myší bez stisku tlačítka
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftPressed = true;
                }

                if (e.getKeyCode() == KeyEvent.VK_C) {
                    // Smazat plátno - Uloha1 požadavek
                    clearCanvas();
                }

                if (e.getKeyCode() == KeyEvent.VK_G) {
                    // Přepnutí režimu gradientu
                    gradientMode = !gradientMode;
                    System.out.println("Gradient mode: " + (gradientMode ? "ON" : "OFF"));
                    panel.repaint();
                }

                // Uzavření polygonu - např. mezerník
                if (e.getKeyCode() == KeyEvent.VK_SPACE && polygonVertices.size() > 2) {
                    closePolygon();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftPressed = false;
                }
            }
        });
    }

    // Metoda pro přichytávání k osám (režim Shift)
    private Point getSnappedPoint(int startX, int startY, int endX, int endY) {
        int dx = endX - startX;
        int dy = endY - startY;

        // Určení nejbližšího směru (vodorovný, svislý nebo diagonální)
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        angle = (angle + 360) % 360; // Normalizace na 0-360

        if (angle >= 337.5 || angle < 22.5) {
            // Vodorovný doprava
            return new Point(endX, startY);
        } else if (angle >= 22.5 && angle < 67.5) {
            // Diagonální ↗
            int dist = Math.min(Math.abs(dx), Math.abs(dy));
            return new Point(startX + dist, startY + dist);
        } else if (angle >= 67.5 && angle < 112.5) {
            // Svislý nahoru
            return new Point(startX, endY);
        } else if (angle >= 112.5 && angle < 157.5) {
            // Diagonální ↖
            int dist = Math.min(Math.abs(dx), Math.abs(dy));
            return new Point(startX - dist, startY + dist);
        } else if (angle >= 157.5 && angle < 202.5) {
            // Vodorovný doleva
            return new Point(endX, startY);
        } else if (angle >= 202.5 && angle < 247.5) {
            // Diagonální ↙
            int dist = Math.min(Math.abs(dx), Math.abs(dy));
            return new Point(startX - dist, startY - dist);
        } else if (angle >= 247.5 && angle < 292.5) {
            // Svislý dolů
            return new Point(startX, endY);
        } else {
            // Diagonální ↘
            int dist = Math.min(Math.abs(dx), Math.abs(dy));
            return new Point(startX + dist, startY - dist);
        }
    }

    // Metoda pro vykreslení čáry (s gradientem nebo bez)
    private void drawLine(int x1, int y1, int x2, int y2) {
        if (gradientMode && lineRasterizer instanceof FilledLineRasterizer) {
            // Režim s gradientem
            ((FilledLineRasterizer) lineRasterizer).rasterizeWithGradient(
                    x1, y1, currentColor1, x2, y2, currentColor2);
        } else {
            // Normální režim
            lineRasterizer.rasterize(x1, y1, x2, y2);
        }
    }

    // Vykreslení celého polygonu
    private void drawPolygon() {
        if (polygonVertices.size() < 2) return;

        for (int i = 0; i < polygonVertices.size() - 1; i++) {
            Point p1 = polygonVertices.get(i);
            Point p2 = polygonVertices.get(i + 1);
            drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    // Uzavření polygonu - spojení posledního s prvním bodem
    private void closePolygon() {
        if (polygonVertices.size() > 2) {
            Point first = polygonVertices.get(0);
            Point last = polygonVertices.get(polygonVertices.size() - 1);
            drawLine(last.x, last.y, first.x, first.y);
            panel.repaint();

            // Po uzavření polygonu můžeme začít kreslit nový
            polygonVertices.clear();
            drawing = false;
        }
    }

    // Smazání plátna - Uloha1 požadavek
    private void clearCanvas() {
        raster.clear();
        polygonVertices.clear();
        drawing = false;
        panel.repaint();
    }
}