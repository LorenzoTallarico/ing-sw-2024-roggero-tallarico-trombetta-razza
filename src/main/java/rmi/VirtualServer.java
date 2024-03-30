package rmi;

import it.polimi.ingsw.model.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServer extends Remote {

    void connect(VirtualView client) throws RemoteException;

    void placeCard(Card card, int row, int column) throws RemoteException;

    void drawCard(int index) throws RemoteException;

    void selectAchievementCard(int position) throws RemoteException;

}
