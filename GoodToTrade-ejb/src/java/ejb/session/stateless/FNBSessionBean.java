/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entitiy.ClientEntity;
import entitiy.FNBEntity;
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
import util.exception.ClientNotFoundException;
import util.exception.CreateFNBException;
import util.exception.FNBExistException;
import util.exception.FNBNotFoundException;
import util.exception.InputDataValidationException;
import javax.persistence.PersistenceException;
import util.enumeration.FNBEnum;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ong Bik Jeun
 */
@Stateless
public class FNBSessionBean implements FNBSessionBeanLocal {

    @EJB
    private ClientSessionBeanLocal clientSessionBean;

    @PersistenceContext(unitName = "GoodToTrade-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public FNBSessionBean() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    /**
     *
     * @param fnb
     * @param clientID
     * @return
     * @throws FNBExistException
     * @throws UnknownPersistenceException
     * @throws InputDataValidationException
     * @throws CreateFNBException
     */
    @Override
    public Long createNewFnB(FNBEntity fnb, String clientID) throws FNBExistException, UnknownPersistenceException, InputDataValidationException, CreateFNBException {
        Set<ConstraintViolation<FNBEntity>> constraintViolations = validator.validate(fnb);
        if (constraintViolations.isEmpty()) {
            try {
                ClientEntity client = clientSessionBean.retrieveClientByCounterPartyID(clientID);
                fnb.setClient(client);
                client.getFnbs().add(fnb);

                em.persist(fnb);
                em.flush();

                return fnb.getId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new FNBExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (ClientNotFoundException ex) {
                throw new CreateFNBException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    /**
     *
     * @return all FNB entity in database
     */
    @Override
    public List<FNBEntity> retrieveAllFNB() {
        Query query = em.createQuery("SELECT f FROM FNBEntity f");
        return query.getResultList();
    }

    /**
     *
     * @param id
     * @return
     * @throws FNBNotFoundException
     */
    @Override
    public FNBEntity retrieveFNBByID(Long id) throws FNBNotFoundException {
        FNBEntity fnb = em.find(FNBEntity.class, id);

        if (fnb != null) {
            return fnb;
        } else {
            throw new FNBNotFoundException("FNB ID " + id + " does not exist!");
        }
    }

    /**
     *
     * @param clientid
     * @param type
     * @return FNB entity based on the type and which client it belongs to
     * @throws FNBNotFoundException
     */
    @Override
    public FNBEntity retrieveByClientNType(String clientid, FNBEnum type) throws FNBNotFoundException {
        try {
            Query query = em.createQuery("SELECT f FROM FNBEntity f JOIN f.client c WHERE c.counterPartyId = :clientId AND f.reportingCounterParty = :type");
            query.setParameter("clientId", clientid);
            query.setParameter("type", type);

            return (FNBEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new FNBNotFoundException("clientid: " + clientid + " has no FNBEntity of type: " + type);
        }
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public List<FNBEntity> retrieveByClientId(Long id) {
        Query query = em.createQuery("SELECT f FROM FNBEntity f JOIN f.client c WHERE c.id = :id");
        query.setParameter("id", id);

        return query.getResultList();
    }

    /**
     *
     * @param trade
     * @return fnb entity for the trade
     * @throws FNBNotFoundException
     */
    @Override
    public FNBEntity retrieveByTradeId(Long trade) throws FNBNotFoundException {
        try {
            Query query = em.createQuery("SELECT f FROM FNBEntity f JOIN f.trade t WHERE t.id = :trade");
            query.setParameter("trade", trade);

            return (FNBEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new FNBNotFoundException("FNB for Trade " + trade + " not found");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<FNBEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
