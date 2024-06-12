
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
    private Socket serSocket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    private BlockingQueue<Action> serverActions;
    private BlockingQueue<Action> clientActions;
    private ArrayList<VirtualView> clients;
    
    private boolean gui;
    private boolean ping;
    private boolean online;
    private String nickname = null;
    private boolean gameStarted;

    public ClientSocket(Socket serSocket, BlockingQueue<Action> serverActions, BlockingQueue<Action> clientActions, ArrayList<VirtualView> clients, boolean gameStarted) throws IOException {
        this.serSocket = serSocket;
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

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

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
            System.out.println("Sending action to client");
            outputStream.writeObject(act);
            outputStream.flush();
            outputStream.reset();
        }
    }


    @Override
    public void run() {
        while(true) {
            Action action = null;
            try {
                action = (Action) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                if(nickname != null) {
                    if(action.getType().equals(ActionType.PONG)){
                        ping = true;
                    }
                    else {
                        serverActions.put(action);
                    }
                }
                else if(action.getType().equals(ActionType.SETNICKNAME)) {
                    if(!gameStarted) {
                        String tempNickname = checkNickname((SetNicknameAction) action);
                        Action response = new SetNicknameAction(tempNickname, false);

                        if (tempNickname != null) {
                            online = true;
                            nickname = tempNickname;
                            gui = ((SetNicknameAction) action).getGui();
                            System.out.println("> Allowed Socket connection to a new client named \"" + nickname + "\".");
                        } else {
                            System.err.println("> Denied connection to a new client, name already in use");
                            outputStream.writeObject(response);
                            outputStream.flush();
                            outputStream.reset();
                            closeResources();
                            return;
                        }
                        outputStream.writeObject(response);
                        outputStream.flush();
                        outputStream.reset();

                        // lo faccio qui perché va aggiunto per ultimo dopo tutti i controlli
//                        synchronized (clients) {
//                            setOnline(true);
//                            setPing(true);
//                            clients.add(this);
//                        }


                        String destNickname = null;
                        for (int i = 0; i < clients.size() && destNickname == null; i++) {
                            if (clients.get(i).getOnline()) {
                                destNickname = clients.get(i).getNickname();
                            }
                        }
                        int count = 0;
                        for (VirtualView v : this.clients) {
                            if (v.getOnline() && v.getNickname() != null)
                                count++;
                        }
                        Action act = new AskingStartAction(destNickname, count);
                        clientActions.put(act);

                        act = new JoiningPlayerAction(nickname, count, 4);
                        try {
                            clientActions.put(act);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            for (VirtualView v : clients) {
                                System.out.println("Nome: " + v.getNickname());
                            }
                        }


                    } else {
                        {  //Reconnect
                            //Ricerca VirtualView Vecchia
                            nickname = ((SetNicknameAction) action).getNickname();
                            VirtualView oldVirtualView = null;
                            for(VirtualView c : clients){
                                if(c.getNickname().equals(nickname)){
                                    oldVirtualView = c;
                                    break;
                                }
                            }
                            if(oldVirtualView!= null && !oldVirtualView.getOnline()) {
                                // il client si è già connesso in precedenza e deve recuperare i dati

                                // mando maction "reconnect" che manda nickname e la nuova virtualview
                                try {
                                    serverActions.put(new ReconnectedPlayerAction(nickname, oldVirtualView, this));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                //aggiornamento mappe
                                int index = clients.indexOf(oldVirtualView);
                                clients.remove(index);
                                clients.add(index, this);

                                outputStream.writeObject(new SetNicknameAction(nickname, false));
                                outputStream.flush();
                                outputStream.reset();
                            }
                            else{
                                outputStream.writeObject(new SetNicknameAction(null, false));
                                outputStream.flush();
                                outputStream.reset();
                                System.out.println("> User " + nickname + " already online or doesn't exist");
                            }
                        }
                    }
                }
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

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
//            if (serSocket != null && !serSocket.isClosed())
//                serSocket.close();
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura delle risorse: " + e.getMessage());
        }
    }

}
