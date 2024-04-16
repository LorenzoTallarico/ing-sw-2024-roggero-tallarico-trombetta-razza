package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;


public class GameController {
    //Attributi ridondanti, da vedere se utili o togliere
    private Player currentPlayer;
    private int playersNumber;
    private int position, index;
    private final Game model;
    private final ArrayList<Player> players;

    public GameController(int playersNumber) {
        model = Game.getInstance();
        this.playersNumber= playersNumber;
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player p) {
        synchronized (this.players) {
            if (players.size() <= playersNumber && model.getGameState().equals(GameState.LOBBY)) {
                players.add(p);
                //System.out.println("size " + players.size());
                if (players.size() == playersNumber) {
                    model.addPlayers(players);
                    model.setGameState(GameState.INIT);
                    if(model.getGameState().equals(GameState.INIT)){
                        System.out.println("Il gioco può iniziare");
                    }
                }
            }
        }
    }

    public boolean placeCard(Card card, int row, int column) {
            synchronized (this.model) {
                if(model.getGameState().equals(GameState.GAME)||model.getGameState().equals(GameState.LASTROUND)) {
                    if (model.getPlayers().get(model.getCurrPlayer()).placeable(card, row, column)) {
                        model.getPlayers().get(model.getCurrPlayer()).getArea().setSpace(card, row, column);
                        model.getPlayers().get(model.getCurrPlayer()).getHand().remove(card);
                        return true;
                    } else{
                        return false;
                    }
                }
                return false;
            }
    }

    public void drawCard(int index){
        synchronized (this.model) {
            if (model.getGameState().equals(GameState.GAME) || model.getGameState().equals(GameState.LASTROUND))
                model.getPlayers().get(model.getCurrPlayer()).getHand().add(model.draw(index));

        }
    }

    public void selectAchievementCard(int position){
        synchronized (this.model) {
            if(model.getGameState().equals(GameState.SELECTACHIEVEMENT)) {
                ArrayList<AchievementCard> goal = new ArrayList<AchievementCard>();
                goal.add(model.getPlayers().get(model.getCurrPlayer()).getSecretAchievement().get(position));
                model.getPlayers().get(model.getCurrPlayer()).setSecretAchievement(goal);
            }
            model.nextPlayer(true);
        }
    }

    //**********to decide if it's necessary to synchronize***********
    public boolean isPlayerInTurn(Player p) {
        synchronized (this.model){
            return model.getCurrPlayer() == model.getPlayers().indexOf(p);
        }
    }


    public void getNickames(){
        synchronized (this.model){

        }
    }




    public  int getCurrPlayersNumber() {
        synchronized(this.players) {
            if (!players.isEmpty())
                return players.size();
            else
                return 999;
        }
    }

    public int getMaxPlayersNumber() {
        return playersNumber;
    }
    /* ########## FINE METODI DA RIMUOVERE, UTILI SOLO AL TESTING DEL NETOWRK ############# */

}
