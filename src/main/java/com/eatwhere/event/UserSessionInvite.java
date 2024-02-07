package com.eatwhere.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSessionInvite {
    
    private Long sessionId;
    private String sessionOwnerName;

    public UserSessionInvite(Long sessionId, String sessionOwnerName) {
        this.sessionId = sessionId;
        this.sessionOwnerName = sessionOwnerName;
    }

}
