/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.tqs104_rentua_restapi.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.security.Key;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;
import pt.ua.tqs104_rentua_restapi.ent.Property;
import pt.ua.tqs104_rentua_restapi.ent.RentUser;
import pt.ua.tqs104_rentua_restapi.ent.Rental;
import pt.ua.tqs104_rentua_restapi.facade.PropertyFacade;
import pt.ua.tqs104_rentua_restapi.facade.RentalFacade;
import pt.ua.tqs104_rentua_restapi.filter.JWTTokenNeeded;
import pt.ua.tqs104_rentua_restapi.util.SimpleKeyGenerator;

/**
 *
 * @author migas
 */
@Path("/rental")
//@javax.enterprise.context.RequestScoped
@Stateless
@Transactional
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class RentalFacadeREST {

    @Inject
    RentalFacade rentF;
    @Inject
    PropertyFacade propF;
    @PersistenceContext
    private EntityManager em;

    @POST
    @JWTTokenNeeded
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response create(@HeaderParam("Authorization") String token,
            @FormParam("startDate") String startDate, @FormParam("endDate") String endDate,
            @FormParam("propertyId") int propertyId) {
        String justTheToken = token.substring("Bearer".length()).trim();
        Key key = new SimpleKeyGenerator().generateKey();
        String name = Jwts.parser().setSigningKey(key).parseClaimsJws(justTheToken).getBody().getSubject();

        TypedQuery<RentUser> query = em.createNamedQuery(RentUser.FIND_BY_LOGIN, RentUser.class);
        query.setParameter("login", name);
        try {
            if (!startDate.matches("^[0-9]{4}-[1-2]{1}$") || !endDate.matches("^[0-9]{4}-[1-2]{1}$") || startDate.compareTo(endDate)>0) {
                return Response.status(422).build();
            }

            Property property = propF.find(new Long(propertyId));
            if (property == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            TypedQuery<Rental> query2 = em.createNamedQuery(Rental.FIND_BY_PROPERTY_FROM_TO, Rental.class);
            query2.setParameter("propertyId", property);
            query2.setParameter("startDate", startDate);
            query2.setParameter("endDate", endDate);
            if (query2.getResultList().size() > 0) {
                return Response.status(Response.Status.CONFLICT).build();
            }
            Rental rental = new Rental();
            rental.setStartDate(startDate);
            rental.setEndDate(endDate);
            rental.setRenter(query.getSingleResult());
            rental.setProperty(property);
            rentF.create(rental);
            em.flush();
            return Response.status(Response.Status.CREATED).header("rantalId", String.valueOf(rental.getId())).build();
        } catch (javax.persistence.NoResultException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String find(@PathParam("id") Long id) {
        ObjectMapper mapper = new ObjectMapper();
        Rental prop = rentF.find(id);
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
            return mapper.writeValueAsString(rentF.findAll());
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
        TypedQuery<Rental> query2 = em.createNamedQuery(Rental.FIND_BY_USER, Rental.class);
        try {
            query2.setParameter("renterId", query.getSingleResult());
            List<Rental> userRentals = query2.getResultList();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(userRentals);
        } catch (JsonProcessingException ex) {
            throw new InternalServerErrorException();
        } catch (javax.persistence.NoResultException ex) {
            throw new NotFoundException();
        }
    }

    @GET
    @Path("property/{propertyId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String findByProperty(@PathParam("propertyId") long propertyId) {
        TypedQuery<Rental> query = em.createNamedQuery(Rental.FIND_BY_PROPERTY, Rental.class);
        try {
            query.setParameter("propertyId", propF.find(propertyId));
            List<Rental> propRentals = query.getResultList();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(propRentals);
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
        return String.valueOf(rentF.count());
    }
}
