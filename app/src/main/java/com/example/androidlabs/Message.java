package com.example.androidlabs;

public class Message{

    private String text;
    private Type type;

    public Message(String messageTxt, Type type){
        this.text = messageTxt;
        this.type = type;
    }

    public String getText(){
        return text;
    }

    public Type getType(){
        return type;
    }

    public enum Type{
        SENT,
        RECEIVED
    }
}