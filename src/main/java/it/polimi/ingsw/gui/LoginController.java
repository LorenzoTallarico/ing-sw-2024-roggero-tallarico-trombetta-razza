package it.polimi.ingsw.gui;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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
    private ImageView join1Img;
    @FXML
    private ImageView join2Img;
    @FXML
    private ImageView join3Img;

    @FXML
    private SplitMenuButton pnumSMB;

    @FXML
    private Label errorLbl;
    @FXML
    private Label join1Lbl;
    @FXML
    private Label join2Lbl;
    @FXML
    private Label join3Lbl;


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

    @FXML
    protected void onJoin1MouseIn() {
        Font font = join1Lbl.getFont();
        join1Lbl.setFont(new Font(font.getName(), font.getSize()+2));
        join1Lbl.setLayoutY(join1Lbl.getLayoutY() - 1);
        join1Img.setFitHeight(join1Img.getFitHeight()+2);
        join1Img.setFitWidth(join1Img.getFitWidth()+2);
        join1Img.setLayoutX(join1Img.getLayoutX() - 1);
    }

    @FXML
    protected void onJoin1MouseOut() {
        Font font = join1Lbl.getFont();
        join1Lbl.setFont(new Font(font.getName(), font.getSize()-2));
        join1Lbl.setLayoutY(join1Lbl.getLayoutY() + 1);
        join1Img.setFitHeight(join1Img.getFitHeight() - 2);
        join1Img.setFitWidth(join1Img.getFitWidth() - 2);
        join1Img.setLayoutX(join1Img.getLayoutX() + 1);
    }

    @FXML
    protected void onJoin2MouseIn() {
        Font font = join2Lbl.getFont();
        join2Lbl.setFont(new Font(font.getName(), font.getSize()+2));
        join2Lbl.setLayoutY(join2Lbl.getLayoutY() - 1);
        join2Img.setFitHeight(join2Img.getFitHeight() + 2);
        join2Img.setFitWidth(join2Img.getFitWidth() + 2);
        join2Img.setLayoutX(join2Img.getLayoutX() - 1);
    }

    @FXML
    protected void onJoin2MouseOut() {
        Font font = join2Lbl.getFont();
        join2Lbl.setFont(new Font(font.getName(), font.getSize()-2));
        join2Lbl.setLayoutY(join2Lbl.getLayoutY() + 1);
        join2Img.setFitHeight(join2Img.getFitHeight()-2);
        join2Img.setFitWidth(join2Img.getFitWidth()-2);
        join2Img.setLayoutX(join2Img.getLayoutX() + 1);
    }

    @FXML
    protected void onJoin3MouseIn() {
        Font font = join3Lbl.getFont();
        join3Lbl.setFont(new Font(font.getName(), font.getSize()+2));
        join3Lbl.setLayoutY(join3Lbl.getLayoutY() - 1);
        join3Img.setFitHeight(join3Img.getFitHeight()+2);
        join3Img.setFitWidth(join3Img.getFitWidth()+2);
        join3Img.setLayoutX(join3Img.getLayoutX() - 1);
    }

    @FXML
    protected void onJoin3MouseOut() {
        Font font = join3Lbl.getFont();
        join3Lbl.setFont(new Font(font.getName(), font.getSize()-2));
        join3Lbl.setLayoutY(join3Lbl.getLayoutY() + 1);
        join3Img.setFitHeight(join3Img.getFitHeight()-2);
        join3Img.setFitWidth(join3Img.getFitWidth()-2);
        join3Img.setLayoutX(join3Img.getLayoutX() + 1);
    }
    
    //public methods

    public void setNickname(String nick) {
        nameFld.setText(nick);
        onNameBtnClick();
    }

    public void notifyJoiningPlayer(String nick, int curr, int size) {
        String line;
        if(size > 0)
            line = nick + " joined " + curr + "/" + size;
        else
            line = "You are the first player";
        if(!join1Img.isVisible()) {
            join1Img.setVisible(true);
            join1Lbl.setText(line);
            join1Lbl.setVisible(true);
        } else if(!join2Img.isVisible()) {
            join2Img.setVisible(true);
            join2Lbl.setText(line);
            join2Lbl.setVisible(true);
        } else if(!join3Img.isVisible()) {
            join3Img.setVisible(true);
            join3Lbl.setText(line);
            join3Lbl.setVisible(true);
        }

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