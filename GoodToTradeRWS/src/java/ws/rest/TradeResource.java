/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.TradeSessionBeanLocal;
import entitiy.ClientEntity;
import entitiy.DocumentEntity;
import entitiy.FNBEntity;
import entitiy.TradeEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.NoTradesFoundException;

/**
 * REST Web Service
 *
 * @author Ong Bik Jeun
 */
@Path("Trade")
public class TradeResource {

    TradeSessionBeanLocal tradeSessionBean;
    private final SessionBeanLookup sessionBeanLookUp;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of TradeResource
     */
    public TradeResource() {
        sessionBeanLookUp = new SessionBeanLookup();
        tradeSessionBean = sessionBeanLookUp.tradeSessionBean;
    }

    /**
     *
     * @param tradeId
     * @return true if in scope
     */
    @Path("checkGTT")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkGTT(@QueryParam("tradeId") String tradeId) {
        try {
            TradeEntity trade = tradeSessionBean.retreiveTradeByTradeID(tradeId);
            boolean result = tradeSessionBean.checkGTT(trade);
            return Response.status(Response.Status.OK).entity(result).build();
        } catch (NoTradesFoundException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }

    }

    @Path("retrieveTradeById")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveTradeById(@QueryParam("tradeId") String tradeId) {
        try {
            TradeEntity trade = tradeSessionBean.retreiveTradeByTradeID(tradeId);
            trade.setClient(null);
            trade.setFnb(null);

            if (trade != null) {
                return Response.status(Response.Status.OK).entity(trade).build();

            } else {
                return Response.status(Response.Status.OK).entity(null).build();
            }
        } catch (NoTradesFoundException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }

    }

    @Path("retrieveTradeByClient")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveTradeByClient(@QueryParam("clientId") long clientId) {
        List<TradeEntity> tradesAll = tradeSessionBean.retrieveTradesByClient(clientId);

        for (TradeEntity trade : tradesAll) {
            trade.setClient(null);
            trade.setFnb(null);

        }

        GenericEntity<List<TradeEntity>> genericDocs = new GenericEntity<List<TradeEntity>>(tradesAll) {
        };

        return Response.status(Response.Status.OK).entity(genericDocs).build();
    }

    @Path("retrieveClientByTrade")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveClientByTrade(@QueryParam("tradeId") String tradeId) {
        try {
            TradeEntity trade = tradeSessionBean.retreiveTradeByTradeID(tradeId);

            ClientEntity client = trade.getClient();
            client.getFnbs().clear();
            client.getTrades().clear();

            return Response.status(Response.Status.OK).entity(client).build();
        } catch (NoTradesFoundException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }

    }

    @Path("retrieveFnbForTrade")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveFnbForTrade(@QueryParam("tradeId") String tradeId) {
        try {
            TradeEntity trade = tradeSessionBean.retreiveTradeByTradeID(tradeId);
            FNBEntity fnb = trade.getFnb();
            fnb.getDocs().clear();
            fnb.setClient(null);
            fnb.setTrade(null);
            return Response.status(Response.Status.OK).entity(fnb).build();
        } catch (NoTradesFoundException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }

    }

    @Path("getFailedDocs")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFailedDocs(@QueryParam("tradeId") String tradeId) {
        try {
            TradeEntity trade = tradeSessionBean.retreiveTradeByTradeID(tradeId);
            List<DocumentEntity> docs = trade.getFnb().getDocs();

            List<DocumentEntity> failedDocs = new ArrayList<>();
            for (DocumentEntity doc : docs) {
                if (!doc.isStatus()) {
                    doc.setFnb(null);
                    failedDocs.add(doc);
                }
            }
            GenericEntity<List<DocumentEntity>> genericDocs = new GenericEntity<List<DocumentEntity>>(failedDocs) {
            };

            return Response.status(Response.Status.OK).entity(genericDocs).build();
        } catch (NoTradesFoundException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("getClientsWithFailedDate")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientsWithFailedDate(@QueryParam("date") String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            List<TradeEntity> allTrades = tradeSessionBean.retrieveTradesByDate(formatter.parse(date));
            List<ClientEntity> clients = new ArrayList<>();

            for (TradeEntity trade : allTrades) {
                ClientEntity client = trade.getClient();
                if (!clients.contains(client)) {
                    clients.add(client);
                }
            }

            for (ClientEntity client : clients) {
                client.getFnbs().clear();
                client.getTrades().clear();
            }

            GenericEntity<List<ClientEntity>> genericDocs = new GenericEntity<List<ClientEntity>>(clients) {

            };
            return Response.status(Response.Status.OK).entity(genericDocs).build();
        } catch (NoTradesFoundException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
}
