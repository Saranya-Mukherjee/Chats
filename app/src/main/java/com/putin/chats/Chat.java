package com.putin.chats;

public class Chat {

    String sender, receiver, message, reply;

    public Chat(String sender, String receiver, String message, String reply) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.reply = reply;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
