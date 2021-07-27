/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ejb.session.stateless.ClientSessionBeanLocal;
import ejb.session.stateless.DocumentSessionBeanLocal;
import ejb.session.stateless.FNBSessionBeanLocal;
import ejb.session.stateless.TradeSessionBeanLocal;
import entitiy.ClientEntity;
import entitiy.DocumentEntity;
import entitiy.FNBEntity;
import entitiy.TradeEntity;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.json.JsonArray;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.DocEnum;
import util.enumeration.FNBEnum;
import util.enumeration.FinancingTransactionEnum;
import util.enumeration.ReportingSideEnum;
import util.exception.AlreadyAssociatedException;
import util.exception.AssociationException;
import util.exception.ClientExistException;
import util.exception.ClientNotFoundException;
import util.exception.CreateDocumentException;
import util.exception.CreateFNBException;
import util.exception.CreateNewTradeException;
import util.exception.DocumentExistException;
import util.exception.FNBExistException;
import util.exception.FNBNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;
import org.jboss.ejb3.annotation.TransactionTimeout;

/**
 *
 * @author Ong Bik Jeun
 */
@Startup
@LocalBean
@Singleton
public class DataInitSessionBean implements DataInitSessionBeanLocal {

    @EJB
    private TradeSessionBeanLocal tradeSessionBean;

    @EJB
    private FNBSessionBeanLocal fNBSessionBean;

    @EJB
    private DocumentSessionBeanLocal documentSessionBean;

    @EJB
    private ClientSessionBeanLocal clientSessionBean;

    @PersistenceContext(unitName = "GoodToTrade-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct() {
        if (em.find(ClientEntity.class, 1l) == null) {
            dataInit();
        }

    }

    public void persist(Object object) {
        em.persist(object);
    }

    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 5)
    private void dataInit() {
        readAPI();
        readTrade();
    }

    private void readAPI() {
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get("C:\\Users\\Mitsuki\\Desktop\\Code to Connect\\gtt_api_data.json"));
            ObjectMapper mapper = new ObjectMapper();
            JsonParser jp = new JsonFactory().createParser(reader);
            jp.setCodec(mapper);
            jp.nextToken();
            while (jp.hasCurrentToken()) {
                JsonNode parser = jp.readValueAsTree();

                String clientID = parser.fieldNames().next();
                clientSessionBean.createNewClient(new ClientEntity(clientID));
                fNBSessionBean.createNewFnB(new FNBEntity(FNBEnum.UK), clientID);
                fNBSessionBean.createNewFnB(new FNBEntity(FNBEnum.EU), clientID);
                fNBSessionBean.createNewFnB(new FNBEntity(FNBEnum.SG), clientID);

                ArrayNode details = (ArrayNode) parser.get(clientID);
                for (int i = 0; i < details.size(); i++) {
                    JsonNode arrayElement = details.get(i);

                    String fnbType = arrayElement.get("entityId").asText();
                    String docType = arrayElement.get("documentId").asText();
                    String status = arrayElement.get("status").asText();
                    DocEnum type = null;
                    boolean statusBool = false;
                    FNBEnum fnb = null;

                    if (fnbType.equals("FNB-UK")) {
                        fnb = FNBEnum.UK;
                    } else if (fnbType.equals("FNB-EU")) {
                        fnb = FNBEnum.EU;
                    }

                    if (docType.equals("LEI")) {
                        type = DocEnum.LEI;
                    } else if (docType.equals("REPORTING_CONSENT")) {
                        type = DocEnum.REPORTINGCONSENT;
                    } else {
                        type = DocEnum.AML;
                    }

                    if (status.equals("GREEN")) {
                        statusBool = true;
                    } else {
                        statusBool = false;
                    }
                    documentSessionBean.associateDocToClient(documentSessionBean.createNewDoc(new DocumentEntity(type, statusBool)), clientID, fnb);

                }
                jp.nextToken();
            }
        } catch (IOException | ClientExistException | UnknownPersistenceException | InputDataValidationException | FNBExistException | CreateFNBException | DocumentExistException | CreateDocumentException | AssociationException | AlreadyAssociatedException ex) {
            Logger.getLogger(DataInitSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(DataInitSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 5)
    private void readTrade() {
        BufferedReader readerTrade = null;
        try {
            //please change this line to the directory of where the file will be at
            readerTrade = Files.newBufferedReader(Paths.get("C:\\Users\\Mitsuki\\Desktop\\Code to Connect\\test_trade.json")); //sorry passing the entire data file resulted in a timeout, so i shrank the data..
            ObjectMapper mapperTrade = new ObjectMapper();
            JsonParser jpTrade = new JsonFactory().createParser(readerTrade);
            jpTrade.setCodec(mapperTrade);
            jpTrade.nextToken();
            while (jpTrade.hasCurrentToken()) {
                JsonNode parser = jpTrade.readValueAsTree();
                JsonNode clientDetails = parser.get(parser.fieldNames().next());
                String client = clientDetails.get("counterpartyID").asText();
                String fnbTypeTrade = clientDetails.get("reportingCounterpartyID").asText();
                FNBEnum fnbTrade = null;
                if (fnbTypeTrade.equals("FNB-UK")) {
                    fnbTrade = FNBEnum.UK;
                } else if (fnbTypeTrade.equals("FNB-EU")) {
                    fnbTrade = FNBEnum.EU;
                } else {
                    fnbTrade = FNBEnum.SG;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                String date = parser.get("date").asText();
                String tradeId = parser.get("tradeID").asText();
                String reportingSide = parser.get("reportingSide").asText();
                ReportingSideEnum side = null;
                if (reportingSide.equals("FIRM")) {
                    side = ReportingSideEnum.FIRM;
                } else {
                    side = ReportingSideEnum.CLIENT;
                }
                String regulation = parser.get("regulation").asText();
                String jurisdiction = parser.get("jurisdiction").asText();
                String securitiesFinancingTransactionType = parser.get("securitiesFinancingTransactionType").asText();
                FinancingTransactionEnum fin = null;
                if (securitiesFinancingTransactionType.equals("SECURITIES_LENDING")) {
                    fin = FinancingTransactionEnum.SECURITIES_LENDING;
                } else if (securitiesFinancingTransactionType.equals("BUY_BACK")) {
                    fin = FinancingTransactionEnum.BUY_BACK;
                } else if (securitiesFinancingTransactionType.equals("MARGIN_LENDING")) {
                    fin = FinancingTransactionEnum.MARGIN_LENDING;
                } else if (securitiesFinancingTransactionType.equals("REPURCHASE")) {
                    fin = FinancingTransactionEnum.REPURCHASE;
                } else {
                    fin = FinancingTransactionEnum.OTHER;
                }
                TradeEntity trade = new TradeEntity(formatter.parse(date), tradeId, side, regulation, jurisdiction, fin);

                tradeSessionBean.createNewTrade(client, fNBSessionBean.retrieveByClientNType(client, fnbTrade).getId(), trade);
                jpTrade.nextToken();

            }
        } catch (IOException | FNBNotFoundException | CreateNewTradeException | InputDataValidationException | ParseException ex) {
            Logger.getLogger(DataInitSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                readerTrade.close();
            } catch (IOException ex) {
                Logger.getLogger(DataInitSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
