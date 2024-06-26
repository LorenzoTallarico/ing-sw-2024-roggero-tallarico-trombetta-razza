package it.polimi.ingsw.networking;


import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.networking.action.Action;

public interface VirtualView extends Remote {

    boolean getOnline() throws RemoteException;

    String getNickname() throws RemoteException;

    boolean getPing() throws RemoteException;

    boolean getStarter()throws RemoteException;

    String getNicknameFirst() throws RemoteException;

    void setOnline(boolean b) throws RemoteException;

    void setNickname(String nick) throws RemoteException;

    void setPing(boolean b) throws RemoteException;

    void setStarter(boolean b) throws RemoteException;

    void showAction(Action act) throws IOException;
}
