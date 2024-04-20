package it.polimi.ingsw.model;
import java.io.Serializable;
import java.time.LocalTime;

public class Message implements Serializable {

    private final String text;
    private final String author;
    private final String recipient;
    private final LocalTime time;

    public Message(String text, String author) {
        this.text = text;
        this.author = author;
        time = LocalTime.now();
        this.recipient = "";
    }

    public Message(String text, String author, String recipient) {
        this.text = text;
        this.author = author;
        time = LocalTime.now();
        this.recipient = recipient;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getTime() {
        return time.toString();
    }

    @Override
    public String toString() {
        return (time.toString().substring(0,5) + " " + author + ": " + text);
    }
}
