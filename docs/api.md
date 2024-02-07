# APIs

- [APIs](#apis)
	- [User Requests](#user-requests)
		- [User Online Request](#user-online-request)
	- [User Notifications](#user-notifications)
		- [User Session Invites Notification](#user-session-invites-notification)
		- [User Joined Sessions Notification](#user-joined-sessions-notification)
	- [Session Requests](#session-requests)
		- [Create Session Request](#create-session-request)
		- [End Session Request](#end-session-request)
		- [Session User Invite Request](#session-user-invite-request)
		- [Session User Join Request](#session-user-join-request)
		- [Session User Restaurant Request](#session-user-restaurant-request)
	- [Session Notifications](#session-notifications)
		- [Session Subscribe Notification](#session-subscribe-notification)
		- [Session End Notification](#session-end-notification)
		- [Session User Invite Notification](#session-user-invite-notification)
		- [Session User Joined Notification](#session-user-joined-notification)
		- [Session User Restaurant Submit Notification](#session-user-restaurant-submit-notification)

## User Requests

------------------------------------------------------------------------------------------

### User Online Request

<summary><code><b>/app/user/online</b></code></summary>

#### Body - Json <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `type` <br> *required* | string | Value is `ONLINE` |
> | `userId` <br> *required* | long | User's id |

#### Sample <!-- omit from toc -->
```json
{
	"type": "ONLINE",
	"userId": 1
}
```

## User Notifications

Notifications received after subscribing to `/user/{userId}/queue/events`

------------------------------------------------------------------------------------------

### User Session Invites Notification

Contains a list of session identifiers this user has invitations to.

#### Body - Json <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `type` <br> *required* | string | Value is `INVITE` |
> | `userId` <br> *required* | long | User's id |
> | `invitedSessions` <br> *required* | List<UserSessionInvite> | List of objects which contains session identifiers |

#### UserSessionInvite <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `sessionId` <br> *required* | string | Session's id |
> | `sessionOwnerName` <br> *required* | long | Session owner's name |

#### Sample <!-- omit from toc -->
```json
{
    "userId": 1,
    "type": "INVITE",
    "invitedSessions": [
        {
            "sessionId": 9,
            "sessionOwnerName": "john"
        }
    ]
}
```

------------------------------------------------------------------------------------------

### User Joined Sessions Notification

Contains a list of session identifiers this user has joined.

#### Body - Json <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `type` <br> *required* | string | Value is `INVITE` |
> | `userId` <br> *required* | long | User's id |
> | `joinedSessions` <br> *required* | List<UserSessionJoin> | List of objects which contains session identifiers |

#### UserSessionJoin <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `sessionId` <br> *required* | string | Session's id |
> | `sessionOwnerUserId` <br> *required* | long | Session owner's id |
> | `sessionOwnerName` <br> *required* | string | Session owner's name |
> | `sessionState` <br> *required* | string | Session state: `STARTED` or `ENDED` |
> | `restaurant` <br> *optional* | string | Restaurant picked if `sessionState` is `ENDED` |
 
#### Sample <!-- omit from toc -->
```json
{
    "userId": 1,
    "type": "JOIN",
    "joinedSessions": [
        {
            "sessionId": 9,
            "sessionOwnerUserId": 2,
            "sessionOwnerName": "john",
            "sessionState": "ENDED",
            "restaurant": "mcdonalds"
        }
    ]
}
```

## Session Requests

------------------------------------------------------------------------------------------

### Create Session Request

<summary><code><b>/app/session/create</b></code></summary>

#### Body - Json <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `type` <br> *required* | string | Value is `SESSION_CREATE` |
> | `senderUserId` <br> *required* | long | User's id |
> | `senderName` <br> *required* | string | User's name |

#### Sample <!-- omit from toc -->
```json
{
	"type": "SESSION_CREATE",
	"senderUserId": 1,
	"senderName": "john"
}
```

------------------------------------------------------------------------------------------

### End Session Request

<summary><code><b>/app/session/{sessionId}/end</b></code></summary>

#### Body - Json <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `type` <br> *required* | string | Value is `SESSION_END` |
> | `senderUserId` <br> *required* | long | User's id |
> | `senderName` <br> *required* | string | User's name |
> | `sessionId` <br> *required* | long | Session's id to end |

#### Sample <!-- omit from toc -->
```json
{
	"type": "SESSION_END",
	"senderUserId": 1,
	"senderName": "john",
	"sessionId": 99
}
```

------------------------------------------------------------------------------------------

### Session User Invite Request

<summary><code><b>/app/session/{sessionId}/invite</b></code></summary>

#### Body - Json <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `type` <br> *required* | string | Value is `USER_INVITE` |
> | `senderUserId` <br> *required* | long | User's id |
> | `senderName` <br> *required* | string | User's name |
> | `sessionId` <br> *required* | long | Session's id |
> | `userId` <br> *required* | long | Invited user's id |
> | `name` <br> *required* | string | Invited user's name |
> | `sessionState` <br> *required* | string | Session's state |

#### Sample <!-- omit from toc -->
```json
{
	"senderUserId": 1,
    "senderName": "john",
    "sessionId": 12,
    "type": "USER_INVITE",
    "userId": 3,
    "name": "jeff",
    "sessionState": "STARTED"
}
```

------------------------------------------------------------------------------------------

### Session User Join Request

<summary><code><b>/app/session/{sessionId}/join</b></code></summary>

#### Body - Json <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `type` <br> *required* | string | Value is `USER_JOIN` |
> | `senderUserId` <br> *required* | long | User's id |
> | `senderName` <br> *required* | string | User's name |
> | `sessionId` <br> *required* | long | Session's id |

#### Sample <!-- omit from toc -->
```json
{
    "senderUserId": 3,
    "senderName": "jeff",
    "sessionId": 12,
    "type": "USER_JOIN"
}
```

------------------------------------------------------------------------------------------

### Session User Restaurant Request

<summary><code><b>/app/session/{sessionId}/restaurant</b></code></summary>

#### Body - Json <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `type` <br> *required* | string | Value is `USER_RESTAURANT_SUBMIT` |
> | `senderUserId` <br> *required* | long | User's id |
> | `senderName` <br> *required* | string | User's name |
> | `sessionId` <br> *required* | long | Session's id |
> | `restaurant` <br> *required* | string | User's restaurant choice |

#### Sample <!-- omit from toc -->
```json
{
    "senderUserId": 3,
    "senderName": "jeff",
    "sessionId": 12,
    "type": "USER_RESTAURANT_SUBMIT",
    "restaurant": "thai food"
}
```


------------------------------------------------------------------------------------------

## Session Notifications

Notifications received after subscribing to `/app/session/${sessionId}`

------------------------------------------------------------------------------------------

### Session Subscribe Notification

Contains full session data for this session.

#### Body - Json <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `type` <br> *required* | string | Value is `SESSION_SUBSCRIBE` |
> | `sessionState` <br> *required* | string | Session state |
> | `sessionUsers` <br> *required* | List<SessionUser> | List of objects which contains session user data |

#### SessionUser <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `userId` <br> *required* | long | User's id |
> | `name` <br> *required* | string | User's name |
> | `state` <br> *required* | string | User's state in this session: `INVITED` or `JOINED` |
> | `restaurant` <br> *optional* | string | User's restaurant choice |

#### Sample <!-- omit from toc -->
```json
{
    "sessionId": 12,
    "type": "SESSION_SUBSCRIBE",
    "sessionState": "STARTED",
    "sessionUsers": [
        {
            "userId": 1,
            "name": "john",
            "state": "JOINED",
            "restaurant": "mcdonalds"
        },
        {
            "userId": 3,
            "name": "jeff",
            "state": "JOINED"
        }
    ]
}
```

------------------------------------------------------------------------------------------

### Session End Notification

Notifies that the session's `sessionState` is `ENDED`.

#### Body - Json <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `type` <br> *required* | string | Value is `SESSION_END` |
> | `senderUserId` <br> *required* | long | Sender's user id |
> | `senderName` <br> *required* | string | Sender's user name |
> | `restaurant` <br> *required* | string | Selected restaurant for this session |
> 
#### Sample <!-- omit from toc -->
```json
{
    "senderUserId": 3,
    "senderName": "jeff",
    "sessionId": 13,
    "type": "SESSION_END",
    "restaurant": "mcdonalds"
}
```

------------------------------------------------------------------------------------------

### Session User Invite Notification

Notifies that a user has been invited to the session.

#### Body - Json <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `type` <br> *required* | string | Value is `USER_INVITE` |
> | `senderUserId` <br> *required* | long | Sender's user id |
> | `senderName` <br> *required* | string | Sender's user name |
> | `sessionId` <br> *required* | long | Session's id |
> | `userId` <br> *required* | long | Invited user's id |
> | `name` <br> *required* | string | Invited user's name |
> | `sessionState` <br> *required* | string | Session's state |

#### Sample <!-- omit from toc -->
```json
{
    "senderUserId": 3,
    "senderName": "jeff",
    "sessionId": 14,
    "type": "USER_INVITE",
    "userId": 1,
    "name": "john",
    "sessionState": "STARTED"
}
```

------------------------------------------------------------------------------------------

### Session User Joined Notification

Notifies that a user joined the session.

#### Body - Json <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `type` <br> *required* | string | Value is `USER_JOIN` |
> | `senderUserId` <br> *required* | long | Sender's user id |
> | `senderName` <br> *required* | string | Sender's user name |
> | `sessionId` <br> *required* | long | Session's id |

#### Sample <!-- omit from toc -->
```json
{
    "senderUserId": 1,
    "senderName": "john",
    "sessionId": 14,
    "type": "USER_JOIN"
}
```

------------------------------------------------------------------------------------------

### Session User Restaurant Submit Notification

Notifies that a session user submitted their restaurant.

#### Body - Json <!-- omit from toc -->

> | Name | Type | Description |      
> | --- | --- | --- |
> | `type` <br> *required* | string | Value is `USER_RESTAURANT_SUBMIT` |
> | `senderUserId` <br> *required* | long | Sender's user id |
> | `senderName` <br> *required* | string | Sender's user name |
> | `sessionId` <br> *required* | long | Session's id |
> | `restaurant` <br> *required* | long | User's restaurant |

#### Sample <!-- omit from toc -->
```json
{
    "senderUserId": 3,
    "senderName": "jeff",
    "sessionId": 14,
    "type": "USER_RESTAURANT_SUBMIT",
    "restaurant": "mcdonalds"
}
```

------------------------------------------------------------------------------------------

