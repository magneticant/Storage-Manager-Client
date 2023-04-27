//package GUICoordinator;
package rs.np.storage_manager_client.GUICoordinator;

import java.util.HashMap;
import java.util.Map;

import rs.np.storage_manager_client.GUIController.LoginController;
import rs.np.storage_manager_client.form.LoginForm;
import rs.np.storage_manager_client.form.MainForm;
import rs.np.storage_manager_client.form.bill.BillOfLadingForm;
import rs.np.storage_manager_client.form.mode.FormMode;
import rs.np.storage_manager_client.form.note.GoodsReceivedNoteForm;
import rs.np.storage_manager_client.form.product.ProductCreateForm;
import rs.np.storage_manager_client.form.product.ProductForm;
import rs.np.storage_manager_client.form.report.EditCapacityForm;
import rs.np.storage_manager_client.form.report.ReportForm;
import rs.np.storage_manager_client.GUIController.*;

/**
 *
 * @author Milan
 */
public class MainCoordinator {
    private static MainCoordinator instance;
    private final MainController mainController;
    private final Map<String, Object> params;

    private MainCoordinator() {
        this.mainController = new MainController(new MainForm());
        this.params = new HashMap<>();
    }
    
    public static MainCoordinator getInstance() {
        if(instance == null)
            instance = new MainCoordinator();
        
        return instance;
    }
    
    public void openLoginForm(){
        LoginController loginC = new LoginController(new LoginForm());
        loginC.openForm();
    }
    
    public void openMainForm(){
        mainController.openForm();
    }
    
    public void openProductCreateForm(){
        ProductCreateController productCC = new ProductCreateController(
                new ProductCreateForm(mainController.getMainForm(), true));
        productCC.openForm();
    }
    
    public void openProductForm(FormMode mode){
        ProductController productC = new ProductController(new ProductForm(
                mainController.getMainForm(), true, mode));
        productC.openForm(mode);
    }
    
    public void openNoteForm(){
        GoodsReceivedNoteController noteC = new GoodsReceivedNoteController(
                new GoodsReceivedNoteForm(
                        MainCoordinator.getInstance().getMainController().getMainForm(),
                            true));
        noteC.openForm();
//        ProductTablePanelController prodTablePanelC = new ProductTablePanelController(
//                new ProductTablePanel(), new ProductPanel(), new GoodsReceivedNote());
        
    }
    
    public void openReportForm(FormMode mode){
        ReportController reportC = new ReportController(
                new ReportForm(
                        MainCoordinator.getInstance().getMainController().getMainForm(),
                            true, mode)
        );
        reportC.openForm(mode);
    }
    
    public void openBillForm(){
        BillOfLadingController billC = new BillOfLadingController(
            new BillOfLadingForm(
                    MainCoordinator.getInstance().getMainController().getMainForm(),
                        true)
        );
        billC.openForm();
    }

    public Object getParam(String key) {
        return params.get(key);
    }

    public void setParam(String name, Object key) {
        params.put(name, key);
    }

    public MainController getMainController() {
        return mainController;
    }

    public void openEditCapacityForm(ReportForm parent) {
        EditCapacityController editCapC = new EditCapacityController(
                new EditCapacityForm(parent, true)
        );
        editCapC.openForm(parent);
    }
    
}
