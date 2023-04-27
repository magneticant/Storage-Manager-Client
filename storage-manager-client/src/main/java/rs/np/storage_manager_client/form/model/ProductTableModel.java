//package form.model;
package rs.np.storage_manager_client.form.model;


import java.math.BigDecimal;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import rs.np.storage_manager_client.form.mode.FormMode;
import rs.np.storage_manager_common.domain.Product;
import rs.np.storage_manager_common.domain.ProductType;

/**
 *
 * @author Milan
 */
public class ProductTableModel extends AbstractTableModel{
    private final String[] columnNames = { 
        "ID", "Product Name", "Weight", "Fragile", "Amount", "Type", "Price",
    };
    private final Object[] columnTypes = {
        Integer.class, String.class, Double.class,
        Boolean.class, Integer.class, ProductType.class, 
        BigDecimal.class
    };
    private final FormMode mode;
    private List<Product> products;

    public ProductTableModel(List<Product> prod, FormMode mode) {
        this.mode = mode;
        products = prod;
    }

    @Override
    public int getRowCount() {
        return (products == null? 0 : products.size());
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product p = products.get(rowIndex);
        switch (columnIndex) {
            case 0: return p.getID();
            case 1: return p.getProductName();
            case 2: return p.getWeight();
            case 3: return p.getFragile();
            case 4: return p.getAmount();
            case 5: return p.getType();
            case 6: return p.getPrice();
            default:
                return "n/a";
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if(mode!=FormMode.UPDATE)
            return false;
        
        switch(column){
            case 0: return false;
            case 1: 
            case 2: 
            case 3:
            case 4:
            case 5:
            case 6:return true;
            default: return false;
        }
    }

    @Override
    public String getColumnName(int i) {
        return (i < 0 ? "n/a" : columnNames[i]);
    }

    @Override
    public Class<?> getColumnClass(int i) {
        return (Class<?>) columnTypes[i];
    }
    
    public void addProduct(Product p){
        products.add(p);
        fireTableRowsInserted(products.size()-1, products.size()-1);
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void deleteProduct(int rowIndex) {
        if(rowIndex < 0 || rowIndex > products.size())
            return;
        products.remove(rowIndex);
        fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Product product = products.get(rowIndex);
        switch(columnIndex){
            case 0: break;
            case 1:
                try{
                validateNameInput((String)aValue);
                product.setProductName(aValue.toString().trim()); break;
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    System.out.println(ex.getMessage());
                }
            case 2: product.setWeight((Double)aValue); break;
            case 3: product.setFragile((Boolean)aValue); break;
            case 4: product.setAmount((Integer)aValue); break;
            case 5: product.setType((ProductType)aValue); break;
            case 6: product.setPrice((BigDecimal)aValue); break;
            default: break;
        }
    }

    private void validateNameInput(String string) throws Exception {
        String errorLog = "";
        if(string == null || string.trim().isEmpty()
                || string.length() < 2){
            errorLog += "Product name cannot be empty or 1 character long.";
        }
        try{
            Integer.parseInt(string.trim());
            errorLog += "Product's name cannot consist of only numbers.";
        }catch(NumberFormatException ex){ }
        
        if(string.length() > 30){
            errorLog += "Product's name cannot be longer than 30 characters.";
        }
        
        if(!errorLog.isEmpty()){
            throw new Exception(errorLog);
        }
    }
    
    
}
