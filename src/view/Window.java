package view;

import javax.swing.*;
import java.awt.*; // Importujeme BorderLayout a další

public class Window extends JFrame {

    private final Panel panel;

    public Window(int width, int height) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("PGRF1 2024/2025");

        // --- NOVÝ KÓD: Nastavení BorderLayout ---
        // Nastavíme BorderLayout, abychom mohli vložit plátno a text
        setLayout(new BorderLayout());
        // --- KONEC NOVÉHO KÓDU ---

        panel = new Panel(width, height);

        // Přidáme plátno do CENTRA
        add(panel, BorderLayout.CENTER);

        // --- NOVÝ KÓD: Vytvoření textové oblasti s nápovědou ---
        JTextArea hintArea = new JTextArea();

        // --- Text nápovědy (AKTUALIZOVANÁ VERZE) ---
        String hintText =
                " REŽIMY:\n" +
                        "   O - Kreslit Obdélník | L - Kreslit Úsečky | K - Režim Ořezání\n" +
                        "   F - Vyplnění (Pozadí) | B - Vyplnění (Hranice) | T - Přepnout Vzor (Šachovnice)\n" +
                        " KLÁVESY:\n" +
                        "   C - Smazat Vše | G - Přepnout Gradient | R/Y/P - Změna Barvy\n" +
                        "   SPACE - Uzavřít Polygon | SHIFT (držet) - Přichytávání k osám\n" +
                        " MYŠ (Editace):\n" +
                        "   Pravé Tlačítko (táhnout) - Vybrat a Posunout Vrchol\n" +
                        "   Ctrl + Klik - Smazat Vrchol | Alt + Klik - Přidat Vrchol na Hranu\n";
        // --- KONEC AKTUALIZOVANÉ VERZE ---

        hintArea.setText(hintText);
        hintArea.setEditable(false); // Aby uživatel nemohl text měnit
        hintArea.setBackground(Color.DARK_GRAY); // Nastavíme pozadí
        hintArea.setForeground(Color.LIGHT_GRAY); // Nastavíme barvu písma
        hintArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Nastavíme font

        // Přidáme textovou oblast DOLŮ (SOUTH)
        add(hintArea, BorderLayout.SOUTH);
        // --- KONEC NOVÉHO KÓDU ---

        pack(); // Přizpůsobí velikost okna obsahu
        setVisible(true); // Zobrazíme okno

        panel.setFocusable(true);
        panel.grabFocus();
    }

    public Panel getPanel() {
        return panel;
    }
}