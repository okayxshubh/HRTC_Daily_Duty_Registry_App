package com.dit.hp.hrtc_app.Modals;

import java.io.Serializable;

public class RelationPojo implements Serializable {
    private int relationId;
    private String relationName;

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    @Override
    public String toString() {
        return relationName;
    }
}