
module it.polimi.ingsw {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires gson;
    opens it.polimi.ingsw to javafx.fxml;

    exports it.polimi.ingsw;
}