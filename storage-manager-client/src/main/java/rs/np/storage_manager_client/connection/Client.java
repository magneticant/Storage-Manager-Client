//package connection;
package rs.np.storage_manager_client.connection;


import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import rs.np.storage_manager_common.connection.Operation;
import rs.np.storage_manager_common.connection.abstraction.Receiver;
import rs.np.storage_manager_common.connection.abstraction.Request;
import rs.np.storage_manager_common.connection.abstraction.Response;
import rs.np.storage_manager_common.connection.abstraction.Sender;
import rs.np.storage_manager_common.connection.abstraction.JSONImpl.*;
import rs.np.storage_manager_common.connection.abstraction.objectImpl.*;
import rs.np.storage_manager_common.domain.*;
import rs.np.storage_manager_common.domain.abstraction.implementation.*;
import rs.np.storage_manager_common.domain.abstraction.*;

/**
 * Ovo je glavna klijentska klasa pomocu koje se salju i primaju zahtevi i odgovori servera i prosledjuju
 * dalje kontroleru korisnickog interfejsa klijenta.
 * @author Milan
 * @since 1.0.0
 */
public class Client {
	/**
	 * atribut socket, koji predstavlja soket za komunikaciju klijentske strane, tipa {@link Socket}
	 */
    Socket socket;
    /**
     * atribut sender, koji ima metodu za slanje request-a serveru (i odgovora nazad ka klijentu 
     * ako smo sa serverske strane). Tip {@link Sender}
     */
    Sender sender; 
    /**
     * atribut receiver, koji predstavlja objekat koji sadrzi metodu receiveObject koja 
     * moze da primi i zahtev i odgovor (koristi je i klijent i server)
     */
    Receiver receiver;
    /** 
     * privatni staticki atribut instanca klase, za Singleton pattern.
     */
    private static Client instance;
    /**
     * privatni atribut odgovor servera (response), interfejs {@link Response}
     */
    private Response response;
    /**
     * privatni atribut zahtev za server (request), interfejs {@link Request}
     */
    private Request request;
    
    /**
     * privatni neparametrizovani konstruktor
     * @throws IOException ako je nemoguce uspostaviti vezu sa serverom
     */
    private Client() throws IOException {
        socket = new Socket("127.0.0.1", 6969);
        System.out.println("Client connected to the server on port 6969.");
        sender = new SenderJSON(socket);
        receiver = new ReceiverJSON(socket);
        response = new ResponseJSON();
        request = new RequestJSON();
        
    }
    /**
     * getInstance metoda karakteristicna za Singleton pattern, staticka je i vraca instancu klase
     * @return instance, tipa {@link Client}
     * @throws IOException ukoliko neparametrizovani konstruktor ove klase baci Exception (ako je nemoguce povezati se sa serverom)
     */
    public static Client getInstance() throws IOException{
        if(instance == null)
            instance = new Client();
        
        return instance;
    }
    /**
     * metoda koja vraca sve artikle sa servera (iz baze podataka)
     * @return lista artikala ({@link Product})
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public List<Product> getAllProducts() throws Exception {
        sendReceiveThrowEx(null, Operation.SELECT_ALL_PRODUCTS);
        return (List<Product>)response.getResponse(Product.class, true);
    }
    /**
     * metoda za login korisnika
     * @param username korisnicko ime tipa {@link String}
     * @param password lozinka tipa {@link String}
     * @return korisnik tipa {@link User}
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public User login(String username, String password) throws Exception {
        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        user.setMode(WhereClauseMode.BY_USERNAME_PASSWORD);
        sendReceiveThrowEx(user, Operation.LOGIN);
        return (User)response.getResponse(User.class);
    }
    /**
     * metoda koja vraca sve artikle ({@link Product}) po zadatom parametru sa servera (iz baze podataka)
     * @param p tipa {@link Product} i on sadrzi podatke o parametrima za pretragu u bazi podataka
     * @return lista proizvoda ({@link Product})
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public List<Product> getAllProducts(Product p) throws Exception {
    	sendReceiveThrowEx(p, Operation.SELECT_ALL_PRODUCTS_PARAM);
        return (List<Product>)response.getResponse(Product.class, true);
    }
    /**
     * metoda koja azurira odredjeni artikal ({@link Product}) preko servera (u bazi podataka)
     * @param product artikal koji je neophodno azurirati, tipa {@link Product}
     * @return artikal koji je azuriran u bazi podataka ({@link Product})
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public Product updateProduct(Product product) throws Exception {
    	sendReceiveThrowEx(product, Operation.UPDATE_PRODUCT);
        return (Product)response.getResponse(Product.class);
    }
    /**
     * metoda koja brise odredjeni artikal ({@link Product}) preko servera (iz baze podataka)
     * @param product artikal koji je neophodno obrisati, tipa {@link Product}
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public void deleteProduct(Product product) throws Exception {
        sendReceiveThrowEx(product, Operation.DELETE_PRODUCT);
    }
    /**
     * metoda koja unosi odredjeni artikal ({@link Product}) u bazu podataka (uz pomoc servera)
     * @param product artikal koji je neophodno uneti u bazu podataka, tipa {@link Product}
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public void insertProduct(Product product) throws Exception {
        sendReceiveThrowEx(product, Operation.INSERT_PRODUCT);
    }
    /**
     * metoda koja unosi odredjeni izvestaj ({@link Report}) u bazu podataka (uz pomoc servera)
     * @param report izvestaj koji je neophodno uneti u bazu podataka, tipa {@link Report}
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public void insertReport(Report report) throws Exception {
        sendReceiveThrowEx(report, Operation.INSERT_REPORT);
    }
    /**
     * metoda koja vraca sve poslovne partnere sa servera (iz baze podataka)
     * @return lista poslovnih partnera ({@link Partner})
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public List<Partner> getAllPartners() throws Exception {
    	sendReceiveThrowEx(null, Operation.SELECT_ALL_PARTNERS);
        return (List<Partner>)response.getResponse(Partner.class, true);
    }
    /**
     * metoda koja vraca sve firme sa servera (iz baze podataka)
     * @return lista firmi ({@link Firm})
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public List<Firm> getAllFirms() throws Exception {
    	sendReceiveThrowEx(null, Operation.SELECT_ALL_FIRMS);
        return (List<Firm>)response.getResponse(Firm.class, true);
    }
    /**
     * metoda koja unosi prosledjenu prijemnicu ({@link GoodsReceivedNote}) u bazu podataka (uz pomoc servera)
     * @param note prijemnica koju je potrebno uneti u bazu podataka ({@link GoodsReceivedNote})
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public void insertGoodsReceivedNote(GoodsReceivedNote note) throws Exception {
        sendReceiveThrowEx(note, Operation.INSERT_NOTE);
    }
    /**
     * metoda koja vraca sva fizicka lica sa servera (iz baze podataka)
     * @return lista fizickih lica ({@link NaturalPerson})
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public List<? extends SecondParticipant> getAllNaturalPersons() throws Exception {
    	sendReceiveThrowEx(null, Operation.SELECT_ALL_NATURAL_PERSONS);
        return (List<NaturalPerson>)response.getResponse(NaturalPerson.class, true);
    }
    /**
     * metoda koja vraca sva pravna lica sa servera (iz baze podataka)
     * @return lista pravnih lica ({@link LegalPerson})
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public List<? extends SecondParticipant> getAllLegalPersons() throws Exception {
    	sendReceiveThrowEx(null, Operation.SELECT_ALL_LEGAL_PERSONS);
        return (List<LegalPerson>)response.getResponse(LegalPerson.class, true);
    }
    /**
     * metoda koja unosi prosledjenu otpremnicu ({@link BillOfLading}) u bazu podataka (uz pomoc servera)
     * @param bill otpremnica koju je potrebno uneti u bazu podataka ({@link BillOfLading})
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public void insertBillOfLading(BillOfLading bill) throws Exception {
         sendReceiveThrowEx(bill, Operation.INSERT_BILL);
    }
    /**
     * metoda koja vraca sve izvestaje sa servera (iz baze podataka)
     * @return lista izvestaja ({@link Report})
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public List<Report> getAllReports() throws Exception {
    	sendReceiveThrowEx(null, Operation.SELECT_ALL_REPORTS);
    	return (List<Report>)response.getResponse(Report.class, true);
    }
    /**
     * metoda koja vraca sve izvestaje ({@link Report}) po zadatom parametru sa servera (iz baze podataka)
     * @param report tipa {@link Report} i on sadrzi podatke o parametrima za pretragu u bazi podataka
     * @return lista izvestaja ({@link Report})
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    public List<Report> getAllReports(Report report) throws Exception {
    	sendReceiveThrowEx(report, Operation.SELECT_ALL_REPORTS_PARAM);
    	return (List<Report>)response.getResponse(Report.class, true);
    }
    /**
     * privatna metoda koja obradjuje sam postupak slanja, prijema i eventualnog bacanja izuzetka ukoliko
     * je isti nastao na serverskoj strani. Na ovu metodu se pozivaju sve ostale metode klase i ona sluzi 
     * da izvrsi direktnu komunikaciju sa serverskom stranom (serverski projekat).
     * @param object objekat koji ce biti poslat na server, tipa {@link DomainClass}
     * @param operation CRUD operacija koju je neophodno da server obavi
     * @throws Exception ako je primljen izuzetak sa serverske strane
     */
    private void sendReceiveThrowEx(DomainClass object, Operation operation) throws Exception{
    	request = (Request) Class.forName(request.getClass().getName())
    			.getDeclaredConstructor().newInstance();
        request.setObj(object);
        request.setOperation(operation);
        this.sender.sendObject(request);
        Response resp = receiver.receiveObject(response.getClass());
        response = resp;
        
        if(response.getExMessage() != null){
            throw new Exception(response.getExMessage());
        }
//        return response;
    }

    }

