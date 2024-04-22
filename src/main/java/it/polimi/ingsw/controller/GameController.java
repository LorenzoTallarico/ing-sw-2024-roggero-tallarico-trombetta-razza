package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import java.util.ArrayList;

public class GameController {
    //Attributi ridondanti, da vedere se utili o togliere
    private Player currentPlayer;
    private final int playersNumber;
    private int position, index;
    private final Game model;
    private final ArrayList<Player> players;
    private final Chat chat;

    public GameController(int playersNumber) {
        model = Game.getInstance();
        this.playersNumber = playersNumber;
        this.players = new ArrayList<>();
        chat = Chat.getInstance();
    }

    public void addPlayer(Player p) {
        synchronized (this.players) {
            if (players.size() <= playersNumber && model.getGameState().equals(GameState.LOBBY)) {
                players.add(p);
                if (players.size() == playersNumber) {
                    model.addPlayers(players);
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

    public boolean drawCard(int index) {
        synchronized (this.model) {
            if (model.getGameState().equals(GameState.GAME) || model.getGameState().equals(GameState.LASTROUND)) {
                Card card = model.draw(index);
                if (model.draw(index) != null) {
                    model.getPlayers().get(model.getCurrPlayer()).getHand().add(card);
                    model.nextPlayer(true);
                    return true;
                }
            }
            return false;
        }
    }

    public boolean selectAchievementCard(int position) {
        synchronized (this.model) {
            if(model.getGameState().equals(GameState.SELECTACHIEVEMENT)) {
                if (position == 0 || position == 1) {
                    ArrayList<AchievementCard> goal = new ArrayList<AchievementCard>();
                    goal.add(model.getPlayers().get(model.getCurrPlayer()).getSecretAchievement().get(position));
                    model.getPlayers().get(model.getCurrPlayer()).setSecretAchievement(goal);
                    model.nextPlayer(true);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean calculateEndPoints() {
        synchronized (this.model) {
            if(model.getGameState().equals(GameState.FINALSCORE)) {
                model.calculateEndPoints();
                model.nextState();
                return true;
            }
        }
        return false;
    }

    //**********to decide if it's necessary to synchronize***********
    public boolean isPlayerInTurn(Player p) {
        synchronized (this.model){
            return model.getCurrPlayer() == model.getPlayers().indexOf(p);
        }
    }

    public void assignStarterAchievement(Player p1) {
        synchronized (this.model) {
            ArrayList<AchievementCard> goals = new ArrayList<AchievementCard>();
            goals.add(model.popAchievementCard());
            goals.add(model.popAchievementCard());
            p1.setSecretAchievement(goals);
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

    public ArrayList<Player> getPlayers() {
        return model.getPlayers();
    }

    public Message sendChatMessage(Message msg) {
        synchronized (this.chat) {
            chat.sendMessage(msg);
            return chat.getLastMessage();
        }
    }

    public ArrayList<Message> getWholeChat() {
        synchronized (this.chat) {
            return chat.getWholeChat();
        }
    }

}
