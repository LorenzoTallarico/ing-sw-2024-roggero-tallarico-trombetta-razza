package it.polimi.ingsw.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.*;

public class PlayController {

    @FXML
    private Label prova;

    @FXML
    protected void provaIn() {
        Font font = prova.getFont();
        prova.setFont(new Font(font.getName(), font.getSize()+3));
    }

    @FXML
    protected void provaOut() {
        Font font = prova.getFont();
        prova.setFont(new Font(font.getName(), font.getSize()-3));
    }

    @FXML
    protected void provaClick() {
        prova.setUnderline(!prova.isUnderline());
    }


}
