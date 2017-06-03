package pt.ua.tqs104_rentua_restapi.ent;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.StringReader;
import java.net.URI;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import javax.json.JsonObject;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;

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
                .addPackage("pt.ua.tqs104_rentua_restapi.ent")
                .addPackage("pt.ua.tqs104_rentua_restapi.util")
                .addPackage("pt.ua.tqs104_rentua_restapi.filter")
                .addPackage("pt.ua.tqs104_rentua_restapi.rest")
                .addPackage("pt.ua.tqs104_rentua_restapi.facade")
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
        /*ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource webResource = client.resource(UriBuilder.fromUri(baseURL).path("rest/user/login").build());
        MultivaluedMap formData = new MultivaluedMapImpl();
        formData.add("login", "ze");
        formData.add("password", "pass");
        try {
            ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
            assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        } catch (UniformInterfaceException e) {
            assertEquals(Status.UNAUTHORIZED.getStatusCode(), e.getResponse().getStatus());
            assertNull(e.getResponse().getHeaders().get(HttpHeaders.AUTHORIZATION));
        }*/
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

    // ======================================
    // =           Private methods          =
    // ======================================
    private String getUserId(Response response) {
        String location = response.getHeaderString("location");
        return location.substring(location.lastIndexOf("/") + 1);
    }

    private static JsonObject readJsonContent(Response response) {
        JsonReader jsonReader = readJsonStringFromResponse(response);
        return jsonReader.readObject();
    }

    private static JsonReader readJsonStringFromResponse(Response response) {
        String jsonString = response.readEntity(String.class);
        StringReader stringReader = new StringReader(jsonString);
        return Json.createReader(stringReader);
    }
}