package it.polimi.ingsw.gui;

import it.polimi.ingsw.model.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Objects;

public class PlayController {

    private String myNickname = "";
    private Player myPlayer;
    private String currentRecipient = "";
    private ArrayList<Player> otherPlayers;
    public int starterChoice = 0;
    public int achievementChoice = 0;
    private ArrayList<AchievementCard> choosableAchievements;
    private ArrayList<AchievementCard> achievements;
    public ArrayList<Message> messagesToSendQueue = new ArrayList<>();
    private final Image singleUser = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/icons/single-user.png")));
    private final Image multipleUsers = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/icons/multiple-users.png")));



    @FXML
    private TextField chatFld;

    @FXML
    private TextArea chatTextArea;

    @FXML
    private ImageView chooseRecipientImg, selectCard1Img, selectCard2Img;

    @FXML
    private MenuItem everyoneItem, p1Item, p2Item, p3Item;

    @FXML
    private AnchorPane cardChoicePane;

    @FXML
    private  Label selectCardLbl;

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
    
    @FXML
    protected void onSelectCard1In() {
        selectCard1Img.setLayoutY(selectCard1Img.getLayoutY() - 6);
        selectCard1Img.setLayoutX(selectCard1Img.getLayoutX() - 9);
        selectCard1Img.setFitHeight(selectCard1Img.getFitHeight() + 12);
        selectCard1Img.setFitWidth(selectCard1Img.getFitWidth() + 18);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        selectCard2Img.setEffect(colorAdjust);
    }

    @FXML
    protected void onSelectCard1Out() {
        selectCard1Img.setLayoutY(selectCard1Img.getLayoutY() + 6);
        selectCard1Img.setLayoutX(selectCard1Img.getLayoutX() + 9);
        selectCard1Img.setFitHeight(selectCard1Img.getFitHeight() - 12);
        selectCard1Img.setFitWidth(selectCard1Img.getFitWidth() - 18);
        selectCard2Img.setEffect(null);
    }

    @FXML
    protected void onSelectCard2In() {
        selectCard2Img.setLayoutY(selectCard2Img.getLayoutY() - 6);
        selectCard2Img.setLayoutX(selectCard2Img.getLayoutX() - 9);
        selectCard2Img.setFitHeight(selectCard2Img.getFitHeight() + 12);
        selectCard2Img.setFitWidth(selectCard2Img.getFitWidth() + 18);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        selectCard1Img.setEffect(colorAdjust);
    }

    @FXML
    protected void onSelectCard2Out() {
        selectCard2Img.setLayoutY(selectCard2Img.getLayoutY() + 6);
        selectCard2Img.setLayoutX(selectCard2Img.getLayoutX() + 9);
        selectCard2Img.setFitHeight(selectCard2Img.getFitHeight() - 12);
        selectCard2Img.setFitWidth(selectCard2Img.getFitWidth() - 18);
        selectCard1Img.setEffect(null);
    }

    @FXML
    protected void onSelectCard1Click() {
        if(selectCardLbl.getText().equals("Choose the side of your starter card"))
            starterChoice = 1;
        else if(selectCardLbl.getText().equals("Choose your own secret achievement")) {
            achievementChoice = 1;
            cardChoicePane.setDisable(true);
            cardChoicePane.setVisible(false);
        }
        selectCard1Img.setDisable(true);
        selectCard2Img.setDisable(true);
    }

    @FXML
    protected void onSelectCard2Click() {
        if(selectCardLbl.getText().equals("Choose the side of your starter card"))
            starterChoice = 2;
        else if(selectCardLbl.getText().equals("Choose your own secret achievement")) {
            achievementChoice = 2;
            cardChoicePane.setDisable(true);
            cardChoicePane.setVisible(false);
        }
        selectCard1Img.setDisable(true);
        selectCard2Img.setDisable(true);
    }

    private void updateTableCards(ArrayList<GoldCard> commonGold, Resource goldDeck,  ArrayList<ResourceCard> commonResource, Resource resourceDeck) {
        //to do
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

    public void passStarterCard(StarterCard str, Player self, ArrayList<GoldCard> commonGold, Resource goldDeck,  ArrayList<ResourceCard> commonResource, Resource resourceDeck) {
        myPlayer = self;
        updateTableCards(commonGold, goldDeck, commonResource, resourceDeck);
        Image frontSide = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/front/" + str.getID() + ".png")));
        Image backSide = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/back/" + str.getID() + ".png")));
        selectCardLbl.setText("Choose the side of your starter card");
        selectCard1Img.setImage(frontSide);
        selectCard2Img.setImage(backSide);
        selectCard1Img.setDisable(false);
        selectCard2Img.setDisable(false);
        cardChoicePane.setVisible(true);
    }

    public void passAchievement(ArrayList<AchievementCard> ach, ArrayList<AchievementCard> achievements) {
        this.achievements = achievements;
        choosableAchievements = ach;
        Image ach1 = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/front/" + ach.get(0).getID() + ".png")));
        Image ach2 = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/front/" + ach.get(1).getID() + ".png")));
        selectCardLbl.setText("Choose your own secret achievement");
        selectCard1Img.setImage(ach1);
        selectCard2Img.setImage(ach2);
        selectCard1Img.setDisable(false);
        selectCard2Img.setDisable(false);
        cardChoicePane.setVisible(true);

    }

    //getters and setters
    public void setNickname(String nick) {
        myNickname = nick;
    }

}
