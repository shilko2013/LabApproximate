package com.shilko.ru.approximate;

import java.util.List;

//searches koefs for equation y = a*lnx + b
public class LogApproximation implements Approximation {
    @Override
    public Pair<Pair<Double, Double>, Point> approximateReturnWorstPoint(List<Point> points) {
        Pair<Double, Double> koefs = approximate(points);
        double a = koefs.getFirst();
        double b = koefs.getSecond();
        double maxDeviation = -1;
        int indexMaxDeviation = -1;
        for (int i = 0; i < points.size(); ++i) {
            double x = points.get(i).getX();
            double deviation = Math.abs(a * Math.log(x) + b - points.get(i).getY());
            if (deviation > maxDeviation) {
                maxDeviation = deviation;
                indexMaxDeviation = i;
            }
        }
        return new Pair<>(koefs, points.get(indexMaxDeviation));
    }

    @Override
    public Pair<Double, Double> approximate(List<Point> points) {
        double sumLNXX = points.stream().mapToDouble(Point::getX).map(Math::log).map(x -> x * x).sum();
        double sumLNX = points.stream().mapToDouble(Point::getX).map(Math::log).sum();
        double sumYLNX = points.stream().mapToDouble(point -> point.getY() * Math.log(point.getX())).sum();
        double sumY = points.stream().mapToDouble(Point::getY).sum();
        return KramerMatrix.getTwoRoots(new Double[][]{{sumLNXX, sumLNX, sumYLNX}, {sumLNX, ((Integer) points.size()).doubleValue(), sumY}});
    }
}
