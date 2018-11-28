package com.shilko.ru.approximate;

public class KramerMatrix {

    public static Pair<Double, Double> getTwoRoots(Double[][] koefs) {
        double det = koefs[0][0] * koefs[1][1] - koefs[0][1] * koefs[1][0];
        double detA = koefs[0][2] * koefs[1][1] - koefs[0][1] * koefs[1][2];
        double detB = koefs[0][0] * koefs[1][2] - koefs[0][2] * koefs[1][0];
        return new Pair<>(detA / det, detB / det);
    }
}
