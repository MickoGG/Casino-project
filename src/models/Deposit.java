/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Micko
 */
@Entity
@Table(name = "deposit")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Deposit.findAll", query = "SELECT d FROM Deposit d"),
    @NamedQuery(name = "Deposit.findByIDDep", query = "SELECT d FROM Deposit d WHERE d.iDDep = :iDDep"),
    @NamedQuery(name = "Deposit.findByDate", query = "SELECT d FROM Deposit d WHERE d.date = :date"),
    @NamedQuery(name = "Deposit.findByAmount", query = "SELECT d FROM Deposit d WHERE d.amount = :amount")})
public class Deposit implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDDep")
    private Integer iDDep;
    @Basic(optional = false)
    @Column(name = "Date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "Amount")
    private BigDecimal amount;
    @JoinColumn(name = "IDUser", referencedColumnName = "IDUser")
    @ManyToOne(optional = false)
    private User iDUser;

    public Deposit() {
    }

    public Deposit(Integer iDDep) {
        this.iDDep = iDDep;
    }

    public Deposit(Integer iDDep, Date date, BigDecimal amount) {
        this.iDDep = iDDep;
        this.date = date;
        this.amount = amount;
    }

    public Integer getIDDep() {
        return iDDep;
    }

    public void setIDDep(Integer iDDep) {
        this.iDDep = iDDep;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public User getIDUser() {
        return iDUser;
    }

    public void setIDUser(User iDUser) {
        this.iDUser = iDUser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iDDep != null ? iDDep.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Deposit)) {
            return false;
        }
        Deposit other = (Deposit) object;
        if ((this.iDDep == null && other.iDDep != null) || (this.iDDep != null && !this.iDDep.equals(other.iDDep))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "models.Deposit[ iDDep=" + iDDep + " ]";
    }
    
}
