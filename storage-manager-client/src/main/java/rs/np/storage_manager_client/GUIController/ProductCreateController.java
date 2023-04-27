//package GUIController;
package rs.np.storage_manager_client.GUIController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import rs.np.storage_manager_client.GUICoordinator.MainCoordinator;
import rs.np.storage_manager_client.connection.Client;
import rs.np.storage_manager_client.form.product.ProductCreateForm;
import rs.np.storage_manager_common.domain.Product;
import rs.np.storage_manager_common.domain.ProductType;

/**
 *
 * @author Milan
 */
public class ProductCreateController {
    private final ProductCreateForm productCreateForm;
    
    public ProductCreateController(ProductCreateForm productCreateForm) {
        this.productCreateForm = productCreateForm;
        AddActionListeners();
    }

    public void openForm() {
        productCreateForm.setLocationRelativeTo(
                MainCoordinator.getInstance().getMainController().getMainForm()
        );
        productCreateForm.setTitle("Product - CREATE");
        this.prepareForm();
        productCreateForm.setVisible(true);
    }

    public ProductCreateForm getProductCreateForm() {
        return productCreateForm;
    }
    
    private void AddActionListeners(){
        productCreateForm.getBtnInsert().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertProduct();
            }
            
            private void insertProduct(){
                String[] values = fetchAllTextFieldValues();
                Boolean fragile = (Boolean)productCreateForm.getCbFragile().getSelectedItem();
                ProductType type = (ProductType)productCreateForm.getCbType().getSelectedItem();
             try {
                validateAllInputs(values);
                String name = values[0];
                Double weight = Double.parseDouble(values[1]);
                Integer stock = Integer.parseInt(values[2]);
                BigDecimal price = new BigDecimal(values[3]);
        
                Product product = new Product(0, name, weight, fragile, stock, type, price);
                Client.getInstance().insertProduct(product);
                JOptionPane.showMessageDialog(productCreateForm, "Product successfully inserted.");
            } catch (Exception ex) {
                Logger.getLogger(ProductCreateForm.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(productCreateForm, ex.getMessage());
            }
            }
            private String[] fetchAllTextFieldValues() {
                 String inputName = productCreateForm.getTxtProductName().getText().trim();
                 String inputWeight = productCreateForm.getTxtWeight().getText().trim();
                 String inputStock = productCreateForm.getTxtStock().getText().trim();
                 String inputPrice = productCreateForm.getTxtPrice().getText().trim();
        
        String[] stringArr = {
          inputName, inputWeight, inputStock, inputPrice  
        };
        
        return stringArr;
    }
            private void validateAllInputs(String[] array) throws Exception{
        String inputName = array[0];
        String inputWeight = array[1];
        String inputStock = array[2];
        String inputPrice = array[3];
        String errorLog = "";
        if(inputName == null || inputWeight == null || inputStock == null || inputPrice == null){
            throw new Exception("Please input all values correctly.");
        }
        
        if(inputName.isEmpty() || inputWeight.isEmpty() || inputStock.isEmpty() || inputPrice.isEmpty()){
            throw new Exception("Input fields cannot remain empty.");
        }
        try{
            Integer.parseInt(inputName.trim());
            errorLog += "Please input valid product name. \n";
        }catch(NumberFormatException ex){}
        
        try{
            Double.parseDouble(inputWeight.trim());
        } catch(NumberFormatException ex){
            errorLog += "Please input a valid weight value (format: nnnn.nn).\n";
        }
        
        try{
            Integer.parseInt(inputStock.trim());
        } catch(NumberFormatException ex){
            errorLog += "Please input a valid numeric stock value.\n";
        }
        
        try{
            Double.parseDouble(inputPrice.trim());
        } catch(NumberFormatException ex){
            errorLog += "Please input a valid price value (format: nnnn.nn)\n";
        }
        
        if(!errorLog.isEmpty()){
            throw new Exception(errorLog);
        }
    }
        });
        
        productCreateForm.getBtnCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productCreateForm.dispose();
            }
        });
    }
    public void prepareForm() {
        alterComboBoxFragile();
        alterComboBoxType();
        productCreateForm.getTxtID().setEditable(false);
    }

    private void alterComboBoxFragile() {
        productCreateForm.getCbFragile().removeAllItems();
        productCreateForm.getCbFragile().addItem(false);
        productCreateForm.getCbFragile().addItem(true);
    }

    private void alterComboBoxType() {
        productCreateForm.getCbType().removeAllItems();
        for(ProductType type : ProductType.values()){
            productCreateForm.getCbType().addItem(type);
        }
    }
}
