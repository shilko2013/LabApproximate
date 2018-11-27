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
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {

    public static void main(final String[] args) {

        final MainFrame demo = new MainFrame("Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

    private final List<Point> pointList = new ArrayList<>();
    private final XYSeries points = new XYSeries("Points");
    private final XYSeries func = new XYSeries("Func");
    private final XYSeriesCollection dataset = new XYSeriesCollection();
    private final IntegerTextField x = new IntegerTextField();
    private final IntegerTextField y = new IntegerTextField();
    private PointTable pointTable;
    private JScrollPane scrollPaneTable;
    private ChartPanel chartPanel;
    private final JButton addPoint = new JButton("Добавить точку");
    private final JButton deletePoint = new JButton("Удалить точки");
    private final JButton approximate = new JButton("Аппроксимировать функцию");

    public MainFrame(final String title) {
        super(title);
        initGraph();
        initScrollPointTable();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addOnExitListener();
        initButtons();
        JPanel p = new JPanel();
        x.setPreferredSize(new Dimension(200, 20));
        y.setPreferredSize(new Dimension(200, 20));
        p.add(chartPanel);
        p.add(x);
        p.add(y);
        p.add(scrollPaneTable);
        p.add(addPoint);
        p.add(deletePoint);
        p.add(approximate);
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
                pointList.remove(new Point(((Double)((Vector) (vector.elementAt(selectedRows[i]))).elementAt(0)),
                        ((Double)((Vector) (vector.elementAt(selectedRows[i]))).elementAt(1))));
                pointTable.removeRow(selectedRows[i]);
            } catch (Exception ignore){}
            scrollPaneTable.revalidate();
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
        final JFreeChart chart = initChart(initDataset());
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
    }

    private XYDataset initDataset() {

        points.clear();
        points.add(-1.0, 1.0);
        points.add(2.0, 4.0);
        points.add(3.0, 3.0);
        points.add(4.0, 5.0);
        points.add(5.0, 5.0);
        points.add(6.0, -7.0);
        points.add(7.0, 7.0);
        points.add(8.0, 8.0);

        func.clear();
        func.add(-1.0, 5.0);
        func.add(2.0, 7.0);
        func.add(3.0, 6.0);
        func.add(4.0, -8.0);
        func.add(5.0, 4.0);
        func.add(6.0, 4.0);
        func.add(7.0, 2.0);
        func.add(8.0, 1.0);

        dataset.removeAllSeries();
        dataset.addSeries(points);
        dataset.addSeries(func);

        return dataset;

    }

    private JFreeChart initChart(final XYDataset dataset) {

        final JFreeChart chart = ChartFactory.createXYLineChart(
                null,      // chart title
                "X",                      // x axis label
                "Y",                      // y axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                false,                     // include legend
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
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;

    }
}
