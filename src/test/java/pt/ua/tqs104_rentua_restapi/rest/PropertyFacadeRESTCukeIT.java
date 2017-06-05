/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.tqs104_rentua_restapi.rest;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.runner.RunWith;
import cucumber.runtime.arquillian.CukeSpace;
import cucumber.runtime.arquillian.api.Features;
import java.io.File;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import pt.ua.tqs104_rentua_restapi.ent.RentUser;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Ignore;

/**
 *
 * @author migas
 */
@Ignore
@RunWith(CukeSpace.class)
@Features({"src/test/resources/feature/list_all_properties.feature"})
public class PropertyFacadeRESTCukeIT {

    @Deployment
    public static WebArchive createTestArchive() {
        //WebArchive[] cuke = Maven.resolver().resolve("com.github.cukespace:cukespace-core:1.6.7").withTransitivity().as(JavaArchive.class);
        File[] libraries = Maven.resolver().resolve("com.github.cukespace:cukespace-core:1.6.7")
                .withTransitivity()
                .asFile();

        return ShrinkWrap.create(WebArchive.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(libraries);
    }

    @Given("^a House located in the district of 'Aveiro' with the price per semester '(\\d+)'$")
    public void a_House_located_in_the_district_of_Aveiro_with_the_price_per_semester(int arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    
    @Given("^a Room located in the district of 'Aveiro' with the price per semester '(\\d+)'$")
    public void a_Room_located_in_the_district_of_Aveiro_with_the_price_per_semester(int arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    
    @When("^the student enters the properties store$")
    public void the_student_enters_the_properties_store() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    
    @Then("^(\\d+) properties should have been found$")
    public void properties_should_have_been_found(int arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    
    @Then("^House (\\d+) should have the district 'Aveiro'$")
    public void house_should_have_the_district_Aveiro(int arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
