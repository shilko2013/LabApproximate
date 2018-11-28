package com.shilko.ru.approximate;

import java.util.List;

public interface Approximation {

    //returns pair of koefs<a,b> and worst point
    Pair<Pair<Double, Double>, Point> approximateReturnWorstPoint(List<Point> points);

    //returns pair of koefs<a,b>
    Pair<Double, Double> approximate(List<Point> points);
}
