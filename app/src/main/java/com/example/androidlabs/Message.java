package com.example.androidlabs;

public class Message {

    private final Long id;
    private final String text;
    private final Type type;

    public Message(String text, Type type) {
        this.id = null;
        this.text = text;
        this.type = type;
    }

    public Message(Long id, String text, Type type) {
        this.id = id;
        this.text = text;
        this.type = type;
    }

    public Message(Long id, Message message) {
        this.id = id;
        this.text = message.getText();
        this.type = message.getType();
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        SENT,
        RECEIVED
    }
}