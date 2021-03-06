package com.shilko.ru.approximate;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseEvent;

class PointTable extends JTable {
    private DefaultTableModel model;

    @Override
    public boolean isCellEditable(int a, int b) {
        return false;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);

        try {
            tip = getValueAt(rowIndex, colIndex).toString();
        } catch (RuntimeException e1) {
            //catch null pointer exception if mouse is over an empty line
        }

        return tip;
    }

    public PointTable(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
        model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class getColumnClass(int column) {
                return double.class;
            }
        };
        this.setDefaultRenderer(int.class,new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        });
        //model.setColumnIdentifiers(columnNames);
        this.setModel(model);
        getColumnModel().getColumn(0).setPreferredWidth(100);
        getColumnModel().getColumn(1).setPreferredWidth(100);
        setMinimumSize(getSize());
        setFillsViewportHeight(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(model);
        rowSorter.setRowFilter(null);
        //setRowSorter(rowSorter);
    }

    public void addRow(Object[] row) {
        model.addRow(row);
        model.fireTableRowsInserted(model.getRowCount() - 1, model.getRowCount() - 1);
    }

    public void removeRow(int number) {
        model.removeRow(number);
        model.fireTableRowsDeleted(number, number);
    }

    public DefaultTableModel getTableModel() {
        return model;
    }
}