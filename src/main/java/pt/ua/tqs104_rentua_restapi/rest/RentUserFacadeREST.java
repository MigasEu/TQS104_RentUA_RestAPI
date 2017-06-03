/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.tqs104_rentua_restapi.rest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import pt.ua.tqs104_rentua_restapi.util.KeyGenerator;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ws.rs.core.GenericEntity;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import pt.ua.tqs104_rentua_restapi.ent.RentUser;
import pt.ua.tqs104_rentua_restapi.util.JaxbList;
import pt.ua.tqs104_rentua_restapi.util.PasswordUtils;

/**
 *
 * @author migas
 */
@Path("/user")
@javax.enterprise.context.RequestScoped
@Transactional
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class RentUserFacadeREST {

    @Context
    private UriInfo uriInfo;

    @Inject
    private transient Logger logger;

    @Inject
    private KeyGenerator keyGenerator;

    @PersistenceContext
    private EntityManager em;

    // ======================================
    // =          Business methods          =
    // ======================================
    @POST
    @Path("login")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("name") String login,
            @FormParam("password") String password) {

        try {

            logger.info("#### login/password : " + login + "/" + password);

            // Authenticate the user using the credentials provided
            authenticate(login, password);

            // Issue a token for the user
            String token = issueToken(login);

            // Return the token on the response
            return Response.ok().header(AUTHORIZATION, "Bearer " + token).build();

        } catch (Exception e) {
            return Response.status(UNAUTHORIZED).build();
        }
    }

    private void authenticate(String login, String password) throws Exception {
        TypedQuery<RentUser> query = em.createNamedQuery(RentUser.FIND_BY_LOGIN_PASSWORD, RentUser.class);
        query.setParameter("login", login);
        query.setParameter("password", PasswordUtils.digestPassword(password));
        RentUser user = query.getSingleResult();

        if (user == null) {
            throw new SecurityException("Invalid user/password");
        }
    }

    private String issueToken(String login) {
        Key key = keyGenerator.generateKey();
        String jwtToken = Jwts.builder()
                .setSubject(login)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(toDate(LocalDateTime.now().plusMinutes(15L)))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        logger.info("#### generating token for a key : " + jwtToken + " - " + key);
        return jwtToken;

    }

    @POST
    public Response create(RentUser user) {
        TypedQuery<RentUser> query = em.createNamedQuery(RentUser.FIND_BY_LOGIN, RentUser.class);
        query.setParameter("login", user.getName());
        if (!query.getResultList().isEmpty()) {
            throw new ForbiddenException("User with that name already exists");         //If user already exists
        }
        em.persist(user);
        return Response.created(uriInfo.getAbsolutePathBuilder().path("/").build()).build();
    }

    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") Long id) {
        RentUser user = em.find(RentUser.class, id);

        if (user == null) {
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(user).build();
    }

    @GET
    public List<RentUser> findAllUsers() {
        TypedQuery<RentUser> query = em.createNamedQuery(RentUser.FIND_ALL, RentUser.class);
        List<RentUser> allUsers = query.getResultList();
        
        return allUsers;
    }

    // ======================================
    // =          Private methods           =
    // ======================================
    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
