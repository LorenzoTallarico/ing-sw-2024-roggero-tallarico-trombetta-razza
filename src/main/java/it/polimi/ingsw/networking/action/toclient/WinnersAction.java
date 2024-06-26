package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;

public class WinnersAction extends Action {

    private final ArrayList<Player> players;

    public WinnersAction(ArrayList<Player> players) {
        super(ActionType.WINNERS, null, null);
        this.players = players;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
