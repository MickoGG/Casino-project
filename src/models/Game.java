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
@Table(name = "game")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Game.findAll", query = "SELECT g FROM Game g"),
    @NamedQuery(name = "Game.findByIDGame", query = "SELECT g FROM Game g WHERE g.iDGame = :iDGame"),
    @NamedQuery(name = "Game.findByName", query = "SELECT g FROM Game g WHERE g.name = :name")})
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDGame")
    private Integer iDGame;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
    private List<Currentlyplaying> currentlyplayingList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "iDGame")
    private List<Played> playedList;

    public Game() {
    }

    public Game(Integer iDGame) {
        this.iDGame = iDGame;
    }

    public Game(Integer iDGame, String name) {
        this.iDGame = iDGame;
        this.name = name;
    }

    public Integer getIDGame() {
        return iDGame;
    }

    public void setIDGame(Integer iDGame) {
        this.iDGame = iDGame;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public List<Currentlyplaying> getCurrentlyplayingList() {
        return currentlyplayingList;
    }

    public void setCurrentlyplayingList(List<Currentlyplaying> currentlyplayingList) {
        this.currentlyplayingList = currentlyplayingList;
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
        hash += (iDGame != null ? iDGame.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Game)) {
            return false;
        }
        Game other = (Game) object;
        if ((this.iDGame == null && other.iDGame != null) || (this.iDGame != null && !this.iDGame.equals(other.iDGame))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "models.Game[ iDGame=" + iDGame + " ]";
    }
    
}
