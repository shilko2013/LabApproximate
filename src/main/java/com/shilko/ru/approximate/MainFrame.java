package com.shilko.ru.approximate;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {

    public static void main(final String[] args) {

        final MainFrame mainFrame = new MainFrame("Аппроксимация функции");
        mainFrame.pack();
        mainFrame.setMinimumSize(mainFrame.getSize());
        RefineryUtilities.centerFrameOnScreen(mainFrame);
        mainFrame.setVisible(true);

    }

    private final List<Point> pointList = new ArrayList<>();
    private final XYSeries points = new XYSeries("Точки");
    private final XYSeries func1 = new XYSeries("Аппроксимирующая функция");
    private final XYSeries func2 = new XYSeries("Аппроксимирующая функция, исключая наиболее отклоняющуюся точку");
    private final XYSeries badPoint = new XYSeries("Наиболее отклоняющаяся точка");
    private final XYSeriesCollection dataset = new XYSeriesCollection();
    private final IntegerTextField x = new IntegerTextField();
    private final IntegerTextField y = new IntegerTextField();
    private PointTable pointTable;
    private JScrollPane scrollPaneTable;
    private ChartPanel chartPanel;
    private final JButton addPoint = new JButton("Добавить точку");
    private final JButton deletePoint = new JButton("Удалить выделенные точки");
    private final JButton deleteAllPoints = new JButton("Удалить все точки");
    private final JButton approximate = new JButton("Аппроксимировать функцию");
    private final JLabel xLabel = new JLabel("X: ");
    private final JLabel yLabel = new JLabel("Y: ");
    private final JLabel functionLabel = new JLabel("Функция: ");
    private final JComboBox<String> functions =
            new JComboBox<String>(new String[]{"y = a*x + b", "y = a*lnx + b", "y = b*e^(a*x)"});
    private final JLabel oldA = new JLabel("");
    private final JLabel oldB = new JLabel("");
    private final JLabel newA = new JLabel("");
    private final JLabel newB = new JLabel("");
    private final JLabel badPointX = new JLabel("");
    private final JLabel badPointY = new JLabel("");

    public MainFrame(final String title) {
        super(title);
        initGraph();
        initScrollPointTable();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addOnExitListener();
        initButtons();
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        x.setPreferredSize(new Dimension(200, 20));
        y.setPreferredSize(new Dimension(200, 20));
        JPanel graphAndTable = new JPanel(new BorderLayout());
        graphAndTable.add(chartPanel, BorderLayout.CENTER);
        graphAndTable.add(scrollPaneTable, BorderLayout.EAST);
        p.add(graphAndTable);
        JPanel inputs = new JPanel();
        inputs.setLayout(new BoxLayout(inputs, BoxLayout.PAGE_AXIS));
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(50);
        JPanel coordPanel = new JPanel(flowLayout);
        JPanel xPanel = new JPanel();
        JPanel yPanel = new JPanel();
        xPanel.add(xLabel);
        xPanel.add(x);
        yPanel.add(yLabel);
        yPanel.add(y);
        coordPanel.add(xPanel);
        coordPanel.add(yPanel);
        JPanel funcPanel = new JPanel();
        funcPanel.add(functionLabel);
        funcPanel.add(functions);
        inputs.add(funcPanel);
        inputs.add(coordPanel);
        p.add(inputs);

        JPanel labelOldKoef = new JPanel();
        labelOldKoef.add(new JLabel("Значения коэффициентов для аппроксимации со всеми точками"));
        p.add(labelOldKoef);
        JPanel oldKoefs = new JPanel(flowLayout);
        JPanel oldAPanel = new JPanel();
        oldAPanel.add(new JLabel("a = "));
        oldAPanel.add(oldA);
        oldKoefs.add(oldAPanel);
        JPanel oldBPanel = new JPanel();
        oldBPanel.add(new JLabel("b = "));
        oldBPanel.add(oldB);
        oldKoefs.add(oldBPanel);
        p.add(oldKoefs);

        JPanel labelNewKoef = new JPanel();
        labelNewKoef.add(new JLabel("Значения коэффициентов для аппроксимации со всеми точками кроме самой отклоняющейся"));
        p.add(labelNewKoef);
        JPanel newKoefs = new JPanel(flowLayout);
        JPanel newAPanel = new JPanel();
        newAPanel.add(new JLabel("a = "));
        newAPanel.add(newA);
        newKoefs.add(newAPanel);
        JPanel newBPanel = new JPanel();
        newBPanel.add(new JLabel("b = "));
        newBPanel.add(newB);
        newKoefs.add(newBPanel);
        p.add(newKoefs);

        JPanel coordBadPoint = new JPanel();
        coordBadPoint.add(new JLabel("Значения координат самой отклоняющейся точки"));
        p.add(coordBadPoint);
        JPanel badPoint = new JPanel(flowLayout);
        JPanel newXPanel = new JPanel();
        newXPanel.add(new JLabel("x = "));
        newXPanel.add(badPointX);
        badPoint.add(newXPanel);
        JPanel newYPanel = new JPanel();
        newYPanel.add(new JLabel("y = "));
        newYPanel.add(badPointY);
        badPoint.add(newYPanel);
        p.add(badPoint);

        GridLayout gridLayout = new GridLayout(0, 2);
        gridLayout.setHgap(5);
        gridLayout.setVgap(5);
        JPanel buttons = new JPanel(gridLayout);
        buttons.add(addPoint);
        buttons.add(deletePoint);
        buttons.add(deleteAllPoints);
        buttons.add(approximate);
        p.add(buttons);
        setContentPane(p);
    }

    private void initButtons() {
        JFrame frame = this;
        addPoint.addActionListener(actionEvent -> {
            double xarg, yarg;
            try {
                xarg = Double.parseDouble(x.getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Неверная координата Х!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                yarg = Double.parseDouble(y.getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Неверная координата Y!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            pointList.add(new Point(xarg, yarg));
            pointTable.addRow(new Object[]{xarg, yarg});
            x.setText("");
            y.setText("");
        });
        deletePoint.addActionListener(actionEvent -> {
            Vector vector = pointTable.getTableModel().getDataVector();
            int[] selectedRows = pointTable.getSelectedRows();
            if (selectedRows.length == 0)
                JOptionPane.showMessageDialog(frame, "Выделите нужные точки в таблице!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
            Arrays.sort(selectedRows);
            for (int i = selectedRows.length - 1; i >= 0; --i)
                try {
                    pointList.remove(new Point(((Double) ((Vector) (vector.elementAt(selectedRows[i]))).elementAt(0)),
                            ((Double) ((Vector) (vector.elementAt(selectedRows[i]))).elementAt(1))));
                    pointTable.removeRow(selectedRows[i]);
                } catch (Exception ignore) {
                }
            scrollPaneTable.revalidate();
        });
        deleteAllPoints.addActionListener(actionEvent -> {
            for (int i = pointTable.getRowCount(); i > 0; --i)
                pointTable.removeRow(0);
            pointList.clear();
        });
        //approximate.setFocusPainted(false);
        approximate.setBackground(new Color(201, 255, 227));
        approximate.setFont(new Font("Tahoma", Font.BOLD, 12));
        approximate.addActionListener(actionEvent -> {
            if (pointList.size() < 3) {
                JOptionPane.showMessageDialog(frame, "Для аппроксимации функции надо ввести 3 точки (с учетом того, что наиболее отклоняющаяся будет убрана)!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Pair<Pair<Double, Double>, Point> oldKoefs;
            Pair<Double, Double> newKoefs;
            Approximation approximation = null;
            int n = functions.getSelectedIndex();
            if (n == 1 && pointList.stream().map(Point::getX).anyMatch(x -> x <= 0)) {
                JOptionPane.showMessageDialog(frame, "Некоторые точки не входят в область определения функции (x > 0)\nПожалуйста, измените ввод!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (n == 2 && pointList.stream().map(Point::getY).anyMatch(y -> y <= 0)) {
                JOptionPane.showMessageDialog(frame, "Некоторые точки не входят в область определения функции (y > 0)\nПожалуйста, измените ввод!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            switch (n) {
                case 0:
                    approximation = new LinearApproximation();
                    break;
                case 1:
                    approximation = new LogApproximation();
                    break;
                case 2:
                    approximation = new ExponentialApproximation();
                    break;
            }
            oldKoefs = approximation.approximateReturnWorstPoint(pointList);
            oldA.setText(oldKoefs.getFirst().getFirst().toString());
            oldB.setText(oldKoefs.getFirst().getSecond().toString());
            List<Point> pointListWithoutWorst = new ArrayList<>(pointList);
            pointListWithoutWorst.remove(oldKoefs.getSecond());
            badPointX.setText(Double.toString(oldKoefs.getSecond().getX()));
            badPointY.setText(Double.toString(oldKoefs.getSecond().getY()));
            newKoefs = approximation.approximate(pointListWithoutWorst);
            newA.setText(newKoefs.getFirst().toString());
            newB.setText(newKoefs.getSecond().toString());
            initDataset(n, oldKoefs.getFirst(), newKoefs, oldKoefs.getSecond());
        });
    }

    private void initScrollPointTable() {
        pointTable = new PointTable(new Object[][]{}, new String[]{"X", "Y"});
        pointTable.setFont(getFont());
        scrollPaneTable = new JScrollPane(pointTable);
        scrollPaneTable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneTable.setPreferredSize(new Dimension(200, 200));
    }

    private void addOnExitListener() {
        final MainFrame mainFrame = this;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(mainFrame, "Вы действительно хотите выйти?", "Закрытие программы", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == 0)
                    System.exit(0);
            }
        });
    }

    private void initGraph() {
        final JFreeChart chart = initChart(initDataset(-1, null, null, null));
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
    }

    private XYDataset initDataset(int mode, Pair<Double, Double> oldKoefs, Pair<Double, Double> newKoefs, Point badPointArg) {

        dataset.removeAllSeries();
        points.clear();
        func1.clear();
        func2.clear();
        badPoint.clear();
        if (oldKoefs == null || newKoefs == null)
            return dataset;

        badPoint.add(badPointArg.getX(),badPointArg.getY());

        double minBound = pointList.stream().mapToDouble(Point::getX).min().getAsDouble();
        double maxBound = pointList.stream().mapToDouble(Point::getX).max().getAsDouble();
        final int numberSteps = 500;
        double dx = maxBound / numberSteps - minBound / numberSteps;
        double oldA = oldKoefs.getFirst();
        double oldB = oldKoefs.getSecond();
        double newA = newKoefs.getFirst();
        double newB = newKoefs.getSecond();
        double koefOfBound = 30;

        switch (mode) {
            case -1:
                break;
            case 0:
                for (double x = minBound - numberSteps * dx / koefOfBound; x <= minBound; x += dx)
                    func1.add(x, oldA * x + oldB);
                for (double x = minBound; x <= maxBound; x += dx) {
                    func1.add(x, oldA * x + oldB);
                    func2.add(x, newA * x + newB);
                }
                for (double x = maxBound; x <= maxBound + numberSteps * dx / koefOfBound; x += dx)
                    func2.add(x, newA * x + newB);
                break;
            case 1:
                for (double x = minBound - numberSteps * dx / koefOfBound; x <= minBound; x += dx)
                    func1.add(x, oldA * Math.log(x) + oldB);
                for (double x = minBound; x <= maxBound; x += dx) {
                    func1.add(x, oldA * Math.log(x) + oldB);
                    func2.add(x, newA * Math.log(x) + newB);
                }
                for (double x = maxBound; x <= maxBound + numberSteps * dx / koefOfBound; x += dx)
                    func2.add(x, newA * Math.log(x) + newB);
                break;
            case 2:
                for (double x = minBound - numberSteps * dx / koefOfBound; x <= minBound; x += dx)
                    func1.add(x, oldB * Math.pow(Math.E, oldA * x));
                for (double x = minBound; x <= maxBound; x += dx) {
                    func1.add(x, oldB * Math.pow(Math.E, oldA * x));
                    func2.add(x, newB * Math.pow(Math.E, newA * x));
                }
                for (double x = maxBound; x <= maxBound + numberSteps * dx / koefOfBound; x += dx)
                    func2.add(x, newB * Math.pow(Math.E, newA * x));
                break;
        }

        if (mode >= 0) {
            pointList.forEach(e -> points.add(e.getX(), e.getY()));
            dataset.addSeries(badPoint);
            dataset.addSeries(points);
            dataset.addSeries(func1);
            dataset.addSeries(func2);
        }

        return dataset;

    }

    private JFreeChart initChart(final XYDataset dataset) {

        final JFreeChart chart = ChartFactory.createXYLineChart(
                null,      // chart title
                "X",                      // x axis label
                "Y",                      // y axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                false,                     // tooltips
                false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
        //      legend.setDisplaySeriesShapes(true);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(2, false);
        renderer.setSeriesShapesVisible(3, false);
        renderer.setSeriesLinesVisible(1, false);
        final double ellipseRadius = 5;
        renderer.setSeriesShape(0, new Ellipse2D.Double(-ellipseRadius / 2, -ellipseRadius / 2, ellipseRadius, ellipseRadius));
        renderer.setSeriesShape(1, new Ellipse2D.Double(-ellipseRadius / 2, -ellipseRadius / 2, ellipseRadius, ellipseRadius));
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;

    }
}
