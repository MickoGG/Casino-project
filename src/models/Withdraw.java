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
@Table(name = "withdraw")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Withdraw.findAll", query = "SELECT w FROM Withdraw w"),
    @NamedQuery(name = "Withdraw.findByIDWit", query = "SELECT w FROM Withdraw w WHERE w.iDWit = :iDWit"),
    @NamedQuery(name = "Withdraw.findByDate", query = "SELECT w FROM Withdraw w WHERE w.date = :date"),
    @NamedQuery(name = "Withdraw.findByAmount", query = "SELECT w FROM Withdraw w WHERE w.amount = :amount")})
public class Withdraw implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDWit")
    private Integer iDWit;
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

    public Withdraw() {
    }

    public Withdraw(Integer iDWit) {
        this.iDWit = iDWit;
    }

    public Withdraw(Integer iDWit, Date date, BigDecimal amount) {
        this.iDWit = iDWit;
        this.date = date;
        this.amount = amount;
    }

    public Integer getIDWit() {
        return iDWit;
    }

    public void setIDWit(Integer iDWit) {
        this.iDWit = iDWit;
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
        hash += (iDWit != null ? iDWit.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Withdraw)) {
            return false;
        }
        Withdraw other = (Withdraw) object;
        if ((this.iDWit == null && other.iDWit != null) || (this.iDWit != null && !this.iDWit.equals(other.iDWit))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "models.Withdraw[ iDWit=" + iDWit + " ]";
    }
    
}
