package it.polimi.ingsw.action;

import java.io.Serializable;

public class Action  implements Serializable {

    private final ActionType type;
    private final Object obj;
    private final String author;
    private final String recipient;

    public Action(ActionType type, Object obj, String author, String recipient) {
        this.type = type;
        this.obj = obj;
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

    public Object getObject() {
        return obj;
    }

    public String getAuthor() {
        return author;
    }

    public String getRecipient() {
        return recipient;
    }
}
