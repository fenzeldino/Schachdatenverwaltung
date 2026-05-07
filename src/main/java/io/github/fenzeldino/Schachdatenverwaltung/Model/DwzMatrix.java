package io.github.fenzeldino.Schachdatenverwaltung.Model;

public class DwzMatrix {

    // Untergrenzen der DWZ-Bereiche aus der linken Spalte
    private static final int[] DWZ_THRESHOLDS = {
            2001,1801,1601,1401,1101,801,501, 201,0
    };

    // Obergrenzen der Altersgruppen aus der Kopfzeile
    private static final int[] AGE_THRESHOLDS = {
            6, 12, 16, 20, 35, 50, 60, 70, Integer.MAX_VALUE
    };

    // Die Faktoren-Matrix (Zeile = DWZ-Bereich, Spalte = Altersgruppe)
    private static final double[][] FAKTOREN = {
            // bis 6 | 7-12 | 13-16 | 17-20 | 21-35 | 36-50 | 51-60 | 61-70 | > 70
            { 1.5,   1.0,   0.5,   0.0,   0.0,   0.0,   0.5,   1.0,   1.5 }, // über 2000
            { 2.0,   1.5,   1.5,   1.0,   0.5,   1.0,   1.5,   1.5,   2.0 }, // 1801 - 2000
            { 2.5,   3.0,   1.5,   1.5,   1.0,   1.5,   1.5,   2.0,   2.5 }, // 1601 - 1800
            { 2.5,   2.5,   2.0,   1.5,   1.5,   1.5,   2.0,   2.5,   2.5 }, // 1401 - 1600
            { 3.0,   2.5,   2.5,   2.0,   1.5,   2.0,   2.5,   2.5,   3.0 }, // 1101 - 1400
            { 3.5,   3.0,   2.5,   0.0,   0.0,   0.0,   0.0,   0.0,   0.0 }, // 801 - 1100
            { 3.5,   3.0,   3.0,   0.0,   0.0,   0.0,   0.0,   0.0,   0.0 }, // 501 - 800
            { 4.0,   3.5,   3.5,   0.0,   0.0,   0.0,   0.0,   0.0,   0.0 }, // 201 - 500
            { 4.5,   4.0,   4.5,   0.0,   0.0,   0.0,   0.0,   0.0,   0.0 }  // unter 201
    };

    /**
     * Sucht den Beschleunigungsfaktor aus der Matrix.
     */
    public static double getFactor(int dwz, int alter) {
        int row = 0;
        int col = 0;

        // Zeile bestimmen (DWZ)
        for (int i = 0; i < DWZ_THRESHOLDS.length - 1; i++) {
            if (dwz >= DWZ_THRESHOLDS[i]) {
                row = i;
                break;
            }
        }

        // Spalte bestimmen (Alter)
        for (int j = 0; j < AGE_THRESHOLDS.length; j++) {
            if (alter <= AGE_THRESHOLDS[j]) {
                col = j;
                break;
            }
        }

        return FAKTOREN[row][col];
    }
}