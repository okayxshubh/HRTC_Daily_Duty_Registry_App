package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;
public class CastePojo implements Serializable{
    private int casteId;
    private String casteName;

    public int getCasteId() {
        return casteId;
    }

    public void setCasteId(int casteId) {
        this.casteId = casteId;
    }

    public String getCasteName() {
        return casteName;
    }

    public void setCasteName(String casteName) {
        this.casteName = casteName;
    }

    @Override
    public String toString() {
        return casteName;
    }
}