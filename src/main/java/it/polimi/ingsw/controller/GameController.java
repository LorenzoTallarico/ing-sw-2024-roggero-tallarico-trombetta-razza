package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;


public class GameController {
    //Attributi ridondanti, da vedere se utili o togliere
    private Player currentPlayer;
    private int playersNumber;
    private int position, index;
    private final Game model;

    public GameController() {
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
            ArrayList<AchievementCard> goal = new ArrayList<AchievementCard>();
            goal.add(model.getPlayers().get(model.getCurrPlayer()).getSecretAchievement().get(position));
            model.getPlayers().get(model.getCurrPlayer()).setSecretAchievement( goal );
        }
    }

    //**********to decide if it's necessary to synchronize***********
    public boolean isPlayerInTurn(Player p) {
        return model.getCurrPlayer() == model.getPlayers().indexOf(p);
    }

    public void assignStarterAchievement(Player p1){
        synchronized (this.model) {
            ArrayList<AchievementCard> goals = new ArrayList<AchievementCard>();
            goals.add(model.popAchievementCard());
            goals.add(model.popAchievementCard());
            p1.setSecretAchievement(goals);
        }
    }




}
