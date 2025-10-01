package rasterize;

/**
 * Implementace rasterizace úsečky pomocí Bresenhamova algoritmu.
 *
 * Výhody:
 * - Používá pouze celočíselnou aritmetiku (rychlé)
 * - Žádné dělení ani násobení s plovoucí čárkou
 * - Vysoká přesnost pro všechny směry
 *
 * Nevýhody:
 * - Složitější implementace než DDA
 * - Nutnost řešit všechny oktanty
 */
public class FilledLineRasterizer extends LineRasterizer {

    public FilledLineRasterizer(RasterBufferedImage raster) {
        super(raster);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        // Bresenhamův algoritmus pro všechny směry

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        // Určení směru kreslení
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;

        int err = dx - dy;
        int e2;

        int x = x1;
        int y = y1;

        while (true) {
            // Vykreslení aktuálního pixelu
            if (x >= 0 && x < raster.getWidth() && y >= 0 && y < raster.getHeight()) {
                raster.setPixel(x, y, 0xff0000); // Červená
            }

            // Ukončení při dosažení koncového bodu
            if (x == x2 && y == y2) break;

            e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }

            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    /**
     * Vykreslí úsečku s barevným přechodem mezi dvěma barvami
     * Použitý algoritmus: Bresenham s lineární interpolací barev
     *
     * Výhody: Plynulý barevný přechod, zachovává přesnost Bresenhamova algoritmu
     * Nevýhody: Vyšší výpočetní náročnost kvůli interpolaci barev
     */
    public void rasterizeWithGradient(int x1, int y1, int color1, int x2, int y2, int color2) {
        // Rozložení barev na RGB komponenty
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;

        int err = dx - dy;
        int e2;

        int x = x1;
        int y = y1;

        // Celková délka úsečky pro interpolaci
        double totalLength = Math.sqrt(dx * dx + dy * dy);

        while (true) {
            if (x >= 0 && x < raster.getWidth() && y >= 0 && y < raster.getHeight()) {
                // Výpočet poměru (0.0 - 1.0) pro interpolaci
                double currentLength = Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
                double ratio = (totalLength > 0) ? currentLength / totalLength : 0;
                ratio = Math.max(0, Math.min(1, ratio)); // Omezení na rozsah 0-1

                // Lineární interpolace barev
                int r = (int) (r1 + (r2 - r1) * ratio);
                int g = (int) (g1 + (g2 - g1) * ratio);
                int b = (int) (b1 + (b2 - b1) * ratio);

                int color = (r << 16) | (g << 8) | b;
                raster.setPixel(x, y, color);
            }

            // Ukončení při dosažení koncového bodu
            if (x == x2 && y == y2) break;

            e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }

            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }
}