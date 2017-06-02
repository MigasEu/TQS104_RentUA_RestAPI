/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.tqs104_rentua_restapi.ent;

import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.ua.tqs104_rentua_restapi.facade.UserFacade;

/**
 *
 * @author migas
 */
@Ignore
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.ROLLBACK)
public class PersTest {

    @EJB
    UserFacade userF;

    @Deployment
    public static JavaArchive createTestArchive() {
        return ShrinkWrap.create(JavaArchive.class) // Create jar
                .addPackage(RentUser.class.getPackage()) // Add classes
                .addPackage(UserFacade.class.getPackage()) // Add more classes
                // FEST Assert is not part of Arquillian JUnit
                .addPackages(true, "org.fest")
                // .addClasses(Person.class, PersonBean.class) is an alternative
                // for addPackage
                .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml")) // b
                .addAsResource("META-INF/persistence.xml");
    }

    @Test
    @UsingDataSet("dataset/UserBeanIntegrationTest.xml")
    public void findPersonbyId() {
        RentUser result = userF.find(1L);

        assertEquals("foo", result.getName());
        assertEquals("foo@ua.pt", result.getEmail());
    }

    @Test
    @UsingDataSet("dataset/UserBeanIntegrationTest.xml")
    public void findAll() {
        List<RentUser> result = userF.findAll();

        assertEquals(2, result.size());
        assertEquals("foo", result.get(0).getName());
        assertEquals("foo@ua.pt", result.get(0).getEmail());
        assertEquals("duke", result.get(1).getName());
        assertEquals("duke@ua.pt", result.get(1).getEmail());
    }

    @Test
    @UsingDataSet("dataset/UserBeanIntegrationTest.xml")
    @ShouldMatchDataSet("dataset/UserBeanIntegrationTest_save.xml")
    @Transactional(TransactionMode.COMMIT)
    public void save() {
        RentUser p = new RentUser();
        p.setName("firstname");
        p.setEmail("email@ua.pt");
        p.setPassword("asd");
        
        userF.create(p);
    }
}
