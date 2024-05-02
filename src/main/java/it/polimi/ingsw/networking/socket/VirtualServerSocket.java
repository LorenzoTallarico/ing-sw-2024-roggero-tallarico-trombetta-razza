package it.polimi.ingsw.networking.socket;

public interface VirtualServerSocket {

    public void connect(VirtualViewSocket client);

    public void add(Integer number);

    public void reset();


}
