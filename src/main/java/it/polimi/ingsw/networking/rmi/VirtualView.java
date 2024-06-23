package it.polimi.ingsw.networking.rmi;


import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import it.polimi.ingsw.networking.action.*;

public interface VirtualView extends Remote {

    void reportError(String details) throws RemoteException;

    void showAction(Action act) throws IOException;

    String getNickname() throws RemoteException;

    boolean getOnline() throws RemoteException;

    boolean getGui() throws RemoteException;

    void setOnline(boolean b) throws RemoteException;

    void setNickname(String nick) throws RemoteException;

    void setPing(boolean b) throws RemoteException;

    boolean getPing() throws RemoteException;

    void setInTurn(boolean b) throws RemoteException;

    boolean getInTurn() throws RemoteException;

    void setStarter(boolean b) throws RemoteException;

    String getNicknameFirst() throws RemoteException;

    boolean getStarter()throws RemoteException;
}
