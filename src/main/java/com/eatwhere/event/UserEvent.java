package com.eatwhere.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEvent {

    private Long userId;
    private UserEventType type;

    public UserEvent(Long userId, UserEventType type) {
        this.userId = userId;
        this.type = type;
    }

}
