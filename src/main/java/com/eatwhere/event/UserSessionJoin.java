package com.eatwhere.event;

import com.eatwhere.model.SessionState;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSessionJoin {
    
    private Long sessionId;
    private Long sessionOwnerUserId;
    private String sessionOwnerName;
    private SessionState sessionState;
    private String restaurant;

    public UserSessionJoin(Long sessionId, Long sessionOwnerUserId, String sessionOwnerName, SessionState sessionState,
            String restaurant) {
        this.sessionId = sessionId;
        this.sessionOwnerUserId = sessionOwnerUserId;
        this.sessionOwnerName = sessionOwnerName;
        this.sessionState = sessionState;
        this.restaurant = restaurant;
    }

}
