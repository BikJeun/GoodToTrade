/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.ClientSessionBeanLocal;
import entitiy.ClientEntity;
import entitiy.DocumentEntity;
import entitiy.FNBEntity;
import entitiy.TradeEntity;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.enumeration.DocEnum;
import util.enumeration.FNBEnum;
import util.enumeration.ReportingSideEnum;
import util.exception.ClientNotFoundException;

/**
 * REST Web Service
 *
 * @author Ong Bik Jeun
 */
@Path("Client")
public class ClientResource {

    ClientSessionBeanLocal clientSessionBean;
    private final SessionBeanLookup sessionBeanLookUp;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ClientResource
     */
    public ClientResource() {
        sessionBeanLookUp = new SessionBeanLookup();
        clientSessionBean = sessionBeanLookUp.clientSessionBean;
    }

    @Path("retrieveAllClient")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllClient() {
        try {
            List<ClientEntity> clients = clientSessionBean.retrieveAllClient();
            for (ClientEntity client : clients) {
                client.getFnbs().clear();
                client.getTrades().clear();
            }

            GenericEntity<List<ClientEntity>> genericClient = new GenericEntity<List<ClientEntity>>(clients) {

            };
            return Response.status(Response.Status.OK).entity(genericClient).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }

    }

    /**
     *
     * @param clientId
     * @param type
     * @return a list of trade belonging to the client based on FNB
     */
    @Path("retrieveClientById")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveClientById(@QueryParam("clientID") String clientId, @QueryParam("type") String type) {
        try {
            FNBEnum typeWanted = FNBEnum.EU;
            if (type.equals("UK")) {
                typeWanted = FNBEnum.UK;
            }

            List<TradeEntity> trades = clientSessionBean.retrieveClientIDResults(clientId, typeWanted);
            for (TradeEntity trade : trades) {
                trade.setClient(null);
                trade.setFnb(null);

            }
            GenericEntity<List<TradeEntity>> genericTrade = new GenericEntity<List<TradeEntity>>(trades) {

            };
            return Response.status(Response.Status.OK).entity(genericTrade).build();
        } catch (ClientNotFoundException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("retrieveFNBByIdEU")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveFNBByIdEU(@QueryParam("clientID") String clientId) {
        try {
            List<DocumentEntity> docs = clientSessionBean.retrieveDocsOfClientEU(clientId);
            for (DocumentEntity doc : docs) {
                doc.setFnb(null);
            }
            GenericEntity<List<DocumentEntity>> genericDocs = new GenericEntity<List<DocumentEntity>>(docs) {

            };
            return Response.status(Response.Status.OK).entity(genericDocs).build();
        } catch (ClientNotFoundException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("retrieveFNBByIdUK")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveFNBByIdUK(@QueryParam("clientID") String clientId) {
        try {
            List<DocumentEntity> docs = clientSessionBean.retrieveDocsOfClientUK(clientId);
            for (DocumentEntity doc : docs) {
                doc.setFnb(null);
            }
            GenericEntity<List<DocumentEntity>> genericDocs = new GenericEntity<List<DocumentEntity>>(docs) {

            };
            return Response.status(Response.Status.OK).entity(genericDocs).build();
        } catch (ClientNotFoundException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

}
