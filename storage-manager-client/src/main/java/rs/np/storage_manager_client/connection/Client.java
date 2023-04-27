//package connection;
package rs.np.storage_manager_client.connection;


import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import rs.np.storage_manager_common.connection.*;
import rs.np.storage_manager_common.domain.*;
import rs.np.storage_manager_common.domain.abstraction.implementation.*;
import rs.np.storage_manager_common.domain.abstraction.*;

/**
 *
 * @author Milan
 */
public class Client {
    Socket socket;
    Sender sender; 
    Receiever receiver;
    private static Client instance;

    public Client() throws IOException {
        socket = new Socket("127.0.0.1", 6969);
        System.out.println("Client connected to the server on port 6969.");
        sender = new Sender(socket);
        receiver = new Receiever(socket);
    }
    
    public static Client getInstance() throws IOException{
        if(instance == null)
            instance = new Client();
        
        return instance;
    }
    
    public List<Product> getAllProducts() throws Exception{
        Response resp = sendReceiveThrowEx(null, Operation.SELECT_ALL_PRODUCTS);
        return (List<Product>)resp.getResponse();
    }

    public User login(String username, String password) throws Exception {
        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        user.setMode(WhereClauseMode.BY_USERNAME_PASSWORD);

        Response resp = sendReceiveThrowEx(user, Operation.LOGIN);
        return (User)resp.getResponse();
    }

    public List<Product> getAllProducts(Product p) throws Exception {
        Response resp = sendReceiveThrowEx(p, Operation.SELECT_ALL_PRODUCTS_PARAM);
        return (List<Product>)resp.getResponse();
    }

    public Product updateProduct(Product product) throws Exception {
        Response resp = sendReceiveThrowEx(product, Operation.UPDATE_PRODUCT);
        return (Product)resp.getResponse();
    }

    public void deleteProduct(Product product) throws Exception {
        sendReceiveThrowEx(product, Operation.DELETE_PRODUCT);
    }

    public void insertProduct(Product product) throws Exception {
        sendReceiveThrowEx(product, Operation.INSERT_PRODUCT);
    }

    public void insertReport(Report report) throws Exception {
        sendReceiveThrowEx(report, Operation.INSERT_REPORT);
    }

    public List<Partner> getAllPartners() throws Exception {
        Response resp = sendReceiveThrowEx(null, Operation.SELECT_ALL_PARTNERS);
        return (List<Partner>)resp.getResponse();
    }
    
    public List<Firm> getAllFirms() throws Exception {
        Response resp = sendReceiveThrowEx(null, Operation.SELECT_ALL_FIRMS);
        return (List<Firm>)resp.getResponse();
    }
    
    public void insertGoodsReceivedNote(GoodsReceivedNote note) throws Exception {
        sendReceiveThrowEx(note, Operation.INSERT_NOTE);
    }
    
    public List<? extends SecondParticipant> getAllNaturalPersons() throws Exception {
        Response resp = sendReceiveThrowEx(null, Operation.SELECT_ALL_NATURAL_PERSONS);
        return (List<NaturalPerson>)resp.getResponse();
    }
    
    public List<? extends SecondParticipant> getAllLegalPersons() throws Exception {
        Response resp = sendReceiveThrowEx(null, Operation.SELECT_ALL_LEGAL_PERSONS);
        return (List<LegalPerson>)resp.getResponse();
    }
    
    public void insertBillOfLading(BillOfLading bill) throws Exception {
         sendReceiveThrowEx(bill, Operation.INSERT_BILL);
    }
    
    public List<Report> getAllReports() throws Exception {
         Response resp = sendReceiveThrowEx(null, Operation.SELECT_ALL_REPORTS);
         return (List<Report>)resp.getResponse();
    }
    
    public List<Report> getAllReports(Report report) throws Exception {
        Response resp = sendReceiveThrowEx(report, Operation.SELECT_ALL_REPORTS_PARAM);
        return (List<Report>)resp.getResponse();
    }
    
    private Response sendReceiveThrowEx(DomainClass object, Operation operation) throws Exception {
        Request req = new Request(object, operation);
        sender.sendObject(req);
        Response resp = (Response) receiver.receiveObject();
        
        if(resp.getEx() != null){
            throw resp.getEx();
        }
        return resp;
    }
}
