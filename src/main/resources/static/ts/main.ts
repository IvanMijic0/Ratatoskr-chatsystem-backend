import SockJS from 'sockjs-client';
import {Client, over} from 'stompjs';

// This is a village person <seljački> frontend code, but it'll do for testing...

const usernamePage: HTMLElement = document.querySelector('#username-page');
const chatPage: HTMLElement = document.querySelector('#chat-page');
const usernameForm: HTMLElement = document.querySelector('#usernameForm');
const messageForm: HTMLElement = document.querySelector('#messageForm');
const messageInput: HTMLInputElement = document.querySelector('#message');
const messageArea: HTMLElement = document.querySelector('#messageArea');
const connectingElement: HTMLDivElement = document.querySelector('.connecting');
const leftSectionContainer: HTMLElement = document.querySelector('.left-section-container');

let stompClient: Client = null;
let username: string = null;

const colors: string[] = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

const addButton = (text: string, id: string) => {
    const button: HTMLButtonElement = document.createElement('button');
    button.className = 'left-section-button';
    button.id = id;
    button.textContent = text;

    leftSectionContainer.appendChild(button);
};

const removeButtonByText = (text: string): void => {
    const leftSectionContainer: HTMLElement = document.getElementById('left-section-container');
    const buttons: HTMLCollectionOf<HTMLButtonElement> = leftSectionContainer.getElementsByTagName('button');

    for (let i: number = 0; i < buttons.length; i++) {
        const button: HTMLButtonElement = buttons[i];
        if (button.textContent === text) {
            leftSectionContainer.removeChild(button);
            break;
        }
    }
};


const getAvatarColor = (messageSender: string): string => {
    const hash: number = messageSender
        .split('')
        .reduce((acc: number, char: string) => 31 * acc + char.charCodeAt(0), 0);
    const index: number = Math.abs(hash) % colors.length;
    return colors[index];
};

const onPublicMessageReceive = (payload: { body: string; }): void => {
    const message = JSON.parse(payload.body);
    const messageElement: HTMLElement = document.createElement('li');
    let messageClass: string;

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
}

const onPrivateMessageReceived = (payload: { body: string; }): void => {
    const message = JSON.parse(payload.body);
}

const onConnected = (): void => {

    // Subscribe to the Public Topic
    stompClient.subscribe('/chatroom/public', onPublicMessageReceive);
    // Subscribe to the Private Topic
    stompClient.subscribe("/user/".concat(username.toString()).concat("/private"), onPrivateMessageReceived)

    // Tell username to the server
    stompClient.send(
        '/app/chat.addUser',
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
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

const sendPublicMessage = (e: { preventDefault: () => void; }): void => {
    e.preventDefault();

    const messageContent: String = messageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage: { sender: String, content: String, type: String } = {
            sender: username,
            content: messageContent,
            type: 'MESSAGE'
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

// Check if Chat-room or User_to_User
messageForm.addEventListener('submit', sendPublicMessage, true);
