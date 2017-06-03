/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.tqs104_rentua_restapi.ent;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import pt.ua.tqs104_rentua_restapi.util.PasswordUtils;

/**
 *
 * @author migas
 */
@Entity
@NamedQueries({
    @NamedQuery(name = RentUser.FIND_ALL, query = "SELECT u FROM RentUser u ORDER BY u.name DESC")
    ,
        @NamedQuery(name = RentUser.FIND_BY_LOGIN_PASSWORD, query = "SELECT u FROM RentUser u WHERE u.name = :login AND u.password = :password")
    ,
        @NamedQuery(name = RentUser.COUNT_ALL, query = "SELECT COUNT(u) FROM RentUser u")
    ,
        @NamedQuery(name = RentUser.FIND_BY_LOGIN, query = "SELECT u FROM RentUser u WHERE u.name = :login")
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RentUser implements Serializable {
    public static final String FIND_ALL = "RentUser.findAll";
    public static final String COUNT_ALL = "RentUser.countAll";
    public static final String FIND_BY_LOGIN_PASSWORD = "RentUser.findByLoginAndPassword";
    public static final String FIND_BY_LOGIN = "RentUser.findByLogin";
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @Column(unique=true)
    private String name;
    private String email;
    @NotNull
    private String password;
    @OneToMany(mappedBy = "owner")
    private List<Property> properties;
    @OneToMany(mappedBy = "renter")
    private List<Rental> rentals;

    public RentUser() { }
    
    public RentUser(String login, String password) {
        name = login;
        this.password = password;
    }

    @PrePersist
    private void setUUID() {
        password = PasswordUtils.digestPassword(password);
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
        if (!(object instanceof RentUser)) {
            return false;
        }
        RentUser other = (RentUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pt.ua.tqs104_rentua_restapi.ent.RentUser[ id=" + id + " ]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlTransient
    public List<Property> getProperties() {
        return properties;
    }

    @XmlTransient
    public List<Rental> getRentals() {
        return rentals;
    }

    public void addProperty(Property ppt) {
        if (!this.properties.contains(ppt)) {
            this.properties.add(ppt);
            ppt.setOwner(this);
        }
    }

    public void addRental(Rental rnt) {
        if (!this.rentals.contains(rnt)) {
            this.rentals.add(rnt);
            rnt.setRenter(this);
        }
    }
}
