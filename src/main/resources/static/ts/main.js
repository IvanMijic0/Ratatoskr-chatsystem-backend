"use strict";

// This is a village person <seljački> frontend code, but it'll do for testing...
const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');
let stompClient = null;
let username = null;
const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];
const getAvatarColor = (messageSender) => {
    const hash = messageSender
        .split('')
        .reduce((acc, char) => 31 * acc + char.charCodeAt(0), 0);
    const index = Math.abs(hash) % colors.length;
    return colors[index];
};
const onMessageReceive = (payload) => {
    const message = JSON.parse(payload.body);
    const messageElement = document.createElement('li');
    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender.concat(' joined!');
    }
    else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender.concat(' left!');
    }
    else {
        messageElement.classList.add('chat-message');
        const avatarElement = document.createElement('i');
        const avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);
        messageElement.appendChild(avatarElement);
        const usernameElement = document.createElement('span');
        const usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }
    const textElement = document.createElement('p');
    const messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
};
const onConnected = () => {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceive);
    // Tell username to the server
    stompClient.send('/app/chat.addUser', {}, JSON.stringify({ sender: username, type: 'JOIN' }));
    connectingElement.classList.add('hidden');
};
const onError = () => {
    connectingElement.textContent = 'Could not connect to WebSocket. Please refresh this page and try again!';
    connectingElement.style.color = 'red';
};
const connect = (e) => {
    e.preventDefault();
    const inputElement = document.querySelector('#name');
    username = inputElement.value.trim();
    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
};
const sendMessage = (e) => {
    e.preventDefault();
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };
        stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
};
usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
//# sourceMappingURL=main.js.map