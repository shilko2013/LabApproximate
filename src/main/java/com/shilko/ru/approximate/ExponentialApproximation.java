package com.shilko.ru.approximate;

import java.util.List;

//searches koefs for equation y = b*e^(a*x)
public class ExponentialApproximation implements Approximation {

    @Override
    public Pair<Pair<Double, Double>, Point> approximateReturnWorstPoint(List<Point> points) {
        Pair<Double, Double> koefs = approximate(points);
        double a = koefs.getFirst();
        double b = koefs.getSecond();
        double maxDeviation = Double.MIN_VALUE;
        int indexMaxDeviation = -1;
        for (int i = 0; i < points.size(); ++i) {
            double x = points.get(i).getX();
            double deviation = Math.abs(b * Math.pow(Math.E, a * x) - points.get(i).getY());
            if (deviation > maxDeviation) {
                maxDeviation = deviation;
                indexMaxDeviation = i;
            }
        }
        return new Pair<>(koefs, points.get(indexMaxDeviation));
    }

    @Override
    public Pair<Double, Double> approximate(List<Point> points) {
        double sumXX = points.stream().mapToDouble(Point::getX).map(x -> x * x).sum();
        double sumX = points.stream().mapToDouble(Point::getX).sum();
        double sumXLNY = points.stream().mapToDouble(point -> point.getX() * Math.log(point.getY())).sum();
        double sumLNY = points.stream().mapToDouble(Point::getY).map(Math::log).sum();
        Pair<Double, Double> koefs = KramerMatrix.getTwoRoots(new Double[][]{{sumXX, sumX, sumXLNY}, {sumX, ((Integer) points.size()).doubleValue(), sumLNY}});
        koefs.setSecond(Math.pow(Math.E, koefs.getSecond()));
        return koefs;
    }
}
