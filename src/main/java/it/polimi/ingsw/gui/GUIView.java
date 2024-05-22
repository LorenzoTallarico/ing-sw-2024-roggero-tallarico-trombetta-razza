package it.polimi.ingsw.gui;

import it.polimi.ingsw.AFXtemp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GUIView extends Application {

    /*public static void launchGUI() {
        launch();
    }*/

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIView.class.getResource("Login-view.fxml"));
        //Parent root = fxmlLoader.load();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Codex Naturalis");
        Image logo = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/misc/logo.png")));
        stage.getIcons().add(logo);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
