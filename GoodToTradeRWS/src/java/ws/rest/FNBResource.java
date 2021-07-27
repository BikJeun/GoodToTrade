/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.FNBSessionBeanLocal;
import entitiy.FNBEntity;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.FNBNotFoundException;

/**
 * REST Web Service
 *
 * @author Ong Bik Jeun
 */
@Path("FNB")
public class FNBResource {

    FNBSessionBeanLocal fNBSessionBean;
    private final SessionBeanLookup sessionBeanLookUp;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of FNBResource
     */
    public FNBResource() {
        sessionBeanLookUp = new SessionBeanLookup();
        fNBSessionBean = sessionBeanLookUp.fNBSessionBean;
    }

    @Path("retrieveFNBFromTrade")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveFNBFromTrade(@QueryParam("tradeId") Long tradeId) {
        try {
            FNBEntity fnb = fNBSessionBean.retrieveByTradeId(tradeId);

            fnb.getDocs().clear();
            fnb.setClient(null);
            fnb.setTrade(null);
            return Response.status(Response.Status.OK).entity(fnb).build();
        } catch (FNBNotFoundException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("retrieveFNBFromClient")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveFNBFromClient(@QueryParam("clientId") Long clientId) {
        try {
            List<FNBEntity> fnbs = fNBSessionBean.retrieveByClientId(clientId);
            for (FNBEntity fnb : fnbs) {
                fnb.getDocs().clear();
                fnb.setClient(null);
                fnb.setTrade(null);
            }

            GenericEntity<List<FNBEntity>> genericDocs = new GenericEntity<List<FNBEntity>>(fnbs) {
            };

            return Response.status(Response.Status.OK).entity(genericDocs).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
}
