'use strict';

const usernamePage = document.querySelector('#username-page');
const usernameForm = document.querySelector('#usernameForm');
const sessionsPage = document.querySelector('#sessions-page');
const createSessionBtn = document.querySelector('#create-session');

let cName = null;
let cUserId = null;

let stompClient = null;


function connect(event) {
    cName = document.querySelector('#username').value.trim();
    if (cName) {
        usernamePage.classList.add('hidden');
        sessionsPage.classList.remove('hidden');
        findOrCreateUser(cName).then(wsConnect);
    }

    event.preventDefault();
}

function wsConnect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onWsConnect, onWsError);
}

function onWsConnect() {
    stompClient.subscribe(`/user/${cUserId}/queue/events`, onUserEventReceived);

    sendUserOnline(cUserId);
}

function onWsError() {
}

async function onUserEventReceived(event) {
    const userEvent = JSON.parse(event.body);
    if (userEvent.type == 'INVITE') {
        if (userEvent.invitedSessions) {
            userEvent.invitedSessions.forEach(invite => {
                appendSessionInviteElement(invite);
            });
        }
    } else if (userEvent.type == 'JOIN') {
        if (userEvent.joinedSessions) {
            let appendToFirst = false;
            if (userEvent.joinedSessions && userEvent.joinedSessions.length == 1) {
                appendToFirst = true;
            }
            userEvent.joinedSessions.forEach(join => {
                appendSessionContainerElement(join.sessionId, join.sessionOwnerUserId, join.sessionOwnerName, join.sessionState, join.restaurant, appendToFirst);
                stompClient.subscribe(`/app/session/${join.sessionId}`, onSessionEventReceived);
                stompClient.subscribe(`/session/${join.sessionId}`, onSessionEventReceived);
            });
        }
    } 
}

async function onSessionEventReceived(event) {
    const sessionEvent = JSON.parse(event.body);

    if (sessionEvent.type === 'SESSION_SUBSCRIBE') {
        if (sessionEvent.sessionUsers) {
            sessionEvent.sessionUsers.forEach(sessionUser => {
                appendSessionUserElement(sessionEvent.sessionId, sessionUser.userId, sessionUser.name, sessionUser.state, sessionUser.restaurant, sessionEvent.sessionState);
            });
        }

    } else if (sessionEvent.type === 'SESSION_END') {
        if (sessionEvent.restaurant) {
            updateSessionEndElement(sessionEvent.sessionId, sessionEvent.restaurant);
        }

    } else if (sessionEvent.type === 'USER_INVITE') {
        if (sessionEvent.senderUserId && sessionEvent.name) {
            appendSessionUserElement(sessionEvent.sessionId, sessionEvent.senderUserId, sessionEvent.name, 'INVITED', '', sessionEvent.sessionState);
        }
    } else if (sessionEvent.type === 'USER_JOIN') {
        if (sessionEvent.senderUserId) {
            updateSessionUserStateElement(sessionEvent.sessionId, sessionEvent.senderUserId, 'JOINED');
        }
    } else if (sessionEvent.type === 'USER_RESTAURANT_SUBMIT') {
        if (sessionEvent.restaurant) {
            updateSessionUserRestaurantElement(sessionEvent.sessionId, sessionEvent.senderUserId, sessionEvent.restaurant)
        }
    }
}


function appendSessionInviteElement(invitedSession) {
    const pendingSessionInvitesList = document.getElementById('invites');
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = 'invite-' + invitedSession.sessionId;

    const joinSessionLink = document.createElement('a');
    joinSessionLink.href = 'javascript:void(0)';
    joinSessionLink.innerHTML = 'Join Session ' + invitedSession.sessionOwnerName;
    joinSessionLink.classList.add('btn');

    joinSessionLink.addEventListener('click', function(event) { 
            sendUserJoin(event, invitedSession.sessionId, cUserId, cName);
            const removeInvite = document.getElementById('invite-' + invitedSession.sessionId);
            removeInvite.remove();
        }, true);

    listItem.appendChild(joinSessionLink);

    pendingSessionInvitesList.appendChild(listItem);
}

function appendSessionContainerElement(sessionId, sessionOwnerUserId, sessionOwnerName, sessionState, restaurant, appendToFirst) {
    const sessionContainerList = document.getElementById('sessions');
    const listItem = document.createElement('li');
    listItem.id = 'session-' + sessionId;
    listItem.classList.add('sessions-li');

    const innerContainerList = document.createElement('ul');
    const ownerInnerListItem = document.createElement('li');
    const ownerSpan = document.createElement('span');
    ownerSpan.textContent = 'Session Owner: ' + sessionOwnerName;
    ownerInnerListItem.appendChild(ownerSpan);
    innerContainerList.appendChild(ownerInnerListItem);

    const sessionStateInnerListItem = document.createElement('li');
    const sessionStateSpan = document.createElement('span');
    sessionStateSpan.id = 'sessionState-' + sessionId;

    if (sessionState == 'ENDED') {
        sessionStateSpan.textContent = 'Session State: ' + sessionState + '; Restaurant selected: ' + restaurant;
    } else {
        sessionStateSpan.textContent = 'Session State: ' + sessionState;
    }
    
    sessionStateInnerListItem.appendChild(sessionStateSpan);
    innerContainerList.appendChild(sessionStateInnerListItem);

    if (cUserId == sessionOwnerUserId && sessionState == 'STARTED') {

        const sessionEndBtnInnerListItem = document.createElement('li');
        const sessionEndLink = document.createElement('a');
        sessionEndLink.href = 'javascript:void(0)';
        sessionEndLink.innerHTML = 'End Session';
        sessionEndLink.classList.add('btn');
        sessionEndLink.classList.add('remove-on-end-session-' + sessionId);

        sessionEndLink.addEventListener('click', function(event){ sendSessionEnd(event, sessionId, cUserId, cName); }, true);
        sessionEndBtnInnerListItem.appendChild(sessionEndLink);
        innerContainerList.appendChild(sessionEndBtnInnerListItem);
        
        const sessionInviteBtnInnerListItem = document.createElement('li');
        const sessionInviteInput = document.createElement('input');
        sessionInviteInput.id = 'sessionInviteInput-' + sessionId;
        sessionInviteInput.classList.add('remove-on-end-session-' + sessionId);
        sessionInviteBtnInnerListItem.appendChild(sessionInviteInput);

        const sessionInviteLink = document.createElement('a');
        sessionInviteLink.href = 'javascript:void(0)';
        sessionInviteLink.innerHTML = 'Invite User';
        sessionInviteLink.classList.add('btn');
        sessionInviteLink.classList.add('remove-on-end-session-' + sessionId);

        sessionInviteLink.addEventListener('click', function(event) {
                sendUserInvite(event, sessionId, cUserId, cName, document.getElementById('sessionInviteInput-' + sessionId).value, sessionState); 
                document.getElementById('sessionInviteInput-' + sessionId).innerHTML = '';
            }, true);
        sessionInviteBtnInnerListItem.appendChild(sessionInviteLink);
        innerContainerList.appendChild(sessionInviteBtnInnerListItem);
    }

    const sessionUsersContainerList = document.createElement('ul');
    sessionUsersContainerList.id = 'sessionUserList-' + sessionId;
    innerContainerList.appendChild(sessionUsersContainerList);

    listItem.appendChild(innerContainerList);

    if (appendToFirst) {
        sessionContainerList.insertBefore(listItem, sessionContainerList.children[0]);
    } else {
        sessionContainerList.appendChild(listItem);
    }

    const sessionUserContainerList = document.getElementById('sessionUserList-' + sessionId);
    
    const headerListItem = document.createElement('li');
    const headerNameSpan = document.createElement('span');
    headerNameSpan.textContent = 'Name';
    headerNameSpan.classList.add('session-user-span');
    headerNameSpan.classList.add('session-user-header-span');
    headerListItem.appendChild(headerNameSpan);

    const headerStateSpan = document.createElement('span');
    headerStateSpan.textContent = 'State';
    headerStateSpan.classList.add('session-user-span');
    headerStateSpan.classList.add('session-user-header-span');
    headerListItem.appendChild(headerStateSpan);

    const headerRestaurantSpan = document.createElement('span');
    headerRestaurantSpan.textContent = 'Restaurant';
    headerRestaurantSpan.classList.add('session-user-span');
    headerRestaurantSpan.classList.add('session-user-header-span');
    headerListItem.appendChild(headerRestaurantSpan);

    sessionUserContainerList.appendChild(headerListItem);
}

function appendSessionUserElement(sessionId, sessionUserId, sessionUserName, sessionUserState, sessionUserRestaurant, sessionState) {
    const sessionUserContainerList = document.getElementById('sessionUserList-' + sessionId);

    const listItem = document.createElement('li');
    listItem.id = 'sessionUser-' + sessionId + '-' + sessionUserId;

    const sessionUserNameSpan = document.createElement('span');
    sessionUserNameSpan.textContent = sessionUserName;
    sessionUserNameSpan.classList.add('session-user-span');

    const sessionUserStateSpan = document.createElement('span');
    sessionUserStateSpan.id = 'sessionUserState-' + sessionId + '-' + sessionUserId;
    sessionUserStateSpan.textContent = sessionUserState;
    sessionUserStateSpan.classList.add('session-user-span');

    const sessionUserRestaurantSpan = document.createElement('span');
    sessionUserRestaurantSpan.id = 'sessionUserRestaurant-' + sessionId + '-' + sessionUserId;
    if (sessionUserRestaurant) {
        sessionUserRestaurantSpan.textContent = sessionUserRestaurant;
    } else if (cUserId == sessionUserId && !sessionUserRestaurant && sessionState == 'STARTED') {
        const restaurantInput = document.createElement('input');
        restaurantInput.id = 'sessionUserRestaurantInput-' + sessionId + '-' + sessionUserId;
        restaurantInput.classList.add('remove-on-end-session-' + sessionId);
        sessionUserRestaurantSpan.appendChild(restaurantInput);

        const submitRestaurantLink = document.createElement('a');
        submitRestaurantLink.href = 'javascript:void(0)';
        submitRestaurantLink.innerHTML = 'Submit Restaurant';
        submitRestaurantLink.classList.add('btn');
        submitRestaurantLink.classList.add('remove-on-end-session-' + sessionId);
        submitRestaurantLink.addEventListener('click', function(event){ sendUserRestaurantSubmit(event, sessionId, cUserId, cName, document.getElementById('sessionUserRestaurantInput-' + sessionId + '-' + sessionUserId).value); }, true);
        sessionUserRestaurantSpan.appendChild(submitRestaurantLink);
    } else {
        sessionUserRestaurantSpan.textContent = '(no restaurant submitted)';
    }
    
    sessionUserRestaurantSpan.classList.add('session-user-span');

    listItem.appendChild(sessionUserNameSpan);
    listItem.appendChild(sessionUserStateSpan);
    listItem.appendChild(sessionUserRestaurantSpan);

    sessionUserContainerList.appendChild(listItem);
}

function updateSessionEndElement(sessionId, restaurant) {
    const sessionStateSpan = document.getElementById('sessionState-' + sessionId);
    sessionStateSpan.textContent = 'Session State: ENDED; Restaurant selected: ' + restaurant;

    const sessionUsersContainerList = document.getElementById('sessionUserList-' + sessionId);
    const childNodes = sessionUsersContainerList.getElementsByTagName('*');
    for (var node of childNodes) {
        node.disabled = true;
    }

    const removeElements = document.getElementsByClassName('remove-on-end-session-' + sessionId);
    for (var removeElementsCtr = 0; removeElementsCtr < removeElements.length; removeElementsCtr++) {
        var removeElement = removeElements[removeElementsCtr];
        removeElement.classList.add('hidden');
    }

    const removeInvite = document.getElementById('invite-' + sessionId);
    if (removeInvite) {
        removeInvite.remove();
    }
    
}

function updateSessionUserStateElement(sessionId, userId, newSessionUserState) {
    const sessionUserStateSpan = document.getElementById('sessionUserState-' + sessionId + '-' + userId);
    sessionUserStateSpan.textContent = newSessionUserState;
}

function updateSessionUserRestaurantElement(sessionId, userId, restaurant) {
    const sessionUserRestaurantSpan = document.getElementById('sessionUserRestaurant-' + sessionId + '-' + userId);
    sessionUserRestaurantSpan.replaceChildren();
    sessionUserRestaurantSpan.textContent = restaurant;
}

function sendCreateSession() {

    if (stompClient) {
        stompClient.send("/app/session/create", {}, JSON.stringify({senderUserId: cUserId, senderName: cName, type: 'SESSION_CREATE'}));
    }
}

function sendUserInvite(event, sessionId, senderUserId, senderName, recepientName, sessionState) {
    
    findUserIdByName(recepientName).then(returnedUserId => {  
        if (stompClient) {
            stompClient.send("/app/session/" + sessionId + "/invite", {}, JSON.stringify({senderUserId: senderUserId, senderName: senderName, sessionId: sessionId, type: 'USER_INVITE', userId: returnedUserId, name: recepientName, sessionState: sessionState}));
        }
    });

    event.preventDefault();
}

function sendUserJoin(event, sessionId, senderUserId, senderName) {
    if (stompClient) {
        stompClient.send("/app/session/" + sessionId + "/join", {}, JSON.stringify({senderUserId: senderUserId, senderName: senderName, sessionId: sessionId, type: 'USER_JOIN'}));
    }
    event.preventDefault();
}

function sendUserRestaurantSubmit(event, sessionId, senderUserId, senderName, restaurant) {
    if (stompClient) {
        stompClient.send("/app/session/" + sessionId + "/restaurant", {}, JSON.stringify({senderUserId: senderUserId, senderName: senderName, sessionId: sessionId, type: 'USER_RESTAURANT_SUBMIT', restaurant: restaurant}));
    }
    event.preventDefault();
}

function sendUserOnline(userId) {
    if (stompClient) {
        stompClient.send("/app/user/online", {}, JSON.stringify({userId: userId, type: 'ONLINE'}));    
    }
}

function sendSessionEnd(event, sessionId, senderUserId, senderName) {
    if (stompClient) {
        stompClient.send("/app/session/" + sessionId + "/end", {}, JSON.stringify({senderUserId: senderUserId, senderName: senderName, sessionId: sessionId, type: 'SESSION_END'}));
    }
    event.preventDefault();
}

async function findUserIdByName(name) {
    const findUserResponse = await fetch(`/users/search?name=${name}`);
    const isFindUserResponseSuccessful = findUserResponse.ok;

    if (isFindUserResponseSuccessful) {
        const findUserResponseData = await findUserResponse.json();
        return findUserResponseData.id;
    }

    return null;
}

async function findOrCreateUser() {
    const findUserResponse = await fetch(`/users/search?name=${cName}`);
    const isFindUserResponseSuccessful = findUserResponse.ok;
    const findUserResponseStatusCode = findUserResponse.status;

    if (isFindUserResponseSuccessful) {
        const findUserResponseData = await findUserResponse.json();
        cUserId = findUserResponseData.id;
    } else {
        if (findUserResponseStatusCode == 404) {
            const createUserResponse = await fetch("/users", {
                method: "POST",
                body: JSON.stringify({
                    name: cName
                }),
                headers: {
                  "Content-type": "application/json; charset=UTF-8"
                }
            });

            const isCreateUserResponseSuccessful = createUserResponse.ok;
            if (isCreateUserResponseSuccessful) {
                const createUserResponseData = await createUserResponse.json();
                cUserId = createUserResponseData.id;
            }
        }
    }
}

usernameForm.addEventListener('submit', connect, true);
createSessionBtn.addEventListener('click', sendCreateSession, true);