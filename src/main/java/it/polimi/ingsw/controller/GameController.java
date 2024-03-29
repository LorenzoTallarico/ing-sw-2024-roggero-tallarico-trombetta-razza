package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;


public class GameController {
    private Player currentPlayer;
    private int playersNumber;
    private int position, index;
    //link to the model
    private final Game model;

    public GameController() {
        //To fix, might be a problem with Game.Game attribute
        model = Game.getInstance();
    }

    public void placeCard(Card card, int row, int column) {
        synchronized (this.model) {
            if (model.getPlayers().get(model.getCurrPlayer()).placeable(card, row, column)){
                model.getPlayers().get(model.getCurrPlayer()).getArea().setSpace(card, row, column);
                model.getPlayers().get(model.getCurrPlayer()).getHand().remove(card);
            }
        }
    }

    public void drawCard(int index){
        synchronized (this.model){
            model.getPlayers().get(model.getCurrPlayer()).getHand().add(model.draw(index));
        }
    }

    public void selectAchievementCard(int position){
        synchronized (this.model) {
            model.getPlayers().get(model.getCurrPlayer()).setChosenSecretAchievement(position);
        }
    }
}
