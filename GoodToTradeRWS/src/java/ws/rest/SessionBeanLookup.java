/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.ClientSessionBeanLocal;
import ejb.session.stateless.DocumentSessionBeanLocal;
import ejb.session.stateless.FNBSessionBeanLocal;
import ejb.session.stateless.TradeSessionBeanLocal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Ong Bik Jeun
 */
public class SessionBeanLookup {

    ClientSessionBeanLocal clientSessionBean = lookupClientSessionBeanLocal();

    TradeSessionBeanLocal tradeSessionBean = lookupTradeSessionBeanLocal();

    FNBSessionBeanLocal fNBSessionBean = lookupFNBSessionBeanLocal();

    DocumentSessionBeanLocal documentSessionBean = lookupDocumentSessionBeanLocal();

    private final String ejbModuleJndiPath;

    public SessionBeanLookup() {
        ejbModuleJndiPath = "java:global/GoodToTrade/GoodToTradeRWS/";
    }

    private DocumentSessionBeanLocal lookupDocumentSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (DocumentSessionBeanLocal) c.lookup("java:global/GoodToTrade/GoodToTrade-ejb/DocumentSessionBean!ejb.session.stateless.DocumentSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private FNBSessionBeanLocal lookupFNBSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (FNBSessionBeanLocal) c.lookup("java:global/GoodToTrade/GoodToTrade-ejb/FNBSessionBean!ejb.session.stateless.FNBSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private TradeSessionBeanLocal lookupTradeSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (TradeSessionBeanLocal) c.lookup("java:global/GoodToTrade/GoodToTrade-ejb/TradeSessionBean!ejb.session.stateless.TradeSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private ClientSessionBeanLocal lookupClientSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (ClientSessionBeanLocal) c.lookup("java:global/GoodToTrade/GoodToTrade-ejb/ClientSessionBean!ejb.session.stateless.ClientSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
