package com.eatwhere.event;

import com.eatwhere.model.SessionUserState;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionUserPayload {
    
    private Long userId;
    private String name;
    private SessionUserState state;
    private String restaurant;

    public SessionUserPayload(Long userId, String name, SessionUserState state, String restaurant) {
        this.userId = userId;
        this.name = name;
        this.state = state;
        this.restaurant = restaurant;
    }

}
