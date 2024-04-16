package it.polimi.ingsw.model;
import java.io.Serializable;
import java.time.LocalTime;

public class Message implements Serializable {

    private final String text;
    private final String author;
    private final LocalTime time;

    public Message(){
        text = null;
        author = null;
        time = null;
    }

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

    public void print() {
        System.out.println(time.toString() + " " + author + ": " + text + ".");
    }
}
