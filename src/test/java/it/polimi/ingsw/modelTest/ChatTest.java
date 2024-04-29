package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.model.Chat;
import it.polimi.ingsw.model.Message;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChatTest {
    @Test
    void chatTest2() {
        Chat chat = Chat.getInstance();
        Message msh = new Message("a", "b");
        chat.sendMessage(msh);
        Message msh2 = new Message("c", "d");
        chat.sendMessage(msh2);
        Message msh3 = new Message("e","f","g");
        chat.sendMessage(msh3);
        assertEquals(chat.getLastMessage().getText(),"e");
        assertEquals(chat.getLastMessage().getAuthor(),"f");
        assertEquals(chat.getLastMessage().getRecipient(),"g");
        assertEquals(chat.getWholeChat().get(0).getText(),"a");
        assertEquals(chat.getWholeChat().get(0).getAuthor(),"b");
        assertEquals(chat.getWholeChat().get(1).getText(),"c");
        assertEquals(chat.getWholeChat().get(1).getAuthor(),"d");
    }
}
