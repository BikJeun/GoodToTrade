/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entitiy.ClientEntity;
import entitiy.DocumentEntity;
import entitiy.FNBEntity;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CreateDocumentException;
import util.exception.DocumentExistException;
import util.exception.FNBNotFoundException;
import util.exception.InputDataValidationException;
import javax.persistence.PersistenceException;
import util.enumeration.FNBEnum;
import util.exception.AlreadyAssociatedException;
import util.exception.AssociationException;
import util.exception.ClientNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ong Bik Jeun
 */
@Stateless
public class DocumentSessionBean implements DocumentSessionBeanLocal {

    @EJB
    private ClientSessionBeanLocal clientSessionBean;

    @EJB
    private FNBSessionBeanLocal fNBSessionBean;

    @PersistenceContext(unitName = "GoodToTrade-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public DocumentSessionBean() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    /**
     *
     * @param doc
     * @return
     * @throws DocumentExistException
     * @throws UnknownPersistenceException
     * @throws CreateDocumentException
     * @throws InputDataValidationException
     */
    @Override
    public Long createNewDoc(DocumentEntity doc) throws DocumentExistException, UnknownPersistenceException, CreateDocumentException, InputDataValidationException {
        Set<ConstraintViolation<DocumentEntity>> constraintViolations = validator.validate(doc);
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(doc);
                em.flush();

                return doc.getId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new DocumentExistException();
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
     * @param docID
     * @param clientID
     * @param type
     * @throws AssociationException
     * @throws AlreadyAssociatedException
     */
    @Override
    public void associateDocToClient(Long docID, String clientID, FNBEnum type) throws AssociationException, AlreadyAssociatedException {
        try {
            ClientEntity client = clientSessionBean.retrieveClientByCounterPartyID(clientID);
            DocumentEntity doc = retrieveDocByID(docID);
            Long fnbId = null;

            for (FNBEntity fnbs : client.getFnbs()) {
                if (fnbs.getReportingCounterParty() == type) {
                    fnbId = fnbs.getId();
                    break;
                }
            }
            FNBEntity fnb = fNBSessionBean.retrieveFNBByID(fnbId);
            if (client.getFnbs().contains(doc)) {
                throw new AlreadyAssociatedException("Doc is already associated with client");
            } else {
                fnb.getDocs().add(doc);
                doc.setFnb(fnb);
            }
        } catch (ClientNotFoundException ex) {
            throw new AssociationException(ex.getMessage());
        } catch (FNBNotFoundException ex) {
            throw new AssociationException(ex.getMessage());
        }

    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public DocumentEntity retrieveDocByID(Long id) {
        return em.find(DocumentEntity.class, id);
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<DocumentEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
