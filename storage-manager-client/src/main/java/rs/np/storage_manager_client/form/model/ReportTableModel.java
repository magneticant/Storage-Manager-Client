//package form.model;
package rs.np.storage_manager_client.form.model;


import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import rs.np.storage_manager_client.form.mode.FormMode;
import rs.np.storage_manager_client.form.report.ReportForm;
import rs.np.storage_manager_common.domain.Report;
import rs.np.storage_manager_common.domain.ReportItem;

/**
 *
 * @author Milan
 */
public class ReportTableModel extends AbstractTableModel{
    private final String[] columnNames = {
      "Ordinal number", "Product name", "Capacity available", "Capacity used (%)"   
    };
    private final Object[] columnTypes = {
        Integer.class, String.class, Integer.class, Double.class
    };
    private Report report;
    private ReportForm form;
    
    
    public ReportTableModel(Report report, ReportForm form) {
        this.form = form;
        this.report = report;
    }

    @Override
    public int getRowCount() {
        if(report != null){
            return report.getReportItems().size();
        }
        return 0;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try{ReportItem ri = report.getReportItems().get(rowIndex);
        
        switch(columnIndex){
           case 0: return rowIndex;
            case 1: 
                if(ri == null || ri.getProduct() == null || ri.getProduct().getProductName() == null){
                    return "Unknown product";
                }else{
                    return ri.getProduct().getProductName();
                }
            
            case 2: return ri.getTotalAvailableCapacity();
            case 3: 
                //int totalCap = ri.getTotalAvailableCapacity();
                return Math.round(ri.calculateCapacity() * 100.0) / 100.0;
            
            default:
                return "";
        }
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return null;
          
    }

    @Override
    public boolean isCellEditable(int row, int column) {
       return (column == 2 && form.getMode() == FormMode.INSERT);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex){
            case 0: return Integer.class;
            case 1: return String.class;
            case 2: return Integer.class;
            case 3: return Double.class;
            
            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        if(column < 0 || column > 3)
            return "n/a";
        
        return columnNames[column];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(form.getMode() == FormMode.SELECT)
            return;
        if(columnIndex != 2)
            return;
        ReportItem ri = report.getReportItems().get(rowIndex);
        if((Integer)aValue < ri.getProduct().getAmount()){
            aValue = ri.getProduct().getAmount();
        }
        report.getReportItems().get(rowIndex).setTotalAvailableCapacity((Integer)aValue);
        fireTableDataChanged();
        form.fillTotalCapacity();
        form.setCapChanged(true);
        form.adjustLabel();
        
    }

    public void setReport(Report report) {
        this.report = report;
    }
    
    public Report getReport() {
        return report;
    }

    public void deleteReport() {
        report = new Report();
        fireTableDataChanged();
    }

    public void addReport(Report report) {
        this.report = report;
        fireTableDataChanged();
    }
}
