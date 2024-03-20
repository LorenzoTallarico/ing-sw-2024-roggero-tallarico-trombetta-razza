package it.polimi.ingsw.model;
import java.util.ArrayList;

public class Chat {
    private static Chat instance;
    private final ArrayList<Message> chat;

    //singleton
    private Chat() {
        chat = new ArrayList<>();
    }

    public static Chat getInstance(){
        if(instance == null){
            instance = new Chat();
        }
        return instance;
    }

    public void sendMessage(Message mex) {
        chat.add(mex);
    }

    public Message getLastMessage() {
        return chat.get(chat.size() - 1);
    }

    public ArrayList<Message> getWholeChat() {
        return chat;
    }
}
