package com.example.blooddonarfinder;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Chats")
public class Chats extends ParseObject {
    private String dateTime = "DateTime";
    private String textMessage = "TextMessage";
    private String type = "Type";
    private String sender = "Sender";
    private String receiver = "Receiver";
    private String parseFile = "ChatFile";

    public Chats() {
    }



    public String getDateTime() {
        return getString("DateTime");
    }

    public void setDateTime(String DateTime) {
       put(dateTime,DateTime);
    }

    public String getTextMessage() {
        return getString("TextMessage");
    }

    public void setTextMessage(String TextMessage) {
        put(textMessage,TextMessage);
    }

    public ParseFile getImageMessage() {
        return getParseFile("ChatFile");
    }

    public void setImageMessage(ParseFile ChatFile) {
        put(parseFile,ChatFile);
    }

    public String getType() {
        return getString("Type");
    }

    public void setType(String Type) {
       put(type,Type);
    }

    public String getSender() {
        return getString("Sender");
    }

    public void setSender(String Sender) {
       put(sender,Sender);
    }

    public String getReceiver() {
        return getString("Receiver");
    }

    public void setReceiver(String Receiver) {
        put(receiver,Receiver);
    }
}
