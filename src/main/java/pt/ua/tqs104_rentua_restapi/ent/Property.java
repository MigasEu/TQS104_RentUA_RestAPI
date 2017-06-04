/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.tqs104_rentua_restapi.ent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author migas
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Property.FIND_BY_USER, query = "SELECT p FROM Property p WHERE p.owner = :ownerId")
})
@XmlRootElement
public class Property implements Serializable {
    public static final String FIND_BY_USER = "Property.findByUser";
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @NotNull
    private String title;
    private String description;
    private int bathrooms;
    @Min(0) @Max(1)
    private int type;
    @Min(0)
    private double price;
    @NotNull @ManyToOne
    protected RentUser owner;
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    @JsonIgnore
    protected transient List<Rental> rentals = new ArrayList<>();

    public static final int TYPE_HOUSE = 0;
    public static final int TYPE_BEDROOM = 1;
    
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
        if (!(object instanceof Property)) {
            return false;
        }
        Property other = (Property) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pt.ua.tqs104_rentua_restapi.ent.Property[ id=" + id + " ]";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public RentUser getOwner() {
        return owner;
    }

    public void setOwner(RentUser own) {
        if (!own.equals(this.owner)) {
            this.owner = own;
            own.addProperty(this);
        }
    }

    @XmlTransient
    public List<Rental> getRentals() {
        return rentals;
    }
    
    public void addRental(Rental rnt) {
        if (!this.rentals.contains(rnt)) {
            this.rentals.add(rnt);
            rnt.setProperty(this);
        }
    }
}
