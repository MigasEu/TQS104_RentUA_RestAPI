package pt.ua.tqs104_rentua_restapi.rest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.Json;
import javax.json.JsonReader;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;
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
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import javax.json.JsonObject;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Ignore;
import pt.ua.tqs104_rentua_restapi.ent.RentUser;
import pt.ua.tqs104_rentua_restapi.facade.UserFacade;
import pt.ua.tqs104_rentua_restapi.filter.JWTTokenNeeded;
import pt.ua.tqs104_rentua_restapi.util.PasswordUtils;

@RunWith(Arquillian.class)
@RunAsClient
public class RentUserFacadeRESTTest {

    // ======================================
    // =             Attributes             =
    // ======================================
    private static final RentUser TEST_USER = new RentUser("login", "password");
    private static String userId;

    // ======================================
    // =          Injection Points          =
    // ======================================
    @ArquillianResource
    private URI baseURL;

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

    // ======================================
    // =          Lifecycle methods         =
    // ======================================
    // ======================================
    // =            Test methods            =
    // ======================================
    @Test
    public void shouldFailLogin() {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(UriBuilder.fromUri(baseURL).path("rest/user/login").build().toString());

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("param-1", "12345"));
        params.add(new BasicNameValuePair("param-2", "Hello!"));

        try {
            //Execute and get the response.
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);
            assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatusLine().getStatusCode());
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    @InSequence(1)    
    public void shouldGetAllUsers() throws Exception {
        HttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(UriBuilder.fromUri(baseURL).path("rest/user").build().toString());
        HttpResponse response = client.execute(request);
        assertEquals(Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());
    }
    
    @Test
    @InSequence(2)
    public void shouldCreateUser() throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(UriBuilder.fromUri(baseURL).path("rest/user").build().toString());
        httppost.addHeader("content-type", "application/json");
        httppost.setEntity(new StringEntity("{\"name\":\"ze\",\"password\":\"pass\"}"));
        try {
            //Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            assertEquals(Status.CREATED.getStatusCode(), response.getStatusLine().getStatusCode());
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    @InSequence(3)
    public void shouldGetAlreadyCreatedUser() throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(UriBuilder.fromUri(baseURL).path("rest/user/1").build().toString());

        try {
            //Execute and get the response.
            HttpResponse response = httpclient.execute(httpget);
            JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
            assertEquals(Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());
            assertEquals("1", jsonObject.getString("id"));
            assertEquals("ze", jsonObject.getString("name"));
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
    }
}
