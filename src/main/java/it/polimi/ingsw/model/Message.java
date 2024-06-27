package it.polimi.ingsw.model;
import java.io.Serializable;
import java.time.LocalTime;

/**
 * Message that can be written by a user and sent to others
 */
public class Message implements Serializable {

    /**
     * Original plain text typed by the player
     */
    private final String text;

    /**
     * Author of the message
     */
    private final String author;

    /**
     * Specific recipient if any, an empty string is the default for a public message
     */
    private final String recipient;

    /**
     * Time of the creation of the message
     */
    private final LocalTime time;

    /**
     * Constructor of the class, creates a simple message, public for everyone
     * @param text Original plain text written by the author
     * @param author Author of the message
     */
    public Message(String text, String author) {
        this.text = text;
        this.author = author;
        time = LocalTime.now();
        this.recipient = "";
    }

    /**
     * Constructor of the class, creates a message to be red only by a specific player
     * @param text Original plain text written by the author
     * @param author Author of the message
     * @param recipient Recipient of the message, only user who will be able to read it
     */
    public Message(String text, String author, String recipient) {
        this.text = text;
        this.author = author;
        time = LocalTime.now();
        this.recipient = recipient;
    }

    /**
     * Plain original text
     * @return Text in a string
     */
    public String getText() {
        return text;
    }

    /**
     * Retrieve the name of the user who typed the message
     * @return Author of the message
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Retrieve the name of the user for whom this message is intended
     * @return Recipient of the message
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Retrieve the time of the creation of the message
     * @return Time converted to a string
     */
    public String getTime() {
        return time.toString();
    }

    /**
     * Converts the whole message to a string including all the details
     * @return String representing the message
     */
    @Override
    public String toString() {
        return (time.toString().substring(0,5) + " " + author + ": " + text);
    }
}
