package it.polimi.ingsw.networkingProva;

import it.polimi.ingsw.networking.action.ActionType;
import it.polimi.ingsw.networking.action.toclient.AskingStartAction;
import it.polimi.ingsw.networking.action.toclient.JoiningPlayerAction;
import it.polimi.ingsw.networking.action.toserver.ReconnectedPlayerAction;
import it.polimi.ingsw.networking.action.toserver.SetNicknameAction;
import it.polimi.ingsw.networking.rmi.VirtualView;
import it.polimi.ingsw.networking.action.Action;

import java.io.IOException;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class ClientSocket implements VirtualView, Runnable {
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    private final BlockingQueue<Action> serverActions;
    private final BlockingQueue<Action> clientActions;
    private final ArrayList<VirtualView> clients;

    private boolean gui;
    private boolean ping;
    private boolean online;
    private String nickname = null;
    private final boolean gameStarted;

    public ClientSocket(Socket serSocket, BlockingQueue<Action> serverActions, BlockingQueue<Action> clientActions, ArrayList<VirtualView> clients, boolean gameStarted) throws IOException {
        this.outputStream = new ObjectOutputStream(serSocket.getOutputStream());
        this.inputStream = new ObjectInputStream(serSocket.getInputStream());
        this.serverActions = serverActions;
        this.clientActions = clientActions;
        this.clients = clients;
        this.gameStarted = gameStarted;
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
    public boolean getGui() {
        return gui;
    }

    @Override
    public boolean getPing() {
        return ping;
    }

    @Override
    public String getNicknameFirst() throws RemoteException {
        return "";
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public void setPing(boolean b) {
        ping = b;
    }

    @Override
    public void reportError(String details) throws RemoteException {
        // Error reporting logic (to be implemented if needed)
    }

    @Override
    public void showAction(Action act) throws IOException {
        outputStream.writeObject(act);
        outputStream.flush();
        outputStream.reset();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Action action = (Action) inputStream.readObject();
                handleAction(action);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break; // Exit loop on error
            }
        }
    }

    private void handleAction(Action action) {
        try {
            if (nickname != null) {
                if (action.getType().equals(ActionType.PONG)) {
                    ping = true;
                } else {
                    serverActions.put(action);
                }
            } else if (action.getType().equals(ActionType.SETNICKNAME)) {
                handleSetNicknameAction((SetNicknameAction) action);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSetNicknameAction(SetNicknameAction action) throws IOException, InterruptedException {
        if (!gameStarted) {
            String tempNickname = checkNickname(action);
            Action response = new SetNicknameAction(tempNickname, false);
            if (tempNickname != null) {
                online = true;
                nickname = tempNickname;
                gui = action.getGui();
                System.out.println(">Allowed Socket connection to a new client named \"" + nickname + "\".");
            }
            outputStream.writeObject(response);
            outputStream.flush();
            outputStream.reset();

            sendLobbyUpdates(tempNickname);
        } else {
            handleReconnection(action);
        }
    }

    private void handleReconnection(SetNicknameAction action) throws IOException, InterruptedException {
        nickname = action.getNickname();
        VirtualView oldVirtualView = null;
        synchronized (clients) {
            for (VirtualView c : clients) {
                if (c.getNickname().equals(nickname)) {
                    oldVirtualView = c;
                    break;
                }
            }
            if (oldVirtualView != null && !oldVirtualView.getOnline()) {
                serverActions.put(new ReconnectedPlayerAction(nickname, oldVirtualView, this));
                int index = clients.indexOf(oldVirtualView);
                clients.set(index, this);

                outputStream.writeObject(new SetNicknameAction(nickname, false));
                outputStream.flush();
                outputStream.reset();
            } else {
                outputStream.writeObject(new SetNicknameAction(null, false));
                outputStream.flush();
                outputStream.reset();
                System.out.println("> User " + nickname + " already online or doesn't exist");
            }
        }
    }


    private void sendLobbyUpdates(String tempNickname) throws InterruptedException, RemoteException {
        synchronized (clients) {
            String destNickname = null;
            for (VirtualView client : clients) {
                if (client.getOnline() && destNickname == null) {
                    destNickname = client.getNickname();
                }
            }
            int count = 0;
            for (VirtualView client : clients) {
                if (client.getOnline()) {
                    count++;
                }
            }
            clientActions.put(new AskingStartAction(destNickname, count));
            clientActions.put(new JoiningPlayerAction(tempNickname, count, 4));
        }
    }

    private String checkNickname(SetNicknameAction action) throws RemoteException {
        String tempNickname = action.getNickname();
        synchronized (clients) {
            for (VirtualView c : clients) {
                if (tempNickname.equalsIgnoreCase(c.getNickname())) {
                    return null; // Nickname already exists
                }
            }
        }
        return tempNickname;
    }
}