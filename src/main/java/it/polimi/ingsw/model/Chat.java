package it.polimi.ingsw.model;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a chat system in the game.
 * This class implements the Singleton pattern to ensure only one instance exists throughout the application.
 */
public class Chat implements Serializable {

    /**
     * The singleton instance of the Chat class.
     */
    private static Chat instance;

    /**
     * The list of messages stored in the chat.
     */
    private final ArrayList<Message> chat;

    /**
     * Private constructor to initialize the chat instance with an empty list of messages.
     */
    private Chat() {
        chat = new ArrayList<>();
    }


    /**
     * Retrieves the singleton instance of the Chat class.
     *
     * @return The singleton instance of Chat.
     */
    public static Chat getInstance() {
        if(instance == null){
            instance = new Chat();
        }
        return instance;
    }

    /**
     * Sends a message and adds it to the chat.
     *
     * @param mex The message to be added.
     */
    public void sendMessage(Message mex) {
        chat.add(mex);
    }

    /**
     * Retrieves the last message sent in the chat.
     *
     * @return The last message sent.
     */
    public Message getLastMessage() {
        return chat.get(chat.size() - 1);
    }

    /**
     * Retrieves all messages in the chat that have no recipient specified,
     * the recipient represents the name of the player who will receive the message, if null everyone will receive the message
     * @return An ArrayList containing all messages without a recipient
     */
    public ArrayList<Message> getWholeChat() {
        ArrayList<Message> tempArr = new ArrayList<>();
        for(Message m : chat) {
            if(m.getRecipient().isEmpty())
                tempArr.add(m);
        }
        return tempArr;
    }

    /**
     * Clears the chat log if needed since it's a singleton
     */
    public void dispose() {
        chat.clear();
    }

}
