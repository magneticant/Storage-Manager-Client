//package GUIController;
package rs.np.storage_manager_client.GUIController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import rs.np.storage_manager_client.connection.Client;
import rs.np.storage_manager_client.form.report.EditCapacityForm;
import rs.np.storage_manager_client.form.report.ReportForm;
import rs.np.storage_manager_common.domain.Product;
import rs.np.storage_manager_common.domain.ReportItem;

/**
 *
 * @author Milan
 */
public class EditCapacityController {
    private EditCapacityForm capForm;
    
    public EditCapacityController(EditCapacityForm editCapacityForm) {
        capForm = editCapacityForm;
        addActionListeners();
    }

    public void openForm(ReportForm parent) {
        capForm.setParentDialog(parent); 
        capForm.setTitle("Alter product storage cap");
        capForm.setLocationRelativeTo(parent);
    }
    
    
    private void addActionListeners() {
        capForm.getBtnConfirm().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(capForm, "ACTION PERFORMED");
                confirm();
            }
            
            private void confirm(){
                JOptionPane.showMessageDialog(capForm, "CONFIRM METHOD");
                 String userInput = capForm.getTxtValue().getText().trim();
        try {
            validateInput(userInput);
            changeTotalCap(userInput, capForm.getParentDialog());
            capForm.getParentDialog().setCapChanged(true);
            JOptionPane.showMessageDialog(capForm, "Total capacity edited successfully");
            capForm.dispose();
        } catch (Exception ex) {
            Logger.getLogger(EditCapacityForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(capForm, ex.getMessage());
        }
        }
            
        });
        
    }
    private void validateInput(String userInput) throws Exception {
        String errorLog = "";
        
        if(userInput == null || userInput.isEmpty()){
            errorLog += "Please input a valid numeric value.\n";
            throw new Exception(errorLog);
        }
        try{
            int inputNumber = Integer.parseInt(userInput);
            if(inputNumber<=0){
                errorLog += "The warehouse must house at least 1 unit for each product.\n";
            }
            List<Product> allProducts = Client.getInstance().getAllProducts();
            int maxValue = findMaxValueInStock(allProducts);
            if(inputNumber < maxValue){
                errorLog += "We have found items with higher stocks than inputted."
                        + " The highest found was " + maxValue + ". Please input a number greater than or same as "
                        + maxValue + ".";
            }
        }catch(NumberFormatException ex){
            errorLog += "Your input must contain a valid numeric value. Your input is either too large or not numeric.\n";
        }
        if(!"".equals(errorLog)){
            throw new Exception(errorLog);
        }
    }
    
    private int findMaxValueInStock(List<Product> allProducts) {
        if(allProducts == null || allProducts.isEmpty()){
            return Integer.MAX_VALUE;
        }
        int maxValue = allProducts.get(0).getAmount();
        if(maxValue == 0){
            return Integer.MAX_VALUE;
        }
        int listSize = allProducts.size();
        
        for(int i = 0;i<listSize;i++){
            if(allProducts.get(i).getAmount() > maxValue){
                maxValue = allProducts.get(i).getAmount();
            }
        }
        return maxValue;
    }
    
    private void changeTotalCap(String userInput, ReportForm parentDialog) {
        List<ReportItem> items = parentDialog.getReport().getReportItems();
        List<Product> products = new ArrayList<>();
        for(ReportItem reportItem : items){
            products.add(reportItem.getProduct());
        }
//        for(ReportItem item: items){
//            item.setTotalAvailableCapacity(Integer.valueOf(userInput));
//        }
          parentDialog.fillTable(products, Integer.valueOf(userInput));
          parentDialog.setCapChanged(true);
          parentDialog.fillTotalCapacity();
          parentDialog.adjustLabel();
    }
}
