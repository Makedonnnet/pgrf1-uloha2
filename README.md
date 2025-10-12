# PGRF1 – Úloha 1: Čára, barevný přechod a n-úhelník

**Autor:** Maksym Makedonskyi  
**Rok:** 2025  
**Jazyk:** Java 21

---

##  Cíl úlohy

Cílem bylo vytvořit jednoduchou aplikaci pro kreslení úseček a n-úhelníků do rastrového obrázku. Aplikace umožňuje interaktivní práci myší, kreslení pružné čáry, vytváření vrcholů polygonu a práci s barevným přechodem (gradientem).

---

##  Implementace

### Raster
- **Soubor:** `Raster.java`, `RasterBufferedImage.java`
- Definuje rozhraní a implementaci rastru pomocí třídy `BufferedImage`
- Metody: `setPixel`, `getPixel`, `clear`, kontrola hranic

### Line Rasterizers
- **Soubor:** `LineRasterizer.java`, `LineRasterizerTrivial.java`, `FilledLineRasterizer.java`
- Třída `LineRasterizerTrivial` – jednoduché vykreslování (triviální metoda)
- Třída `FilledLineRasterizer` – implementuje **Bresenhamův algoritmus**
- Podporuje **barevný přechod (gradient)** mezi dvěma barvami

### Polygon
- **Soubor:** `Polygon.java`
- Umožňuje přidávání, mazání a mazání všech vrcholů
- Vrcholy jsou ukládány v seznamu a kresleny jako uzavřený n-úhelník

### Controller
- **Soubor:** `Controller2D.java`
- Obsahuje hlavní logiku ovládání myší a klávesnicí
- **Funkce:**
    - Kreslení pružné čáry (natahovaná úsečka)
    - Přidávání vrcholů polygonu levým tlačítkem myši
    - Úprava existujícího vrcholu pravým tlačítkem
    - Přidání vrcholu na hranu pomocí **Alt + levé tlačítko**
    - Režim **Shift** – zarovnání na vodorovnou/svislou/úhlopříčnou osu
    - Klávesa **C** – vymazání obrazovky
    - Klávesa **L** – přepínání mezi režimem čar a polygonů

### View
- **Soubor:** `Panel.java`, `Window.java`
- Zajišťují zobrazení rastrového obrázku a GUI okna

---

##  Barevný přechod

Barevný přechod je implementován v `FilledLineRasterizer.rasterize(x1, y1, x2, y2, color1, color2)` a zároveň dostupný v metodě `Controller2D.drawGradientLine`. Interpolace RGB probíhá lineárně podle délky úsečky.

**Barevný přechod:** Červená (0xFF0000) → Modrá (0x0000FF)

---

##  Použitý algoritmus

**Bresenhamův algoritmus**

**Výhody:**
- Používá pouze celočíselné výpočty → rychlý
- Není potřeba pracovat s floaty nebo násobeními
- Efektivita - nevyžaduje násobení ani dělení v hlavním cyklu

**Nevýhody:**
- Nelze snadno kombinovat s antialiasingem
- Nutnost implementace zvlášť pro různé kvadranty
- Složitější inicializace než u DDA algoritmu

---

## ️ Ovládání

| Akce | Popis |
|------|-------|
| **Levý klik + tažení** | Kreslení pružné úsečky |
| **Shift + tažení** | Zarovnání čáry (vodorovně/svisle/úhlopříčně) |
| **Alt + levý klik** | Přidání vrcholu na hranu polygonu |
| **Pravý klik + tažení** | Úprava nejbližšího vrcholu |
| **Ctrl + levý klik** | Smazání nejbližšího vrcholu |
| **Klávesa C** | Vymazání celé plochy |
| **Klávesa G** | Přepnutí režimu gradientu |
| **Klávesa L** | Přepnutí mezi režimem čar a polygonů |
| **Klávesa R, B, Y, P** | Výběr barvy čáry (červená, modrá, žlutá, růžová) |
| **Klávesa Space** | Uzavření polygonu |
| **Klávesa Delete** | Smazání vybraného vrcholu |

---

##  Bonusové funkce

- ✅ Přidávání vrcholu na nejbližší hranu (Alt + klik)
- ✅ Úprava vrcholů myší (pravý klik + tažení)
- ✅ Mazání vrcholů (Ctrl + klik)
- ✅ Barevný přechod mezi dvěma barvami
- ✅ Režim Shift pro přesné úhly
- ✅ Uzavírání polygonu (Space)
- ✅ Výběr barev pro kreslení
- ✅ Režim pro kreslení jednotlivých čar (L)

---

##  Spuštění

1. Otevřete projekt v **IntelliJ IDEA** nebo jiném IDE
2. Ujistěte se, že používáte **JDK 21+**
3. Spusťte soubor `src/view/Main.java`
4. Otevře se okno s kreslicí plochou 800x600 pixelů

---

##  Struktura projektu
src/
├── controller/ # Řízení událostí myši a klávesnice
│ └── Controller2D.java
├── rasterize/ # Algoritmy rasterizace a datové struktury
│ ├── Point.java
│ ├── Polygon.java
│ ├── Raster.java
│ ├── RasterBufferedImage.java
│ ├── LineRasterizer.java
│ ├── FilledLineRasterizer.java
│ ├── LineRasterizerTrivial.java
│ └── LineRasterizerGraphics.java
└── view/ # Uživatelské rozhraní
│    ├──Panel.java
│    ├──Window.java
└── Main.java