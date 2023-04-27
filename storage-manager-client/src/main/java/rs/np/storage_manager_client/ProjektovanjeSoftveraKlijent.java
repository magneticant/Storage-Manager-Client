package rs.np.storage_manager_client;

import rs.np.storage_manager_client.GUICoordinator.MainCoordinator;

/**
 *
 * @author Milan
 */
public class ProjektovanjeSoftveraKlijent {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        new LoginForm().setVisible(true);
        MainCoordinator.getInstance().openLoginForm();
    }
    
}
