package it.polimi.ingsw.networkingProva;

import it.polimi.ingsw.networking.action.ActionType;
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
    private Socket serSocket = null;
    private final ObjectOutputStream outputStream;
    private final BlockingQueue<Action> serverActions;
    private final ObjectInputStream inputStream;
    private final ArrayList<VirtualView> clients;
    private String nickname = null;
    private boolean connected = true;
    private boolean gui;

    public ClientSocket(Socket serSocket, BlockingQueue<Action> serverActions, ArrayList<VirtualView> clients) throws IOException {
        this.serSocket = serSocket;
        this.outputStream = new ObjectOutputStream(serSocket.getOutputStream());
        this.inputStream = new ObjectInputStream(serSocket.getInputStream());
        this.serverActions = serverActions;
        this.clients = clients;
    }

    @Override
    public void reportError(String details) throws RemoteException {

    }

    @Override
    public void showAction(Action act) throws IOException {
        //qui il server sta MANDANDO l'azione al client
        outputStream.writeObject(act);
        outputStream.flush();
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public boolean getOnline() {
        return connected;
    }

    @Override
    public boolean getGui() {
        return gui;
    }


    public void setNickname(String nickname){
        this.nickname = nickname;
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
                if(nickname != null ) {
                    serverActions.put(action);
                }
                else if(action.getType().equals(ActionType.SETNICKNAME)) {
                    boolean checkAlreadyExists = false;
                    String tempNickname = ((SetNicknameAction) action).getNickname();
                    for(VirtualView c : clients)
                        if(tempNickname.equalsIgnoreCase(c.getNickname()))
                            checkAlreadyExists = true;
                    if(checkAlreadyExists)// se ritorna il messaggio con null al client il nick non è valido, altrimenti se torna lo stesso nick al client è stato accettato
                        tempNickname = null;
                    Action response = new SetNicknameAction(tempNickname, false);
                    if(tempNickname != null){
                        connected = true;
                        nickname = tempNickname;
                        gui = ((SetNicknameAction) action).getGui();
                        System.out.println(">Allowed Socket connection to a new client named \""+nickname+"\".");
                    }
                    outputStream.writeObject(response);
                    outputStream.flush();
                    //aggiungere il messaggio fake di aggiunta di un client
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
