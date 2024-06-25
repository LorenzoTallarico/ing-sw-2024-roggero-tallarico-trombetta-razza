
package it.polimi.ingsw.networkingProva;

import it.polimi.ingsw.networking.action.ActionType;
import it.polimi.ingsw.networking.action.toclient.AskingPlayersNumberAction;
import it.polimi.ingsw.networking.action.toclient.ErrorAction;
import it.polimi.ingsw.networking.action.toclient.JoiningPlayerAction;
import it.polimi.ingsw.networking.action.toserver.ReconnectedPlayerAction;
import it.polimi.ingsw.networking.action.toserver.SetNicknameAction;
import it.polimi.ingsw.networking.action.toserver.StartAction;
import it.polimi.ingsw.networking.rmi.VirtualView;
import it.polimi.ingsw.networking.action.Action;


import java.io.IOException;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class ClientSocket implements VirtualView, Runnable {
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    private final BlockingQueue<Action> serverActions;
    private final BlockingQueue<Action> clientActions;
    private final ArrayList<VirtualView> clients;
    

    private boolean ping;
    private boolean online;
    private String nickname = null;
    private final boolean gameStarted;
    private final int playersNumber;
    private boolean starter= false;

    public ClientSocket(Socket serSocket, BlockingQueue<Action> serverActions, BlockingQueue<Action> clientActions, ArrayList<VirtualView> clients, boolean gameStarted, int playersNumber) throws IOException {
        this.outputStream = new ObjectOutputStream(serSocket.getOutputStream());
        this.inputStream = new ObjectInputStream(serSocket.getInputStream());
        this.serverActions = serverActions;
        this.clientActions = clientActions;
        this.clients = clients;
        this.gameStarted = gameStarted;
        this.playersNumber = playersNumber;
    }

    //GETTER & SETTER
    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public boolean getOnline() {
        return online;
    }


    @Override
    public boolean getPing() {
        return ping;
    }

    @Override
    public void setInTurn(boolean b) throws RemoteException {

    }

    @Override
    public boolean getInTurn() throws RemoteException {
        return false;
    }

    @Override
    public void setStartRoutine(boolean b) throws RemoteException {

    }

    @Override
    public boolean getStartRoutine() throws RemoteException {
        System.out.println("Sto chiamando il metodo di ClientSocket************************************************************************************************");
        return false;
    }

    @Override
    public String getNicknameFirst() throws RemoteException {
        return "";
    }

    @Override
    public boolean getStarter() throws RemoteException {
        return starter;
    }

    @Override
    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    @Override
    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public void setPing(boolean b) {
        ping=b;
    }


    @Override
    public void reportError(String details) throws RemoteException {

    }

    @Override
    public void showAction(Action act) throws IOException {
        //qui il server sta MANDANDO l'azione al client
        synchronized (outputStream) {
            try{
                System.out.println("Sending action to client" + act.getType());
                outputStream.writeObject(act);
                outputStream.flush();
                outputStream.reset();
            } catch (SocketException e) {
                System.err.println("Connection reset by peer");
                closeResources();
            }
        }
    }


    @Override
    public void run() {
        try {
            while (true) {
                Action action = null;
                //vedere se serve sincronizzare anche su inputStream
                try {
                    action = (Action) inputStream.readObject();
                    System.out.println("Messaggio Arrivato: " + action.getType());
                } catch (SocketException e) {
                    System.out.println("Connection reset by peer");
                    // (VEDERE SE TOGLIERE)  questo break permette di uscire dal ciclo se c'è un problema di lettura del socket
                    break;

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    // (VEDERE SE TOGLIERE)  questo break permette di uscire dal ciclo se c'è un problema di lettura del socket
                    break;

                }
                //try {
                    System.out.println("Stampo in ordine: nickname, online, ping" + nickname + online + ping);


                    if (nickname != null) {
                        if (action.getType().equals(ActionType.PONG)) {
                            this.ping = true;
                            System.out.println("ho settato il ping a true di " + nickname);
                        } else {
                            try {
                                serverActions.put(action);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else if (action.getType().equals(ActionType.SETNICKNAME)) {

                        if (!gameStarted) { // connection to Lobby
                            String nick = ((SetNicknameAction) action).getNickname();
                            System.out.println("Nick: " + nick);
                            boolean sizeRequest = false;
                            if (!clients.isEmpty()) {
                                for (VirtualView v : this.clients) {
                                    if (nick != null && v.getNickname() != null && v.getNickname().equalsIgnoreCase(nick)) {
                                        System.out.println("> Denied connection to a new client, user \"" + nick + "\" already existing and now online.");
                                        try {
                                            showAction(new ErrorAction(nick, "Denied connection to a new client, user \"" + nick + "\" already existing and now online."));
                                            showAction(new SetNicknameAction(null, false));
                                            return;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if (countOnlinePlayer() >= 4) {
                                    System.out.println("> Denied connection to a new client, max number of players already reached.");
                                    try {
                                        showAction(new ErrorAction(nick, "Max amount of players reached."));
                                        showAction(new SetNicknameAction(null, false));
                                        return;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                            if (clients.size() == 1 && playersNumber == 0) {
                                try {
                                    showAction(new ErrorAction(nick, "Another player has just started a game, they still haven't chosen the size of the game, wait some seconds before reconnecting."));
                                    showAction(new SetNicknameAction(null, false));
                                    return;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (clients.isEmpty()) {
                                sizeRequest = true;
                            }
                            online = true;
                            ping = true;
                            nickname = nick;
                            System.out.println("nickname: " + nickname);
                            System.out.println("> Allowed Socket connection to a new client named \"" + nick + "\".");
                            clients.add(this);
                            showAction(new SetNicknameAction(nickname, false));
                            if (sizeRequest) {
                                try {
                                    clientActions.put(new AskingPlayersNumberAction(nick));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                if (clients.size() > 1) {
                                    clientActions.put(new JoiningPlayerAction(nick, countOnlinePlayer(), playersNumber));
                                    serverActions.put(new StartAction(null));
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //return;
                        } else {
                            //Reconnect
                            //Ricerca VirtualView Vecchia
                            nickname = ((SetNicknameAction) action).getNickname();
                            VirtualView oldVirtualView = null;
                            for (VirtualView c : clients) {
                                if (c.getNickname().equalsIgnoreCase(nickname)) {
                                    oldVirtualView = c;
                                    break;
                                }
                            }
                            if (oldVirtualView != null && !oldVirtualView.getOnline()) {
                                try {

                                    starter = oldVirtualView.getStarter();
                                    online = true;
                                    ping = true;
                                    int index = clients.indexOf(oldVirtualView);
                                    clients.remove(index);
                                    clients.add(index, this);
                                    showAction(new SetNicknameAction(nickname, false));
                                    serverActions.put(new ReconnectedPlayerAction(nickname, oldVirtualView, this));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                } else {
                                    showAction(new SetNicknameAction(null, false));
                                    System.out.println("> User " + nickname + " already online or doesn't exist");
                                }

                        }


                    }

//                } catch (IOException e) {           //eventualmente da aggiungere una eccezione
//                    e.printStackTrace();
//                }


                }

            //}

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("> Client disconnected: " + nickname);
            closeResources();
        }
    }

    //serve???
    private String checkNickname(SetNicknameAction action) throws RemoteException {
        boolean checkAlreadyExists = false;
        String tempNickname = action.getNickname();
        for (VirtualView c : clients) {
            if (tempNickname.equalsIgnoreCase(c.getNickname()))
                checkAlreadyExists = true;
        }
        if (checkAlreadyExists)// se ritorna il messaggio con null al client il nick non è valido, altrimenti se torna lo stesso nick al client è stato accettato
            tempNickname = null;
        return tempNickname;
    }


    private void closeResources() {
        try {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura delle risorse: " + e.getMessage());
        }
    }

    private int countOnlinePlayer() throws RemoteException {
        int count = 0;
        synchronized (clients) {
            for (VirtualView c : this.clients) {
                if (c.getOnline())
                    count++;
            }
        }
        return count;
    }

    public void setStarter(boolean starter) {
        this.starter = starter;
    }
}
