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
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    public static void main(final String[] args) {

        final MainFrame demo = new MainFrame("Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

    private final XYSeries points = new XYSeries("Points");
    private final XYSeries func = new XYSeries("Func");
    private final XYSeriesCollection dataset = new XYSeriesCollection();
    private final IntegerTextField x = new IntegerTextField();
    private final IntegerTextField y = new IntegerTextField();
    private PointTable pointTable;
    private JScrollPane scrollPaneTable;
    private ChartPanel chartPanel;

    public MainFrame(final String title) {
        super(title);
        initGraph();
        initScrollPointTable();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addOnExitListener();
        JPanel p = new JPanel(new BorderLayout());
        p.add(chartPanel, BorderLayout.CENTER);
        p.add(x, BorderLayout.AFTER_LAST_LINE);
        p.add(scrollPaneTable, BorderLayout.BEFORE_FIRST_LINE);
        setContentPane(p);
    }

    private void initScrollPointTable() {
        pointTable = new PointTable(new Object[][]{{11,2},{1,2},{1,2},{1,2},{1,2},{1,2},{1,2},{1,2},{1,2},{1,2},{1,2},{1,2},{1,2},{1,2},{1,2},{1,2},{1,2}}, new String[]{"X", "Y"});
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
