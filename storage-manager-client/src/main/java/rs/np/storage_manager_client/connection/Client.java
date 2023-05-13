//package connection;
package rs.np.storage_manager_client.connection;


import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    SenderJSON sender; 
    ReceiverJSON receiver;
    private static Client instance;
    private Gson gson;
    
    public Client() throws IOException {
        socket = new Socket("127.0.0.1", 6969);
        System.out.println("Client connected to the server on port 6969.");
        sender = new SenderJSON(socket);
        receiver = new ReceiverJSON(socket);
        
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls()
        		.setDateFormat("yyyy-MM-dd");
        gson = gsonBuilder.create();
        
    }
    
    public static Client getInstance() throws IOException{
        if(instance == null)
            instance = new Client();
        
        return instance;
    }
    
    public List<Product> getAllProducts() throws Exception {
        ResponseJSON resp = sendReceiveThrowEx(null, Operation.SELECT_ALL_PRODUCTS, ArrayList.class);
        return (List<Product>)resp.getResponse(Product.class, true);
    }

    public User login(String username, String password) throws Exception {
        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        user.setMode(WhereClauseMode.BY_USERNAME_PASSWORD);
        ResponseJSON resp = sendReceiveThrowEx(user, Operation.LOGIN, User.class);
        return (User)resp.getResponse(User.class);
//        return gson.fromJson(resp.getResponse(User.class, false).toString(), User.class);
    }

    public List<Product> getAllProducts(Product p) throws Exception {
    	ResponseJSON resp = sendReceiveThrowEx(p, Operation.SELECT_ALL_PRODUCTS_PARAM, Product.class);
//    	Type productListType = new TypeToken<List<Product>>(){}.getType();
//        List<Product> productList = gson.fromJson(resp.getResponse(Product.class, true).toString(), productListType);
        return (List<Product>)resp.getResponse(Product.class, true);
    }

    public Product updateProduct(Product product) throws Exception {
        ResponseJSON resp = sendReceiveThrowEx(product, Operation.UPDATE_PRODUCT, Product.class);
        return (Product)resp.getResponse(Product.class);
//        return gson.fromJson(resp.getResponse().toString(), Product.class);
    }

    public void deleteProduct(Product product) throws Exception {
        sendReceiveThrowEx(product, Operation.DELETE_PRODUCT,Product.class);
    }

    public void insertProduct(Product product) throws Exception {
        sendReceiveThrowEx(product, Operation.INSERT_PRODUCT, Product.class);
    }

    public void insertReport(Report report) throws Exception {
        sendReceiveThrowEx(report, Operation.INSERT_REPORT, Report.class);
    }

    public List<Partner> getAllPartners() throws Exception {
    	ResponseJSON resp = sendReceiveThrowEx(null, Operation.SELECT_ALL_PARTNERS, ArrayList.class);
//        Type partnerListType = new TypeToken<List<Partner>>(){}.getType();
//        String responseObjectJSON = gson.toJson(resp.getResponse());
//        List<Partner> partnerList = gson.fromJson(resp.getResponse().toString(), partnerListType);
        return (List<Partner>)resp.getResponse(Partner.class, true);
    }
    
    public List<Firm> getAllFirms() throws Exception {
    	ResponseJSON resp = sendReceiveThrowEx(null, Operation.SELECT_ALL_FIRMS, ArrayList.class);
//        Type firmListType = new TypeToken<List<Firm>>(){}.getType();
//        List<Firm> firmList = gson.fromJson(resp.getResponse().toString(), firmListType);
        return (List<Firm>)resp.getResponse(Firm.class, true);
    }
    
    public void insertGoodsReceivedNote(GoodsReceivedNote note) throws Exception {
        sendReceiveThrowEx(note, Operation.INSERT_NOTE, GoodsReceivedNote.class);
    }
    
    public List<? extends SecondParticipant> getAllNaturalPersons() throws Exception {
    	ResponseJSON resp = sendReceiveThrowEx(null, Operation.SELECT_ALL_NATURAL_PERSONS, ArrayList.class);
//        Type NaturalPersonListType = new TypeToken<List<NaturalPerson>>(){}.getType();
//        List<NaturalPerson> naturalPersonList = gson.fromJson(resp.getResponse().toString(), NaturalPersonListType);
        return (List<NaturalPerson>)resp.getResponse(NaturalPerson.class, true);
    }
    
    public List<? extends SecondParticipant> getAllLegalPersons() throws Exception {
    	ResponseJSON resp = sendReceiveThrowEx(null, Operation.SELECT_ALL_LEGAL_PERSONS, ArrayList.class);
//        Type legalPersonListType = new TypeToken<List<LegalPerson>>(){}.getType();
//        List<LegalPerson> legalPersonList = gson.fromJson(resp.getResponse().toString(), legalPersonListType);
        return (List<LegalPerson>)resp.getResponse(LegalPerson.class, true);
    }
    
    public void insertBillOfLading(BillOfLading bill) throws Exception {
         sendReceiveThrowEx(bill, Operation.INSERT_BILL, BillOfLading.class);
    }
    
    public List<Report> getAllReports() throws Exception {
    	ResponseJSON resp = sendReceiveThrowEx(null, Operation.SELECT_ALL_REPORTS, ArrayList.class);
//        Type reportListType = new TypeToken<List<Report>>(){}.getType();
//        List<Report> reportList = gson.fromJson(resp.getResponse().toString(), reportListType);
//        return reportList;
    	return (List<Report>)resp.getResponse(Report.class, true);
    }
    
    public List<Report> getAllReports(Report report) throws Exception {
    	ResponseJSON resp = sendReceiveThrowEx(report, Operation.SELECT_ALL_REPORTS_PARAM, Report.class);
//        Type reportListType = new TypeToken<List<Report>>(){}.getType();
//        List<Report> reportList = gson.fromJson(resp.getResponse().toString(), reportListType);
//        return reportList;
    	return (List<Report>)resp.getResponse(Report.class, true);
    }
    
//    private Response sendReceiveThrowEx(DomainClass object, Operation operation) throws Exception {
//        Request req = new Request(object, operation);
//        sender.sendObject(req);
//        
//        String resp = receiver.receiveObject();
//        
//        Response respObj = gson.fromJson(resp, Response.class);
//        
//        if(respObj.getExMessage() != null){
//        	String message = respObj.getExMessage();
//        	if(respObj.getExMessage().contains("com.google.gson.stream.MalformedJsonException: Unterminated object at line")) {
//        		message = "Please refrain from using blank characters."
//        				+ "Use \"_\" or - or camel case instead.";
//        	}
//        	Exception ex = new Exception(message);
//            throw ex;
//        }
//        respObj.setResponse(gson.toJson(respObj.getResponse()));
//        
//        return respObj;
//    }
    private ResponseJSON sendReceiveThrowEx(DomainClass object, Operation operation, Class<?>className) throws Exception{
        RequestJSON req = new RequestJSON(gson.toJson(object,className), operation);
        sender.sendObject(req);
        ResponseJSON resp = null;

        resp = (ResponseJSON) receiver.receiveObject(ResponseJSON.class);

        if(resp.getExMessage() != null){
            throw new Exception(resp.getExMessage());
        }
        return resp;
    }
}
