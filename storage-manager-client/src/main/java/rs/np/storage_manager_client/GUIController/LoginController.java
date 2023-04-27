//package GUIController;
package rs.np.storage_manager_client.GUIController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

import rs.np.storage_manager_client.GUICoordinator.MainCoordinator;
import rs.np.storage_manager_client.connection.Client;
import rs.np.storage_manager_client.constant.Constant;
import rs.np.storage_manager_client.form.LoginForm;
import rs.np.storage_manager_common.domain.User;

/**
 *
 * @author Milan
 */
public class LoginController {
    private final LoginForm loginForm;
    
    
    public LoginController(LoginForm loginForm) {
        this.loginForm = loginForm;
        addActionListener();
    }

    public void openForm() {
        loginForm.setTitle("Please login");
        loginForm.setLocationRelativeTo(null);
        loginForm.setVisible(true);
    }

    private void addActionListener() {
        loginForm.btnLoginAddActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }

            private void attemptLogin() {
                try {
            
            // TODO add your handling code here:
            String username = loginForm.getTxtUsername().getText().trim();
            char[] password =loginForm.getTxtPassword().getPassword();
            String password1 = convertCharArrToString(password);
            
            System.out.println("Username je:" + username);
            System.out.println("Password je:" + password1);
            
            validateFields(username, password1);
            
//            User user1 = new User(1, "Milan", "Stankovic", username, password1);
            User user = Client.getInstance().login(username, password1);
            
            JOptionPane.showMessageDialog(loginForm, "Login successful. Welcome " + user.getName());
            //new MainForm(user).setVisible(true);
            MainCoordinator.getInstance().setParam(Constant.CURRENT_USER, user);
            MainCoordinator.getInstance().openMainForm();
            loginForm.dispose();
        } catch (Exception ex) {
            loginForm.setAttemptsRemaining(
            loginForm.getAttemptsRemaining()-1);
            JOptionPane.showMessageDialog(loginForm, ex.getMessage() + " Attempts remaining: " + loginForm.getAttemptsRemaining());
            if(loginForm.getAttemptsRemaining() == 0){
                loginForm.dispose();
            }
        }
            }

        private String convertCharArrToString(char[] password) {
            return new String(password);
            }

            private void validateFields(String username, String password1) throws Exception {
                String errMessage = "";
        
        if(username.isEmpty()){
            errMessage += "Username must be inputted!\n";
        }
        if(password1.isEmpty()){
            errMessage += "Password must be inputted!\n";
        }
        if(username.contains(" ") || username.contains("\n") || username.contains("\t")){
            errMessage += "Username cannot contain blank characters\n";
        }
        if(password1.contains(" ") || password1.contains("\n") || username.contains("\t")){
            errMessage += "Password cannot contain blank characters\n";
        }
        if(username.length()<6 || password1.length()<6){
            errMessage += "Username and password must be at least 6 characters.\n";
        }
        if(username.length()> 20 || password1.length()> 20){
            errMessage += "The length of your username and password must be at most 20 characters.\n";
        }
        if(!errMessage.isEmpty()){
            throw new Exception("Your login failed for said reasons: \n" + errMessage);
        }
       }
        });
    }
    
}
