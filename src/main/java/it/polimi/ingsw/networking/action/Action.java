package it.polimi.ingsw.networking.action;

import java.io.Serializable;

// Se finite tutte, da mettere abstract e togliere obj
public class Action implements Serializable {

    private final ActionType type;
    private final String author;
    private final String recipient;

    public Action(ActionType type, String author, String recipient) {
        this.type = type;
        if(author != null)
            this.author = author;
        else
            this.author = "";
        if(recipient != null)
            this.recipient = recipient;
        else
            this.recipient = "";
    }

    public ActionType getType() {
        return type;
    }

    public String getAuthor() {
        return author;
    }

    public String getRecipient() {
        return recipient;
    }
}
