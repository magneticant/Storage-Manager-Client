//package GUIController;
package rs.np.storage_manager_client.GUIController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

import rs.np.storage_manager_client.GUICoordinator.MainCoordinator;
import rs.np.storage_manager_client.constant.Constant;
import rs.np.storage_manager_client.form.MainForm;
import rs.np.storage_manager_client.form.mode.FormMode;
import rs.np.storage_manager_common.domain.User;

/**
 *
 * @author Milan
 */
public class MainController {
    private final MainForm mainForm;
    
    
    public MainController(MainForm mainForm) {
        this.mainForm = mainForm;
        addActionListener();
    }

    public void openForm() {
        User user = (User)MainCoordinator.getInstance().getParam(Constant.CURRENT_USER);
        mainForm.prepareForm(user);
        mainForm.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainForm.setVisible(true);
    }

    public MainForm getMainForm() {
        return mainForm;
    }

    private void addActionListener() {
        mainForm.miCreateProductActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                miCreateProductActionPerformed(e);
            }
            
            private void miCreateProductActionPerformed(ActionEvent e) {
                MainCoordinator.getInstance().openProductCreateForm();
            }
        });
        mainForm.miSelectProductActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                miSearchProducts(e);
            }
            
            private void miSearchProducts(ActionEvent e){
                MainCoordinator.getInstance().openProductForm(FormMode.SELECT);
            }
        });
        mainForm.miDeleteProductActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                miDeleteProduct(e);
            }
            
            private void miDeleteProduct(ActionEvent e) {
                MainCoordinator.getInstance().openProductForm(FormMode.DELETE);
            }
        });
        mainForm.miUpdateProductActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                miUpdateProduct(e);
            }
            
            private void miUpdateProduct(ActionEvent e){
                MainCoordinator.getInstance().openProductForm(FormMode.UPDATE);
            }
        });
        mainForm.miSelectAllProductsActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                miShowAllProducts(e);
            }
            
            private void miShowAllProducts(ActionEvent e){
                MainCoordinator.getInstance().openProductForm(FormMode.SELECT_ALL);
            }
        });
        mainForm.miCreateNoteActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                miCreateNote(e);
            }
            
            private void miCreateNote(ActionEvent e){
                MainCoordinator.getInstance().openNoteForm();
            }
        });
        mainForm.miCreateReportActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                miCreateReport(e);
            }
            
            private void miCreateReport(ActionEvent e){
                MainCoordinator.getInstance().openReportForm(FormMode.INSERT);
            }
        });
        mainForm.miCreateBillActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                miCreateBill(e);
            }
            
            private void miCreateBill(ActionEvent e){
                MainCoordinator.getInstance().openBillForm();
            }
        });
        mainForm.miSearchReportActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                miSearchReport(e);
            }

            private void miSearchReport(ActionEvent e) {
                MainCoordinator.getInstance().openReportForm(FormMode.SELECT);
            }
            
            
        });
    }
    
}
