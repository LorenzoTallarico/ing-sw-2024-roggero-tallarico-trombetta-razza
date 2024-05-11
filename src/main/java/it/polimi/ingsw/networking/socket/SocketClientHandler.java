package it.polimi.ingsw.networking.socket;
/*
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.networking.rmi.VirtualView;
import it.polimi.ingsw.networking.socket.VirtualServerSocket;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClientHandler implements VirtualViewSocket {
    final GameController controller;
    //final SocketServer server;
    final BufferedReader input;
    //da capire se ha senso usare Reader e Writer *BUFFERED*
    final PrintWriter output;;
    //final VirtualViewSocket view;


    //permette di leggere dal socket e comunicare cosa abbiamo ricevuto e applicare modifiche tramite controller
    // ci da un modo per comunicare ai client
    public SocketClientHandler(GameController controller, SocketServer server, BufferedReader input, PrintWriter output/*, VirtualViewSocket view*//*) {
        this.controller = controller;
        this.server = server;
        this.input = input;
        this.output = output;
        //this.view = new ClientProxy(output);
    }

    //
    public void runVirtualView() throws IOException {
        String line;
        // Read message type
        while ((line = input.readLine()) != null) {
            // Read message and perform action

            //NB: qui funziona tutto a messaggi, una soluzione che si potrebbe attuare è quella di utilizzare una interfaccia tipo SocketMessage
            // in questa interfaccia possiamo elencare tutti i possibili id dei tipi di informazioni che dobbiamo passare nella rete e riconoscendo
            // l'identificatore del messaggio poter capirne il tipo e poi agire di conseguenza
            // si va a prendere l'ID del messaggio, lo si manda sul socket, quando dall'altro lato allora lo andro a deserializzare in base al tipo ottenuto
            //esempio: Formato.deserialize<TypeOfMessage> --> nella prima riga del case di riferimento
            switch (line) {
                case "add":
                    this.controller.add(Integer.parseInt(input.readLine()));
                    this.server.broadcastUpdate(this.controller.getCurrent());
                    break;
                case "reset":
                    this.controller.reset();
                    this.server.broadcastUpdate(this.controller.getCurrent());
                    break;
                default :
                    System.err.println("[INVALID MESSAGE]");
            }
        }
    }

    @Override
    public void showUpdate(Integer number) {
        //qui il server ci ha chiesto di mostrare un update al client
        // per comunicare tramite socket, quindi devo mandare un messaggio (prima l'header che ci identifica il tipo di messaggio, cioè "update"
        this.output.println("update");
        this.output.println(number);
        //flush per sicurezza dato che sto usando uno StreamBuffer
        // il messaggio viene mandato ogni tanto con i Buffer quindi con il flush ci assicuriamo che venga mandato
        this.output.flush();



//        synchronized (this.view) {
//            this.view.showValue(number);
//        }
    }

    @Override
    public void reportError(String details) {
        this.output.println("error");
        this.output.println(details);
        this.output.flush();

//        synchronized (this.view) {
//            this.view.reportError(details);
//        }
    }

//    @Override
//    public void connect(VirtualViewSocket client) {
//
//    }
//
//    @Override
//    public void add(Integer number) {
//
//    }
//
//    @Override
//    public void reset() {
//
//    }



}
*/