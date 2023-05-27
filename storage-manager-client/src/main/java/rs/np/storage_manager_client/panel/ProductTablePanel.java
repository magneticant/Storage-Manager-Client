//package panel;
package rs.np.storage_manager_client.panel;


import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import rs.np.storage_manager_client.form.model.DocumentTableModel;
import rs.np.storage_manager_common.domain.Product;
import rs.np.storage_manager_common.domain.abstraction.AbstractDocumentItem;
import rs.np.storage_manager_common.domain.abstraction.implementation.GoodsReceivedNoteItem;

/**
 *
 * @author Milan
 */
public class ProductTablePanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -3360694614239464072L;
	/**
     * Creates new form ProductTablePanel
     */
    public ProductTablePanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnAddItem = new javax.swing.JButton();
        btnDeleteItem = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        cbProducts = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblItem = new javax.swing.JTable();

        btnAddItem.setText("Add Item");
        btnAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemActionPerformed(evt);
            }
        });

        btnDeleteItem.setText("Delete Item");
        btnDeleteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteItemActionPerformed(evt);
            }
        });

        jLabel15.setText("Products:");

        cbProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbProductsActionPerformed(evt);
            }
        });

        tblItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblItem);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAddItem, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDeleteItem, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddItem)
                    .addComponent(btnDeleteItem)
                    .addComponent(jLabel15)
                    .addComponent(cbProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemActionPerformed
        // TODO add your handling code here:
        DocumentTableModel model = (DocumentTableModel)tblItem.getModel();
        
        
        AbstractDocumentItem item = new GoodsReceivedNoteItem();
        item.setProduct((Product)getCbProducts().getSelectedItem());
//        if(item == null){
//            JOptionPane.showMessageDialog(this, "Item is null.");
//            return;
//        }
        if(model.checkIfExists(item)){
            JOptionPane.showMessageDialog(this, "Item already added.");
            return;
        }
        model.addItem(item);
    }//GEN-LAST:event_btnAddItemActionPerformed

    private void btnDeleteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteItemActionPerformed
        // TODO add your handling code here:
        DocumentTableModel model = (DocumentTableModel)tblItem.getModel();
        model.removeItem(tblItem.getSelectedRow());
//        model.adjustTotalCost(txtCost);
    }//GEN-LAST:event_btnDeleteItemActionPerformed

    private void cbProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbProductsActionPerformed
        // TODO add your handling code here:
//        showSelectedCBProduct((Product)getCbProducts().getSelectedItem());
    }//GEN-LAST:event_cbProductsActionPerformed

     public JComboBox<Object> getCbProducts() {
        return cbProducts;
    }

    public JButton getBtnAddItem() {
        return btnAddItem;
    }

    public void setBtnAddItem(JButton btnAddItem) {
        this.btnAddItem = btnAddItem;
    }

    public JButton getBtnDeleteItem() {
        return btnDeleteItem;
    }

    public void setBtnDeleteItem(JButton btnDeleteItem) {
        this.btnDeleteItem = btnDeleteItem;
    }

    public JTable getTblItem() {
        return tblItem;
    }

    public void setTblItem(JTable tblItem) {
        this.tblItem = tblItem;
    }

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddItem;
    private javax.swing.JButton btnDeleteItem;
    private javax.swing.JComboBox<Object> cbProducts;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblItem;
    // End of variables declaration//GEN-END:variables
}
