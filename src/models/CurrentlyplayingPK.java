/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Micko
 */
@Embeddable
public class CurrentlyplayingPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "IDUser")
    private int iDUser;
    @Basic(optional = false)
    @Column(name = "IDGame")
    private int iDGame;

    public CurrentlyplayingPK() {
    }

    public CurrentlyplayingPK(int iDUser, int iDGame) {
        this.iDUser = iDUser;
        this.iDGame = iDGame;
    }

    public int getIDUser() {
        return iDUser;
    }

    public void setIDUser(int iDUser) {
        this.iDUser = iDUser;
    }

    public int getIDGame() {
        return iDGame;
    }

    public void setIDGame(int iDGame) {
        this.iDGame = iDGame;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) iDUser;
        hash += (int) iDGame;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CurrentlyplayingPK)) {
            return false;
        }
        CurrentlyplayingPK other = (CurrentlyplayingPK) object;
        if (this.iDUser != other.iDUser) {
            return false;
        }
        if (this.iDGame != other.iDGame) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "models.CurrentlyplayingPK[ iDUser=" + iDUser + ", iDGame=" + iDGame + " ]";
    }
    
}
