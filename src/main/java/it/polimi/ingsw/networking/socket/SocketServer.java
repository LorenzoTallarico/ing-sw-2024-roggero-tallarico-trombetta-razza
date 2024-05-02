package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.controller.GameController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketServer {
    final ServerSocket listenSocket;
    static int PORT = 1235;
    final GameController controller;
    final List<SocketClientHandler> clients = new ArrayList<>();

    public SocketServer(ServerSocket listenSocket, GameController controller) {
        this.listenSocket = listenSocket;
        this.controller = controller;
    }

    private void runServer() throws IOException {
        Socket clientSocket = null;
        while ((clientSocket = this.listenSocket.accept()) != null) {
            InputStreamReader socketRx = new InputStreamReader(clientSocket.getInputStream());
            OutputStreamWriter socketTx = new OutputStreamWriter(clientSocket.getOutputStream());

            //funziona da interfaccia con il client (svolge una funzione simile allo 'stub')
            SocketClientHandler handler = new SocketClientHandler(this.controller, this, new BufferedReader(socketRx), new PrintWriter(socketTx));

            //aggiunto all'elenco degli handlers per comunicare con i client (come facevamo con gli stub)
            synchronized (this.clients){
                clients.add(handler);
            }
            //creo il thread effettivo (su RMI venivano creati in maniera autonoma)
            new Thread(() -> {
                try {
                    handler.runVirtualView();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    //qua è meglio usare un canale differente usando un thread o altro (magari simile alla blocking queue)
    public void broadcastUpdate(Integer value) {
        synchronized (this.clients) {
            for (SocketClientHandler client : this.clients) {
                client.showUpdate(value);
            }
        }
    }


    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        ServerSocket listenSocket = new ServerSocket(PORT);

        //playersNumber va implementato in maniera corretta
        new SocketServer(listenSocket, new GameController()).runServer(); //! changed gamecontroller, not param!!!
    }
}