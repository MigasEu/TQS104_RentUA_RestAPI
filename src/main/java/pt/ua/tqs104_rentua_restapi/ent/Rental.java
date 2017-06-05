/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.tqs104_rentua_restapi.ent;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author migas
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Rental.FIND_BY_USER, query = "SELECT r FROM Rental r WHERE r.renter = :renterId"),
        @NamedQuery(name = Rental.FIND_BY_PROPERTY, query = "SELECT r FROM Rental r WHERE r.property = :propertyId"),
        @NamedQuery(name = Rental.FIND_BY_PROPERTY_FROM_TO, query = "SELECT r FROM Rental r WHERE r.property = :propertyId AND ((r.startDate >= :startDate AND r.startDate <= :endDate) OR (r.endDate >= :startDate AND r.endDate <= :endDate))")
})
@XmlRootElement
public class Rental implements Serializable {
    public static final String FIND_BY_USER = "Rental.findByUser";
    public static final String FIND_BY_PROPERTY = "Rental.findByProperty";
    public static final String FIND_BY_PROPERTY_FROM_TO = "Rental.findByPropertyFromTo";

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;
    @ManyToOne
    private Property property;
    @ManyToOne
    private RentUser renter;

    public Rental() { }

    public Rental(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Rental)) {
            return false;
        }
        Rental other = (Rental) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pt.ua.tqs104_rentua_restapi.ent.Rental[ id=" + id + " ]";
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property ppt) {
        if (!ppt.equals(this.property)) {
            this.property = ppt;
            ppt.addRental(this);
        }
    }

    public RentUser getRenter() {
        return renter;
    }

    public void setRenter(RentUser rtr) {
        if (!rtr.equals(this.renter)) {
            this.renter = rtr;
            rtr.addRental(this);
        }
    }
    
}
