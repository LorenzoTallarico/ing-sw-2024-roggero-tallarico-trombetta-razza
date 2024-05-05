package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.rmi.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class GameController {
    //Attributi ridondanti, da vedere se utili o togliere
    private Player currentPlayer;
    private int playersNumber;
    private int position, index;
    private final Game model;
    private final ArrayList<Player> players;
    private final ArrayList<VirtualView> clients;
    private final Chat chat;
    private int startCountdown;

    public GameController() {
        model = Game.getInstance();
        this.players = new ArrayList<>();
        this.clients = new ArrayList<>();
        chat = Chat.getInstance();
    }


    //NB: TUTTI I METODI DEVONO ESSERE CORRETTAMENTE SINCRONIZZATI NEL CONTROLLER

    public void setStarterCardSide(String playerName, boolean front) throws RemoteException {
        model.setStarterCard(playerName, front);
    }

    public void addPlayer(Player p, VirtualView v) throws RemoteException {
        synchronized (this.players) {
            if (players.size() <= playersNumber && model.getGameState().equals(GameState.LOBBY)) {
                players.add(p);
                clients.add(v);
                if (players.size() == playersNumber) {
                    model.addPlayers(players, clients);
                }
            } else {
                System.out.println("> Controller couldn't add player " + p.getName());
            }
        }
    }


    public boolean placeCard(Card card, boolean side, int row, int column) throws RemoteException {
            synchronized (this.model) {
                if(model.getGameState().equals(GameState.GAME)||model.getGameState().equals(GameState.LASTROUND)) {
                    return model.getPlayers().get(model.getCurrPlayer()).place(card, side, row, column);
                }
                return false;
            }
    }

    public boolean drawCard(int index) {
        synchronized (this.model) {
            if (model.getGameState().equals(GameState.GAME) || model.getGameState().equals(GameState.LASTROUND)) {
                if(getPlayers().get(model.getCurrPlayer()).getHand().size() == 2){
                    Card card = model.draw(index);
                    if (model.draw(index) != null) {
                        model.getPlayers().get(model.getCurrPlayer()).getHand().add(card);
                        model.nextPlayer(true);
                        return true;
                    }
                }
            }
            return false;
        }
    }
/* NON USATO ! -  AL SUO POSTO setSecretAchievement()
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
*/
    public void setSecretAchievement(String playerName, AchievementCard achievement) {
        ArrayList<AchievementCard> secretAch = new ArrayList<>();
        secretAch.add(achievement);
        for(Player p : model.getPlayers()) {
            if(p.getName().equalsIgnoreCase(playerName)) {
                p.setSecretAchievement(secretAch);
                countdown();
                return;
            }
        }
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


    //everytime a player set his game (successfully chooses starter card and achievement)
    //the countdown decrease
    //if it reaches zero the game can start and the gamestate is set to GAME
    private void countdown() {
        startCountdown--;
        if(startCountdown == 0) {
            model.setGameState(GameState.GAME);
        }
    }

    public boolean isPlayerInTurn(Player p) {
        synchronized (this.model){
            return model.getCurrPlayer() == model.getPlayers().indexOf(p);
        }
    }
/* non usato
    public void assignStarterAchievement(Player p1) {
        synchronized (this.model) {
            ArrayList<AchievementCard> goals = new ArrayList<AchievementCard>();
            goals.add(model.popAchievementCard());
            goals.add(model.popAchievementCard());
            p1.setSecretAchievement(goals);
        }
    }
*/

    public  int getCurrPlayersNumber() {
        synchronized(this.players) {
            if (!players.isEmpty())
                return players.size();
            else
                return 0;
        }
    }

    public int getMaxPlayersNumber() {
        return this.playersNumber;
    }

    public ArrayList<Player> getPlayers() {
        synchronized(this.players){
            return model.getPlayers();
        }
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

    public void setPlayersNumber(int playersNumber) {
        this.playersNumber = playersNumber;
    }

    // ******* METODI UTILI PER TESTARE SOCKET **************
    public boolean add(Integer number) {
        synchronized (this.model) {
            return this.model.add(number);
        }
    }

    public Integer getCurrent() {
        synchronized (this.model) {
            return this.model.get();
        }
    }

    public boolean reset() {
        synchronized (this.model) {
            return this.model.add(- this.model.get());
        }
    }



}
