/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entitiy.TradeEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewTradeException;
import util.exception.InputDataValidationException;
import util.exception.NoTradesFoundException;

/**
 *
 * @author Ong Bik Jeun
 */
@Local
public interface TradeSessionBeanLocal {

    public Long createNewTrade(String counterPartyId, Long fnbId, TradeEntity trade) throws CreateNewTradeException, InputDataValidationException;

    public List<TradeEntity> retrieveAllTrades();

    public TradeEntity retreiveTradeByTradeID(String tradeID) throws NoTradesFoundException;

    public List<TradeEntity> retrieveTradesByDate(Date date) throws NoTradesFoundException;

    public boolean checkGTT(TradeEntity trade);

    public List<TradeEntity> retrieveTradesByClient(long id);

}
