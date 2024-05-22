package it.polimi.ingsw.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;


public class LoginController {

    @FXML
    private TextField nameFld;

    @FXML
    private Button nameBtn;

    @FXML
    private ImageView nameBtnImg;

    @FXML
    protected void onNameBtnClick() {
        String name = nameFld.getText().trim();
        if(name.isEmpty()) {
            nameFld.clear();
        } else {
            nameFld.setDisable(true);
            nameBtn.setDisable(true);

            String path = String.valueOf(Objects.requireNonNull(this.getClass().getResource("img/icons/double-check.png")));
            Image doubleCheck = new Image(path, 20.0, 20.0, true, true);
            nameBtnImg.setImage(doubleCheck);
        }
    }

    @FXML
    protected void onNameFldEnter(KeyEvent event) {
        System.out.println("pressed");
        //System.out.println(e.getClass() + " |  " + e.getText() + " |  " + e.getCharacter());
    }

}