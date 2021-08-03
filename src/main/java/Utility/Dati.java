/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

/**
 *
 * @author diego
 */
public class Dati {

    public static double[][] CostiC1ij() {
        double[][] C1ij = {
            {   0,       49.41,   72,   173.01,   24.75,   69.6,   86.4,   82.35},
            {76.95,       0.00, 37.44,  100.74,   34.65,   63.6,   79.2,   69.54},
            {142.5,      47.58,     0,  148.92,   99.00,   16.8,  80.00,  102.48},
            {225.15,     84.18, 97.92,    0.00,    92.4,  111.6,   62.4,   80.52},
            {42.75,      38.43,   86.4, 122.64,    0.00,   45.6,  60.00,  102.48},
        };
        return C1ij;
    }

    public static double[][] CostiC2ij() {
        double[][] C2ij = {
            {0.00, 50.22, 46.50, 116.13, 39.15, 20.88, 43.20, 71.55},
            {25.92, 0.00, 24.18, 67.62, 54.81, 19.08, 39.60, 60.42},
            {48.00, 48.36, 0.00, 99.96, 156.60, 5.04, 90.00, 89.04},
            {75.84, 85.56, 63.24, 0.00, 146.16, 33.48, 31.20, 69.96},
            {14.40, 39.06, 55.80, 82.32, 0.00, 13.68, 30.00, 89.04},};

        return C2ij;
    }

    public static double[] CostiFissiGiornalieri() {
        double[] CF_annui = {150000, 145000, 180000, 175000, 190000};
        double[] CF_gg = new double[CF_annui.length];
        for (int i = 0; i < CF_gg.length; i++) {
            double quoziente = CF_annui[i] / GiorniLavorativi();
            CF_gg[i] = quoziente;
        }
        return CF_gg;
    }

    public static double[] CapacitàGiornaliere() {
        double[] capacità = {60, 42, 58, 41, 60};
        return capacità;

    }

    public static double[][] DomandeGiornaliereProdotti() {
        double[][] Domande = {
            {9.5, 6.1, 4.8, 7.3, 5.5, 4.00, 8.00, 6.1},
            {3.2, 6.2, 3.1, 4.9, 8.7,  1.2, 4.00, 5.3},};
        return Domande;

    }

    public static double GiorniLavorativi() {
        return 300.00;
    }

    public static double CostoTrasportoUnitario() {
        return 0.3;
    }

    public static int NumeroProdotti() {
        return 2;
    }

    public static int NumeroSitiPotenziali() {
        return 5;
    }

    public static int NumeroSitiDaServire() {
        return 8;
    }
}
