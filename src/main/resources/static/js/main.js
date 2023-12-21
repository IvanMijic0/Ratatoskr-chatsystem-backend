"use strict";

// This is a village person <seljački> frontend code, but it'll do for testing...

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');
const leftSectionContainer = document.querySelector('.left-section-container');
const videoButton = document.getElementById("videoButton");
let stompClient = null;
let webRtcPeer = null;
let username = null;
const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];
let userButton;

const addButton = (text, id) => {
    userButton = document.createElement('button');
    userButton.className = 'left-section-button';
    userButton.id = id;
    userButton.textContent = text;
    leftSectionContainer.appendChild(userButton);
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
            userButton.addEventListener('click', handleUserButton, true)
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

const onWebRTCMessageReceived = payload => {
    const signalData = JSON.parse(payload.body);

    if (signalData.type === 'offer') {
        // Handle SDP offer
        receiveVideoCall();
    } else if (signalData.type === 'answer') {
        // Handle SDP answer
        webRtcPeer.signal(signalData);
    } else if (signalData.type === 'candidate') {
        // Handle ICE candidate
        if (
            signalData.sdpMid &&
            signalData.sdpMLineIndex &&
            signalData.candidate
        ) {
            const iceCandidate = new RTCIceCandidate(signalData);
            webRtcPeer.addIceCandidate(iceCandidate); // Assuming webRtcPeer is your SimplePeer instance
        } else {
            console.error('Invalid ICE candidate data:', signalData);
        }
    }
}

const onConnected = () => {
    // // Subscribe to the Public Topic
    stompClient.subscribe('/chatroom/public', onPublicMessageReceive);
    stompClient.subscribe('/webrtc/public', onWebRTCMessageReceived)

    // stompClient.subscribe('/app/webrtc.signal', (message) => {
    //     const signalData = JSON.parse(message.body);
    //     webRtcPeer.signal(signalData);
    // });

    // Subscribe to the Private Topic
    stompClient.subscribe("/user/".concat(username.toString()).concat("/private"), onPrivateMessageReceived);
    // Tell username to the server
    stompClient.send('/app/chat.addUser', {}, JSON.stringify({sender: username, type: 'JOIN'}));
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
const startVideoCall = () => {
    const webRtcPeer = new SimplePeer({initiator: true});

    console.log("Start video call");

    navigator.mediaDevices.getUserMedia({video: true, audio: false})
        .then((localStream) => {
            // Attach the local stream to a video element (assuming you have a local video element)
            const localVideo = document.querySelector('#localVideo');
            localVideo.srcObject = localStream;
        })
        .catch((error) => {
            console.error("getUserMedia error: ", error);
        });

    // Handle signaling for WebRTC
    webRtcPeer.on('signal', (signalData) => {
        stompClient.send('/app/webrtc.signal', {}, JSON.stringify(signalData));
    });
};

const receiveVideoCall = () => {
    webRtcPeer = new SimplePeer({
        initiator: false,
    }); // Set initiator to false

    console.log("Receive video call");

    webRtcPeer.on('stream', (stream) => {
        const remoteVideo = document.querySelector('#remoteVideo');
        console.log("Remote video received")
        remoteVideo.srcObject = stream;
        remoteVideo.play();
    });

    webRtcPeer.on('signal', (signalData) => {
        stompClient.send('/app/webrtc.signal', {}, JSON.stringify(signalData));
    });
};

const handleUserButton = () => {


    console.log(userButton.textContent);
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendPublicMessage, true);
// videoButton.addEventListener('click', receiveVideoCall)