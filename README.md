# PGRF1 – Úloha 1: Čára, barevný přechod a n-úhelník
**Autor:** Maksym Makedonskyi  
**Rok:** 2025  
**Jazyk:** Java 21

---

## 🎯 Cíl úlohy
Cílem bylo vytvořit jednoduchou aplikaci pro kreslení úseček a n-úhelníků do rastrového obrázku.  
Aplikace má umožňovat interaktivní práci myší, kreslení pružné čáry, vytváření vrcholů polygonu a práci s barevným přechodem (gradientem).

---

## 🧩 Implementace

### Raster
- **Soubor:** `Raster.java`, `RasterBufferedImage.java`
- Definuje rozhraní a implementaci rastru pomocí třídy `BufferedImage`.
- Metody: `setPixel`, `getPixel`, `clear`, kontrola hranic.

### Line Rasterizers
- **Soubor:** `LineRasterizer.java`, `LineRasterizerTrivial.java`, `FilledLineRasterizer.java`
- Třída `LineRasterizerTrivial` – jednoduché vykreslování (triviální metoda).
- Třída `FilledLineRasterizer` – implementuje **Bresenhamův algoritmus**.
- Podporuje **barevný přechod (gradient)** mezi dvěma barvami.

### Polygon
- **Soubor:** `Polygon.java`
- Umožňuje přidávání, mazání a mazání všech vrcholů.
- Vrcholy jsou ukládány v seznamu a kresleny jako uzavřený n-úhelník.

### Controller
- **Soubor:** `Controller2D.java`
- Obsahuje hlavní logiku ovládání myší a klávesnicí.
- Funkce:
    - kreslení pružné čáry (natahovaná úsečka),
    - přidávání vrcholů polygonu levým tlačítkem myši,
    - úprava existujícího vrcholu pravým tlačítkem,
    - přidání vrcholu na hranu pomocí **Alt + levé tlačítko**,
    - režim **Shift** – zarovnání na vodorovnou/svislou/úhlopříčnou osu,
    - klávesa **C** – vymazání obrazovky.

### View
- **Soubor:** `Panel.java`, `Window.java`
- Zajišťují zobrazení rastrového obrázku a GUI okna.

---

## 🎨 Barevný přechod
Barevný přechod je implementován v `FilledLineRasterizer.rasterize(x1, y1, x2, y2, color1, color2)`  
a zároveň dostupný v metodě `Controller2D.drawGradientLine`.  
Interpolace RGB probíhá lineárně podle délky úsečky.

---

## 🧮 Použitý algoritmus
**Bresenhamův algoritmus**
- Výhody:
    - používá pouze celočíselné výpočty → rychlý,
    - není potřeba pracovat s floaty nebo násobeními.
- Nevýhody:
    - nelze snadno kombinovat s antialiasingem,
    - nutnost implementace zvlášť pro různé kvadranty.

---

## 🕹️ Ovládání

| Akce | Popis |
|------|--------|
| **Levý klik + tažení** | Kreslení pružné úsečky |
| **Shift + tažení** | Zarovnání čáry (vodorovně/svisle/úhlopříčně) |
| **Alt + levý klik** | Přidání vrcholu na hranu polygonu |
| **Pravý klik** | Úprava nejbližšího vrcholu |
| **Klávesa C** | Vymazání celé plochy |
| **Barevný režim (Gradient)** | Automaticky aktivní při kreslení úsečky |

---

## 🧠 Bonusové funkce
- Přidávání vrcholu na nejbližší hranu (Alt + klik)
- Úprava vrcholů myší
- Barevný přechod mezi dvěma barvami
- Režim Shift pro přesné úhly

---

## ▶️ Spuštění
1. Otevřete projekt v **IntelliJ IDEA** nebo jiném IDE.
2. Ujistěte se, že používáte JDK 21+.
3. Spusťte soubor `src/Main.java`.
4. Otevře se okno s kreslicí plochou.

---

## 📁 Struktura projektu
src/
├── rasterize/
│ ├── Raster.java
│ ├── RasterBufferedImage.java
│ ├── LineRasterizer.java
│ ├── LineRasterizerTrivial.java
│ ├── FilledLineRasterizer.java
│ └── Polygon.java
├── controller/
│ └── Controller2D.java
├── view/
│ ├── Panel.java
│ └── Window.java
└── Main.java