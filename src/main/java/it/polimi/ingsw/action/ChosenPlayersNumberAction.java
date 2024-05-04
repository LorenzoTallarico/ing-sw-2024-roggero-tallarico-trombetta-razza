package it.polimi.ingsw.action;

public class ChosenPlayersNumberAction extends Action {

    private final int playersNumber;
    public ChosenPlayersNumberAction(int playersNumber){
        super(ActionType.CHOSENPLAYERSNUMBER, null, null);
        this.playersNumber = playersNumber;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }
}
