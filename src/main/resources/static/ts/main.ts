'use strict';
import SockJS from 'sockjs-client';
import { Client, over } from 'stompjs';

// This is a village person <seljački> frontend code, but it'll do for testing...

const usernamePage: HTMLElement = document.querySelector('#username-page');
const chatPage: HTMLElement = document.querySelector('#chat-page');
const usernameForm: HTMLElement = document.querySelector('#usernameForm');
const messageForm: HTMLElement = document.querySelector('#messageForm');
const messageInput: HTMLInputElement = document.querySelector('#message');
const messageArea: HTMLElement = document.querySelector('#messageArea');
const connectingElement: HTMLDivElement = document.querySelector('.connecting');

let stompClient: Client = null;
let username: String = null;

const colors: string[] = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

const getAvatarColor = (messageSender: string): string => {
    const hash: number = messageSender
        .split('')
        .reduce((acc: number, char: string) => 31 * acc + char.charCodeAt(0), 0);
    const index: number = Math.abs(hash) % colors.length;
    return colors[index];
};

const onMessageReceive = (payload: { body: string; }): void => {
    const message = JSON.parse(payload.body);

    const messageElement: HTMLElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender.concat(' joined!');
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender.concat(' left!');
    } else {
        messageElement.classList.add('chat-message');

        const avatarElement: HTMLElement = document.createElement('i');
        const avatarText: Text = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        const usernameElement: HTMLSpanElement = document.createElement('span');
        const usernameText: Text = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    const textElement: HTMLParagraphElement = document.createElement('p');
    const messageText: Text = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

const onConnected = (): void => {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceive);

    // Tell username to the server
    stompClient.send(
        '/app/chat.addUser',
        {},
        JSON.stringify({ sender: username, type: 'JOIN' })
    );
    connectingElement.classList.add('hidden');
}

const onError = (): void => {
    connectingElement.textContent = 'Could not connect to WebSocket. Please refresh this page and try again!'
    connectingElement.style.color = 'red';
}

const connect = (e: { preventDefault: () => void; }): void => {
    e.preventDefault();

    const inputElement: HTMLInputElement = document.querySelector('#name') as HTMLInputElement;
    username = inputElement.value.trim();
    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        const socket: WebSocket = new SockJS('/ws');
        stompClient = over(socket);

        stompClient.connect({}, onConnected, onError);
    }
}

const sendMessage = (e: { preventDefault: () => void; }): void => {
    e.preventDefault();

    const messageContent: String = messageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage: { sender: String, content: String, type: String } = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };
        stompClient.send(
            '/app/chat.sendMessage',
            {},
            JSON.stringify(chatMessage)
        );
        messageInput.value = '';
    }
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
