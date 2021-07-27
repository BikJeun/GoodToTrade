/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entitiy.ClientEntity;
import entitiy.DocumentEntity;
import entitiy.TradeEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.ClientExistException;
import util.exception.ClientNotFoundException;
import util.exception.InputDataValidationException;
import javax.persistence.PersistenceException;
import util.enumeration.FNBEnum;
import util.enumeration.ReportingSideEnum;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ong Bik Jeun
 */
@Stateless
public class ClientSessionBean implements ClientSessionBeanLocal {

    @PersistenceContext(unitName = "GoodToTrade-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ClientSessionBean() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    /**
     *
     * @param client
     * @return
     * @throws ClientExistException
     * @throws UnknownPersistenceException
     * @throws InputDataValidationException
     */
    @Override
    public Long createNewClient(ClientEntity client) throws ClientExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<ClientEntity>> constraintViolations = validator.validate(client);
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(client);
                em.flush();

                return client.getId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new ClientExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    /**
     *
     * @return
     */
    @Override
    public List<ClientEntity> retrieveAllClient() {
        Query query = em.createQuery("SELECT c FROM ClientEntity c");
        return query.getResultList();
    }

    /**
     *
     * @param id
     * @return
     * @throws ClientNotFoundException
     */
    @Override
    public ClientEntity retrieveClientByCounterPartyID(String id) throws ClientNotFoundException {
        Query query = em.createQuery("SELECT c FROM ClientEntity c WHERE c.counterPartyId = :id");
        query.setParameter("id", id);

        try {
            return (ClientEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ClientNotFoundException("Client ID " + id + " does not exist");
        }

    }

    /**
     *
     * @param id
     * @param type
     * @return a list of all trades associated with the client after running GTT
     * @throws ClientNotFoundException
     */
    @Override
    public List<TradeEntity> retrieveClientIDResults(String id, FNBEnum type) throws ClientNotFoundException {
        try {
            ClientEntity client = retrieveClientByCounterPartyID(id);
            List<TradeEntity> results = gttCheck(client, type);
            return results;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ClientNotFoundException("Client ID " + id + " does not exist");
        }

    }

    /**
     *
     * @param id
     * @return documents under FNB-EU which failed
     * @throws ClientNotFoundException
     */
    @Override
    public List<DocumentEntity> retrieveDocsOfClientEU(String id) throws ClientNotFoundException {
        try {
            ClientEntity client = retrieveClientByCounterPartyID(id);
            List<DocumentEntity> allDocs = client.getFnbs().get(1).getDocs();

            List<DocumentEntity> failedDocs = new ArrayList<>();
            for (DocumentEntity doc : allDocs) {
                if (!doc.isStatus()) {
                    failedDocs.add(doc);
                }
            }
            return failedDocs;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ClientNotFoundException("Client ID " + id + " does not exist");
        }

    }

    /**
     *
     * @param id
     * @return documents under FNB-UK which failed
     * @throws ClientNotFoundException
     */
    @Override
    public List<DocumentEntity> retrieveDocsOfClientUK(String id) throws ClientNotFoundException {
        try {
            ClientEntity client = retrieveClientByCounterPartyID(id);
            List<DocumentEntity> allDocs = client.getFnbs().get(0).getDocs();

            List<DocumentEntity> failedDocs = new ArrayList<>();
            for (DocumentEntity doc : allDocs) {
                if (!doc.isStatus()) {
                    failedDocs.add(doc);
                }
            }
            return failedDocs;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ClientNotFoundException("Client ID " + id + " does not exist");
        }

    }

    private List<TradeEntity> gttCheck(ClientEntity queryClient, FNBEnum type) {
        List<TradeEntity> allTrades = filterTrade(queryClient.getTrades(), type);
        return allTrades;
    }

    /**
     * Finding Trades which are in scope
     */
    private List<TradeEntity> filterTrade(List<TradeEntity> trades, FNBEnum type) {
        List<TradeEntity> passTrade = new ArrayList<>();

        System.out.println(trades.size());

        for (TradeEntity trade : trades) {
            System.out.println(trade.getId());
            if (!trade.getRegulation().equals("SFT_REPORTING") || trade.getReportingSide() != ReportingSideEnum.FIRM
                    || trade.getJurisdiction().equals("SG") || trade.getSecuritiesFinancingTransactionType() == null
                    || trade.getFnb().getReportingCounterParty() != type) {
                continue;
            } else {
                passTrade.add(trade);
            }
        }
        return passTrade;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ClientEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
