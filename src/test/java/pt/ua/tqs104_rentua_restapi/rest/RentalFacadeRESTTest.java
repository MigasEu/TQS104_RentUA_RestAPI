/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.tqs104_rentua_restapi.rest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import javax.ejb.embeddable.EJBContainer;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import pt.ua.tqs104_rentua_restapi.ent.RentUser;
import pt.ua.tqs104_rentua_restapi.facade.UserFacade;
import pt.ua.tqs104_rentua_restapi.filter.JWTTokenNeeded;
import pt.ua.tqs104_rentua_restapi.util.PasswordUtils;

/**
 *
 * @author migas
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RentalFacadeRESTTest {
    
    @ArquillianResource
    private URI baseURL;

    private static String token;
    private static long propertyId;
    private static long rentalId;

    // ======================================
    // =         Deployment methods         =
    // ======================================
    @Deployment(testable = false)
    public static WebArchive createDeployment() {

        // Import Maven runtime dependencies
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeDependencies().resolve().withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class)
                .addPackage(RentUser.class.getPackage())
                .addPackage(PasswordUtils.class.getPackage())
                .addPackage(JWTTokenNeeded.class.getPackage())
                .addPackage(RentUserFacadeREST.class.getPackage())
                .addPackage(UserFacade.class.getPackage())
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(files);
    }

    /**
     * Test of create method, of class PropertyFacadeREST.
     */
    @Test
    @InSequence(1)
    public void shouldCreateUser() throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(UriBuilder.fromUri(baseURL).path("rest/user").build().toString());
        httppost.addHeader("content-type", "application/json");
        httppost.setEntity(new StringEntity("{\"name\":\"ze\",\"password\":\"pass\"}"));
        try {
            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatusLine().getStatusCode());
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    @InSequence(2)
    public void shouldLoginUser() throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(UriBuilder.fromUri(baseURL).path("rest/user/login").build().toString());
        httppost.addHeader("content-type", APPLICATION_FORM_URLENCODED);
        ArrayList<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("name", "ze"));
        postParameters.add(new BasicNameValuePair("password", "pass"));
        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
        try {
            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());
            token = response.getFirstHeader(AUTHORIZATION).getValue();
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    @InSequence(3)
    public void createProperty() throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(UriBuilder.fromUri(baseURL).path("rest/property").build().toString());
        httppost.addHeader("content-type", "application/json");
        httppost.setHeader(AUTHORIZATION, token);
        httppost.setEntity(new StringEntity("{\"title\":\"casa\",\"price\":800,\"type\":0}"));
        try {
            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatusLine().getStatusCode());
            propertyId = Long.parseLong(response.getFirstHeader("propertyId").getValue());
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    /**
     * Test of create method, of class RentalFacadeREST.
     */
    @Test
    @InSequence(4)
    public void testCreate() throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(UriBuilder.fromUri(baseURL).path("rest/rental").build().toString());
        httppost.addHeader("content-type", APPLICATION_FORM_URLENCODED);
        httppost.setHeader(AUTHORIZATION, token);
        ArrayList<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("startDate", "2017-1"));
        postParameters.add(new BasicNameValuePair("endDate", "2017-2"));
        postParameters.add(new BasicNameValuePair("propertyId", String.valueOf(propertyId)));
        httppost.setEntity(new UrlEncodedFormEntity(postParameters));
        try {
            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatusLine().getStatusCode());
            rentalId = Long.parseLong(response.getFirstHeader("rantalId").getValue());
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    /**
     * Test of find method, of class RentalFacadeREST.
     */
    @Test
    @InSequence(5)
    public void testFind() throws Exception {
        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(UriBuilder.fromUri(baseURL).path("rest/rental/"+rentalId).build().toString());
        HttpResponse response = client.execute(request);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());
    }

    /**
     * Test of findAll method, of class RentalFacadeREST.
     */
    @Test
    public void testFindAll() throws Exception {
        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(UriBuilder.fromUri(baseURL).path("rest/rental").build().toString());
        HttpResponse response = client.execute(request);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());
    }

    /**
     * Test of findByUser method, of class RentalFacadeREST.
     */
    @Test
    @InSequence(6)
    public void testFindByUser() throws Exception {
        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(UriBuilder.fromUri(baseURL).path("rest/rental/user/ze").build().toString());
        HttpResponse response = client.execute(request);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());
    }

    /**
     * Test of findByProperty method, of class RentalFacadeREST.
     */
    @Test
    @InSequence(7)
    public void testFindByProperty() throws Exception {
        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(UriBuilder.fromUri(baseURL).path("rest/rental/property/"+propertyId).build().toString());
        HttpResponse response = client.execute(request);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());
    }    
}
