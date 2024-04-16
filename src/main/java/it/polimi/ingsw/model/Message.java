package it.polimi.ingsw.model;
import java.io.Serializable;
import java.time.LocalTime;

public class Message implements Serializable {

    private final String text;
    private final String author;
    private final LocalTime time;


    public Message(String text, String author){
        this.text = text;
        this.author = author;
        time = LocalTime.now();
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public String getTime() {
        return time.toString();
    }

    @Override
    public String toString() {
        return (time.toString().substring(0,5) + " " + author + ": " + text + ".");
    }
}
