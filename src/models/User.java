/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Micko
 */
@Entity
@Table(name = "user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByIDUser", query = "SELECT u FROM User u WHERE u.iDUser = :iDUser"),
    @NamedQuery(name = "User.findByFullName", query = "SELECT u FROM User u WHERE u.fullName = :fullName"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password"),
    @NamedQuery(name = "User.findByRegistrationDate", query = "SELECT u FROM User u WHERE u.registrationDate = :registrationDate"),
    @NamedQuery(name = "User.findByBalance", query = "SELECT u FROM User u WHERE u.balance = :balance")})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDUser")
    private Integer iDUser;
    @Basic(optional = false)
    @Column(name = "FullName")
    private String fullName;
    @Basic(optional = false)
    @Column(name = "Email")
    private String email;
    @Basic(optional = false)
    @Column(name = "Username")
    private String username;
    @Basic(optional = false)
    @Column(name = "Password")
    private String password;
    @Basic(optional = false)
    @Column(name = "RegistrationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "Balance")
    private BigDecimal balance;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "iDUser")
    private List<Session> sessionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Currentlyplaying> currentlyplayingList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "iDUser")
    private List<Deposit> depositList;
    @JoinColumn(name = "IDPri", referencedColumnName = "IDPri")
    @ManyToOne(optional = false)
    private Privilege iDPri;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "iDUser")
    private List<Withdraw> withdrawList;

    public User() {
    }

    public User(Integer iDUser) {
        this.iDUser = iDUser;
    }

    public User(Integer iDUser, String fullName, String email, String username, String password, Date registrationDate, BigDecimal balance) {
        this.iDUser = iDUser;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.registrationDate = registrationDate;
        this.balance = balance;
    }

    public Integer getIDUser() {
        return iDUser;
    }

    public void setIDUser(Integer iDUser) {
        this.iDUser = iDUser;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @XmlTransient
    public List<Session> getSessionList() {
        return sessionList;
    }

    public void setSessionList(List<Session> sessionList) {
        this.sessionList = sessionList;
    }

    @XmlTransient
    public List<Currentlyplaying> getCurrentlyplayingList() {
        return currentlyplayingList;
    }

    public void setCurrentlyplayingList(List<Currentlyplaying> currentlyplayingList) {
        this.currentlyplayingList = currentlyplayingList;
    }

    @XmlTransient
    public List<Deposit> getDepositList() {
        return depositList;
    }

    public void setDepositList(List<Deposit> depositList) {
        this.depositList = depositList;
    }

    public Privilege getIDPri() {
        return iDPri;
    }

    public void setIDPri(Privilege iDPri) {
        this.iDPri = iDPri;
    }

    @XmlTransient
    public List<Withdraw> getWithdrawList() {
        return withdrawList;
    }

    public void setWithdrawList(List<Withdraw> withdrawList) {
        this.withdrawList = withdrawList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iDUser != null ? iDUser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.iDUser == null && other.iDUser != null) || (this.iDUser != null && !this.iDUser.equals(other.iDUser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "models.User[ iDUser=" + iDUser + " ]";
    }
    
}
