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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "currentlyplaying")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Currentlyplaying.findAll", query = "SELECT c FROM Currentlyplaying c"),
    @NamedQuery(name = "Currentlyplaying.findByIDUser", query = "SELECT c FROM Currentlyplaying c WHERE c.currentlyplayingPK.iDUser = :iDUser"),
    @NamedQuery(name = "Currentlyplaying.findByIDGame", query = "SELECT c FROM Currentlyplaying c WHERE c.currentlyplayingPK.iDGame = :iDGame"),
    @NamedQuery(name = "Currentlyplaying.findByStartTime", query = "SELECT c FROM Currentlyplaying c WHERE c.startTime = :startTime"),
    @NamedQuery(name = "Currentlyplaying.findByTotalWager", query = "SELECT c FROM Currentlyplaying c WHERE c.totalWager = :totalWager"),
    @NamedQuery(name = "Currentlyplaying.findByTotalReceived", query = "SELECT c FROM Currentlyplaying c WHERE c.totalReceived = :totalReceived")})
public class Currentlyplaying implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CurrentlyplayingPK currentlyplayingPK;
    @Basic(optional = false)
    @Column(name = "StartTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "TotalWager")
    private BigDecimal totalWager;
    @Basic(optional = false)
    @Column(name = "TotalReceived")
    private BigDecimal totalReceived;
    @JoinColumn(name = "IDGame", referencedColumnName = "IDGame", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Game game;
    @JoinColumn(name = "IDUser", referencedColumnName = "IDUser", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user;

    public Currentlyplaying() {
    }

    public Currentlyplaying(CurrentlyplayingPK currentlyplayingPK) {
        this.currentlyplayingPK = currentlyplayingPK;
    }

    public Currentlyplaying(CurrentlyplayingPK currentlyplayingPK, Date startTime, BigDecimal totalWager, BigDecimal totalReceived) {
        this.currentlyplayingPK = currentlyplayingPK;
        this.startTime = startTime;
        this.totalWager = totalWager;
        this.totalReceived = totalReceived;
    }

    public Currentlyplaying(int iDUser, int iDGame) {
        this.currentlyplayingPK = new CurrentlyplayingPK(iDUser, iDGame);
    }

    public CurrentlyplayingPK getCurrentlyplayingPK() {
        return currentlyplayingPK;
    }

    public void setCurrentlyplayingPK(CurrentlyplayingPK currentlyplayingPK) {
        this.currentlyplayingPK = currentlyplayingPK;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getTotalWager() {
        return totalWager;
    }

    public void setTotalWager(BigDecimal totalWager) {
        this.totalWager = totalWager;
    }

    public BigDecimal getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(BigDecimal totalReceived) {
        this.totalReceived = totalReceived;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (currentlyplayingPK != null ? currentlyplayingPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Currentlyplaying)) {
            return false;
        }
        Currentlyplaying other = (Currentlyplaying) object;
        if ((this.currentlyplayingPK == null && other.currentlyplayingPK != null) || (this.currentlyplayingPK != null && !this.currentlyplayingPK.equals(other.currentlyplayingPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "models.Currentlyplaying[ currentlyplayingPK=" + currentlyplayingPK + " ]";
    }
    
}
