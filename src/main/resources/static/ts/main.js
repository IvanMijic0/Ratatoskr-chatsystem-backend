"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const sockjs_client_1 = __importDefault(require("sockjs-client"));
const stompjs_1 = require("stompjs");
// This is a village person <seljački> frontend code, but it'll do for testing...
const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');
const leftSectionContainer = document.querySelector('.left-section-container');
let stompClient = null;
let username = null;
const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];
const addButton = (text, id) => {
    const button = document.createElement('button');
    button.className = 'left-section-button';
    button.id = id;
    button.textContent = text;
    leftSectionContainer.appendChild(button);
};
const removeButtonByText = (text) => {
    const leftSectionContainer = document.getElementById('left-section-container');
    const buttons = leftSectionContainer.getElementsByTagName('button');
    for (let i = 0; i < buttons.length; i++) {
        const button = buttons[i];
        if (button.textContent === text) {
            leftSectionContainer.removeChild(button);
            break;
        }
    }
};
const getAvatarColor = (messageSender) => {
    const hash = messageSender
        .split('')
        .reduce((acc, char) => 31 * acc + char.charCodeAt(0), 0);
    const index = Math.abs(hash) % colors.length;
    return colors[index];
};
const onPublicMessageReceive = (payload) => {
    const message = JSON.parse(payload.body);
    const messageElement = document.createElement('li');
    let messageClass;
    switch (message.type) {
        case 'JOIN':
            addButton(message.sender, "users-button");
            messageClass = 'event-message';
            message.content = message.sender.concat(' joined!');
            break;
        case 'LEAVE':
            removeButtonByText(message.sender);
            messageClass = 'event-message';
            message.content = message.sender.concat(' left!');
            break;
        default:
            messageClass = 'chat-message';
            const avatarElement = document.createElement('i');
            const avatarText = document.createTextNode(message.sender[0]);
            avatarElement.appendChild(avatarText);
            avatarElement.style['background-color'] = getAvatarColor(message.sender);
            messageElement.appendChild(avatarElement);
            const usernameElement = document.createElement('span');
            const usernameText = document.createTextNode(message.sender);
            usernameElement.appendChild(usernameText);
            messageElement.appendChild(usernameElement);
            break;
    }
    messageElement.classList.add(messageClass);
    const textElement = document.createElement('p');
    const messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
};
const onPrivateMessageReceived = (payload) => {
    const message = JSON.parse(payload.body);
};
const onConnected = () => {
    // Subscribe to the Public Topic
    stompClient.subscribe('/chatroom/public', onPublicMessageReceive);
    // Subscribe to the Private Topic
    stompClient.subscribe("/user/".concat(username.toString()).concat("/private"), onPrivateMessageReceived);
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
        const socket = new sockjs_client_1.default('/ws');
        stompClient = (0, stompjs_1.over)(socket);
        stompClient.connect({}, onConnected, onError);
    }
};
const sendPublicMessage = (e) => {
    e.preventDefault();
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage = {
            sender: username,
            content: messageContent,
            type: 'MESSAGE'
        };
        stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
};
usernameForm.addEventListener('submit', connect, true);
// Check if Chat-room or User_to_User
messageForm.addEventListener('submit', sendPublicMessage, true);
//# sourceMappingURL=main.js.map