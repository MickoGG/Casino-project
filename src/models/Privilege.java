/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Micko
 */
@Entity
@Table(name = "privilege")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Privilege.findAll", query = "SELECT p FROM Privilege p"),
    @NamedQuery(name = "Privilege.findByIDPri", query = "SELECT p FROM Privilege p WHERE p.iDPri = :iDPri"),
    @NamedQuery(name = "Privilege.findByName", query = "SELECT p FROM Privilege p WHERE p.name = :name")})
public class Privilege implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDPri")
    private Integer iDPri;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "iDPri")
    private List<User> userList;

    public Privilege() {
    }

    public Privilege(Integer iDPri) {
        this.iDPri = iDPri;
    }

    public Privilege(Integer iDPri, String name) {
        this.iDPri = iDPri;
        this.name = name;
    }

    public Integer getIDPri() {
        return iDPri;
    }

    public void setIDPri(Integer iDPri) {
        this.iDPri = iDPri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iDPri != null ? iDPri.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Privilege)) {
            return false;
        }
        Privilege other = (Privilege) object;
        if ((this.iDPri == null && other.iDPri != null) || (this.iDPri != null && !this.iDPri.equals(other.iDPri))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "models.Privilege[ iDPri=" + iDPri + " ]";
    }
    
}
