package com.eatwhere.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SessionState {
    STARTED("STARTED"),
    ENDED("ENDED");

    private String value;

	SessionState(String value) {
		this.value = value;
	}
    
    @JsonValue
	public String getSessionState() {
		return value;
	}
	
    @JsonCreator
    public static SessionState fromValue(String value) {
        switch(value.trim())
        {
            case "STARTED":
                return STARTED;
            case "ENDED":
                return ENDED;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
