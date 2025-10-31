 PGRF1 - Úloha 2: Vyplnění a ořezání n-úhelníkové oblasti

Autor: Maksym Makedonskyi
Rok: 2025
Jazyk: Java 21
Cíl úlohy

Cílem této úlohy bylo navázat na předchozí projekt kreslení polygonů a implementovat pokročilé algoritmy počítačové grafiky: semínkové vyplňování (Flood Fill), ořezání polygonu (Clipping) a vyplňování pomocí Scan-line.
Implementace

Raster

Soubor: `Raster.java`, `RasterBufferedImage.java`
Definuje rozhraní a implementaci rastru pomocí třídy `BufferedImage`.
Metody: `setPixel`, `getPixel`, `clear`.
Line Rasterizers

Soubor: `LineRasterizer.java`, `FilledLineRasterizer.java`
Implementuje `Bresenhamův algoritmus` pro kreslení úseček.
Podporuje barevný přechod (gradient) (klávesa **G**).
Polygon & Rectangle

Soubor: `Polygon.java`, `Rectangle.java`
`Polygon` ukládá vrcholy. `Rectangle` dědí z `Polygon` a implementuje logiku pro kreslení obdélníku ze základny a třetího bodu (klávesa **O**).
Fill (Vyplňování)

Soubor: `Fill/SeedFiller.java`, `Fill/ScanLineFiller.java`
`SeedFiller` implementuje algoritmus semínkového vyplnění. Podporuje dva režimy:
* **Režim 'F' (Pozadí):** Vyplnění omezené barvou pozadí (černá).
* **Režim 'B' (Hranice):** Vyplnění omezené barvou hranice (hranice je překreslena na bílo).
  `ScanLineFiller` implementuje algoritmus Scan-line pro vyplnění geometricky zadaného polygonu. Používá se pro automatické vyplnění výsledku ořezání.
  Clipper (Ořezávání)

Soubor: `Clipper/Clipper.java`
Implementuje algoritmus **Sutherland-Hodgman** pro ořezání libovolného polygonu konvexním oknem.
Ořezávací okno je fixně zadaný azurový pětiúhelník.
Controller

Soubor: `Controller2D.java`
Obsahuje hlavní logiku ovládání myší a klávesnicí. Spravuje všechny režimy aplikace.
Funkce:
* Kreslení polygonů, obdélníků ('O') a úseček ('L').
* Přepínání režimů vyplňování ('F', 'B') a ořezávání ('K').
* Editace vrcholů (Bonus 1).
* Přepínání režimu vyplňování vzorem ('T') (Bonus 2).
* Mazání plátna a všech datových struktur ('C').
  View

Soubor: `Panel.java`, `Window.java`
Zajišťují zobrazení rastrového obrázku. `Window.java` obsahuje `JTextArea` se seznamem všech ovládacích prvků.
Použité algoritmy

* **Bresenhamův algoritmus:** Pro rychlou a efektivní rasterizaci úseček.
* **Semínkové vyplnění (Flood Fill):** Pro vyplňování rastrově zadané oblasti (s použitím Stacku pro zamezení rekurze).
* **Sutherland-Hodgman:** Pro ořezání polygonu konvexním oknem.
* **Scan-line algoritmus:** Pro vyplnění geometricky zadaného polygonu (výsledku ořezání) pomocí Tabulky hran (ET) a Tabulky aktivních hran (AET).

---

  Ovládání

Aplikace se ovládá pomocí klávesnice a myši. Legenda je zobrazena pod plátnem.

| Klávesa / Myš | Akce |
| :--- | :--- |
| **LMB** (Klik) | Kreslení vrcholu (v režimu polygonu) |
| **Pravé Tlačítko (táhnout)** | **Editace:** Posun nejbližšího vrcholu (Bonus 1) |
| **Ctrl + Klik** | **Editace:** Smazání nejbližšího vrcholu (Bonus 1) |
| **Alt + Klik** | **Editace:** Přidání vrcholu na nejbližší hranu (Bonus 1) |
| **Delete / Backspace**| **Editace:** Smazání vybraného vrcholu (Bonus 1) |
| | |
| **C** | **Smazat vše** (Clear) |
| **Space** | Uzavření aktuálního polygonu |
| **Shift** (držet) | Přichytávání k osám (Snap) |
| | |
| **O** | Režim kreslení **Obdélníku** |
| **L** | Režim kreslení **Úseček** |
| **K** | Režim **Ořezání** (zobrazí/skryje azurové okno) |
| | |
| **F** | Režim vyplnění (Pozadí) |
| **B** | Režim vyplnění (Hranice) |
| **T** | **Přepnout Vzor** (Bonus 2: Šachovnice) |
| **G** | Přepnout Gradient |
| | |
| **R** | Barva: Červená |
| **Y** | Barva: Žlutá |
| **P** | Barva: Růžová |
| **B** | Barva: Modrá (je přepsána režimem 'B') |

---

  Bonusové funkce

* **Bonus 1:** Implementována plná editace polygonů (přidání, mazání, posun vrcholů).
* **Bonus 2:** Implementováno vyplňování vzorem (`PatternFill`). Režimem 'T' se aktivuje šachovnicový vzor, který funguje jak pro `SeedFiller`, tak pro `ScanLineFiller`.

---

  Spuštění

1.  Otevřete projekt v IntelliJ IDEA.
2.  Ujistěte se, že používáte **JDK 21+**.
3.  Spusťte soubor `Main.java`.

---

 Struktura projektu
 src/
 ├── controller/
 │   └── Controller2D.java
 │
 ├── rasterize/
 │   ├── Point.java
 │   ├── Polygon.java
 │   ├── Rectangle.java
 │   ├── Raster.java
 │   ├── RasterBufferedImage.java
 │   ├── LineRasterizer.java
 │   ├── FilledLineRasterizer.java
 │   ├── LineRasterizerGraphics.java
 │   └── LineRasterizerTrivial.java
 │
 ├── Fill/
 │   ├── SeedFiller.java
 │   ├── ScanLineFiller.java
 │   ├── PatternFill.java
 │   └── CheckerboardPattern.java
 │
 ├── Clipper/
 │   └── Clipper.java
 │
 └── view/
 │   ├── Panel.java
 │   ├── Window.java
 │
 └── Main.java