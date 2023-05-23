//package form.model;
package rs.np.storage_manager_client.form.model;


import java.math.BigDecimal;
import javax.swing.table.AbstractTableModel;

import rs.np.storage_manager_common.domain.abstraction.*;
/**
 *
 * @author Milan
 */
public class DocumentTableModel extends AbstractTableModel{
   private final AbstractDocument document;
   private final String[] columnNames = {
       "Ordinal No.", "Product name", "Product Qty.",
       "Price (per unit)", "Price (total)" 
   };
   private final Object[] columnTypes = {
     Integer.class, String.class, Integer.class, 
       BigDecimal.class, BigDecimal.class
   };
//   private GoodsReceivedNoteForm form;
   
    public DocumentTableModel(AbstractDocument document) {
        this.document = document;
    }

    @Override
    public int getRowCount() {
        if(document == null)
            return 0;
        
        return document.getItems().size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AbstractDocumentItem item = document.getItems().get(rowIndex);
        switch (columnIndex) {
            case 0:
                return rowIndex + 1;
            case 1:
                if(item.getProduct() == null){
                    return null;
                }
                return item.getProduct().getProductName();
            case 2:
                return item.getAmount();
            case 3:
                if(item.getProduct() == null){
                    return null;
                }
                return item.getProduct().getPrice();
            case 4:
                if(item.getProduct()!=null && item.getAmount()!= null){
                    return item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getAmount()));
                }
                return BigDecimal.ZERO;
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex){
            case 0: return Integer.class;
            case 1: return String.class;
            case 2: return Integer.class;
            case 3: return BigDecimal.class;
            case 4: return BigDecimal.class;
            default: return Object.class;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(columnIndex!= 2)
            return;
        if((Integer)aValue < 0){
            aValue = Integer.valueOf(0);
        }
        AbstractDocumentItem item = document.getItems().get(rowIndex);
        item.setAmount((Integer)aValue);
        fireTableDataChanged();
//        adjustTotalCost(form.getTxtCost());
    }

    public AbstractDocument getDocument() {
        return document;
    }
    
    public void removeItem(int rowIndex){
        if(rowIndex <0 || rowIndex >4)
            return;
        document.getItems().remove(rowIndex);
        fireTableRowsDeleted(document.getItems().size()-1,
                document.getItems().size()-1);
    }
    
    public void addItem(AbstractDocumentItem item){
        item.setDocumentID(document.getID());
        item.setFirm(document.getFirm());
        item.setSecondParticipant(document.getSecondParticipant());
        document.addItem(item);
        fireTableRowsInserted(document.getItems().size()-1,
                document.getItems().size()-1);
    }

    public boolean checkIfExists(AbstractDocumentItem item) {
        if(item == null)
            return true;
        for(AbstractDocumentItem noteItem : document.getItems()){
            if(noteItem.getProduct() == null || item.getProduct() == null)
                continue;
            
            if(noteItem.getProduct().getID() == item.getProduct().getID())
                return true;
        }
        return false;
    }

    
}
