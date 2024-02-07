package com.eatwhere.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SessionUserState {
    INVITED("INVITED"),
    JOINED("JOINED");

    private String value;

	SessionUserState(String value) {
		this.value = value;
	}
    
    @JsonValue
	public String getSessionUserState() {
		return value;
	}
	
    @JsonCreator
    public static SessionUserState fromValue(String value) {
        switch(value.trim())
        {
            case "INVITED":
                return INVITED;
            case "JOINED":
                return JOINED;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
