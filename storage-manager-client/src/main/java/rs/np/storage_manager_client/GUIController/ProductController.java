//package GUIController;
package rs.np.storage_manager_client.GUIController;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;

import rs.np.storage_manager_client.connection.Client;
import rs.np.storage_manager_client.form.mode.FormMode;
import rs.np.storage_manager_client.form.model.ProductTableModel;
import rs.np.storage_manager_client.form.product.ProductCreateForm;
import rs.np.storage_manager_client.form.product.ProductForm;
import rs.np.storage_manager_common.domain.Product;
import rs.np.storage_manager_common.domain.ProductType;
import rs.np.storage_manager_common.domain.WhereClauseMode;

/**
 *
 * @author Milan
 */
public class ProductController {
    private final ProductForm productForm;
    
    
    public ProductController(ProductForm productForm) {
        this.productForm = productForm;
        addActionListeners();
    }

    public void openForm(FormMode mode) {
       productForm.setTitle("Products - " + mode);
       productForm.setLocationRelativeTo(null);
       productForm.setMode(mode);
       this.prepareForm(); 
       productForm.setVisible(true);
    }
    
    private void addActionListeners(){
        productForm.getBtnCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productForm.dispose();
            }
        });
        productForm.getBtnFind().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findProducts();
            }
            
            private void findProducts(){
                if(validNameInput(productForm.getTxtSearch().getText().trim())){
            
                String name = productForm.getTxtSearch().getText().trim();
                System.out.println("Name is " + name);
                Product p = new Product();
                
                try {p.setProductName(name);
                p.setMode(WhereClauseMode.BY_NAME);
                List<Product> products = Client.getInstance().getAllProducts(p);
//                processExceptionIfPresent();
                if(products.isEmpty()){
                    JOptionPane.showMessageDialog(productForm, "No items found for this query.");
                }
                JOptionPane.showMessageDialog(productForm, "There are " + products.size() + " items with given name "
                        + "(or containing said characters, or similar).");
                addResultToTable(products);
                
                
            } catch (IOException ex) {
                System.out.println("Exception while processing request. Cannot instantiate Client.");
                Logger.getLogger(ProductCreateForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(productForm, "Product with given ID not found.");
                Logger.getLogger(ProductCreateForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(productForm, "Please input a valid name.");
        }
            }
        });
        productForm.getBtnDelete().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
            private void deleteProduct(){
              int selectedRow = productForm.getTblProduct().getSelectedRow();
                if(selectedRow < 0){
                    JOptionPane.showMessageDialog(productForm, "Please select a product from the table for deletion.");
                    return;
                }
            try {   
                int result = JOptionPane.showConfirmDialog(productForm, "Are you sure you want to delete the given product? This cannot be undone.", "Product deletion", 2);
         
                if(result != 0){
                 System.out.println("ENTERED non 0 WHILE PRESSING");
                 return;
            }
         
            Product product = productForm.getTableModel().getProducts().get(selectedRow);
        
            product.setMode(WhereClauseMode.BY_ID);
            Client.getInstance().deleteProduct(product);
        // setAllValuesToNull();
            productForm.getTableModel().deleteProduct(selectedRow);
            JOptionPane.showMessageDialog(productForm, "Product deleted successfully!");
        } catch (Exception ex) {
            Logger.getLogger(ProductCreateForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(productForm, ex.getMessage());
        }  
            }
        });
        
        productForm.getBtnUpdate().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProduct();
            }
            private void updateProduct(){
                Product product = productForm.getTableModel().getProducts().get(0);
        
            if(product == null){
                JOptionPane.showMessageDialog(productForm, "Product cannot be set to null.");
                return;
            }
            product.setMode(WhereClauseMode.BY_ID);
        try {
            Client.getInstance().updateProduct(product);
            product.setMode(WhereClauseMode.BY_ID);
            Product updatedProduct = Client.getInstance().getAllProducts(product).get(0);
            System.out.println("Product updated!");
                if(updatedProduct != null){
                    prepareCbProducts();
            
            JOptionPane.showMessageDialog(productForm, "Product updated successfully!");
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductCreateForm.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Couldn't instantiate Client object.");
        } catch (Exception ex) {
            Logger.getLogger(ProductCreateForm.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        }
            }
        });
        
        productForm.getCbProducts().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayProduct();
            }
            private void displayProduct(){
                if(productForm.getCbProducts().getSelectedItem() == null){
                    return;
            } else if(!productForm.getTableModel().getProducts().isEmpty()){
                   productForm.getTableModel().deleteProduct(0);
            }
                productForm.getTableModel().getProducts().add((Product)productForm.getCbProducts().getSelectedItem());
            }
        });
    }
        private void addResultToTable(List<Product> products) {
            productForm.getTableModel().setProducts(products);
            productForm.getTableModel().fireTableDataChanged();
        }
        
        private boolean validNameInput(String input) {
        if(input == null || input.isEmpty()) 
            return false;
        try{
            input = input.trim();
            Integer.parseInt(input);
            return false;
        }catch(NumberFormatException ex){
            return true;
        }
        }
        private void prepareCbProducts() {
        try {
            List<Product> products = Client.getInstance().getAllProducts();
            addResultToCBProducts(products);
        } catch (IOException ex) {
            Logger.getLogger(ProductCreateForm.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Unable to instantiate Client.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(productForm, "Impossible exception case.");
            Logger.getLogger(ProductCreateForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
      private void addResultToCBProducts(List<Product> products) {
        productForm.getCbProducts().removeAllItems();
        for(Product p: products){
            productForm.getCbProducts().addItem(p);
        }
    }
      
      public void prepareForm() {
        prepareTable(productForm.getMode());
        prepareAccordingToMode();
    }
      private void prepareTable(FormMode mode) {
        if(mode == FormMode.UPDATE){
            productForm.setTableModel(new ProductTableModel(new ArrayList<Product>(), mode));
            productForm.getTblProduct().setModel(productForm.getTableModel());
            return;
        }
        
        try {
            List<Product> products = Client.getInstance().getAllProducts();
            System.out.println("PRODUCT CONTROLLER! AllProducts: " + products);
            System.out.println(products.get(0));
            System.out.println(products.getClass());
            productForm.setTableModel(new ProductTableModel(products, mode));
            productForm.getTblProduct().setModel(productForm.getTableModel());
            //addCBType(tableModel);
        } catch (Exception ex) {
            Logger.getLogger(ProductForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void prepareAccordingToMode() {
        productForm.getLblWarning().setVisible(false);
        productForm.getLblProducts().setVisible(false);
        productForm.getCbProducts().setVisible(false);
        switch(productForm.getMode()){
            case INSERT: 
                break;
            case UPDATE:
                productForm.getLblWarning().setVisible(true);
                productForm.getBtnDelete().setVisible(false);
                productForm.getBtnFind().setVisible(false);
                productForm.getLblEnterProductName().setVisible(false);
                productForm.getTxtSearch().setVisible(false);
                addCBType(productForm.getTableModel());
                
                productForm.getLblProducts().setVisible(true);
                productForm.getCbProducts().setVisible(true);
                prepareCbProducts();
                break;
            case SELECT:
                productForm.getBtnDelete().setVisible(false);
                productForm.getBtnUpdate().setVisible(false);
                break;
            case DELETE:
                productForm.getBtnUpdate().setVisible(false);
                productForm.getBtnFind().setVisible(true);
                productForm.getLblEnterProductName().setVisible(true);
                productForm.getTxtSearch().setVisible(true);
                break;
            case SELECT_ALL:
                productForm.getBtnUpdate().setVisible(false);
                productForm.getBtnDelete().setVisible(false);
                productForm.getBtnFind().setVisible(false);
                productForm.getLblEnterProductName().setVisible(false);
                productForm.getTxtSearch().setVisible(false);
                break;
            default:
                JOptionPane.showMessageDialog(productForm, "Unknown mode.");
                break;
        }
    }
      private void addCBType(ProductTableModel tableModel) {
        TableColumn typeColumn = productForm.getTblProduct().getColumnModel().getColumn(5);
        JComboBox<ProductType> cbType = new JComboBox<>();
        prepareCbType(cbType);
        typeColumn.setCellEditor(new DefaultCellEditor(cbType));
    }
      private void prepareCbType(JComboBox<ProductType> cbType) {
        cbType.removeAllItems();
        for(ProductType type : ProductType.values()){
            cbType.addItem(type);
        }
    }
}
