/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entitiy.ClientEntity;
import entitiy.FNBEntity;
import entitiy.TradeEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
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
import util.enumeration.FNBEnum;
import util.enumeration.ReportingSideEnum;
import util.exception.ClientNotFoundException;
import util.exception.CreateNewTradeException;
import util.exception.FNBNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.NoTradesFoundException;

/**
 *
 * @author Ong Bik Jeun
 */
@Stateless
public class TradeSessionBean implements TradeSessionBeanLocal {

    @EJB
    private FNBSessionBeanLocal fNBSessionBean;

    @EJB
    private ClientSessionBeanLocal clientSessionBean;

    @PersistenceContext(unitName = "GoodToTrade-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public TradeSessionBean() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    /**
     *
     * @param counterPartyId
     * @param fnbId
     * @param trade
     * @return id of new trade
     * @throws CreateNewTradeException
     * @throws InputDataValidationException
     */
    @Override
    public Long createNewTrade(String counterPartyId, Long fnbId, TradeEntity trade) throws CreateNewTradeException, InputDataValidationException {
        Set<ConstraintViolation<TradeEntity>> constraintViolations = validator.validate(trade);
        if (constraintViolations.isEmpty()) {
            if (trade != null) {

                try {
                    ClientEntity client = clientSessionBean.retrieveClientByCounterPartyID(counterPartyId);
                    FNBEntity fnb = fNBSessionBean.retrieveFNBByID(fnbId);
                    trade.setClient(client);
                    trade.setFnb(fnb);

                    client.getTrades().add(trade);
                    fnb.setTrade(trade);
                    em.persist(trade);
                    em.flush();
                    return trade.getId();
                } catch (ClientNotFoundException ex) {
                    throw new CreateNewTradeException("Error: Client not found!");
                } catch (FNBNotFoundException ex) {
                    throw new CreateNewTradeException("Error: FNB not found!");
                }

            } else {
                throw new CreateNewTradeException("Error: Trade provided is null");
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    /**
     *
     * @return list of all trades in database
     */
    @Override
    public List<TradeEntity> retrieveAllTrades() {

        Query query = em.createQuery("SELECT t FROM TradeEntity t");
        return query.getResultList();

    }

    /**
     *
     * @param tradeID
     * @return trade
     * @throws NoTradesFoundException
     */
    @Override
    public TradeEntity retreiveTradeByTradeID(String tradeID) throws NoTradesFoundException {
        try {
            Query query = em.createQuery("SELECT t FROM TradeEntity t WHERE t.tradeID = :tradeID");
            query.setParameter("tradeID", tradeID);

            TradeEntity trade = (TradeEntity) query.getSingleResult();

            return trade;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new NoTradesFoundException("No trades found!");
        }
    }

    /**
     *
     * @param id
     * @return list of all trades by Client which are in scope
     */
    @Override
    public List<TradeEntity> retrieveTradesByClient(long id) {
        Query query = em.createQuery("SELECT t FROM TradeEntity t JOIN t.client c WHERE c.id = :id");
        query.setParameter("id", id);
        List<TradeEntity> trades = query.getResultList();
        List<TradeEntity> tradeInScope = new ArrayList<>();
        int i = 1;
        for (TradeEntity trade : trades) {

            System.out.println(i);
            if (checkGTT(trade)) {
                tradeInScope.add(trade);
            }
            i++;
        }

        return tradeInScope;

    }

    /**
     *
     * @param date
     * @return list of all trades done on that date which are in scope
     * @throws NoTradesFoundException
     */
    @Override
    public List<TradeEntity> retrieveTradesByDate(Date date) throws NoTradesFoundException {
        try {
            Query query = em.createQuery("SELECT t FROM TradeEntity t WHERE t.date = :date");
            query.setParameter("date", date);
            List<TradeEntity> trades = query.getResultList();

            Collections.sort(trades, new Comparator<TradeEntity>() {
                public int compare(TradeEntity t1, TradeEntity t2) {
                    if (t1.getDate() == null || t2.getDate() == null) {
                        return 0;

                    }
                    return t1.getClient().getCounterPartyId().compareTo(t2.getClient().getCounterPartyId());
                }
            });
            List<TradeEntity> tradeInScope = new ArrayList<>();

            for (TradeEntity trade : trades) {
                if (checkGTT(trade)) {
                    tradeInScope.add(trade);
                }
            }

            return tradeInScope;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new NoTradesFoundException("No trades found!");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<TradeEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

    /**
     *
     * @param trade
     * @return true if trade in scope
     */
    @Override
    public boolean checkGTT(TradeEntity trade) {
        if (!trade.getRegulation().equals("SFT_REPORTING")
                || trade.getJurisdiction().equals("SG") || trade.getSecuritiesFinancingTransactionType() == null
                || trade.getFnb().getReportingCounterParty() == FNBEnum.SG) {
            System.out.println("Trade not in scope!");
            return false;

        } else {
            return true;
        }
    }
}
