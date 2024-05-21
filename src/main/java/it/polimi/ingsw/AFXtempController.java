package it.polimi.ingsw;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class AFXtempController {
    @FXML
    private TextField chatInput;

    @FXML
    private TextArea chatOutput;

    @FXML
    private RadioButton p1Rad, p2Rad, p3Rad, p4Rad;

    @FXML
    private ImageView p1Img, p2Img, p3Img, p4Img;

    @FXML
    private AnchorPane scoreboardPane;

    @FXML
    protected void onInviaButtonClick() {
        String s = chatInput.getText().trim();
        if(!s.isEmpty())
            chatOutput.appendText("Client: " + s + "\n");
        chatInput.clear();
    }

    @FXML
    protected void onEnterKeyPressedChatInput(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER))
            onInviaButtonClick();
    }

    @FXML
    protected void onRadioButtonClick(Event e) {
        System.out.println("RadioButtonClick " + e.getEventType() + " by: " + ((RadioButton)e.getSource()).getId() + " " + ((RadioButton) e.getSource()).getText());
        System.out.println("scoreboardPane x" + scoreboardPane.getLayoutX() + " y" + scoreboardPane.getLayoutY());
        double x = scoreboardPane.getLayoutX(), y = scoreboardPane.getLayoutY();
        switch (((RadioButton)e.getSource()).getId()) {
            case "p1Rad":
                //x + 25 = 0
                //y + 305 = 0
                p2Rad.setSelected(false);
                p3Rad.setSelected(false);
                p4Rad.setSelected(false);
                if(p1Rad.isSelected()) {
                    p1Img.setX(120 - x);
                    p1Img.setY(145 - y);
                } else {
                    p1Img.setX(25 - x);
                    p1Img.setY(305 - y);
                }
                break;
            case "p2Rad":
                p1Rad.setSelected(false);
                p3Rad.setSelected(false);
                p4Rad.setSelected(false);
                if(p2Rad.isSelected()) {
                    p2Img.setX(86);
                    p2Img.setY(75);
                } else {
                    p2Img.setX(25);
                    p2Img.setY(285);
                }
                break;
            case "p3Rad":
                p1Rad.setSelected(false);
                p2Rad.setSelected(false);
                p4Rad.setSelected(false);
                if(p3Rad.isSelected()) {
                    p3Img.setX(30);
                    p3Img.setY(75);
                } else {
                    p3Img.setX(50);
                    p3Img.setY(285);
                }
                break;
            case "p4Rad":
                p1Rad.setSelected(false);
                p2Rad.setSelected(false);
                p3Rad.setSelected(false);
                if(p4Rad.isSelected()) {
                    p4Img.setX(84);
                    p4Img.setY(215);
                } else {
                    p4Img.setX(50);
                    p4Img.setY(305);
                }
                break;
            default:
                p1Rad.setSelected(false);
                p2Rad.setSelected(false);
                p3Rad.setSelected(false);
                p4Rad.setSelected(false);
                break;
        }
    }

    //0cool scoreboard 25, 305
    //0devil scoreboard 25, 285
    //18 scoreboard 120, 145
    //C:\Users\pietr\IdeaProjects\demo\src\main\resources\it\polimi\ingsw\demo
    //width height 20 20
}