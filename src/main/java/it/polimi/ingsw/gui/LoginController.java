package it.polimi.ingsw.gui;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.util.Objects;


public class LoginController {

    private boolean numberChoice = false;
    private int playersNumber = 0;
    private String nickname = "";
    private Image check = null;
    private final Image plusUser = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/icons/user-plus.png")));

    @FXML
    private TextField nameFld;

    @FXML
    private Button nameBtn;
    @FXML
    private Button confirmBtn;

    @FXML
    private ImageView nameBtnImg;
    @FXML
    private ImageView avatarsGroup2Img;
    @FXML
    private ImageView avatarsGroup3Img;
    @FXML
    private ImageView avatarsGroup4Img;
    @FXML
    private ImageView loadingImg;

    @FXML
    private SplitMenuButton pnumSMB;

    @FXML
    private Label errorLbl;

    @FXML
    private TextFlow playersTextFlow;

    @FXML
    protected void onNameBtnClick() {
        String name = nameFld.getText().trim();
        if(name.isEmpty()) {
            nameFld.clear();
        } else {
            nameFld.setDisable(true);
            nameBtn.setDisable(true);
            check = nameBtnImg.getImage();
            String path = String.valueOf(Objects.requireNonNull(this.getClass().getResource("img/icons/double-check.png")));
            Image doubleCheck = new Image(path, 20.0, 20.0, true, true);
            nameBtnImg.setImage(doubleCheck);
            nickname = name;
        }
    }

    @FXML
    protected void onNameFldEnter(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER))
            onNameBtnClick();
    }

    @FXML
    protected void showConfirm(Event e) {
        if(!confirmBtn.isVisible())
            confirmBtn.setVisible(true);
        String id = ((MenuItem) e.getSource()).getId();
        switch(id) {
            case "option2":
                playersNumber = 2;
                avatarsGroup2Img.setVisible(true);
                avatarsGroup3Img.setVisible(false);
                avatarsGroup4Img.setVisible(false);
                break;
            case "option3":
                playersNumber = 3;
                avatarsGroup2Img.setVisible(false);
                avatarsGroup3Img.setVisible(true);
                avatarsGroup4Img.setVisible(false);
                break;
            case "option4":
                playersNumber = 4;
                avatarsGroup2Img.setVisible(false);
                avatarsGroup3Img.setVisible(false);
                avatarsGroup4Img.setVisible(true);
                break;
            default: // shouldn't happen
                playersNumber = 0;
                avatarsGroup2Img.setVisible(false);
                avatarsGroup3Img.setVisible(false);
                avatarsGroup4Img.setVisible(false);
                break;
        }
    }

    @FXML
    protected void onConfirmBtnClick(Event e) {
        if(playersNumber != 0) {
            ((Button) e.getSource()).setDisable(true);
            pnumSMB.setDisable(true);
            numberChoice = true;
        }
    }

    @FXML
    protected void onMouseEnteredError() {
        if(errorLbl.isVisible()) {
            errorLbl.setUnderline(true);
        }
    }

    @FXML
    protected void onMouseExitedError() {
        if (errorLbl.isVisible()) {
            errorLbl.setUnderline(false);
        }
    }

    //public methods

    public void notifyJoiningPlayer(String nick, int curr, int size) {
        Text line = new Text(nick + " joined " + curr + "/" + (size == 0 ? "?" : size));
        line.setFill(Color.GREEN);
        playersTextFlow.getChildren().add(line);
        ImageView playaIcon = new ImageView(plusUser);
        playaIcon.setLayoutX(0);
        playaIcon.setLayoutY(line.getLayoutY());
        playaIcon.setVisible(true);
    }

    public void invalidNickname() {
        nickname = "";
        nameFld.clear();
        nameFld.setPromptText("Invalid nickname, re-enter");
        nameFld.setDisable(false);
        nameBtn.setDisable(false);
        nameBtnImg.setImage(check);
        errorLbl.setVisible(true);
    }

    public void showPlayersNumberMenu() {
        pnumSMB.setVisible(true);
        errorLbl.setVisible(false);
        loadingImg.setVisible(false);
    }

    public void waitForOtherPlayers() {
        errorLbl.setText("Waiting for other players");
        errorLbl.setTextFill(Color.GREEN);
        errorLbl.setLayoutX(218);
        errorLbl.setVisible(true);
        loadingImg.setVisible(true);
    }

    //getters

    public int getPlayersNumber() {
        if(numberChoice)
            return playersNumber;
        else
            return 0;
    }

    public String getNickname() {
        return nickname;
    }

}