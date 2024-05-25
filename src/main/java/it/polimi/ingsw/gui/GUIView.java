package it.polimi.ingsw.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GUIView extends Application {

    private LoginController loginController;
    private PlayController playController;
    private Stage stage;
    private final Image logo;

    public GUIView() {
        logo = new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("img/misc/logo.png")));
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIView.class.getResource("Login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        loginController = fxmlLoader.getController();
        stage.setTitle("Codex Naturalis");
        stage.getIcons().add(logo);
        stage.setScene(scene);
        stage.setMinHeight(400.0);
        stage.setMinWidth(600.0);
        stage.setResizable(false);
        stage.show();
        this.stage = stage;
    }

    public LoginController getLoginController() {
       return loginController;
    }

    public PlayController getPlayController() {
        return playController;
    }

    public void playScene(String playerNickname) {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIView.class.getResource("Play-view.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("CodexNaturalis | " + playerNickname);
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        playController = fxmlLoader.getController();
        stage.setMaximized(true);
    }
    
    public static void main(String[] args) {
        launch();
    }
    
}
