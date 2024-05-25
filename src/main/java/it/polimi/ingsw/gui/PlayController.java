package it.polimi.ingsw.gui;

import it.polimi.ingsw.model.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;

import java.util.ArrayList;
import java.util.Objects;

public class PlayController {

    private String myNickname = "";
    private Player myPlayer;
    private String currentRecipient = "";
    private ArrayList<Player> otherPlayers;
    public ArrayList<Message> messagesToSendQueue = new ArrayList<>();
    private final Image singleUser = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/icons/single-user.png")));
    private final Image multipleUsers = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/icons/multiple-users.png")));

    @FXML
    private TextField chatFld;

    @FXML
    private TextArea chatTextArea;

    @FXML
    private ImageView chooseRecipientImg;

    @FXML
    private MenuItem everyoneItem, p1Item, p2Item, p3Item;

    @FXML
    protected void onSendMessageButtonClick() {
        String s = chatFld.getText().trim();
        if(!s.isEmpty()) {
            Message m = new Message(s, myNickname, currentRecipient);
            messagesToSendQueue.add(m);
            if(!m.getRecipient().isEmpty()) {
                s = ">>> " + m.getTime().substring(0,5) + " You " + " > " + m.getRecipient() + ": " + m.getText() + "\n";
                chatTextArea.appendText(s);
            }
        }
        chatFld.clear();
    }

    @FXML
    protected void onEnterKeyPressedChatFld(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER))
            onSendMessageButtonClick();
    }

    @FXML
    protected void onChatOptionsEvent(Event e){
        initializeChatOptions(otherPlayers);
        String id = ((MenuItem) e.getSource()).getId();
        switch(id) {
            case "everyoneItem":
                currentRecipient = "";
                chooseRecipientImg.setImage(multipleUsers);
                chatFld.setPromptText("Write here to chat with others");
                break;
            case "p1Item":
                currentRecipient = p1Item.getText();
                chooseRecipientImg.setImage(singleUser);
                chatFld.setPromptText("This is a pm for " + p1Item.getText());
                break;
            case "p2Item":
                currentRecipient = p2Item.getText();
                chooseRecipientImg.setImage(singleUser);
                chatFld.setPromptText("This is a pm for " + p2Item.getText());
                break;
            case "p3Item":
                currentRecipient = p3Item.getText();
                chooseRecipientImg.setImage(singleUser);
                chatFld.setPromptText("This is a pm for " + p3Item.getText());
                break;
            default: // shouldn't happen
                currentRecipient = "";
                chooseRecipientImg.setImage(multipleUsers);
                chatFld.setPromptText("Write here to chat with others");
                break;
        }
    }

    //public method
    public void displayChatMessage(Message m) {
        String s = m.toString() + "\n";
        if(m.getRecipient().equalsIgnoreCase(myNickname))
            s = ">>> " + s;
        chatTextArea.appendText(s);
    }

    public void initializeChatOptions(ArrayList<Player> players) {
        otherPlayers = players;
        switch(otherPlayers.size()) {
            case 3:
                p3Item.setText(otherPlayers.get(2).getName());
                p3Item.setVisible(true);
            case 2:
                p2Item.setText(otherPlayers.get(1).getName());
                p2Item.setVisible(true);
            case 1:
                p1Item.setText(otherPlayers.get(0).getName());
                p1Item.setVisible(true);
                break;
            default:
                break;
        }
    }

    //getters and setters
    public void setNickname(String nick) {
        myNickname = nick;
    }

}
