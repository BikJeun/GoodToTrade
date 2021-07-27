/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entitiy.FNBEntity;
import java.util.List;
import javax.ejb.Local;
import util.enumeration.FNBEnum;
import util.exception.CreateFNBException;
import util.exception.FNBExistException;
import util.exception.FNBNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ong Bik Jeun
 */
@Local
public interface FNBSessionBeanLocal {

    public List<FNBEntity> retrieveAllFNB();

    public FNBEntity retrieveFNBByID(Long id) throws FNBNotFoundException;

    public Long createNewFnB(FNBEntity fnb, String clientID) throws FNBExistException, UnknownPersistenceException, InputDataValidationException, CreateFNBException;

    public FNBEntity retrieveByClientNType(String clientid, FNBEnum type) throws FNBNotFoundException;

    public FNBEntity retrieveByTradeId(Long trade) throws FNBNotFoundException;

    public List<FNBEntity> retrieveByClientId(Long id);

}
