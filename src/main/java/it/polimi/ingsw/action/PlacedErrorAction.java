package it.polimi.ingsw.action;

public class PlacedErrorAction extends Action {

    private final String error;


    public PlacedErrorAction(String nickname){
        super(ActionType.PLACEDCARDERROR, null, nickname);
        this.error = "> Card can't be placed in the given position.";
    }

    public String getError() {
        return this.error;
    }

}
