/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entitiy.ClientEntity;
import entitiy.DocumentEntity;
import entitiy.TradeEntity;
import java.util.EnumMap;
import java.util.List;
import javax.ejb.Local;
import util.enumeration.DocEnum;
import util.enumeration.FNBEnum;
import util.exception.ClientExistException;
import util.exception.ClientNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ong Bik Jeun
 */
@Local
public interface ClientSessionBeanLocal {

    public Long createNewClient(ClientEntity client) throws ClientExistException, UnknownPersistenceException, InputDataValidationException;

    public List<ClientEntity> retrieveAllClient();

    public ClientEntity retrieveClientByCounterPartyID(String id) throws ClientNotFoundException;

    public List<DocumentEntity> retrieveDocsOfClientEU(String id) throws ClientNotFoundException;

    public List<DocumentEntity> retrieveDocsOfClientUK(String id) throws ClientNotFoundException;

    public List<TradeEntity> retrieveClientIDResults(String id, FNBEnum type) throws ClientNotFoundException;

}
