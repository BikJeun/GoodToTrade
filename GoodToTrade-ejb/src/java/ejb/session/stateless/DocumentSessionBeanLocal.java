/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entitiy.DocumentEntity;
import javax.ejb.Local;
import util.enumeration.FNBEnum;
import util.exception.AlreadyAssociatedException;
import util.exception.AssociationException;
import util.exception.CreateDocumentException;
import util.exception.DocumentExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ong Bik Jeun
 */
@Local
public interface DocumentSessionBeanLocal {

    public DocumentEntity retrieveDocByID(Long id);

    public Long createNewDoc(DocumentEntity doc) throws DocumentExistException, UnknownPersistenceException, CreateDocumentException, InputDataValidationException;

    public void associateDocToClient(Long docID, String clientID, FNBEnum type) throws AssociationException, AlreadyAssociatedException;

}
