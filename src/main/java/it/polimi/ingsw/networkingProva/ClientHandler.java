package it.polimi.ingsw.networkingProva;

import it.polimi.ingsw.networking.action.Action;
import java.io.IOException;

public interface ClientHandler {
    void sendAction(Action action) throws IOException;
}
