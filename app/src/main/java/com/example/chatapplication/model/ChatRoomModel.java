package com.example.chatapplication.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoomModel {

    String chatroomId;
    List<String> userIds;
    Timestamp lastMessageTimestamp;

    String lastMessageSenderId;
    String lastMessage;

    public ChatRoomModel() {
    }

    public ChatRoomModel(String chatroomId, List<String> userIds, Timestamp lastMessageTimestamp, String lastMessageSenderId) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getchatroomId() {
        return chatroomId;
    }

    public List<String> getuserIds() {
        return userIds;
    }

    public Timestamp getlastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setchatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public void setuserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
