//package GUIController;
package rs.np.storage_manager_client.GUIController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import rs.np.storage_manager_client.GUICoordinator.MainCoordinator;
import rs.np.storage_manager_client.connection.Client;
import rs.np.storage_manager_client.form.mode.FormMode;
import rs.np.storage_manager_client.form.model.ReportTableModel;
import rs.np.storage_manager_client.form.report.ReportForm;
import rs.np.storage_manager_common.domain.Product;
import rs.np.storage_manager_common.domain.Report;
import rs.np.storage_manager_common.domain.ReportItem;
import rs.np.storage_manager_common.domain.WhereClauseMode;

/**
 *
 * @author Milan
 */
public class ReportController {
    private final ReportForm reportForm;

    public ReportController(ReportForm reportForm) {
        this.reportForm = reportForm;
        addActionListeners();
    }

    public void openForm(FormMode mode) {
        reportForm.setTitle("Report form - " + reportForm.getMode());
        reportForm.setLocationRelativeTo(null);
        this.prepareForm(mode);
        reportForm.setCapChanged(false);
        reportForm.setVisible(true);
    }
    
    private void addActionListeners() {
        reportForm.getBtnSubmit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submit();
            }
            private void submit() {
                try {
        // TODO add your handling code here:
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date reportDate = format.parse(reportForm.getTxtDate().getText());
                ReportTableModel model = (ReportTableModel)reportForm.getTblReport().getModel();
                Report report = model.getReport();
                report.setReportDate(reportDate);
                report.assignItemIDs();
                report.setTotalCapacity(Double.parseDouble(reportForm.getTxtCapacity().getText()));
                Client.getInstance().insertReport(report);
        
                JOptionPane.showMessageDialog(reportForm, "Report inserted successfully.");
        
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(reportForm, "Date parsing error.");
                Logger.getLogger(ReportForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(reportForm, "Unable to instantiate Client.");
                Logger.getLogger(ReportForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                if(ex.getMessage().contains("Duplicate")){
                    JOptionPane.showMessageDialog(reportForm, "Reports can only be submitted once a day.");
                }else
                JOptionPane.showMessageDialog(reportForm, ex.getMessage());
                Logger.getLogger(ReportForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        });
        
       
        reportForm.getBtnEdit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEditForm();
            }
            private void openEditForm() {
                MainCoordinator.getInstance().openEditCapacityForm(reportForm);
            }
        });
        
        reportForm.getCbReports().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displaySelectedReport();
            }

            private void displaySelectedReport() {
                ReportTableModel model  = (ReportTableModel)reportForm.getTblReport().getModel();
                if(reportForm.getCbReports().getSelectedItem() == null){
                    return;
                } else if(model.getReport() != null) {
                    model.deleteReport();
                }
                Report report = (Report)reportForm.getCbReports().getSelectedItem();
                model.addReport(report);
                displayOther(report);
            }

            private void displayOther(Report report) {
                setDate(report.getReportDate());
//                fillTotalCapacity();
                reportForm.getTxtCapacity().
                        setText(String.valueOf(report.getTotalCapacity()));
            }
        });
         /*
        private void displayProduct(){
                if(productForm.getCbProducts().getSelectedItem() == null){
                    return;
            } else if(!productForm.getTableModel().getProducts().isEmpty()){
                   productForm.getTableModel().deleteProduct(0);
            }
                productForm.getTableModel().getProducts().add((Product)productForm.getCbProducts().getSelectedItem());
            }
        
        */
         reportForm.getBtnFind().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findAndDisplayReport();
            }

            private void findAndDisplayReport() {
                try {
                    Report report = new Report();
                    report.setMode(WhereClauseMode.BY_ID);
                    Date reportDate = fetchDateForSearch();
                    report.setReportDate(reportDate);
                    
                    List<Report> reports = Client.getInstance().getAllReports(report);
                    populateCbReports(reports);
                    if(reports != null && !reports.isEmpty()) 
                        JOptionPane.showMessageDialog(reportForm, "Reports found for your query!");
                    else
                        JOptionPane.showMessageDialog(reportForm, "No report was issued on this date.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(reportForm, "Unable to instantiate client.");
                    Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(reportForm, ex.getMessage());
                    Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            private Date fetchDateForSearch() throws Exception {
                if(reportForm.getDateChooser().getDate() == null){
                    throw new Exception("Please enter a valid date.");
                }
                return reportForm.getDateChooser().getDate();
            }
         });
    }
    
    private void prepareForm(FormMode mode) {
        
        switch(mode){
            case INSERT:
                reportForm.getBtnFind().setVisible(false);
                reportForm.getCbReports().setVisible(false);
                reportForm.getLblSearch().setVisible(false);
                reportForm.getDateChooser().setVisible(false);
                
                reportForm.getTxtDate().setEditable(false);
                reportForm.getTxtCapacity().setEditable(false);
                setDate(new Date());
                List<Product> products;
            try {
                products = Client.getInstance().getAllProducts();
                System.out.println("*************************************");
                System.out.println(products);
                System.out.println("*************************************");
                fillTable(products, 0);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(reportForm, "Cannot instantiate Client.");
                Logger.getLogger(ReportForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(reportForm, ex.getMessage());
                Logger.getLogger(ReportForm.class.getName()).log(Level.SEVERE, null, ex);
            }

            fillTotalCapacity();
            adjustLabel();
                break;
            case SELECT:
                reportForm.getTxtCapacity().setEditable(false);
                reportForm.getDateChooser().getDateEditor().setEnabled(false);
                reportForm.getLblTitle().setText("STORAGE CAPACITY REPORT - SEARCH");
                reportForm.getLblInfo().setVisible(false);
                reportForm.getTxtDate().setEditable(false);
                
                reportForm.getBtnEdit().setVisible(false);
                reportForm.getBtnSubmit().setVisible(false);
                setModel(new Report());
                prepareCBReports();
                
                break;
            case UPDATE:
            case DELETE:
            case SELECT_ALL:
            default: return;
        }
        
        
    }
    
    private void setDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        Date sysdate = new Date();
        reportForm.getTxtDate().setText(format.format(date));
    }
    
    private void fillTable(List<Product> products, int capacity) {
    try {
        //        setModel();
        reportForm.setReport(new Report());
        int size = products.size();
        for(int i = 0;i<size;i++){
            ReportItem item = new ReportItem();
            item.setProduct(products.get(i));
            item.setProductCapacity((Double.valueOf(products.get(i).getAmount())/item.getTotalAvailableCapacity())*100);
            item.setCapacity();
            if(capacity != 0){
                item.setTotalAvailableCapacity(capacity);
            }
            item.setReportID(reportForm.getReport().getReportDate());
            reportForm.getReport().getReportItems().add(item);
        }
        setModel(reportForm.getReport());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        reportForm.getReport().setReportDate(format.parse(reportForm.getTxtDate().getText()));
//        report.setTotalAvailableCapacity(totalCapAvailable);
        System.out.println(reportForm.getReport());
    } catch (ParseException ex) {
        Logger.getLogger(ReportForm.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(reportForm, "Date parsing exception " + ex.getMessage());
    }
    }
    
    public void setModel(Report report) {
        AbstractTableModel model = new ReportTableModel(report,reportForm);
        reportForm.getTblReport().setModel(model);
    }
    
    private void fillTotalCapacity() {
        try {
            
            double capacityUsed;
            List<Double> individualCapacities = calculateAllCapacities(reportForm.getReport());
            capacityUsed = summarizeIndividualCapacities(individualCapacities);
            reportForm.getTxtCapacity().setText(String.valueOf(Math.round((100.00 - capacityUsed)*100.0)/100.0));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(reportForm, "Error: " + ex.getMessage());
            Logger.getLogger(ReportForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private double summarizeIndividualCapacities(List<Double> individualCapacities) {
        double sum = 0;
        for(int i = 0;i<individualCapacities.size();i++){
            sum+=individualCapacities.get(i);
        }
        return (double)sum/individualCapacities.size();
    }
    
    private List<Double> calculateAllCapacities(Report report) {
        List<Double> responseList = new ArrayList<>();
        for(ReportItem item: report.getReportItems()){
            double value = item.calculateCapacity();
            responseList.add(value);
        }
        return responseList;
    }
    
    private void adjustLabel() {
        if(!reportForm.isCapChanged())
            reportForm.getLblInfo().setVisible(true);
        else
            reportForm.getLblInfo().setVisible(false);
    }

    private void prepareCBReports() {
        try {
            List<Report> reports = Client.getInstance().getAllReports();
            
            populateCbReports(reports);
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(reportForm, ex.getMessage());
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(reportForm, ex.getMessage());
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void populateCbReports(List<Report> reports) {
        reportForm.getCbReports().removeAllItems();
        for(Report report: reports){
                reportForm.getCbReports().addItem(report);
            }
    }
}
