/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.tqs104_rentua_restapi.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import pt.ua.tqs104_rentua_restapi.ent.Property;
import pt.ua.tqs104_rentua_restapi.ent.RentUser;
import pt.ua.tqs104_rentua_restapi.facade.PropertyFacade;
import pt.ua.tqs104_rentua_restapi.facade.UserFacade;
import pt.ua.tqs104_rentua_restapi.filter.JWTTokenNeeded;
import pt.ua.tqs104_rentua_restapi.util.SimpleKeyGenerator;

/**
 *
 * @author migas
 */
@Stateless
@Path("/property")
public class PropertyFacadeREST {

    @Inject
    PropertyFacade propF;
    @Inject
    UserFacade userF;

    @Inject
    private transient Logger logger;
    
    @PersistenceContext
    private EntityManager em;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @JWTTokenNeeded
    public Response create(@HeaderParam("Authorization") String token, Property entity) {
        String justTheToken = token.substring("Bearer".length()).trim();
        Key key = new SimpleKeyGenerator().generateKey();
        String name = Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getBody().getSubject();
        
        logger.info("create Property: "+entity.getTitle()+"+"+entity.getPrice()+"-"+entity.getType());

        TypedQuery<RentUser> query = em.createNamedQuery(RentUser.FIND_BY_LOGIN, RentUser.class);
        query.setParameter("login", name);
        try {
            entity.setOwner(query.getSingleResult());
        } catch (javax.persistence.NoResultException ex) {
            return Response.status(Status.NOT_FOUND).build();
        }
        em.persist(entity);
        em.flush();
        logger.info("prop.id = "+entity.getId());
        return Response.status(Status.CREATED).header("propertyId", String.valueOf(entity.getId())).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String find(@PathParam("id") Long id) {
        ObjectMapper mapper = new ObjectMapper();
        Property prop = propF.find(id);
        if (prop == null) {
            throw new NotFoundException();
        }
        try {
            return mapper.writeValueAsString(prop);
        } catch (JsonProcessingException ex) {
            throw new InternalServerErrorException();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String findAll() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(propF.findAll());
        } catch (JsonProcessingException ex) {
            throw new InternalServerErrorException();
        }
    }

    @GET
    @Path("user/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String findByUser(@PathParam("name") String name) {
        TypedQuery<RentUser> query = em.createNamedQuery(RentUser.FIND_BY_LOGIN, RentUser.class);
        query.setParameter("login", name);
        TypedQuery<Property> query2 = em.createNamedQuery(Property.FIND_BY_USER, Property.class);

        try {
            query2.setParameter("ownerId", query.getSingleResult());
            List<Property> userProps = query2.getResultList();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(userProps);
        } catch (JsonProcessingException ex) {
            throw new InternalServerErrorException();
        } catch (javax.persistence.NoResultException ex) {
            throw new NotFoundException();
        }
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(propF.count());
    }

}
