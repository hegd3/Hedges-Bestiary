package com.hedge.hedges_bestiary.entity.ai.control;

public interface AdvancedTurner {

    public void setTurnType(TurnType turnType);

    public TurnType getTurnType();



    enum TurnType {
        LOCK,
        INSTANT,
        WHOLE_BODY,
        NORMAL
    }
}
