package it.polimi.ingsw.networking.socket;

import java.io.BufferedWriter;
import java.io.PrintWriter;

public class ServerProxy implements VirtualServerSocket {
    final PrintWriter output;


    //classe che implementa i metodi del virtual sewrver.
    //così astriamo il fatto che sia in mezzo della comunicazione e in questo modo nel momento
    // in cui chiamiamo i metodi del virtual server andiamo a mandare i messaggi (nascondiamo il fatto che ci sia della comunicazione dietro)
    // questo ci permette di cambiare il layer di comunicazione.
    // questo è l'anaolog del clientHandler però a lato server.
    public ServerProxy(BufferedWriter output) {
        this.output = new PrintWriter(output);
    }

    @Override
    public void connect(VirtualViewSocket client) {
        output.println("connect");
        output.flush();
    }

    @Override
    public void add(Integer number) {
        output.println("add");
        output.println(number);
        output.flush();
    }

    @Override
    public void reset() {
        output.println("reset");
        output.flush();
    }
}
