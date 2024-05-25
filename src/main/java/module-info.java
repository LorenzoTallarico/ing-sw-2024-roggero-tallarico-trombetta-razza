module it.polimi.ingsw {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires com.google.gson;

    opens it.polimi.ingsw to javafx.fxml, com.google.gson, java.rmi, javafx.controls;
    exports it.polimi.ingsw;
    opens it.polimi.ingsw.model to com.google.gson;
    exports it.polimi.ingsw.model;
    opens it.polimi.ingsw.networking.rmi to java.rmi;
    exports it.polimi.ingsw.gui;
    opens it.polimi.ingsw.gui to javafx.fxml;
    exports it.polimi.ingsw.networking.rmi;
}