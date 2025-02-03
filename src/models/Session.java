/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.io.Serializable;
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
@Table(name = "session")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Session.findAll", query = "SELECT s FROM Session s"),
    @NamedQuery(name = "Session.findByIDSes", query = "SELECT s FROM Session s WHERE s.iDSes = :iDSes"),
    @NamedQuery(name = "Session.findByStartTime", query = "SELECT s FROM Session s WHERE s.startTime = :startTime"),
    @NamedQuery(name = "Session.findByEndTime", query = "SELECT s FROM Session s WHERE s.endTime = :endTime"),
    @NamedQuery(name = "Session.findByDuration", query = "SELECT s FROM Session s WHERE s.duration = :duration")})
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDSes")
    private Integer iDSes;
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
    @JoinColumn(name = "IDUser", referencedColumnName = "IDUser")
    @ManyToOne(optional = false)
    private User iDUser;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "iDSes")
    private List<Played> playedList;

    public Session() {
    }

    public Session(Integer iDSes) {
        this.iDSes = iDSes;
    }

    public Session(Integer iDSes, Date startTime, Date endTime, Date duration) {
        this.iDSes = iDSes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    public Integer getIDSes() {
        return iDSes;
    }

    public void setIDSes(Integer iDSes) {
        this.iDSes = iDSes;
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

    public User getIDUser() {
        return iDUser;
    }

    public void setIDUser(User iDUser) {
        this.iDUser = iDUser;
    }

    @XmlTransient
    public List<Played> getPlayedList() {
        return playedList;
    }

    public void setPlayedList(List<Played> playedList) {
        this.playedList = playedList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iDSes != null ? iDSes.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Session)) {
            return false;
        }
        Session other = (Session) object;
        if ((this.iDSes == null && other.iDSes != null) || (this.iDSes != null && !this.iDSes.equals(other.iDSes))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "models.Session[ iDSes=" + iDSes + " ]";
    }
    
}
