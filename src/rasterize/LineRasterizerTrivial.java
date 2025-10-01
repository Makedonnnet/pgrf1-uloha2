package rasterize;

public class LineRasterizerTrivial extends LineRasterizer {

    public LineRasterizerTrivial(RasterBufferedImage raster) {
        super(raster);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        // Zajistíme, že vždy kreslíme zleva doprava
        if (x1 > x2) {
            // Prohození bodů
            int temp = x1;
            x1 = x2;
            x2 = temp;
            temp = y1;
            y1 = y2;
            y2 = temp;
        }

        // Vodorovná čára
        if (y1 == y2) {
            for (int x = x1; x <= x2; x++) {
                raster.setPixel(x, y1, 0xff0000); // Červená
            }
            return;
        }

        // Svislá čára
        if (x1 == x2) {
            int startY = Math.min(y1, y2);
            int endY = Math.max(y1, y2);
            for (int y = startY; y <= endY; y++) {
                raster.setPixel(x1, y, 0xff0000); // Červená
            }
            return;
        }

        // Šikmé čáry - DDA algoritmus
        float k = (y2 - y1) / (float) (x2 - x1);
        float q = y1 - k * x1;

        for (int x = x1; x <= x2; x++) {
            int y = Math.round(k * x + q);
            if (x >= 0 && x < raster.getWidth() && y >= 0 && y < raster.getHeight()) {
                raster.setPixel(x, y, 0xff0000); // Červená
            }
        }
    }
}