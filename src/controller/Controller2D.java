package controller;

import rasterize.FilledLineRasterizer;
import rasterize.LineRasterizer;
import rasterize.Polygon;
import rasterize.RasterBufferedImage;
import view.Panel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller2D {
    private final Panel panel;
    private final RasterBufferedImage raster;
    private final LineRasterizer lineRasterizer;

    // Proměnné pro interaktivní kreslení
    private int startX, startY; // Počáteční bod úsečky
    private boolean drawing = false; // Režim kreslení
    private Polygon polygon = new Polygon(); // Vrcholy polygonu
    private boolean shiftPressed = false; // Režim přichytávání

    // Proměnné pro gradient
    private boolean gradientMode = false;
    private int currentColor1 = 0xff0000; // Červená
    private int currentColor2 = 0x0000ff; // Modrá

    // NOVÉ: Proměnné pro editaci vrcholů
    private Polygon.Point selectedVertex = null; // Vybraný vrchol pro editaci
    private boolean editing = false; // Režim editace

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.raster = panel.getRaster();
        this.lineRasterizer = new FilledLineRasterizer(raster);

        // Ujistíme se, že panel má focus pro klávesové události
        panel.setFocusable(true);
        panel.requestFocusInWindow();

        initListeners();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // Levá myš
                    if (e.isControlDown()) { // Ctrl + levé tlačítko - mazání vrcholů
                        System.out.println("Ctrl + levé tlačítko - pokus o mazání");
                        if (!drawing) {
                            Polygon.Point vertexToDelete = findNearestVertex(e.getX(), e.getY());
                            if (vertexToDelete != null) {
                                polygon.getVertices().remove(vertexToDelete);
                                raster.clear();
                                drawPolygon();
                                panel.repaint();
                                System.out.println("=== Vrchol úspěšně smazán ===");
                            } else {
                                System.out.println("Žádný vrchol k smazání nebyl nalezen");
                            }
                        }
                    }
                    else if (e.isAltDown()) { // Alt + levé tlačítko - přidání vrcholu na hranu
                        System.out.println("Alt + levé tlačítko - pokus o přidání vrcholu na hranu");
                        if (!drawing) {
                            int edgeIndex = findNearestEdge(e.getX(), e.getY());
                            if (edgeIndex != -1) {
                                // Vložit nový vrchol na nalezenou hranu
                                polygon.getVertices().add(edgeIndex, new Polygon.Point(e.getX(), e.getY()));
                                raster.clear();
                                drawPolygon();
                                panel.repaint();
                                System.out.println("=== Nový vrchol přidán na hranu: [" + e.getX() + ", " + e.getY() + "] ===");
                                System.out.println("Nový počet vrcholů: " + polygon.size());
                            } else {
                                System.out.println("Žádná hrana nebyla nalezena v dosahu");
                            }
                        }
                    }
                    else { // Normální levé tlačítko - kreslení
                        if (!drawing && !editing) {
                            // Začátek kreslení - uložit první bod
                            startX = e.getX();
                            startY = e.getY();
                            drawing = true;

                            // Přidat vrchol do polygonu
                            polygon.addVertex(startX, startY);
                            System.out.println("=== PŘIDÁN PRVNÍ VRCHOL: [" + startX + ", " + startY + "] ===");
                            System.out.println("Počet vrcholů: " + polygon.size());
                        } else if (drawing && !editing) {
                            // Konec kreslení - potvrdit úsečku
                            int endX = e.getX();
                            int endY = e.getY();

                            if (shiftPressed) {
                                // Režim s přichytáváním
                                Polygon.Point snapped = getSnappedPoint(startX, startY, endX, endY);
                                endX = snapped.x;
                                endY = snapped.y;
                            }

                            // Vykreslit finální úsečku
                            drawLine(startX, startY, endX, endY);

                            // Přidat další vrchol polygonu
                            polygon.addVertex(endX, endY);
                            System.out.println("=== PŘIDÁN DALŠÍ VRCHOL: [" + endX + ", " + endY + "] ===");
                            System.out.println("Počet vrcholů: " + polygon.size());

                            // Připravit se na další úsečku
                            startX = endX;
                            startY = endY;
                        }
                    }
                    panel.repaint();
                }
                else if (e.getButton() == MouseEvent.BUTTON3) { // Pravá myš - editace
                    if (!drawing) {
                        // Hledání nejbližšího vrcholu
                        selectedVertex = findNearestVertex(e.getX(), e.getY());
                        if (selectedVertex != null) {
                            editing = true;
                            System.out.println("Vybraný vrchol pro editaci: (" + selectedVertex.x + ", " + selectedVertex.y + ")");
                        } else {
                            System.out.println("Žádný vrchol nebyl nalezen pro editaci");
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (editing) {
                    editing = false;
                    selectedVertex = null;
                    System.out.println("Editace dokončena");
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawing && !editing) {
                    // "Pružná" čára - dočasné vykreslení
                    raster.clear();

                    // Vykreslit již potvrzené úsečky polygonu
                    drawPolygon();

                    // Vykreslit aktuální "pružnou" úsečku
                    int currentX = e.getX();
                    int currentY = e.getY();

                    if (shiftPressed) {
                        Polygon.Point snapped = getSnappedPoint(startX, startY, currentX, currentY);
                        currentX = snapped.x;
                        currentY = snapped.y;
                    }

                    drawLine(startX, startY, currentX, currentY);
                    panel.repaint();
                }
                else if (editing && selectedVertex != null) {
                    // Režim editace - přesun vrcholu
                    raster.clear();

                    // Aktualizace pozice vybraného vrcholu
                    selectedVertex.x = e.getX();
                    selectedVertex.y = e.getY();

                    // Překreslení polygonu
                    drawPolygon();
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
                System.out.println("Stisknuta klávesa: " + KeyEvent.getKeyText(e.getKeyCode()) + " (kód: " + e.getKeyCode() + ")");

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
                if (e.getKeyCode() == KeyEvent.VK_SPACE && polygon.size() > 2) {
                    closePolygon();
                }

                // NOVÉ: Mazání vybraného vrcholu - klávesa Delete nebo Backspace
                if ((e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) && selectedVertex != null) {
                    System.out.println("Delete/Backspace klávesa - pokus o mazání vybraného vrcholu");
                    polygon.getVertices().remove(selectedVertex);
                    raster.clear();
                    drawPolygon();
                    panel.repaint();
                    selectedVertex = null;
                    editing = false;
                    System.out.println("=== Vybraný vrchol smazán ===");
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
    private Polygon.Point getSnappedPoint(int startX, int startY, int endX, int endY) {
        int dx = endX - startX;
        int dy = endY - startY;

        // Určení nejbližšího směru (vodorovný, svislý nebo diagonální)
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        angle = (angle + 360) % 360; // Normalizace na 0-360

        if (angle >= 337.5 || angle < 22.5) {
            // Vodorovný doprava
            return new Polygon.Point(endX, startY);
        } else if (angle >= 22.5 && angle < 67.5) {
            // Diagonální ↗
            int dist = Math.min(Math.abs(dx), Math.abs(dy));
            return new Polygon.Point(startX + dist, startY + dist);
        } else if (angle >= 67.5 && angle < 112.5) {
            // Svislý nahoru
            return new Polygon.Point(startX, endY);
        } else if (angle >= 112.5 && angle < 157.5) {
            // Diagonální ↖
            int dist = Math.min(Math.abs(dx), Math.abs(dy));
            return new Polygon.Point(startX - dist, startY + dist);
        } else if (angle >= 157.5 && angle < 202.5) {
            // Vodorovný doleva
            return new Polygon.Point(endX, startY);
        } else if (angle >= 202.5 && angle < 247.5) {
            // Diagonální ↙
            int dist = Math.min(Math.abs(dx), Math.abs(dy));
            return new Polygon.Point(startX - dist, startY - dist);
        } else if (angle >= 247.5 && angle < 292.5) {
            // Svislý dolů
            return new Polygon.Point(startX, endY);
        } else {
            // Diagonální ↘
            int dist = Math.min(Math.abs(dx), Math.abs(dy));
            return new Polygon.Point(startX + dist, startY - dist);
        }
    }

    /**
     * Najde nejbližší vrchol k zadaným souřadnicím
     * @param x x-ová souřadnice
     * @param y y-ová souřadnice
     * @return nejbližší vrchol nebo null
     */
    private Polygon.Point findNearestVertex(int x, int y) {
        if (polygon.isEmpty()) {
            System.out.println("Seznam vrcholů je prázdný");
            return null;
        }

        Polygon.Point nearest = null;
        double minDistance = Double.MAX_VALUE;
        int threshold = 15; // Zvýšený poloměr hledání v pixelech

        System.out.println("Hledám vrchol poblíž [" + x + ", " + y + "]");
        System.out.println("Počet vrcholů: " + polygon.size());

        for (Polygon.Point vertex : polygon.getVertices()) {
            double distance = Math.sqrt(Math.pow(vertex.x - x, 2) + Math.pow(vertex.y - y, 2));
            System.out.println("Vrchol [" + vertex.x + ", " + vertex.y + "] - vzdálenost: " + distance);

            if (distance < minDistance && distance < threshold) {
                minDistance = distance;
                nearest = vertex;
            }
        }

        if (nearest != null) {
            System.out.println("Nalezen nejbližší vrchol: [" + nearest.x + ", " + nearest.y + "]");
        } else {
            System.out.println("Žádný vrchol nebyl nalezen v dosahu");
        }

        return nearest;
    }

    /**
     * Najde nejbližší hranu k zadaným souřadnicím a vrátí index pro vložení
     * @param x x-ová souřadnice
     * @param y y-ová souřadnice
     * @return index pro vložení nového vrcholu nebo -1 pokud není nalezena hrana
     */
    private int findNearestEdge(int x, int y) {
        if (polygon.size() < 2) return -1;

        int nearestEdgeIndex = -1;
        double minDistance = Double.MAX_VALUE;
        int threshold = 20; // Poloměr hledání hrany v pixelech

        System.out.println("Hledám hranu poblíž [" + x + ", " + y + "]");

        // Procházíme všechny hrany polygonu
        for (int i = 0; i < polygon.size(); i++) {
            Polygon.Point p1 = polygon.getVertices().get(i);
            Polygon.Point p2 = polygon.getVertices().get((i + 1) % polygon.size()); // Uzavřený polygon

            // Vzdálenost od bodu k úsečce
            double distance = pointToLineDistance(x, y, p1.x, p1.y, p2.x, p2.y);

            System.out.println("Hrana [" + p1.x + ", " + p1.y + "] - [" + p2.x + ", " + p2.y + "] - vzdálenost: " + distance);

            if (distance < minDistance && distance < threshold) {
                minDistance = distance;
                nearestEdgeIndex = i + 1; // Vložit za aktuální vrchol
            }
        }

        if (nearestEdgeIndex != -1) {
            System.out.println("Nalezena nejbližší hrana, index pro vložení: " + nearestEdgeIndex);
        } else {
            System.out.println("Žádná hrana nebyla nalezena v dosahu");
        }

        return nearestEdgeIndex;
    }

    /**
     * Spočítá vzdálenost bodu od úsečky
     */
    private double pointToLineDistance(int px, int py, int x1, int y1, int x2, int y2) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = (len_sq != 0) ? dot / len_sq : -1;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
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
        if (polygon.size() < 2) return;

        for (int i = 0; i < polygon.size() - 1; i++) {
            Polygon.Point p1 = polygon.getVertices().get(i);
            Polygon.Point p2 = polygon.getVertices().get(i + 1);
            drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        // Pokud je polygon uzavřený, nakreslit poslední hranu
        if (polygon.size() > 2 && !drawing) {
            Polygon.Point first = polygon.getVertices().get(0);
            Polygon.Point last = polygon.getVertices().get(polygon.size() - 1);
            drawLine(last.x, last.y, first.x, first.y);
        }
    }

    // Uzavření polygonu - spojení posledního s prvním bodem
    private void closePolygon() {
        if (polygon.size() > 2) {
            Polygon.Point first = polygon.getVertices().get(0);
            Polygon.Point last = polygon.getVertices().get(polygon.size() - 1);
            drawLine(last.x, last.y, first.x, first.y);
            panel.repaint();
            System.out.println("=== POLYGON UZAVŘEN ===");
            System.out.println("Počet vrcholů: " + polygon.size());

            // NEMAŽEME vrcholy, pouze ukončíme kreslení
            drawing = false;
            System.out.println("Režim kreslení ukončen, vrcholy zachovány");
        }
    }

    // Smazání plátna - Uloha1 požadavek
    private void clearCanvas() {
        raster.clear();
        System.out.println("=== CLEAR CANVAS ===");
        System.out.println("Počet vrcholů před vyčištěním: " + polygon.size());
        polygon.clear();
        drawing = false;
        editing = false;
        selectedVertex = null;
        panel.repaint();
        System.out.println("Seznam vrcholů vyčištěn");
    }
}