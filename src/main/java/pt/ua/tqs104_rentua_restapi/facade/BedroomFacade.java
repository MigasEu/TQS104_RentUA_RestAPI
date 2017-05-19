/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.tqs104_rentua_restapi.facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import pt.ua.tqs104_rentua_restapi.ent.Bedroom;

/**
 *
 * @author migas
 */
@Stateless
public class BedroomFacade extends AbstractFacade<Bedroom> {

    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BedroomFacade() {
        super(Bedroom.class);
    }
    
}
