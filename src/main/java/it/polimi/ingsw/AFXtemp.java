package it.polimi.ingsw;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class AFXtemp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("AFXtemp-view.fxml"));
        //Parent root = fxmlLoader.load();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Codex Naturalis");
        //Image logo = new Image(Objects.requireNonNull(AFXtemp.class.getResourceAsStream("logo.png")));
        //stage.getIcons().add(logo);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}