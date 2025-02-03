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
@Table(name = "played")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Played.findAll", query = "SELECT p FROM Played p"),
    @NamedQuery(name = "Played.findByIDPlay", query = "SELECT p FROM Played p WHERE p.iDPlay = :iDPlay"),
    @NamedQuery(name = "Played.findByStartTime", query = "SELECT p FROM Played p WHERE p.startTime = :startTime"),
    @NamedQuery(name = "Played.findByEndTime", query = "SELECT p FROM Played p WHERE p.endTime = :endTime"),
    @NamedQuery(name = "Played.findByDuration", query = "SELECT p FROM Played p WHERE p.duration = :duration"),
    @NamedQuery(name = "Played.findByTotalWager", query = "SELECT p FROM Played p WHERE p.totalWager = :totalWager"),
    @NamedQuery(name = "Played.findByTotalReceived", query = "SELECT p FROM Played p WHERE p.totalReceived = :totalReceived"),
    @NamedQuery(name = "Played.findByProfit", query = "SELECT p FROM Played p WHERE p.profit = :profit")})
public class Played implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDPlay")
    private Integer iDPlay;
    @Basic(optional = false)
    @Column(name = "StartTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Basic(optional = false)
    @Column(name = "EndTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    @Basic(optional = false)
    @Column(name = "Duration")
    @Temporal(TemporalType.TIME)
    private Date duration;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "TotalWager")
    private BigDecimal totalWager;
    @Basic(optional = false)
    @Column(name = "TotalReceived")
    private BigDecimal totalReceived;
    @Basic(optional = false)
    @Column(name = "Profit")
    private BigDecimal profit;
    @JoinColumn(name = "IDGame", referencedColumnName = "IDGame")
    @ManyToOne(optional = false)
    private Game iDGame;
    @JoinColumn(name = "IDSes", referencedColumnName = "IDSes")
    @ManyToOne(optional = false)
    private Session iDSes;

    public Played() {
    }

    public Played(Integer iDPlay) {
        this.iDPlay = iDPlay;
    }

    public Played(Integer iDPlay, Date startTime, Date endTime, Date duration, BigDecimal totalWager, BigDecimal totalReceived, BigDecimal profit) {
        this.iDPlay = iDPlay;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.totalWager = totalWager;
        this.totalReceived = totalReceived;
        this.profit = profit;
    }

    public Integer getIDPlay() {
        return iDPlay;
    }

    public void setIDPlay(Integer iDPlay) {
        this.iDPlay = iDPlay;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getDuration() {
        return duration;
    }

    public void setDuration(Date duration) {
        this.duration = duration;
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

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public Game getIDGame() {
        return iDGame;
    }

    public void setIDGame(Game iDGame) {
        this.iDGame = iDGame;
    }

    public Session getIDSes() {
        return iDSes;
    }

    public void setIDSes(Session iDSes) {
        this.iDSes = iDSes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iDPlay != null ? iDPlay.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Played)) {
            return false;
        }
        Played other = (Played) object;
        if ((this.iDPlay == null && other.iDPlay != null) || (this.iDPlay != null && !this.iDPlay.equals(other.iDPlay))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "models.Played[ iDPlay=" + iDPlay + " ]";
    }
    
}
