//package GUIController;
package rs.np.storage_manager_client.GUIController;

import com.toedter.calendar.JTextFieldDateEditor;

import rs.np.storage_manager_client.connection.Client;
import rs.np.storage_manager_client.form.mode.FormMode;
import rs.np.storage_manager_client.form.model.DocumentTableModel;
import rs.np.storage_manager_client.form.note.GoodsReceivedNoteForm;
import rs.np.storage_manager_common.domain.Firm;
import rs.np.storage_manager_common.domain.Partner;
import rs.np.storage_manager_common.domain.Product;
import rs.np.storage_manager_common.domain.ProductType;
import rs.np.storage_manager_common.domain.abstraction.AbstractDocumentItem;
import rs.np.storage_manager_common.domain.abstraction.implementation.GoodsReceivedNote;
import rs.np.storage_manager_common.domain.abstraction.implementation.GoodsReceivedNoteItem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
/**
 *
 * @author Milan
 */
public class GoodsReceivedNoteController {
    private final GoodsReceivedNoteForm noteForm;
    
    public GoodsReceivedNoteController(GoodsReceivedNoteForm goodsReceivedNoteForm) {
        noteForm = goodsReceivedNoteForm;
        addActionListeners();
    }

    public void openForm() {
        noteForm.setTitle("Goods received note - INSERT");
        noteForm.setMode(FormMode.INSERT);
        this.prepareForm();
        noteForm.setLocationRelativeTo(null);
        noteForm.setVisible(true);
    }
    
    private void addActionListeners() {
        noteForm.getBtnCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                noteForm.dispose();
            }
        });
        noteForm.getBtnAddNoteItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNoteItem();
            }
            
            private void addNoteItem(){
                DocumentTableModel model = (DocumentTableModel)noteForm.getTblItem().getModel();
                GoodsReceivedNoteItem item = new GoodsReceivedNoteItem();
                item.setProduct((Product)noteForm.getCbProducts().getSelectedItem());
                    if(item == null){
                        JOptionPane.showMessageDialog(noteForm, "Item is null.");
                    return;
                    }
                    if(model.checkIfExists(item)){
                        JOptionPane.showMessageDialog(noteForm, "Item already added.");
                    return;
                    }
                    model.addItem(item);
                    adjustTotalCost(noteForm.getTxtCost());
            }
            
            
        });
        noteForm.getBtnDeleteNoteItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteItem();
            }

            private void deleteItem() {
                DocumentTableModel model = (DocumentTableModel)noteForm.getTblItem().getModel();
                model.removeItem(noteForm.getTblItem().getSelectedRow());
                adjustTotalCost(noteForm.getTxtCost());
            }
        });
        
        noteForm.getCbProducts().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayProduct();
            }

            private void displayProduct() {
                showSelectedCBProduct((Product)noteForm.getCbProducts().getSelectedItem());
            }
        });
        noteForm.getBtnInsert().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertNote();
            }

            private void insertNote() {
                try {
        // TODO add your handling code here:
                    adjustTotalCost(noteForm.getTxtCost());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    System.out.println("***************************************");
                    String issueDateInput = ((JTextField)noteForm.getIssueDateChooser().getDateEditor().
                            getUiComponent()).getText().trim();
                    System.out.println("*"+issueDateInput+"*");
                    System.out.println("***************************************");

                    Date issueDate = format.parse(issueDateInput);
                    String dueDateInput = ((JTextField)noteForm.getDueDateChooser().getDateEditor().
                            getUiComponent()).getText().trim();
                    System.out.println("***************************************");
                    Date dueDate = format.parse(dueDateInput);
                    validateDates(issueDate, dueDate);
                    validateCB();
                    validateItems();
                    Firm firm = (Firm)(noteForm.getCbFirm().getSelectedItem());
                    Partner partner = (Partner)noteForm.getCbPartner().getSelectedItem();
                    DocumentTableModel model = (DocumentTableModel)noteForm.getTblItem().getModel();
                    GoodsReceivedNote note = (GoodsReceivedNote) model.getDocument();
                    note.setDeadLine(dueDate);
                    note.setIssueDate(issueDate);
                    note.setSecondParticipant(partner);
                    note.setFirm(firm);
                    note.setTotalCost(new BigDecimal(noteForm.getTxtCost().getText()));
                    fixNoteItems(note);
                    Client.getInstance().insertGoodsReceivedNote(note);
                    JOptionPane.showMessageDialog(noteForm, "Goods received note inserted successfully!");
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(noteForm, "Invalid date input.");
                    Logger.getLogger(GoodsReceivedNoteForm.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(noteForm, ex.getMessage());
                    Logger.getLogger(GoodsReceivedNoteForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
    }
    private void prepareForm() {
        alterDateFields();
        alterComboBoxFragile();
        alterComboBoxType();
        prepareForInsert();
        assignModel();
    }
    
    private void alterDateFields() {
        JTextFieldDateEditor issueDateEditor = 
                (JTextFieldDateEditor)noteForm.getIssueDateChooser().getDateEditor();
        issueDateEditor.setEditable(false);
                JTextFieldDateEditor dueDateEditor = 
                (JTextFieldDateEditor)noteForm.getDueDateChooser().getDateEditor();
        dueDateEditor.setEditable(false);
        noteForm.getIssueDateChooser().setDateFormatString("yyyy-MM-dd");
        noteForm.getDueDateChooser().setDateFormatString("yyyy-MM-dd");
    }
    
    private void validateCB() throws Exception {
        String errorLog = "";
        if(noteForm.getCbFirm().getSelectedItem() == null){
            errorLog += "You must select a firm.";
        }
        if(noteForm.getCbPartner().getSelectedItem() == null){
            errorLog += "You must select a partner.";
        }
        if(!errorLog.isEmpty()){
            throw new Exception(errorLog);
        }
    }
    
    private void alterComboBoxFragile() {
        noteForm.getCbFragile().removeAllItems();
        noteForm.getCbFragile().addItem(false);
        noteForm.getCbFragile().addItem(true);
    }

    private void alterComboBoxType() {
        noteForm.getCbType().removeAllItems();
        for(ProductType type : ProductType.values()){
            noteForm.getCbType().addItem(type);
        }
    }

    private void prepareForInsert() {
        noteForm.getTxtProductID().setEditable(false);
        noteForm.getTxtProductName().setEditable(false);
        noteForm.getTxtStock().setEditable(false);
        noteForm.getTxtWeight().setEditable(false);
        noteForm.getTxtPrice().setEditable(false);
        noteForm.getCbType().setEnabled(false);
        noteForm.getCbFragile().setEnabled(false);
        noteForm.getTxtCost().setEditable(false);
        noteForm.getTxtNoteID().setEditable(false);
                
        prepareCbProducts();
        prepareCbPartners();
        prepareCbFirms();
    }
    
    private void assignModel() {
        DocumentTableModel model = new DocumentTableModel(new GoodsReceivedNote());
        noteForm.getTblItem().setModel(model);
    }
    
    private void prepareCbProducts() {
    try {
        List<Product> products = Client.getInstance().getAllProducts();
        List<Object> productObjects = new ArrayList<Object>(products);
        addResultToCB(productObjects,noteForm.getCbProducts());
        if(noteForm.getCbProducts().getSelectedItem()!=null){
            showSelectedCBProduct((Product)noteForm.getCbProducts().getSelectedItem());
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(noteForm, "Unable to instantiate Client.");
        Logger.getLogger(GoodsReceivedNoteForm.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(noteForm, ex.getMessage());
        Logger.getLogger(GoodsReceivedNoteForm.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    }
    private void prepareCbPartners() {
    try {
        List<Partner> partners = Client.getInstance().getAllPartners();
        List<Object> partnerObjects = new ArrayList<Object>(partners);
        addResultToCB(partnerObjects,noteForm.getCbPartners());
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(noteForm, "Unable to instantiate Client.");
        Logger.getLogger(GoodsReceivedNoteForm.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(noteForm, ex.getMessage());
        Logger.getLogger(GoodsReceivedNoteForm.class.getName()).log(Level.SEVERE, null, ex);
    }
    }

    private void prepareCbFirms() {
        try {
        List<Firm> firms = Client.getInstance().getAllFirms();
        List<Object> firmObjects = new ArrayList<Object>(firms);
        addResultToCB(firmObjects, noteForm.getCbFirms());
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(noteForm, "Unable to instantiate Client.");
        Logger.getLogger(GoodsReceivedNoteForm.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(noteForm, ex.getMessage());
        Logger.getLogger(GoodsReceivedNoteForm.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
    
    private void addResultToCB(List<Object> objects, JComboBox<Object> comboBox) {
      if(objects != null && !objects.isEmpty() && noteForm.getMode() == FormMode.SELECT){
            JOptionPane.showMessageDialog(noteForm, "Products with given name (or similar) have been found");
        }
        comboBox.removeAllItems();
        for(Object p: objects){
            comboBox.addItem(p);
        }  
    }
    
    private void showSelectedCBProduct(Product selectedItem) {
        Integer productID = selectedItem.getID();
        String productName = selectedItem.getProductName();
        Double productWeight = selectedItem.getWeight();
        Boolean productFragile = selectedItem.getFragile();
        Integer productAmount = selectedItem.getAmount();
        ProductType productType = selectedItem.getType();
        BigDecimal productPrice = selectedItem.getPrice();
        
        noteForm.getTxtProductID().setText(String.valueOf(productID));
        noteForm.getTxtProductName().setText(productName);
        noteForm.getTxtWeight().setText(String.valueOf(productWeight));
        noteForm.getCbFragile().setSelectedItem(productFragile);
        noteForm.getTxtStock().setText(String.valueOf(productAmount));
        noteForm.getCbType().setSelectedItem(productType);
        noteForm.getTxtPrice().setText(String.valueOf(productPrice));
    }
    private void adjustTotalCost(JTextField txtCost) {
                DocumentTableModel model = (DocumentTableModel)noteForm.getTblItem().getModel();
                BigDecimal cost = new BigDecimal(0);
                    for(int i = 0;i<model.getRowCount();i++){
                        cost = cost.add((BigDecimal)model.getValueAt(i, 4));
                    } 
                txtCost.setText(String.valueOf(cost));
            }
    private void validateDates(Date issueDate, Date dueDate) throws Exception {
        if(issueDate.equals(dueDate) ||
                issueDate.after(dueDate)){
            throw new Exception("Issue date must be set before due dates");
        }
    }
    
    private void validateItems() throws Exception {
        String errorLog = "";
        DocumentTableModel model = (DocumentTableModel)noteForm.getTblItem().getModel();
        if(model.getDocument() == null){
            errorLog += "Table doesn't have a Goods received note assigned to it.";
        }
        if(model.getDocument().getItems() == null || model.getDocument().getItems().isEmpty()){
            errorLog += "You must insert at least one item into the table.";
        }
        if(!errorLog.isEmpty()){
            throw new Exception(errorLog);
        }
}
    
    private void fixNoteItems(GoodsReceivedNote note) {
        for(AbstractDocumentItem item : note.getItems()){
            item.setFirm(note.getFirm());
            item.setSecondParticipant(note.getSecondParticipant());
            item.setDocumentID(note.getID());
        }
    }
}
