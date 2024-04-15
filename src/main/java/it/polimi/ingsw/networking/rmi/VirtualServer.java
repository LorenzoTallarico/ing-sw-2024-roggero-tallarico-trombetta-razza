package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.model.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface VirtualServer extends Remote {

    void connect(VirtualView client) throws RemoteException;

    void getNicknames() throws RemoteException;

    void placeCard(Card card, int row, int column) throws RemoteException;

    void drawCard(int index) throws RemoteException;

    void selectAchievementCard(int position) throws RemoteException;




    /* ########## INIZIO METODI DA RIMUOVERE, UTILI SOLO AL TESTING DEL NETOWRK ############# */
    void addState(Integer number) throws RemoteException;
    void reset() throws RemoteException;
    /* ########## FINE METODI DA RIMUOVERE, UTILI SOLO AL TESTING DEL NETOWRK ############# */

}
