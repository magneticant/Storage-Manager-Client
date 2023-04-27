//package GUIController;
package rs.np.storage_manager_client.GUIController;

import com.toedter.calendar.JTextFieldDateEditor;

import rs.np.storage_manager_client.connection.Client;
import rs.np.storage_manager_client.form.bill.BillOfLadingForm;
import rs.np.storage_manager_client.form.model.DocumentTableModel;
import rs.np.storage_manager_common.domain.*;
import rs.np.storage_manager_common.domain.abstraction.AbstractDocumentItem;
import rs.np.storage_manager_common.domain.abstraction.SecondParticipant;
import rs.np.storage_manager_common.domain.abstraction.implementation.BillOfLading;
import rs.np.storage_manager_common.domain.abstraction.implementation.BillOfLadingItem;
import rs.np.storage_manager_common.domain.abstraction.implementation.LegalPerson;
import rs.np.storage_manager_common.domain.abstraction.implementation.NaturalPerson;

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
public class BillOfLadingController {
    private BillOfLadingForm billForm;
    
    public BillOfLadingController(BillOfLadingForm billOfLadingForm) {
        billForm  = billOfLadingForm;
        addActionListeners();
    }

    public void openForm() {
        System.out.println("ENTERED OPEN FORM BILL");
        billForm.setTitle("Goods received note - INSERT");
        this.prepareForm();
        billForm.setLocationRelativeTo(null);
        
        billForm.setVisible(true);
        
        System.out.println("ADDED ACTION LISTENERS!!!");
    }
    
    private void addActionListeners() {
        billForm.getBtnCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                billForm.dispose();
            }
        });
        billForm.getBtnSwitch().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchCB();
            }

            private void switchCB() {
                if(!billForm.getCbNaturalPersons().isEnabled()){
                        billForm.getCbNaturalPersons().setEnabled(true);
                        billForm.getCbLegalPersons().setEnabled(false);
                }else {
                        billForm.getCbNaturalPersons().setEnabled(false);
                        billForm.getCbLegalPersons().setEnabled(true);
                }
            }
        });
        billForm.getCbProducts().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayProduct();
            }

            private void displayProduct() {
                showSelectedCBProduct((Product)billForm.getCbProducts().getSelectedItem());
            }
        });
        
        billForm.getBtnAddNoteItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItem();
            }

            private void addItem() {
                DocumentTableModel model = (DocumentTableModel)billForm.getTblItems().getModel();
                 BillOfLadingItem item = new BillOfLadingItem();
                    item.setProduct((Product)billForm.getCbProducts().getSelectedItem());
                    if(item == null){
                        JOptionPane.showMessageDialog(billForm, "Item is null.");
                        return;
                    }
                    if(model.checkIfExists(item)){
                        JOptionPane.showMessageDialog(billForm, "Item already added.");
                        return;
                    }
                    model.addItem(item);
                    adjustTotalCost(billForm.getTxtCost());
            }
        });
        
        billForm.getBtnDeleteNoteItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteItem();
            }

            private void deleteItem() {
                DocumentTableModel model = (DocumentTableModel)billForm.getTblItems().getModel();
                model.removeItem(billForm.getTblItems().getSelectedRow());
                adjustTotalCost(billForm.getTxtCost());
            }
        });
        
        billForm.getBtnInsert().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertBill();
            }

            private void insertBill() {
                try {
                // TODO add your handling code here:
                    adjustTotalCost(billForm.getTxtCost());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    System.out.println("***************************************");
                    String issueDateInput = ((JTextField)billForm.getIssueDateChooser().getDateEditor().
                            getUiComponent()).getText().trim();
                    System.out.println("*"+issueDateInput+"*");
                    System.out.println("***************************************");

                    Date issueDate = format.parse(issueDateInput);
                    String dueDateInput = ((JTextField)billForm.getDueDateChooser().getDateEditor().
                            getUiComponent()).getText().trim();
                    System.out.println("***************************************");
                    Date dueDate = format.parse(dueDateInput);
                    validateDates(issueDate, dueDate);
                    validateCB();
                    validateItems();
                    Firm firm = (Firm)(billForm.getCbFirm().getSelectedItem());
            //        Partner partner = (Partner)getCbPartner().getSelectedItem();


                    DocumentTableModel model = (DocumentTableModel)billForm.getTblItems().getModel();
                    BillOfLading bill = (BillOfLading) model.getDocument();

                    if(billForm.getCbLegalPersons().isEnabled()){
                        LegalPerson lp = (LegalPerson)billForm.getCbLegalPersons().getSelectedItem();
                        bill.setSecondParticipant(lp);
                    }else {
                        NaturalPerson np = (NaturalPerson)billForm.getCbNaturalPersons().getSelectedItem();
                        bill.setSecondParticipant(np);
                    }

                    bill.setDeadLine(dueDate);
                    bill.setIssueDate(issueDate);
            //        note.setSecondParticipant(partner);
                    bill.setFirm(firm);
                    bill.setTotalCost(new BigDecimal(billForm.getTxtCost().getText()));
                    fixBillItems(bill);
                    Client.getInstance().insertBillOfLading(bill);
                    JOptionPane.showMessageDialog(billForm, "Bill of lading inserted successfully!");
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(billForm, "Invalid date input.");
                    Logger.getLogger(BillOfLadingForm.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(billForm, ex.getMessage());
                    Logger.getLogger(BillOfLadingForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
        });
        
    }

    private void prepareForm() {
        prepareCbProducts();
        alterDateFields();
        alterComboBoxFragile();
        alterComboBoxType();
        prepareForInsert();
        assignModel();
        
        billForm.getCbNaturalPersons().setEnabled(false);
    }
    
    private void prepareCbProducts() {
    try {
        List<Product> products = Client.getInstance().getAllProducts();
        List<Object> productObjects = new ArrayList<Object>(products);
        addResultToCB(productObjects,billForm.getCbProducts());
        if(billForm.getCbProducts().getSelectedItem()!=null){
            showSelectedCBProduct((Product)billForm.getCbProducts().getSelectedItem());
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(billForm, "Unable to instantiate Client.");
        Logger.getLogger(BillOfLadingForm.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(billForm, ex.getMessage());
        Logger.getLogger(BillOfLadingForm.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
    
    private void addResultToCB(List<Object> objects, JComboBox<Object> comboBox) {
      
        comboBox.removeAllItems();
        for(Object p: objects){
            comboBox.addItem(p);
        }  
    }
    
    private void showSelectedCBProduct(Product selectedItem) {
        if(selectedItem == null) return;
        Integer productID = selectedItem.getID();
        String productName = selectedItem.getProductName();
        Double productWeight = selectedItem.getWeight();
        Boolean productFragile = selectedItem.getFragile();
        Integer productAmount = selectedItem.getAmount();
        ProductType productType = selectedItem.getType();
        BigDecimal productPrice = selectedItem.getPrice();
        
        billForm.getTxtProductID().setText(String.valueOf(productID));
        billForm.getTxtProductName().setText(productName);
        billForm.getTxtWeight().setText(String.valueOf(productWeight));
        billForm.getCbFragile().setSelectedItem(productFragile);
        billForm.getTxtStock().setText(String.valueOf(productAmount));
        billForm.getCbType().setSelectedItem(productType);
        billForm.getTxtPrice().setText(String.valueOf(productPrice));
    }
    
    private void alterDateFields() {
        JTextFieldDateEditor issueDateEditor = 
                (JTextFieldDateEditor)billForm.getIssueDateChooser().getDateEditor();
        issueDateEditor.setEditable(false);
                JTextFieldDateEditor dueDateEditor = 
                (JTextFieldDateEditor)billForm.getDueDateChooser().getDateEditor();
        dueDateEditor.setEditable(false);
        billForm.getIssueDateChooser().setDateFormatString("yyyy-MM-dd");
        billForm.getDueDateChooser().setDateFormatString("yyyy-MM-dd");
    }
    
    private void alterComboBoxFragile() {
        billForm.getCbFragile().removeAllItems();
        billForm.getCbFragile().addItem(false);
        billForm.getCbFragile().addItem(true);
    }
    
    private void alterComboBoxType() {
        billForm.getCbType().removeAllItems();
        for(ProductType type : ProductType.values()){
            billForm.getCbType().addItem(type);
        }
    }
    
    private void prepareForInsert() {
        billForm.getTxtProductID().setEditable(false);
        billForm.getTxtProductName().setEditable(false);
        billForm.getTxtStock().setEditable(false);
        billForm.getTxtWeight().setEditable(false);
        billForm.getTxtPrice().setEditable(false);
        billForm.getCbType().setEnabled(false);
        billForm.getCbFragile().setEnabled(false);
        billForm.getTxtCost().setEditable(false);
        billForm.getTxtNoteID().setEditable(false);
        prepareCbProducts();
        prepareCBLegalPersons();
        prepareCBNaturalPersons(); 
        prepareCbFirms();
    }
    
    private void prepareCBLegalPersons() {
        try {
        List<? extends SecondParticipant> legalPersons = Client.getInstance().getAllLegalPersons();
        addResultToCBParticipant(legalPersons, billForm.getCbLegalPersons());
        
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(billForm, "Unable to instantiate Client.");
        Logger.getLogger(BillOfLadingForm.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(billForm, ex.getMessage());
        Logger.getLogger(BillOfLadingForm.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
    
     private void addResultToCBParticipant(List<? extends SecondParticipant> legalPersons, JComboBox<SecondParticipant> cbLegalPersons) {
        cbLegalPersons.removeAllItems();
        
        for(SecondParticipant p: legalPersons){
            cbLegalPersons.addItem(p);
        }
    
    }
     
     private void prepareCBNaturalPersons() {
        try {
        List<? extends SecondParticipant> naturalPersons = Client.getInstance().getAllNaturalPersons();
        addResultToCBParticipant(naturalPersons, billForm.getCbNaturalPersons());
        
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(billForm, "Unable to instantiate Client.");
        Logger.getLogger(BillOfLadingForm.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(billForm, ex.getMessage());
        Logger.getLogger(BillOfLadingForm.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
     
     private void prepareCbFirms() {
        try {
        List<Firm> firms = Client.getInstance().getAllFirms();
        List<Object> firmObjects = new ArrayList<Object>(firms);
        addResultToCB(firmObjects, billForm.getCbFirms());
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(billForm, "Unable to instantiate Client.");
        Logger.getLogger(BillOfLadingForm.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(billForm, ex.getMessage());
        Logger.getLogger(BillOfLadingForm.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
     
    private void assignModel() {
        DocumentTableModel model = new DocumentTableModel(new BillOfLading());
        billForm.getTblItems().setModel(model);
    }

    public void adjustTotalCost(JTextField txtCost) {
        DocumentTableModel model = (DocumentTableModel)billForm.getTblItems().getModel();
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
    
    private void validateCB() throws Exception {
        String errorLog = "";
        if(billForm.getCbFirm().getSelectedItem() == null){
            errorLog += "You must select a firm.";
        }
//        if(getCbPartner().getSelectedItem() == null){
//            errorLog += "You must select a partner.";
//        }
        if(!errorLog.isEmpty()){
            throw new Exception(errorLog);
        }
    }
    private void validateItems() throws Exception {
        String errorLog = "";
        DocumentTableModel model = (DocumentTableModel)billForm.getTblItems().getModel();
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
    
     private void fixBillItems(BillOfLading bill) {
        for(AbstractDocumentItem item : bill.getItems()){
            item.setFirm(bill.getFirm());
            item.setSecondParticipant(bill.getSecondParticipant());
            item.setDocument(bill);
        }
    }
}
