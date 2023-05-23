//package GUIController;
package rs.np.storage_manager_client.GUIController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import javax.swing.JOptionPane;

import rs.np.storage_manager_client.form.model.DocumentTableModel;
import rs.np.storage_manager_client.panel.ProductPanel;
import rs.np.storage_manager_client.panel.ProductTablePanel;
import rs.np.storage_manager_common.domain.Product;
import rs.np.storage_manager_common.domain.ProductType;
import rs.np.storage_manager_common.domain.abstraction.AbstractDocument;
import rs.np.storage_manager_common.domain.abstraction.AbstractDocumentItem;
import rs.np.storage_manager_common.domain.abstraction.implementation.BillOfLadingItem;
import rs.np.storage_manager_common.domain.abstraction.implementation.GoodsReceivedNote;
import rs.np.storage_manager_common.domain.abstraction.implementation.GoodsReceivedNoteItem;


/**
 *
 * @author Milan
 */
public class ProductTablePanelController {
    private final ProductTablePanel productTablePanel;
    private final AbstractDocument document;
    private final ProductPanel productPanel;
    
    public ProductTablePanelController(ProductTablePanel productTablePanel1, ProductPanel productPanel, AbstractDocument document) {
        this.document = document;
        this.productTablePanel = productTablePanel1;
        this.productPanel = productPanel;
        addActionListeners();
    }
    
    private void addActionListeners(){
        productTablePanel.getBtnAddItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDocumentItem();
            }

            private void addDocumentItem() {
                DocumentTableModel model = (DocumentTableModel)productTablePanel.getTblItem().getModel();
        
        
                AbstractDocumentItem item;
                if(document instanceof GoodsReceivedNote){
                    item = new GoodsReceivedNoteItem();
                } else 
                    item = new BillOfLadingItem();
                
                item.setProduct((Product)productTablePanel.getCbProducts().getSelectedItem());
//                if(item == null){
//                    JOptionPane.showMessageDialog(productTablePanel, "Item is null.");
//                    return;
//                }
                if(model.checkIfExists(item)){
                 JOptionPane.showMessageDialog(productTablePanel, "Item already added.");
                 return;
                }
                model.addItem(item);
            }
        });
        
        productTablePanel.getBtnDeleteItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeDocumentItem();
            }

            private void removeDocumentItem() {
                DocumentTableModel model = (DocumentTableModel)productTablePanel.getTblItem().getModel();
                model.removeItem(productTablePanel.getTblItem().getSelectedRow());
            }
        });
        
        productTablePanel.getCbProducts().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSelectedCBProduct((Product)productTablePanel.getCbProducts().getSelectedItem());
            }

            private void showSelectedCBProduct(Product selectedItem) {
                Integer productID = selectedItem.getID();
                String productName = selectedItem.getProductName();
                Double productWeight = selectedItem.getWeight();
                Boolean productFragile = selectedItem.getFragile();
                Integer productAmount = selectedItem.getAmount();
                ProductType productType = selectedItem.getType();
                        BigDecimal productPrice = selectedItem.getPrice();
        
                productPanel.getTxtProductID().setText(String.valueOf(productID));
                productPanel.getTxtProductName().setText(productName);
                productPanel.getTxtWeight().setText(String.valueOf(productWeight));
                productPanel.getCbFragile().setSelectedItem(productFragile);
                productPanel.getTxtStock().setText(String.valueOf(productAmount));
                productPanel.getCbType().setSelectedItem(productType);
                productPanel.getTxtPrice().setText(String.valueOf(productPrice));
            }   
            
        });
    }
}
