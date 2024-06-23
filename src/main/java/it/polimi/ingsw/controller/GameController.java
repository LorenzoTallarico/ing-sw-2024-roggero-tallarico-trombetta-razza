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
    public void reconnection(String nickname, VirtualView oldVirtualView, VirtualView newVirtualView) throws RemoteException {

        System.out.println("\n\nDentro reconnection in GameController, elenco delle virtualview:");
        System.out.println("oldvirtualview --> " + oldVirtualView);
        System.out.println("newvirtualview --> " + newVirtualView);
        System.out.println("Elenco delle virtualview dentro clients:");
        for(VirtualView v : clients){
            System.out.println(v);
        }
        System.out.println("\n\n");
        /*int index = clients.indexOf(oldVirtualView);
        clients.remove(index);
        clients.add(index, newVirtualView);*/
        model.reconnection(nickname, oldVirtualView, newVirtualView);
    }

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


    public boolean placeCard(String playerName, int cardIndex, boolean side, int row, int column) throws RemoteException {
        synchronized (this.model) {
            if(model.getGameState().equals(GameState.GAME) || model.getGameState().equals(GameState.LASTROUND)) {
                return model.getPlayers().get(model.getCurrPlayer()).place(cardIndex, side, row, column);
            }
            return false;
        }
    }

    public boolean drawCard(String playerName, int index) throws RemoteException {
        //non si sincronizza
        //synchronized (this.model) {
            if (model.getGameState().equals(GameState.GAME) || model.getGameState().equals(GameState.LASTROUND)) {
                if(getPlayers().get(model.getCurrPlayer()).getHand().size() == 2) {
                    Card card = model.draw(playerName, index);
                    if(card != null) {
                        model.nextPlayer();
                        return true;
                    }
                } else {
                    System.out.println("> Player " + playerName + " has too many cards, they can't draw one.");
                }
            } else {
                System.out.println("> Player " + playerName + " can't draw a card, it's not the right state of the game.");
            }
            return false;
        //}
    }
    public void disconnection(String playerName) throws RemoteException {
        synchronized (this.model) {
            model.disconnection(playerName);
            System.err.println("Dentro GameController disconnection");
        }
    }
    public void setSecretAchievement(String playerName, AchievementCard achievement) throws RemoteException {
        ArrayList<AchievementCard> secretAch = new ArrayList<>();
        secretAch.add(achievement);
        for(Player p : model.getPlayers()) {
            if(p.getName().equalsIgnoreCase(playerName)) {
                p.setSecretAchievement(secretAch);
                synchronized (clients) {
                    for (VirtualView c : clients) {
                        if (playerName.equalsIgnoreCase(c.getNickname())) {
                            c.setStarter(true);
                        }
                    }
                }
                countdown();
                return;
            }
        }
    }

    public boolean calculateEndPoints() throws RemoteException {
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
    private void countdown() throws RemoteException {
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

    public int getCurrPlayersNumber() {
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
        startCountdown = playersNumber;
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
