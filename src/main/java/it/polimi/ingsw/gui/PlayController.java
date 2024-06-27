package it.polimi.ingsw.gui;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.toserver.ChosenDrawCardAction;
import it.polimi.ingsw.networking.action.toserver.PlacingCardAction;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.*;

/** Controller for the actual game part of the graphical application*/
public class PlayController {

    /**
     * Nickname of the local player
     */
    private String myNickname = "";

    /**
     * Reference to the local player
     */
    private Player myPlayer;

    /**
     * Current recipient for chat messages, default is all ""
     */
    private String currentRecipient = "";

    /**
     * ArrayList containing all the other players in the game
     */
    private ArrayList<Player> otherPlayers;

    /**
     * Integer representing the choice for the side of the starter card
     */
    public int starterChoice = 0;

    /**
     * Integer representing the choice of the secret achievement
     */
    public int achievementChoice = 0;

    /**
     * Integer representing the choice of the desired card to draw from the table
     */
    public int drawChoice = 0;

    /**
     * Integer representing the choice for the desired card to place in the playground
     */
    public int placeChoice = -1;

    /**
     * Reference to the starter card
     */
    private StarterCard strCard;

    /**
     * Available achievements for the choice of the secret achievements
     */
    private ArrayList<AchievementCard> choosableAchievements;

    /**
     * Common achievements and possibly also the secret one when chosen
     */
    private ArrayList<AchievementCard> achievements;

    /**
     * Indicates if the player can place (placing phase)
     */
    public boolean canPlace = false;

    /**
     * Indicates if the player placed a card (placing phase)
     */
    public boolean hasPlaced = false;

    /**
     * Indicates if the player can draw (drawing phase)
     */
    public boolean canDraw = false;

    /**
     * Queue of message sent by the player from the graphical application
     * ready to be retrieved from the ClientApp
     */
    public ArrayList<Message> messagesToSendQueue = new ArrayList<>();

    /**
     * Images for the chat options button
     */
    private final Image singleUser = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/icons/single-user.png")));
    private final Image multipleUsers = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/icons/multiple-users.png")));

    /**
     * Images for cards in the hand
     */
    private ArrayList<Image> frontHand, backHand;

    /**
     * Images for the achievements card (common + secret)
     */
    private final Image backAchievement = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/back/087.png")));
    private ArrayList<Image> frontAchievements;

    /**
     * Group of buttons to pick the desired card view (hand, table, achievements)
     */
    private ToggleGroup toggleGroup;

    /**
     * Reference to the cards on the table
     */
    private ArrayList<GoldCard> commonGold;
    private ArrayList<ResourceCard> commonResource;

    /**
     * Images for the cards on the table
     */
    private ArrayList<Image> frontCommonGold, frontCommonResource, backCommonGold, backCommonResource;
    private Image backGoldDeck, backResourceDeck;

    /**
     * Images used in the playground to display empty cells or not chosen cards
     */
    private final Image borderCard = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/misc/border-card.png")));
    private final Image anonymousCard = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/misc/anonymous-card.png")));

    /**
     * Constants for the original sizes of the cards in the playground and of the rows and columns of the gridPane
     */
    private final int constCellWidth = 105, constCellHeight = 54, constPch = 90, constPcw = 135;

    /**
     * Current sizes of the cards in the playground and of the rows and columns of the gridPane
     */
    private double cellWidth = 105, cellHeight = 54, pch = 90, pcw = 135;

    /**
     * Array of imageview to display the cards in the cells of the playground gridPane
     */
    private ImageView[][] gridpaneArray = null;

    /**
     * Action created by a listener when placing a card and needed by the ClientApp
     */
    private PlacingCardAction placeAction;

    /**
     * Coordinates representing the positions of the number in the scoreboard
     */
    private final double[][] positions = {  {0.27935, 0.91866}, {0.5, 0.91866}, {0.72064, 0.91866},
                                            {0.83603, 0.81438}, {0.61133, 0.81438}, {0.39068, 0.81438}, {0.16801, 0.81438},
                                            {0.16801, 0.71011}, {0.39068, 0.71011}, {0.61133, 0.71011}, {0.83603, 0.71011},
                                            {0.83603, 0.60583}, {0.61133, 0.60583}, {0.39068, 0.60583}, {0.16801, 0.60583},
                                            {0.16801, 0.50156}, {0.39068, 0.50156}, {0.61133, 0.50156}, {0.83603, 0.50156},
                                            {0.83603, 0.39728}, {0.5, 0.34410},  {0.16801, 0.39728},
                                            {0.16801, 0.29301},
                                            {0.16801, 0.18873},
                                            {0.29554, 0.10114}, {0.5, 0.08342}, {0.70647, 0.10114},
                                            {0.83603, 0.18873},
                                            {0.83603, 0.29301}, {0.5, 0.20959}};

    /**
     * Map linking a color to the image of a pawn
     */
    private Map<it.polimi.ingsw.model.Color, ImageView> colorToPawns;

    /**
     * Map linking a color to the offset needed to avoid overlapping pawns in the scoreboard
     */
    private Map<it.polimi.ingsw.model.Color, int[]> colorToOffset;

    /**
     * Images of pawns and scoreboard, used in the scoreboard pane
     */
    @FXML
    private ImageView redPawnImg, greenPawnImg, yellowPawnImg, bluePawnImg, scoreboardImg;

    /**
     * Field used by the user to type their messages
     */
    @FXML
    private TextField chatFld;

    /**
     * TextArea where chat messages are displayed
     */
    @FXML
    private TextArea chatTextArea;

    /**
     * ImageViews for most of the images (options and cards)
     */
    @FXML
    private ImageView chooseRecipientImg, selectCard1Img, selectCard2Img, handCard1Img, handCard2Img, handCard3Img, achievementCard1Img, achievementCard2Img, achievementCard3Img, tableGold1Img, tableGold2Img, tableResource1Img, tableResource2Img, tableGoldDeckImg, tableResourceDeckImg, tableBackDeck1Img, tableBackDeck2Img, resultImg;

    /**
     * MenuItems for the chat-recipient options
     */
    @FXML
    private MenuItem everyoneItem, p1Item, p2Item, p3Item;

    /**
     * All the panes used by the graphical application (hand, table, chat, playground, ...)
     */
    @FXML
    private AnchorPane fatherPane, cardChoicePane, scoreboardPane, handPane, achievementPane, tablePane;

    /**
     * All the gridpanes used by the graphical application (playground, scoreboard, ...)
     */
    @FXML
    private GridPane playgroundGridPane, resourcesGrid, pawnPlayersGrid;

    /**
     * ScrollPane used to make the chat scrollable
     */
    @FXML
    private ScrollPane playgroundScrollPane;

    /**
     * All the label used in the graphical application
     */
    @FXML
    private Label selectCardLbl, alertLbl, oldAlertLbl, jarCountLbl, scrollCountLbl, plumeCountLbl, wolfCountLbl, mushroomCountLbl, leafCountLbl, butterflyCountLbl, resultLbl;

    /**
     * Buttons of the toggle group used by to pick a cards-view (table, hand, achievements)
     */
    @FXML
    private ToggleButton tableBtn, handBtn, achievementBtn;

    /**
     * ChoiceBox to select a player and view their playground
     */
    @FXML
    private ChoiceBox<String> playgroundChoiceBox;

    /**
     * Slider used to zoom in or zoom out the playground, increasing or decreasing the sizes of cards, rows and columns
     */
    @FXML
    private Slider zoomSlider;


    /**
     * Initializes vital components after @FXML fields are populated.
     * It sets up a listener for the zoom slider, the group of buttons to
     * choose the cards to view (hand / table / achievements), the maps used
     * to visualize the pawns on the scoreboard and the choicebox to select
     * the playground
     */
    @FXML
    public void initialize() {
        initializeGridpane();
        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                pch = constPch * newValue.doubleValue();
                pcw = constPcw * newValue.doubleValue();
                cellHeight = constCellHeight * newValue.doubleValue();
                cellWidth = constCellWidth * newValue.doubleValue();
                if(playgroundChoiceBox.getValue() == null) return;
                if(playgroundChoiceBox.getValue().equalsIgnoreCase("You ")) {
                    printPlayground();
                    if(canPlace)
                        placeablePlayground();
                } else if(otherPlayers != null){
                    for(Player ply : otherPlayers)
                        if(ply.getName().equalsIgnoreCase(playgroundChoiceBox.getValue()))
                            printPlayground(ply);
                }
            }
        });
        toggleGroup = new ToggleGroup();
        tableBtn.setToggleGroup(toggleGroup);
        handBtn.setToggleGroup(toggleGroup);
        achievementBtn.setToggleGroup(toggleGroup);
        handBtn.setSelected(true);
        playgroundChoiceBox.getItems().add("You ");
        colorToPawns = Map.of(
                it.polimi.ingsw.model.Color.RED, redPawnImg,
                it.polimi.ingsw.model.Color.BLUE, bluePawnImg,
                it.polimi.ingsw.model.Color.GREEN, greenPawnImg,
                it.polimi.ingsw.model.Color.YELLOW, yellowPawnImg
        );
        colorToOffset = Map.of(
                it.polimi.ingsw.model.Color.RED, new int[]{-1, -1},
                it.polimi.ingsw.model.Color.BLUE, new int[]{+1, -1},
                it.polimi.ingsw.model.Color.GREEN, new int[]{+1, +1},
                it.polimi.ingsw.model.Color.YELLOW, new int[]{-1, +1}
        );
    }

//---------------- PLAYGROUND FXML METHODS ---------------------

    /**
     * Switches from the current playground view to another
     */
    @FXML
    protected void onChoiceBoxClick() {
        String targetPlayer = playgroundChoiceBox.getValue();
        boolean foundPlayer = false;
        if(otherPlayers != null)
            for(Player plyr : otherPlayers)
                if(plyr.getName().equalsIgnoreCase(targetPlayer)) {
                foundPlayer = true;
                printPlayground(plyr);
                break;
                }
        if(!foundPlayer) {
            printPlayground();
            if(canPlace)
                placeablePlayground();
        }
    }

    /**
     * Resets the playground view to the middle of panel
     */
    @FXML
    protected void onResetViewClick() {
        /* default values
        cellWidth = 105;
        cellHeight = 54;
        pch = 90;
        pcw = 135;*/
        playgroundScrollPane.setHvalue(playgroundScrollPane.getHmax() / 2);
        playgroundScrollPane.setVvalue(playgroundScrollPane.getVmax() / 2);
    }
    
//---------------- SCOREBOARD FXML METHODS ---------------------

    /**
     * Enables the scoreboard view when the mouse hovers it, showing the players names and
     * hiding the resources & items tables
     */
    @FXML
    protected void onScoreboardImgIn() {
        Glow glow = new Glow();
        glow.setLevel(0.1);
        scoreboardImg.setEffect(glow);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setInput(glow);
        scoreboardImg.setEffect(dropShadow);
        resourcesGrid.setVisible(false);
        pawnPlayersGrid.setVisible(true);
    }

    /**
     * Disables the scoreboard view when the mouse exits its area
     */
    @FXML
    protected void onScoreboardImgOut() {
        scoreboardImg.setEffect(null);
        resourcesGrid.setVisible(true);
        pawnPlayersGrid.setVisible(false);
    }

//---------------- CHAT FXML METHODS ---------------------

    /**
     * Sends the message to the chat, assigning the string to a queue and displaying
     * the message in the chat panel
     */
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

    /**
     * Sends the message to the chat when pressing the enter key while typing
     * @param event the key pressed from the keyboard input
     */
    @FXML
    protected void onEnterKeyPressedChatFld(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER))
            onSendMessageButtonClick();
    }

    /**
     * Switches the current recipient to the selected
     * @param e the event used to locate the source and identify the desired recipient
     */
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

//---------------- SELECT STARTER & ACHIEVEMENT CARDS FXML METHODS ---------------------

    /**
     * Applies graphical effects to the starters / achievements cards when hovering the first one
     */
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

    /**
     * Resets the graphical effects on the starters / achievements cards
     */
    @FXML
    protected void onSelectCard1Out() {
        selectCard1Img.setLayoutY(selectCard1Img.getLayoutY() + 6);
        selectCard1Img.setLayoutX(selectCard1Img.getLayoutX() + 9);
        selectCard1Img.setFitHeight(selectCard1Img.getFitHeight() - 12);
        selectCard1Img.setFitWidth(selectCard1Img.getFitWidth() - 18);
        selectCard2Img.setEffect(null);
    }

    /**
     * Applies graphical effects to the starters / achievements cards when hovering the second one
     */
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

    /**
     * Resets the graphical effects on the starters / achievements cards
     */
    @FXML
    protected void onSelectCard2Out() {
        selectCard2Img.setLayoutY(selectCard2Img.getLayoutY() + 6);
        selectCard2Img.setLayoutX(selectCard2Img.getLayoutX() + 9);
        selectCard2Img.setFitHeight(selectCard2Img.getFitHeight() - 12);
        selectCard2Img.setFitWidth(selectCard2Img.getFitWidth() - 18);
        selectCard1Img.setEffect(null);
    }

    /**
     * Selects the first card, assigns the values to be retrieved from the client
     * and initializes the scoreboard if the setup is completed
     */
    @FXML
    protected void onSelectCard1Click() {
        if(selectCardLbl.getText().equals("Choose the side of your starter card")) {
            starterChoice = 1;
            strCard.setFront(true);
            myPlayer.setArea(new Playground());
            myPlayer.getArea().setSpace(strCard, 40, 40);
            printPlayground();
        } else if(selectCardLbl.getText().equals("Choose your own secret achievement")) {
            achievementChoice = 1;
            initializeScoreboard();
            achievements.add(0,choosableAchievements.get(achievementChoice-1));
            for(AchievementCard achCard : achievements)
                achCard.setFront(true);
            frontAchievements.add(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/front/" + achievements.get(0).getSideID() + ".png"))));
            achievementCard3Img.setImage(frontAchievements.get(2));
            achievementCard2Img.setImage(frontAchievements.get(1));
            achievementCard1Img.setImage(frontAchievements.get(0));
            achievementCard3Img.setVisible(true);
        }
        selectCard1Img.setDisable(true);
        selectCard2Img.setDisable(true);
    }

    /**
     * Selects the second card, assigns the values to be retrieved from the client
     * and initializes the scoreboard if the setup is completed
     */
    @FXML
    protected void onSelectCard2Click() {
        if(selectCardLbl.getText().equals("Choose the side of your starter card")) {
            starterChoice = 2;
            strCard.setFront(false);
            myPlayer.setArea(new Playground());
            myPlayer.getArea().setSpace(strCard, 40, 40);
            printPlayground();
        } else if(selectCardLbl.getText().equals("Choose your own secret achievement")) {
            achievementChoice = 2;
            initializeScoreboard();
            achievements.add(0,choosableAchievements.get(achievementChoice-1));
            for(AchievementCard achCard : achievements)
                achCard.setFront(true);
            frontAchievements.add(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/front/" + achievements.get(0).getSideID() + ".png"))));
            achievementCard3Img.setImage(frontAchievements.get(2));
            achievementCard2Img.setImage(frontAchievements.get(1));
            achievementCard1Img.setImage(frontAchievements.get(0));
            achievementCard3Img.setVisible(true);
        }
        selectCard1Img.setDisable(true);
        selectCard2Img.setDisable(true);
    }

//---------------- HAND PANE METHODS ---------------------

    /**
     * Flips all the cards in the hand to view the other side
     */
    @FXML
    protected void onFlipCardsClick() {
        if(myPlayer.getHand().isEmpty())
            return;
        boolean tempSide = !myPlayer.getHand().get(0).isFront();
        switch(myPlayer.getHand().size()) {
            case 3:
                myPlayer.getHand().get(2).setFront(tempSide);
                handCard3Img.setImage(tempSide ? frontHand.get(2) : backHand.get(2));
            case 2:
                myPlayer.getHand().get(1).setFront(tempSide);
                handCard2Img.setImage(tempSide ? frontHand.get(1) : backHand.get(1));
            case 1:
                myPlayer.getHand().get(0).setFront(tempSide);
                handCard1Img.setImage(tempSide ? frontHand.get(0) : backHand.get(0));
                break;
            default: //shouldn't happen
                break;
        }
    }

    /**
     * Selects the first card if the player is in the placing phase
     */
    @FXML
    protected void onHandCard1Click() {
        if(canPlace) {
            placeChoice = 1;
        }
    }

    /**
     * Applies graphical effects to the cards in the hand when hovering the first one
     */
    @FXML
    protected void onHandCard1In() {
        handCard1Img.setLayoutY(handCard1Img.getLayoutY() - 6);
        handCard1Img.setLayoutX(handCard1Img.getLayoutX() - 9);
        handCard1Img.setFitHeight(handCard1Img.getFitHeight() + 12);
        handCard1Img.setFitWidth(handCard1Img.getFitWidth() + 18);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        if(handCard2Img.isVisible())
            handCard2Img.setEffect(colorAdjust);
        if(handCard3Img.isVisible())
            handCard3Img.setEffect(colorAdjust);
    }

    /**
     * Resets the graphical effects on the cards in the hand
     */
    @FXML
    protected void onHandCard1Out() {
        handCard1Img.setLayoutY(handCard1Img.getLayoutY() + 6);
        handCard1Img.setLayoutX(handCard1Img.getLayoutX() + 9);
        handCard1Img.setFitHeight(handCard1Img.getFitHeight() - 12);
        handCard1Img.setFitWidth(handCard1Img.getFitWidth() - 18);
        if(handCard2Img.isVisible())
            handCard2Img.setEffect(null);
        if(handCard3Img.isVisible())
            handCard3Img.setEffect(null);
    }

    /**
     * Flips the side of the first card in the hand when scrolling with the mouse
     */
    @FXML
    protected void onHandCard1Scroll() {
        myPlayer.getHand().get(0).setFront(!myPlayer.getHand().get(0).isFront());
        handCard1Img.setImage(myPlayer.getHand().get(0).isFront() ? frontHand.get(0) : backHand.get(0));
    }

    /**
     * Selects the second card if the player is in the placing phase
     */
    @FXML
    protected void onHandCard2Click() {
        if(canPlace) {
            placeChoice = 2;
        }
    }

    /**
     * Applies graphical effects to the cards in the hand when hovering the second one
     */
    @FXML
    protected void onHandCard2In() {
        handCard2Img.setLayoutY(handCard2Img.getLayoutY() - 6);
        handCard2Img.setLayoutX(handCard2Img.getLayoutX() - 9);
        handCard2Img.setFitHeight(handCard2Img.getFitHeight() + 12);
        handCard2Img.setFitWidth(handCard2Img.getFitWidth() + 18);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        handCard1Img.setEffect(colorAdjust);
        if(handCard3Img.isVisible())
            handCard3Img.setEffect(colorAdjust);
    }

    /**
     * Resets the graphical effects on the cards in the hand
     */
    @FXML
    protected void onHandCard2Out() {
        handCard2Img.setLayoutY(handCard2Img.getLayoutY() + 6);
        handCard2Img.setLayoutX(handCard2Img.getLayoutX() + 9);
        handCard2Img.setFitHeight(handCard2Img.getFitHeight() - 12);
        handCard2Img.setFitWidth(handCard2Img.getFitWidth() - 18);
        handCard1Img.setEffect(null);
        if(handCard3Img.isVisible())
            handCard3Img.setEffect(null);
    }

    /**
     * Flips the side of the second card in the hand when scrolling with the mouse
     */
    @FXML
    protected void onHandCard2Scroll() {
        myPlayer.getHand().get(1).setFront(!myPlayer.getHand().get(1).isFront());
        handCard2Img.setImage(myPlayer.getHand().get(1).isFront() ? frontHand.get(1) : backHand.get(1));
    }

    /**
     * Selects the third card if the player is in the placing phase
     */
    @FXML
    protected void onHandCard3Click() {
        if(canPlace) {
            placeChoice = 3;
        }
    }

    /**
     * Applies graphical effects to the cards in the hand when hovering the third one
     */
    @FXML
    protected void onHandCard3In() {
        handCard3Img.setLayoutY(handCard3Img.getLayoutY() - 6);
        handCard3Img.setLayoutX(handCard3Img.getLayoutX() - 9);
        handCard3Img.setFitHeight(handCard3Img.getFitHeight() + 12);
        handCard3Img.setFitWidth(handCard3Img.getFitWidth() + 18);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        handCard1Img.setEffect(colorAdjust);
        handCard2Img.setEffect(colorAdjust);
    }

    /**
     * Resets the graphical effects on the cards in the hand
     */
    @FXML
    protected void onHandCard3Out() {
        handCard3Img.setLayoutY(handCard3Img.getLayoutY() + 6);
        handCard3Img.setLayoutX(handCard3Img.getLayoutX() + 9);
        handCard3Img.setFitHeight(handCard3Img.getFitHeight() - 12);
        handCard3Img.setFitWidth(handCard3Img.getFitWidth() - 18);
        handCard1Img.setEffect(null);
        handCard2Img.setEffect(null);
    }

    /**
     * Flips the side of the third card in the hand when scrolling with the mouse
     */
    @FXML
    protected void onHandCard3Scroll() {
        myPlayer.getHand().get(2).setFront(!myPlayer.getHand().get(2).isFront());
        handCard3Img.setImage(myPlayer.getHand().get(2).isFront() ? frontHand.get(2) : backHand.get(2));
    }

//---------------- ACHIEVEMENTS PANE METHODS ---------------------

    /**
     * Flips all the achievement cards to view the other side
     */
    @FXML
    protected void onFlipAchievementsClick() {
        if(achievements.isEmpty())
            return;
        boolean tempSide = !achievements.get(0).isFront();
        switch(achievements.size()) {
            case 3:
                achievements.get(2).setFront(tempSide);
                achievementCard3Img.setImage(tempSide ? frontAchievements.get(2) : backAchievement);
            case 2:
                achievements.get(1).setFront(tempSide);
                achievementCard2Img.setImage(tempSide ? frontAchievements.get(1) : backAchievement);
            case 1:
                achievements.get(0).setFront(tempSide);
                achievementCard1Img.setImage(tempSide ? frontAchievements.get(0) : backAchievement);
                break;
            default: //shouldn't happen
                break;
        }
    }

    /**
     * Applies graphical effects to the achievement cards when hovering the first one
     */
    @FXML
    protected void onAchievementCard1In() {
        achievementCard1Img.setLayoutY(achievementCard1Img.getLayoutY() - 6);
        achievementCard1Img.setLayoutX(achievementCard1Img.getLayoutX() - 9);
        achievementCard1Img.setFitHeight(achievementCard1Img.getFitHeight() + 12);
        achievementCard1Img.setFitWidth(achievementCard1Img.getFitWidth() + 18);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        achievementCard2Img.setEffect(colorAdjust);
        if(achievementCard3Img.isVisible())
            achievementCard3Img.setEffect(colorAdjust);
    }

    /**
     * Resets the graphical effects on the achievement cards
     */
    @FXML
    protected void onAchievementCard1Out() {
        achievementCard1Img.setLayoutY(achievementCard1Img.getLayoutY() + 6);
        achievementCard1Img.setLayoutX(achievementCard1Img.getLayoutX() + 9);
        achievementCard1Img.setFitHeight(achievementCard1Img.getFitHeight() - 12);
        achievementCard1Img.setFitWidth(achievementCard1Img.getFitWidth() - 18);
        achievementCard2Img.setEffect(null);
        if(achievementCard3Img.isVisible())
            achievementCard3Img.setEffect(null);
    }

    /**
     * Flips the side of the first achievement card when scrolling with the mouse
     */
    @FXML
    protected void onAchievementCard1Scroll() {
        achievements.get(0).setFront(!achievements.get(0).isFront());
        achievementCard1Img.setImage(achievements.get(0).isFront() ? frontAchievements.get(0) : backAchievement);
    }

    /**
     * Applies graphical effects to the achievement cards when hovering the second one
     */
    @FXML
    protected void onAchievementCard2In() {
        achievementCard2Img.setLayoutY(achievementCard2Img.getLayoutY() - 6);
        achievementCard2Img.setLayoutX(achievementCard2Img.getLayoutX() - 9);
        achievementCard2Img.setFitHeight(achievementCard2Img.getFitHeight() + 12);
        achievementCard2Img.setFitWidth(achievementCard2Img.getFitWidth() + 18);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        achievementCard1Img.setEffect(colorAdjust);
        if(achievementCard3Img.isVisible())
            achievementCard3Img.setEffect(colorAdjust);
    }

    /**
     * Resets the graphical effects on the achievement cards
     */
    @FXML
    protected void onAchievementCard2Out() {
        achievementCard2Img.setLayoutY(achievementCard2Img.getLayoutY() + 6);
        achievementCard2Img.setLayoutX(achievementCard2Img.getLayoutX() + 9);
        achievementCard2Img.setFitHeight(achievementCard2Img.getFitHeight() - 12);
        achievementCard2Img.setFitWidth(achievementCard2Img.getFitWidth() - 18);
        achievementCard1Img.setEffect(null);
        if(achievementCard3Img.isVisible())
            achievementCard3Img.setEffect(null);
    }

    /**
     * Flips the side of the second achievement card when scrolling with the mouse
     */

    @FXML
    protected void onAchievementCard2Scroll() {
        achievements.get(1).setFront(!achievements.get(1).isFront());
        achievementCard2Img.setImage(achievements.get(1).isFront() ? frontAchievements.get(1) : backAchievement);
    }

    /**
     * Applies graphical effects to the achievement cards when hovering the third one
     */
    @FXML
    protected void onAchievementCard3In() {
        achievementCard3Img.setLayoutY(achievementCard3Img.getLayoutY() - 6);
        achievementCard3Img.setLayoutX(achievementCard3Img.getLayoutX() - 9);
        achievementCard3Img.setFitHeight(achievementCard3Img.getFitHeight() + 12);
        achievementCard3Img.setFitWidth(achievementCard3Img.getFitWidth() + 18);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        achievementCard1Img.setEffect(colorAdjust);
        achievementCard2Img.setEffect(colorAdjust);
    }

    /**
     * Resets the graphical effects on the achievement cards
     */
    @FXML
    protected void onAchievementCard3Out() {
        achievementCard3Img.setLayoutY(achievementCard3Img.getLayoutY() + 6);
        achievementCard3Img.setLayoutX(achievementCard3Img.getLayoutX() + 9);
        achievementCard3Img.setFitHeight(achievementCard3Img.getFitHeight() - 12);
        achievementCard3Img.setFitWidth(achievementCard3Img.getFitWidth() - 18);
        achievementCard1Img.setEffect(null);
        achievementCard2Img.setEffect(null);
    }

    /**
     * Flips the side of the third achievement card when scrolling with the mouse
     */
    @FXML
    protected void onAchievementCard3Scroll() {
        achievements.get(2).setFront(!achievements.get(2).isFront());
        achievementCard3Img.setImage(achievements.get(2).isFront() ? frontAchievements.get(2) : backAchievement);
    }

//---------------- PANEL CHOICE BUTTONS ---------------------

    /**
     * Switches the cards-view showing the table one and hiding the others
     */
    @FXML
    protected void onTableButtonClick() {
        if(!handBtn.isSelected() && !achievementBtn.isSelected())
            tableBtn.setSelected(true);
        tablePane.setVisible(true);
        handPane.setVisible(false);
        achievementPane.setVisible(false);
    }

    /**
     * Switches the cards-view showing the hand one and hiding the others
     */
    @FXML
    protected void onHandButtonClick() {
        if(!tableBtn.isSelected() && !achievementBtn.isSelected())
            handBtn.setSelected(true);
        handPane.setVisible(true);
        tablePane.setVisible(false);
        achievementPane.setVisible(false);
    }

    /**
     * Switches the cards-view showing the achievement one and hiding the others
     */
    @FXML
    protected void onAchievementButtonClick() {
        if(!tableBtn.isSelected() && !handBtn.isSelected())
            achievementBtn.setSelected(true);
        achievementPane.setVisible(true);
        tablePane.setVisible(false);
        handPane.setVisible(false);
    }

//---------------- TABLE CARD PANE METHODS ---------------------

    /**
     * Flips the cards on the table to view the other side
     */
    @FXML
    protected void onFlipTableClick() {
        boolean tempSide;
        if(!commonGold.isEmpty())
            tempSide = !commonGold.get(0).isFront();
        else if(!commonResource.isEmpty())
            tempSide = !commonResource.get(0).isFront();
        else
            return;
        for(GoldCard gc : commonGold)
            gc.setFront(tempSide);
        for(ResourceCard rc : commonResource)
            rc.setFront(tempSide);
        if(tableGold1Img.isVisible())
            tableGold1Img.setImage(commonGold.get(0).isFront() ? frontCommonGold.get(0) : backCommonGold.get(0));
        if(tableGold2Img.isVisible())
            tableGold2Img.setImage(commonGold.get(1).isFront() ? frontCommonGold.get(1) : backCommonGold.get(1));
        if(tableResource1Img.isVisible())
            tableResource1Img.setImage(commonResource.get(0).isFront() ? frontCommonResource.get(0) : backCommonResource.get(0));
        if(tableResource2Img.isVisible())
            tableResource2Img.setImage(commonResource.get(1).isFront() ? frontCommonResource.get(1) : backCommonResource.get(1));
    }

    /**
     * Selects the first gold card on the table if the player is in the drawing phase
     */
    @FXML
    protected void onTableGold1Click() {
        if(canDraw) {
            drawChoice = 1;
        }
    }

    /**
     * Applies graphical effects to the cards on the table when hovering the first gold one
     */
    @FXML
    protected void onTableGold1In() {
        tableGold1Img.setFitWidth(tableGold1Img.getFitWidth() + 12);
        tableGold1Img.setFitHeight(tableGold1Img.getFitHeight() + 8);
        tableGold1Img.setLayoutX(tableGold1Img.getLayoutX() - 6);
        tableGold1Img.setLayoutY(tableGold1Img.getLayoutY() - 4);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        if(tableGold2Img.isVisible())
            tableGold2Img.setEffect(colorAdjust);
        if(tableResource1Img.isVisible())
            tableResource1Img.setEffect(colorAdjust);
        if(tableResource2Img.isVisible())
            tableResource2Img.setEffect(colorAdjust);
        if(tableGoldDeckImg.isVisible())
            tableGoldDeckImg.setEffect(colorAdjust);
        if(tableBackDeck1Img.isVisible())
            tableBackDeck1Img.setEffect(colorAdjust);
        if(tableResourceDeckImg.isVisible())
            tableResourceDeckImg.setEffect(colorAdjust);
        if(tableBackDeck2Img.isVisible())
            tableBackDeck2Img.setEffect(colorAdjust);
    }

    /**
     * Resets the graphical effects on the cards on the table
     */
    @FXML
    protected void onTableGold1Out() {
        tableGold1Img.setFitWidth(tableGold1Img.getFitWidth() - 12);
        tableGold1Img.setFitHeight(tableGold1Img.getFitHeight() - 8);
        tableGold1Img.setLayoutX(tableGold1Img.getLayoutX() + 6);
        tableGold1Img.setLayoutY(tableGold1Img.getLayoutY() + 4);
        if(tableGold2Img.isVisible())
            tableGold2Img.setEffect(null);
        if(tableResource1Img.isVisible())
            tableResource1Img.setEffect(null);
        if(tableResource2Img.isVisible())
            tableResource2Img.setEffect(null);
        if(tableGoldDeckImg.isVisible())
            tableGoldDeckImg.setEffect(null);
        if(tableBackDeck1Img.isVisible())
            tableBackDeck1Img.setEffect(null);
        if(tableResourceDeckImg.isVisible())
            tableResourceDeckImg.setEffect(null);
        if(tableBackDeck2Img.isVisible())
            tableBackDeck2Img.setEffect(null);
    }

    /**
     * Flips the side of the first gold card on the table when scrolling with the mouse
     */
    @FXML
    protected void onTableGold1Scroll() {
        commonGold.get(0).setFront(!commonGold.get(0).isFront());
        tableGold1Img.setImage(commonGold.get(0).isFront() ? frontCommonGold.get(0) : backCommonGold.get(0));
    }

    /**
     * Selects the second gold card on the table if the player is in the drawing phase
     */
    @FXML
    protected void onTableGold2Click() {
        if(canDraw) {
            drawChoice = 2;
        }
    }

    /**
     * Applies graphical effects to the cards on the table when hovering the second gold one
     */
    @FXML
    protected void onTableGold2In() {
        tableGold2Img.setFitWidth(tableGold2Img.getFitWidth() + 12);
        tableGold2Img.setFitHeight(tableGold2Img.getFitHeight() + 8);
        tableGold2Img.setLayoutX(tableGold2Img.getLayoutX() - 6);
        tableGold2Img.setLayoutY(tableGold2Img.getLayoutY() - 4);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        if(tableGold1Img.isVisible())
            tableGold1Img.setEffect(colorAdjust);
        if(tableResource1Img.isVisible())
            tableResource1Img.setEffect(colorAdjust);
        if(tableResource2Img.isVisible())
            tableResource2Img.setEffect(colorAdjust);
        if(tableGoldDeckImg.isVisible())
            tableGoldDeckImg.setEffect(colorAdjust);
        if(tableBackDeck1Img.isVisible())
            tableBackDeck1Img.setEffect(colorAdjust);
        if(tableResourceDeckImg.isVisible())
            tableResourceDeckImg.setEffect(colorAdjust);
        if(tableBackDeck2Img.isVisible())
            tableBackDeck2Img.setEffect(colorAdjust);
    }

    /**
     * Resets the graphical effects on the cards on the table
     */
    @FXML
    protected void onTableGold2Out() {
        tableGold2Img.setFitWidth(tableGold2Img.getFitWidth() - 12);
        tableGold2Img.setFitHeight(tableGold2Img.getFitHeight() - 8);
        tableGold2Img.setLayoutX(tableGold2Img.getLayoutX() + 6);
        tableGold2Img.setLayoutY(tableGold2Img.getLayoutY() + 4);
        if(tableGold1Img.isVisible())
            tableGold1Img.setEffect(null);
        if(tableResource1Img.isVisible())
            tableResource1Img.setEffect(null);
        if(tableResource2Img.isVisible())
            tableResource2Img.setEffect(null);
        if(tableGoldDeckImg.isVisible())
            tableGoldDeckImg.setEffect(null);
        if(tableBackDeck1Img.isVisible())
            tableBackDeck1Img.setEffect(null);
        if(tableResourceDeckImg.isVisible())
            tableResourceDeckImg.setEffect(null);
        if(tableBackDeck2Img.isVisible())
            tableBackDeck2Img.setEffect(null);
    }

    /**
     * Flips the side of the second gold card on the table when scrolling with the mouse
     */
    @FXML
    protected void onTableGold2Scroll() {
        commonGold.get(1).setFront(!commonGold.get(1).isFront());
        tableGold2Img.setImage(commonGold.get(1).isFront() ? frontCommonGold.get(1) : backCommonGold.get(1));
    }

    /**
     * Selects the first resource card on the table if the player is in the drawing phase
     */
    @FXML
    protected void onTableResource1Click() {
        if(canDraw) {
            drawChoice = 3;
        }
    }

    /**
     * Applies graphical effects to the cards on the table when hovering the first resource one
     */
    @FXML
    protected void onTableResource1In() {
        tableResource1Img.setFitWidth(tableResource1Img.getFitWidth() + 12);
        tableResource1Img.setFitHeight(tableResource1Img.getFitHeight() + 8);
        tableResource1Img.setLayoutX(tableResource1Img.getLayoutX() - 6);
        tableResource1Img.setLayoutY(tableResource1Img.getLayoutY() - 4);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        if(tableGold1Img.isVisible())
            tableGold1Img.setEffect(colorAdjust);
        if(tableGold2Img.isVisible())
            tableGold2Img.setEffect(colorAdjust);
        if(tableResource2Img.isVisible())
            tableResource2Img.setEffect(colorAdjust);
        if(tableGoldDeckImg.isVisible())
            tableGoldDeckImg.setEffect(colorAdjust);
        if(tableBackDeck1Img.isVisible())
            tableBackDeck1Img.setEffect(colorAdjust);
        if(tableResourceDeckImg.isVisible())
            tableResourceDeckImg.setEffect(colorAdjust);
        if(tableBackDeck2Img.isVisible())
            tableBackDeck2Img.setEffect(colorAdjust);
    }

    /**
     * Resets the graphical effects on the cards on the table
     */
    @FXML
    protected void onTableResource1Out() {
        tableResource1Img.setFitWidth(tableResource1Img.getFitWidth() - 12);
        tableResource1Img.setFitHeight(tableResource1Img.getFitHeight() - 8);
        tableResource1Img.setLayoutX(tableResource1Img.getLayoutX() + 6);
        tableResource1Img.setLayoutY(tableResource1Img.getLayoutY() + 4);
        if(tableGold1Img.isVisible())
            tableGold1Img.setEffect(null);
        if(tableGold2Img.isVisible())
            tableGold2Img.setEffect(null);
        if(tableResource2Img.isVisible())
            tableResource2Img.setEffect(null);
        if(tableGoldDeckImg.isVisible())
            tableGoldDeckImg.setEffect(null);
        if(tableBackDeck1Img.isVisible())
            tableBackDeck1Img.setEffect(null);
        if(tableResourceDeckImg.isVisible())
            tableResourceDeckImg.setEffect(null);
        if(tableBackDeck2Img.isVisible())
            tableBackDeck2Img.setEffect(null);
    }

    /**
     * Flips the side of the first resource card on the table when scrolling with the mouse
     */
    @FXML
    protected void onTableResource1Scroll() {
        commonResource.get(0).setFront(!commonResource.get(0).isFront());
        tableResource1Img.setImage(commonResource.get(0).isFront() ? frontCommonResource.get(0) : backCommonResource.get(0));
    }

    /**
     * Selects the second resource card on the table if the player is in the drawing phase
     */
    @FXML
    protected void onTableResource2Click() {
        if(canDraw) {
            drawChoice = 4;
        }
    }

    /**
     * Applies graphical effects to the cards on the table when hovering the second resource one
     */
    @FXML
    protected void onTableResource2In() {
        tableResource2Img.setFitWidth(tableResource2Img.getFitWidth() + 12);
        tableResource2Img.setFitHeight(tableResource2Img.getFitHeight() + 8);
        tableResource2Img.setLayoutX(tableResource2Img.getLayoutX() - 6);
        tableResource2Img.setLayoutY(tableResource2Img.getLayoutY() - 4);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        if(tableGold1Img.isVisible())
            tableGold1Img.setEffect(colorAdjust);
        if(tableGold2Img.isVisible())
            tableGold2Img.setEffect(colorAdjust);
        if(tableResource1Img.isVisible())
            tableResource1Img.setEffect(colorAdjust);
        if(tableGoldDeckImg.isVisible())
            tableGoldDeckImg.setEffect(colorAdjust);
        if(tableBackDeck1Img.isVisible())
            tableBackDeck1Img.setEffect(colorAdjust);
        if(tableResourceDeckImg.isVisible())
            tableResourceDeckImg.setEffect(colorAdjust);
        if(tableBackDeck2Img.isVisible())
            tableBackDeck2Img.setEffect(colorAdjust);
    }

    /**
     * Resets the graphical effects on the cards on the table
     */
    @FXML
    protected void onTableResource2Out() {
        tableResource2Img.setFitWidth(tableResource2Img.getFitWidth() - 12);
        tableResource2Img.setFitHeight(tableResource2Img.getFitHeight() - 8);
        tableResource2Img.setLayoutX(tableResource2Img.getLayoutX() + 6);
        tableResource2Img.setLayoutY(tableResource2Img.getLayoutY() + 4);
        if(tableGold1Img.isVisible())
            tableGold1Img.setEffect(null);
        if(tableGold2Img.isVisible())
            tableGold2Img.setEffect(null);
        if(tableResource1Img.isVisible())
            tableResource1Img.setEffect(null);
        if(tableGoldDeckImg.isVisible())
            tableGoldDeckImg.setEffect(null);
        if(tableBackDeck1Img.isVisible())
            tableBackDeck1Img.setEffect(null);
        if(tableResourceDeckImg.isVisible())
            tableResourceDeckImg.setEffect(null);
        if(tableBackDeck2Img.isVisible())
            tableBackDeck2Img.setEffect(null);
    }

    /**
     * Flips the side of the second resource card on the table when scrolling with the mouse
     */
    @FXML
    protected void onTableResource2Scroll() {
        commonResource.get(1).setFront(!commonResource.get(1).isFront());
        tableResource2Img.setImage(commonResource.get(1).isFront() ? frontCommonResource.get(1) : backCommonResource.get(1));
    }

    /**
     * Selects the card on top of the gold deck on the table if the player is in the drawing phase
     */
    @FXML
    protected void onTableGoldDeckClick() {
        if(canDraw) {
            drawChoice = 5;
        }
    }

    /**
     * Applies graphical effects to the cards on the table when hovering the gold deck
     */
    @FXML
    protected void onTableGoldDeckIn() {
        tableGoldDeckImg.setFitWidth(tableGoldDeckImg.getFitWidth() + 12);
        tableGoldDeckImg.setFitHeight(tableGoldDeckImg.getFitHeight() + 8);
        tableGoldDeckImg.setLayoutX(tableGoldDeckImg.getLayoutX() - 6);
        tableGoldDeckImg.setLayoutY(tableGoldDeckImg.getLayoutY() - 4);
        tableBackDeck1Img.setFitWidth(tableBackDeck1Img.getFitWidth() + 12);
        tableBackDeck1Img.setFitHeight(tableBackDeck1Img.getFitHeight() + 8);
        tableBackDeck1Img.setLayoutX(tableBackDeck1Img.getLayoutX() - 6);
        tableBackDeck1Img.setLayoutY(tableBackDeck1Img.getLayoutY() - 4);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        if(tableGold1Img.isVisible())
            tableGold1Img.setEffect(colorAdjust);
        if(tableGold2Img.isVisible())
            tableGold2Img.setEffect(colorAdjust);
        if(tableResource1Img.isVisible())
            tableResource1Img.setEffect(colorAdjust);
        if(tableResource2Img.isVisible())
            tableResource2Img.setEffect(colorAdjust);
        if(tableResourceDeckImg.isVisible())
            tableResourceDeckImg.setEffect(colorAdjust);
        if(tableBackDeck2Img.isVisible())
            tableBackDeck2Img.setEffect(colorAdjust);
    }

    /**
     * Resets the graphical effects on the cards on the table
     */
    @FXML
    protected void onTableGoldDeckOut() {
        tableGoldDeckImg.setFitWidth(tableGoldDeckImg.getFitWidth() - 12);
        tableGoldDeckImg.setFitHeight(tableGoldDeckImg.getFitHeight() - 8);
        tableGoldDeckImg.setLayoutX(tableGoldDeckImg.getLayoutX() + 6);
        tableGoldDeckImg.setLayoutY(tableGoldDeckImg.getLayoutY() + 4);
        tableBackDeck1Img.setFitWidth(tableBackDeck1Img.getFitWidth() - 12);
        tableBackDeck1Img.setFitHeight(tableBackDeck1Img.getFitHeight() - 8);
        tableBackDeck1Img.setLayoutX(tableBackDeck1Img.getLayoutX() + 6);
        tableBackDeck1Img.setLayoutY(tableBackDeck1Img.getLayoutY() + 4);
        if(tableGold1Img.isVisible())
            tableGold1Img.setEffect(null);
        if(tableGold2Img.isVisible())
            tableGold2Img.setEffect(null);
        if(tableResource1Img.isVisible())
            tableResource1Img.setEffect(null);
        if(tableResource2Img.isVisible())
            tableResource2Img.setEffect(null);
        if(tableResourceDeckImg.isVisible())
            tableResourceDeckImg.setEffect(null);
        if(tableBackDeck2Img.isVisible())
            tableBackDeck2Img.setEffect(null);
    }

    /**
     * Selects the card on top of the gold deck on the table if the player is in the drawing phase
     */
    @FXML
    protected void onTableResourceDeckClick() {
        if(canDraw) {
            drawChoice = 6;
        }
    }

    /**
     * Applies graphical effects to the cards on the table when hovering the gold deck
     */
    @FXML
    protected void onTableResourceDeckIn() {
        tableResourceDeckImg.setFitWidth(tableResourceDeckImg.getFitWidth() + 12);
        tableResourceDeckImg.setFitHeight(tableResourceDeckImg.getFitHeight() + 8);
        tableResourceDeckImg.setLayoutX(tableResourceDeckImg.getLayoutX() - 6);
        tableResourceDeckImg.setLayoutY(tableResourceDeckImg.getLayoutY() - 4);
        tableBackDeck2Img.setFitWidth(tableBackDeck2Img.getFitWidth() + 12);
        tableBackDeck2Img.setFitHeight(tableBackDeck2Img.getFitHeight() + 8);
        tableBackDeck2Img.setLayoutX(tableBackDeck2Img.getLayoutX() - 6);
        tableBackDeck2Img.setLayoutY(tableBackDeck2Img.getLayoutY() - 4);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.2);
        if(tableGold1Img.isVisible())
            tableGold1Img.setEffect(colorAdjust);
        if(tableGold2Img.isVisible())
            tableGold2Img.setEffect(colorAdjust);
        if(tableResource1Img.isVisible())
            tableResource1Img.setEffect(colorAdjust);
        if(tableResource2Img.isVisible())
            tableResource2Img.setEffect(colorAdjust);
        if(tableGoldDeckImg.isVisible())
            tableGoldDeckImg.setEffect(colorAdjust);
        if(tableBackDeck1Img.isVisible())
            tableBackDeck1Img.setEffect(colorAdjust);
    }

    /**
     * Resets the graphical effects on the cards on the table
     */
    @FXML
    protected void onTableResourceDeckOut() {
        tableResourceDeckImg.setFitWidth(tableResourceDeckImg.getFitWidth() - 12);
        tableResourceDeckImg.setFitHeight(tableResourceDeckImg.getFitHeight() - 8);
        tableResourceDeckImg.setLayoutX(tableResourceDeckImg.getLayoutX() + 6);
        tableResourceDeckImg.setLayoutY(tableResourceDeckImg.getLayoutY() + 4);
        tableBackDeck2Img.setFitWidth(tableBackDeck2Img.getFitWidth() - 12);
        tableBackDeck2Img.setFitHeight(tableBackDeck2Img.getFitHeight() - 8);
        tableBackDeck2Img.setLayoutX(tableBackDeck2Img.getLayoutX() + 6);
        tableBackDeck2Img.setLayoutY(tableBackDeck2Img.getLayoutY() + 4);
        if(tableGold1Img.isVisible())
            tableGold1Img.setEffect(null);
        if(tableGold2Img.isVisible())
            tableGold2Img.setEffect(null);
        if(tableResource1Img.isVisible())
            tableResource1Img.setEffect(null);
        if(tableResource2Img.isVisible())
            tableResource2Img.setEffect(null);
        if(tableGoldDeckImg.isVisible())
            tableGoldDeckImg.setEffect(null);
        if(tableBackDeck1Img.isVisible())
            tableBackDeck1Img.setEffect(null);
    }

//---------------- PRIVATE MISC METHODS ---------------------

    /**
     * Initializes the grid-pane of the playground setting up rows, columns and properties
     */
    private void initializeGridpane() {
        ColumnConstraints colConst = new ColumnConstraints();
        colConst.setHalignment(HPos.CENTER);
        colConst.setMaxWidth(cellWidth);
        colConst.setMinWidth(cellWidth);
        colConst.setPrefWidth(cellWidth);
        RowConstraints rowConst = new RowConstraints();
        rowConst.setValignment(VPos.CENTER);
        rowConst.setMinHeight(cellHeight);
        rowConst.setMaxHeight(cellHeight);
        rowConst.setPrefHeight(cellHeight);;
        for(int i = 0; i < 81; i++) {
            playgroundGridPane.getColumnConstraints().add(colConst);
            playgroundGridPane.getRowConstraints().add(rowConst);
        }
        playgroundScrollPane.setHvalue(playgroundScrollPane.getHmax() / 2);
        playgroundScrollPane.setVvalue(playgroundScrollPane.getVmax() / 2);
    }

    /**
     * Resets the playground, displays the cards of a given player and updates
     * the resources and items table.
     * @param playa the player of the desired playground
     */
    private void printPlayground(Player playa) {
        Playground area = playa.getArea();
        playgroundGridPane.getChildren().clear();
        playgroundGridPane.getColumnConstraints().clear();
        playgroundGridPane.getRowConstraints().clear();
        initializeGridpane();
        //placing cards on the grid
        LinkedHashMap<Card, int[]> orderedCoords = (LinkedHashMap) area.getOrderedCoords();
        ArrayList<Card> keys = new ArrayList<>(orderedCoords.keySet());
        ImageView cImg;
        String cPath;
        int[] cCoords;
        for(Card crd : keys) {
            cCoords = orderedCoords.get(crd);
            cPath = crd.isFront()  ? "front/" : "back/";
            if(crd.getClass() == StarterCard.class)
                cPath = cPath.concat(((StarterCard) crd).getID());
            else if(crd.getClass() == ResourceCard.class)
                cPath = cPath.concat(((ResourceCard) crd).getSideID());
            else if(crd.getClass() == GoldCard.class)
                cPath = cPath.concat(((GoldCard) crd).getSideID());
            else return;
            cImg = new ImageView(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/" + cPath + ".png"))));
            cImg.setFitHeight(pch);
            cImg.setFitWidth(pcw);
            cImg.setEffect(new DropShadow(5, Color.BLACK));
            playgroundGridPane.add(cImg, cCoords[1], cCoords[0]);
        }
        //displaying values of available items and resources
        jarCountLbl.setText(""+area.countItems(Item.JAR));
        plumeCountLbl.setText(""+area.countItems(Item.PLUME));
        scrollCountLbl.setText(""+area.countItems(Item.SCROLL));
        wolfCountLbl.setText(""+area.countResources(Resource.WOLF));
        mushroomCountLbl.setText(""+area.countResources(Resource.MUSHROOM));
        butterflyCountLbl.setText(""+area.countResources(Resource.BUTTERFLY));
        leafCountLbl.setText(""+area.countResources(Resource.LEAF));
    }

    /**
     * Displays the playground of the local player
     */
    private void printPlayground() {
        printPlayground(myPlayer);
    }

    /**
     * Display and or updates the pawns on the scoreboard
     */
    private void printScoreboard() {
        ImageView tempPawn = colorToPawns.get(myPlayer.getColor());
        tempPawn.setLayoutX(scoreboardImg.getFitWidth()*positions[Math.min(29, myPlayer.getPoints())][0] - 9);
        tempPawn.setLayoutY(scoreboardImg.getFitHeight()*positions[Math.min(29, myPlayer.getPoints())][1] - 12);
        tempPawn.setLayoutX(tempPawn.getLayoutX() + colorToOffset.get(myPlayer.getColor())[0]*((tempPawn.getFitWidth()/2) + 3));
        tempPawn.setLayoutY(tempPawn.getLayoutY() + colorToOffset.get(myPlayer.getColor())[1]*((tempPawn.getFitHeight()/2) + 3));
        tempPawn.setVisible(true);
        for(Player pp : otherPlayers) {
            tempPawn = colorToPawns.get(pp.getColor());
            tempPawn.setLayoutX(scoreboardImg.getFitWidth()*positions[Math.min(29, pp.getPoints())][0] - 9);
            tempPawn.setLayoutY(scoreboardImg.getFitHeight()*positions[Math.min(29, pp.getPoints())][1] - 12);
            tempPawn.setLayoutX(tempPawn.getLayoutX() + colorToOffset.get(pp.getColor())[0]*((tempPawn.getFitWidth()/2) + 3));
            tempPawn.setLayoutY(tempPawn.getLayoutY() + colorToOffset.get(pp.getColor())[1]*((tempPawn.getFitHeight()/2) + 3));
            tempPawn.setVisible(true);
        }
    }

    /**
     * Displays the cells where cards could be placed when in placing phase
     * Sets up the listeners to display the selected card when hovering the playground
     */
    private void placeablePlayground() {
        Playground area = myPlayer.getArea();
        ImageView cell;
        gridpaneArray = new ImageView[80][80];
        for(int i = area.getWestBound() == 0 ? 0 : area.getWestBound() - 1 ; i <= (area.getEastBound() == 80 ? 80 : area.getEastBound() + 1); i++) {
            for(int j = area.getNorthBound() == 0 ? 0 : area.getNorthBound() - 1; j <= (area.getSouthBound() == 80 ? 80 : area.getSouthBound() + 1); j++) {
                if(area.getSpace(j, i).isFree() && !area.getSpace(j, i).isDead()) {
                    cell = new ImageView(borderCard);
                    cell.setPickOnBounds(true);
                    cell.setOpacity(0.35);
                    cell.setCursor(Cursor.HAND);
                    cell.setFitWidth(pcw);
                    cell.setFitHeight(pch);
                    cell.setOnMouseEntered(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if(canPlace) {
                                switch(placeChoice) {
                                    case 1:
                                        ((ImageView)event.getSource()).setImage(handCard1Img.getImage());
                                        break;
                                    case 2:
                                        ((ImageView)event.getSource()).setImage(handCard2Img.getImage());
                                        break;
                                    case 3:
                                        ((ImageView)event.getSource()).setImage(handCard3Img.getImage());
                                        break;
                                    case -1:
                                        ((ImageView)event.getSource()).setImage(anonymousCard);
                                        break;
                                    default:
                                        return;
                                }
                                ((ImageView)event.getSource()).setFitHeight(pch);
                                ((ImageView)event.getSource()).setFitWidth(pcw);
                                ((ImageView)event.getSource()).setOpacity(1);
                                if(placeChoice != -1) {
                                    boolean foundCell = false;
                                    boolean canBePlacedHere = false;
                                    for(int tCol = area.getWestBound() == 0 ? 0 : area.getWestBound() - 1 ; tCol <= (area.getEastBound() == 80 ? 80 : area.getEastBound() + 1) && !foundCell; tCol++) {
                                        for (int tRow = area.getNorthBound() == 0 ? 0 : area.getNorthBound() - 1; tRow <= (area.getSouthBound() == 80 ? 80 : area.getSouthBound() + 1) && !foundCell; tRow++) {
                                            if (gridpaneArray[tCol][tRow] != null && gridpaneArray[tCol][tRow].equals((ImageView) event.getSource())) {
                                                canBePlacedHere = myPlayer.placeable(myPlayer.getHand().get(placeChoice-1), tRow, tCol);
                                                foundCell = true;
                                            }
                                        }
                                    }
                                    if(!canBePlacedHere) {
                                        ColorAdjust blackAndWhite = new ColorAdjust();
                                        blackAndWhite.setSaturation(-1);
                                        blackAndWhite.setBrightness(-0.3);
                                        DropShadow shadow = new DropShadow(5, Color.BLACK);
                                        shadow.setInput(blackAndWhite);
                                        ((ImageView) event.getSource()).setEffect(shadow);
                                        ((ImageView) event.getSource()).setOpacity(0.8);
                                    } else
                                        ((ImageView) event.getSource()).setEffect(new DropShadow(5, Color.BLACK));

                                }
                            }
                        }
                    });
                    cell.setOnMouseExited(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            ((ImageView)event.getSource()).setImage(borderCard);
                            ((ImageView)event.getSource()).setOpacity(0.35);
                            ((ImageView)event.getSource()).setEffect(null);
                        }
                    });
                    cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if(!canPlace || placeChoice == -1)
                                return;
                            for(int tCol = area.getWestBound() == 0 ? 0 : area.getWestBound() - 1 ; tCol <= (area.getEastBound() == 80 ? 80 : area.getEastBound() + 1); tCol++) {
                                for(int tRow = area.getNorthBound() == 0 ? 0 : area.getNorthBound() - 1; tRow <= (area.getSouthBound() == 80 ? 80 : area.getSouthBound() + 1); tRow++) {
                                    if(gridpaneArray[tCol][tRow] != null && gridpaneArray[tCol][tRow].equals((ImageView) event.getSource())) {
                                        placeAction = new PlacingCardAction(placeChoice-1, myPlayer.getHand().get(placeChoice-1).isFront(), tRow, tCol, myNickname);
                                        hasPlaced = true;
                                    }
                                }
                            }
                        }
                    });
                    playgroundGridPane.add(cell, i, j);
                    gridpaneArray[i][j] = cell;
                }
            }
        }
    }

    /**
     * Initializes the scoreboard and possibly hide the selection panel
     * Sets up the players name for players table
     * @param all boolean to hide other panels
     */
    private void initializeScoreboard(boolean all) {
        if(all) {
            cardChoicePane.setVisible(false);
            scoreboardPane.setVisible(true);
        }
        printScoreboard();
        ImageView tempImg = new ImageView(colorToPawns.get(myPlayer.getColor()).getImage());
        tempImg.setFitHeight(17);
        tempImg.setFitWidth(17);
        pawnPlayersGrid.add(tempImg, 0, 0);
        Label tempLbl = new Label("(You) " + myPlayer.getName());
        Font tempFont = new Font(14);
        tempLbl.setFont(tempFont);
        pawnPlayersGrid.add(tempLbl, 1, 0);
        for(int i = 0; i < otherPlayers.size(); i++) {
            tempImg = new ImageView(colorToPawns.get(otherPlayers.get(i).getColor()).getImage());
            tempImg.setFitHeight(17);
            tempImg.setFitWidth(17);
            pawnPlayersGrid.add(tempImg, 0, i+1);
            tempLbl = new Label(otherPlayers.get(i).getName());
            tempLbl.setFont(tempFont);
            pawnPlayersGrid.add(tempLbl, 1, i+1);
            tempLbl.setEffect(null);
        }
    }

    /**
     * Initializes the scoreboard with the boolean true, overloading
     */
    private void initializeScoreboard() {
        initializeScoreboard(true);
    }

    /**
     * Updates the cards in the hand of the player
     */
    private void updatePlayerRelated() {
        //hand cards
        handCard1Img.setVisible(false);
        handCard2Img.setVisible(false);
        handCard3Img.setVisible(false);
        frontHand = new ArrayList<>();
        backHand = new ArrayList<>();
        for(Card card : myPlayer.getHand()) {
            card.setFront(true);
            if(card.getClass() == ResourceCard.class) {
                frontHand.add(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/front/" + ((ResourceCard) card).getSideID(true) + ".png"))));
                backHand.add(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/back/" + ((ResourceCard) card).getSideID(false) + ".png"))));
            } else if(card.getClass() == GoldCard.class) {
                frontHand.add(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/front/" + ((GoldCard) card).getSideID(true) + ".png"))));
                backHand.add(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/back/" + ((GoldCard) card).getSideID(false) + ".png"))));
            }
        }
        switch(myPlayer.getHand().size()) {
            case 3:
                handCard3Img.setImage(myPlayer.getHand().get(2).isFront() ? frontHand.get(2) : backHand.get(2));
                handCard3Img.setEffect(null);
                handCard3Img.setVisible(true);
            case 2:
                handCard2Img.setImage(myPlayer.getHand().get(1).isFront() ? frontHand.get(1) : backHand.get(1));
                handCard2Img.setEffect(null);
                handCard2Img.setVisible(true);
            case 1:
                handCard1Img.setImage(myPlayer.getHand().get(0).isFront() ? frontHand.get(0) : backHand.get(0));
                handCard1Img.setEffect(null);
                handCard1Img.setVisible(true);
                break;
            default:
                break;
        }
    }

    /**
     * Updates the achievements of the player
     */
    private void updateAchievement() {
        achievementBtn.setDisable(false);
        frontAchievements = new ArrayList<>();
        for(AchievementCard achCard: achievements) {
            achCard.setFront(true);
            frontAchievements.add(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/front/" + achCard.getSideID() + ".png"))));
        }
        achievementCard1Img.setImage(frontAchievements.get(0));
        achievementCard2Img.setImage(frontAchievements.get(1));
        achievementCard1Img.setVisible(true);
        achievementCard2Img.setVisible(true);
    }



//---------------- PUBLIC MISC METHODS ---------------------

    /**
     * Displays the results (winnner / looser) when the game ends
     */
    public void showResult() {
        boolean win = myPlayer.isWinner();
        Image rImg = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/misc/" + (win ? "award" : "sad") + ".png")));
        String rString = "YOU " + (win ? "WON!" : "LOST");
        resultImg.setImage(rImg);
        resultLbl.setText(rString);
        resultImg.setVisible(true);
        resultLbl.setVisible(true);

    }

    /**
     * Appends a message to the chat log in the chat panel
     * @param m
     */
    public void displayChatMessage(Message m) {
        String s = m.toString() + "\n";
        if(m.getRecipient().equalsIgnoreCase(myNickname))
            s = ">>> " + s;
        chatTextArea.appendText(s);
    }

    /**
     * Sets up the options to switch recipient in the chat
     * @param players ArrayList of other players in the game
     */
    public void initializeChatOptions(ArrayList<Player> players) {
        playgroundChoiceBox.getItems().clear();
        playgroundChoiceBox.getItems().add("You ");
        otherPlayers = players;
        switch(otherPlayers.size()) {
            case 3:
                playgroundChoiceBox.getItems().add(otherPlayers.get(2).getName());
                p3Item.setText(otherPlayers.get(2).getName());
                p3Item.setVisible(true);
            case 2:
                playgroundChoiceBox.getItems().add(otherPlayers.get(1).getName());
                p2Item.setText(otherPlayers.get(1).getName());
                p2Item.setVisible(true);
            case 1:
                playgroundChoiceBox.getItems().add(otherPlayers.get(0).getName());
                p1Item.setText(otherPlayers.get(0).getName());
                p1Item.setVisible(true);
                break;
            default:
                break;
        }
    }

    /**
     * Display a successful card placed message
     * @param recipient name of the player who placed the card
     * @param score integer representing the possible score obtained by the player
     */
    public void displaySuccessfulPlace(String recipient, int score) {
        if(alertLbl.isVisible()) {
            oldAlertLbl.setText(alertLbl.getText());
            oldAlertLbl.setVisible(true);
        }
        if(recipient.equalsIgnoreCase(myNickname)) {
            updatePlayerRelated();
            printPlayground();
            playgroundChoiceBox.setValue("You ");
            alertLbl.setText("You placed the card! " + (score > 0 ? "+" + score + "pts" : ""));
        } else {
            alertLbl.setText(recipient + " placed a card " + (score > 0 ? "+" + score + "pts" : ""));
        }
        alertLbl.setVisible(true);
        printScoreboard();
    }

    /**
     * Displays a successful card drawn message
     * @param recipient name of the player who drew the card
     * @param drawnCard card drawn
     */
    public void displaySuccessfulDrawn(String recipient, Card drawnCard) {
        String drawText = " drew a " + (drawnCard.getClass() == GoldCard.class ? "gold" : "resource") + " card";
        if(alertLbl.isVisible()) {
            oldAlertLbl.setText(alertLbl.getText());
            oldAlertLbl.setVisible(true);
        }
        if(recipient.equalsIgnoreCase(myNickname)) {
            updatePlayerRelated();
            handBtn.setSelected(true);
            onHandButtonClick();
            alertLbl.setText("You" + drawText);
        } else {
            alertLbl.setText(recipient + drawText);
        }
        alertLbl.setVisible(true);
    }

    /**
     * Alerts the player to draw a card from the table
     */
    public void alertToDraw() {
        drawChoice = 0;
        canDraw = true;
        tableBtn.setSelected(true);
        onTableButtonClick();
        if(alertLbl.isVisible()) {
            oldAlertLbl.setText(alertLbl.getText());
            oldAlertLbl.setVisible(true);
        }
        alertLbl.setText("Draw a card!");
        alertLbl.setVisible(true);
    }

    /**
     * Displays a generic alert message for the player
     */
    public void genericAlert(String str) {
        if(alertLbl.isVisible()) {
            oldAlertLbl.setText(alertLbl.getText());
            oldAlertLbl.setVisible(true);
        }
        alertLbl.setText(str);
        alertLbl.setVisible(true);
    }

    /**
     * Displays the two sides of the starter cards allowing the player to choose one,
     * it also loads the images of cards in the hand and in the table
     * @param str the starter card
     * @param self the local player
     * @param commonGold the two gold card cards on the table
     * @param goldDeck the resource of the card on top of the gold deck
     * @param commonResource the two resource card cards on the table
     * @param resourceDeck the resource of the card on top of the resource deck
     */
    public void passStarterCard(StarterCard str, Player self, ArrayList<GoldCard> commonGold, Resource goldDeck,  ArrayList<ResourceCard> commonResource, Resource resourceDeck) {
        resultImg.setVisible(false);
        resultLbl.setVisible(false);
        myPlayer = self;
        strCard = str;
        achievementBtn.setDisable(true);
        updatePlayerRelated();
        updateTableCards(commonGold, goldDeck, commonResource, resourceDeck);
        Image frontSide = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/front/" + str.getID() + ".png")));
        Image backSide = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/back/" + str.getID() + ".png")));
        selectCardLbl.setText("Choose the side of your starter card");
        selectCard1Img.setImage(frontSide);
        selectCard2Img.setImage(backSide);
        selectCard1Img.setDisable(false);
        selectCard2Img.setDisable(false);
        cardChoicePane.setVisible(true);
        scoreboardPane.setVisible(false);
    }

    /**
     * Displays the two possible secrets achievements allowing the player to choose one
     * @param ach the shared achievements
     * @param achievements the two choosable achievements
     */
    public void passAchievement(ArrayList<AchievementCard> ach, ArrayList<AchievementCard> achievements) {
        this.achievements = achievements;
        choosableAchievements = ach;
        updateAchievement();
        Image ach1 = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/front/" + ach.get(0).getID() + ".png")));
        Image ach2 = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/front/" + ach.get(1).getID() + ".png")));
        selectCardLbl.setText("Choose your own secret achievement");
        selectCard1Img.setImage(ach1);
        selectCard2Img.setImage(ach2);
        selectCard1Img.setDisable(false);
        selectCard2Img.setDisable(false);
        cardChoicePane.setVisible(true);
        scoreboardPane.setVisible(false);
    }

    /**
     * Updates the cards on the table
     * @param commonGold the two gold card cards on the table
     * @param goldDeck the resource of the card on top of the gold deck
     * @param commonResource the two resource card cards on the table
     * @param resourceDeck the resource of the card on top of the resource deck
     */
    public void updateTableCards(ArrayList<GoldCard> commonGold, Resource goldDeck,  ArrayList<ResourceCard> commonResource, Resource resourceDeck) {
        //updating values from client
        this.commonGold = commonGold;
        this.commonResource = commonResource;
        //updating images and setting cards to front
        frontCommonGold = new ArrayList<>();
        backCommonGold = new ArrayList<>();
        frontCommonResource = new ArrayList<>();
        backCommonResource = new ArrayList<>();
        for(GoldCard gc : commonGold) {
            gc.setFront(true);
            frontCommonGold.add(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/front/" + gc.getSideID(true) + ".png"))));
            backCommonGold.add(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/back/" + gc.getSideID(false) + ".png"))));
        }
        for(ResourceCard rc : commonResource) {
            rc.setFront(true);
            frontCommonResource.add(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/front/" + rc.getSideID(true) + ".png"))));
            backCommonResource.add(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/back/" + rc.getSideID(false) + ".png"))));
        }
        // gold deck
        if(goldDeck != null) {
            String gcId;
            switch(goldDeck) {
                case WOLF:
                    gcId = "061";
                    break;
                case LEAF:
                    gcId = "051";
                    break;
                case MUSHROOM:
                    gcId = "041";
                    break;
                case BUTTERFLY:
                    gcId = "071";
                    break;
                default: //shouldn't happen
                    gcId = "099";
                    break;
            }
            backGoldDeck = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/back/" + gcId + ".png")));
            tableGoldDeckImg.setImage(backGoldDeck);
            tableResourceDeckImg.setVisible(true);
            tableBackDeck1Img.setVisible(true);
        } else {
            tableGoldDeckImg.setVisible(false);
            tableBackDeck1Img.setVisible(false);
        }
        // resource deck
        if(resourceDeck != null) {
            String gcId;
            switch(resourceDeck) {
                case WOLF:
                    gcId = "021";
                    break;
                case LEAF:
                    gcId = "011";
                    break;
                case MUSHROOM:
                    gcId = "001";
                    break;
                case BUTTERFLY:
                    gcId = "031";
                    break;
                default: //shouldn't happen
                    gcId = "099";
                    break;
            }
            backResourceDeck = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/cards/back/" + gcId + ".png")));
            tableResourceDeckImg.setImage(backResourceDeck);
            tableResourceDeckImg.setVisible(true);
            tableBackDeck1Img.setVisible(true);
        } else {
            tableResourceDeckImg.setVisible(false);
            tableBackDeck1Img.setVisible(false);
        }
        // common gold
        switch(commonGold.size()) {
            case 2:
                tableGold1Img.setImage(frontCommonGold.get(0));
                tableGold2Img.setImage(frontCommonGold.get(1));
                tableGold1Img.setVisible(true);
                tableGold2Img.setVisible(true);
                break;
            case 1:
                tableGold1Img.setImage(frontCommonGold.get(0));
                tableGold1Img.setVisible(true);
                tableGold2Img.setVisible(false);
                break;
            default:
                tableGold1Img.setVisible(false);
                tableGold2Img.setVisible(false);
                break;
        }
        // common resource
        switch(commonResource.size()) {
            case 2:
                tableResource1Img.setImage(frontCommonResource.get(0));
                tableResource2Img.setImage(frontCommonResource.get(1));
                tableResource1Img.setVisible(true);
                tableResource2Img.setVisible(true);
                break;
            case 1:
                tableResource1Img.setImage(frontCommonResource.get(0));
                tableResource1Img.setVisible(true);
                tableResource2Img.setVisible(false);
                break;
            default:
                tableResource1Img.setVisible(false);
                tableResource2Img.setVisible(false);
                break;
        }
    }

    /**
     * Alerts the player to place a card on the playground and displays
     * the cells where cards could be placed
     */
    public void alertToPlace() {
        placeChoice = -1;
        canPlace = true;
        hasPlaced = false;
        printPlayground();
        placeablePlayground();
        playgroundChoiceBox.setValue("You ");
        if(alertLbl.isVisible()) {
            oldAlertLbl.setText(alertLbl.getText());
            oldAlertLbl.setVisible(true);
        }
        alertLbl.setText("Place a card!");
        alertLbl.setVisible(true);
    }

    /**
     * Alerts the player to replace a card on the playground after an irregular
     * placing and displays the cells where cards could be placed
     */
    public void alertToRePlace() {
        placeChoice = -1;
        canPlace = true;
        hasPlaced = false;
        printPlayground();
        placeablePlayground();
        playgroundChoiceBox.setValue("You ");
        if(alertLbl.isVisible()) {
            oldAlertLbl.setText(alertLbl.getText());
            oldAlertLbl.setVisible(true);
        }
        alertLbl.setText("You can't place it there!");
        alertLbl.setVisible(true);
    }

    /**
     * Retrieves possible existing action created when placing a card
     * The client calls this method periodically after an alertToPlace
     * @return placing card action containing the info on the card placed
     */
    public PlacingCardAction discoverPlace() {
        if(hasPlaced) {
            PlacingCardAction a = placeAction;
            placeAction = null;
            canPlace = false;
            hasPlaced = false;
            placeChoice = -1;
            return a;
        } else
            return null;
    }

    /**
     * Retrieves possible existing action created when drawing a card
     * The client calls this method periodically after an alertToDraw
     * @return placing card action containing the info on the card drawn
     */
    public ChosenDrawCardAction discoverDraw() {
        if(canDraw && drawChoice != 0) {
            ChosenDrawCardAction a = new ChosenDrawCardAction(myNickname, drawChoice);
            drawChoice = 0;
            canDraw = false;
            return a;
        } else
            return null;
    }

    /**
     * Sets all the panels of the graphical application on game already started
     * Used by client after a reconnection
     * @param myPlayer local player
     * @param otherPlayers all other players in the game
     * @param achievements achievements of the player, including the secret one
     * @param commonGold the two gold card cards on the table
     * @param goldDeck the resource of the card on top of the gold deck
     * @param commonResource the two resource card cards on the table
     * @param resourceDeck the resource of the card on top of the resource deck
     */
    public void setupStartedGame(Player myPlayer, ArrayList<Player> otherPlayers, ArrayList<AchievementCard> achievements, ArrayList<GoldCard> commonGold, Resource goldDeck,  ArrayList<ResourceCard> commonResource, Resource resourceDeck) {
        cardChoicePane.setVisible(false);
        scoreboardPane.setVisible(true);
        this.otherPlayers = otherPlayers;
        this.achievements = achievements;
        initializeChatOptions(this.otherPlayers);
        setPlayer(myPlayer);
        updateTableCards(commonGold, goldDeck, commonResource, resourceDeck);
        printPlayground();
        updateAchievement();
    }

    //getters and setters

    /**
     * Sets the nickname of the local player
     * @param nick name of the player
     */
    public void setNickname(String nick) {
        myNickname = nick;
    }

    /**
     * Sets or updates the values of the local player
     * @param myPlayer the local player
     */
    public void setPlayer(Player myPlayer) {
        this.myPlayer = myPlayer;
        updatePlayerRelated();
        printScoreboard();
        initializeScoreboard(false);
    }

    /**
     * Sets or updates the values of the other players in the game, reprints
     * a playground if is currently visualized and updates the scoreboard
     * @param otherPlayers
     */
    public void setOtherPlayers(ArrayList<Player> otherPlayers) {
        this.otherPlayers = otherPlayers;
        for(Player playa : otherPlayers)
            if(playa.getName().equalsIgnoreCase(playgroundChoiceBox.getValue()))
                printPlayground(playa);
        printScoreboard();
        initializeScoreboard(false);
    }
}
