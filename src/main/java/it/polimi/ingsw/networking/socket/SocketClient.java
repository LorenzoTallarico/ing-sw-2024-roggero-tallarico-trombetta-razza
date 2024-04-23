package it.polimi.ingsw.networking.socket;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;

public class SocketClient implements VirtualViewSocket {
    final BufferedReader input;
    final ServerProxy server;

    final static int PORT = 1235;

    //il ServerProxy quindi è stato messo 'sopra' al BufferWriter, in questo modo chiamamia i metodi
    // sul serverProxy che poi va veramente a mandare i messaggi
    protected SocketClient(BufferedReader input, BufferedWriter output) {
        this.input = input;
        this.server = new ServerProxy(output);
    }

    private void run() throws RemoteException {
        new Thread(() -> {
            try {
                runVirtualServer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        runCli();
    }

    private void runVirtualServer() throws IOException {
        String line;
        // Read message type
        while ((line = input.readLine()) != null) {
            // Read message and perform action
            switch (line) {
                case "update":
                    this.showUpdate(Integer.parseInt(input.readLine()));
                    break;
                case "error":
                    this.reportError(input.readLine());
                    break;
                default:
                    System.err.println("[INVALID MESSAGE]");
            }
        }
    }

    private void runCli() throws RemoteException {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            int command = scan.nextInt();

            //Qui quando vogliamo inviare i comandi usiamo il serverProxy
            if (command == 0) {
                server.reset();
            } else {
                server.add(command);
            }
        }
    }



    public static void main(String[] args) throws IOException {

        //Qui dobbiamo connetterci al server, però non abbiamo il registry quindi dobbiamo connetterci al socket del server
        // per capire dove connetterci usiamo passiamo negli argomenti le informazioni necessarie

        String host = "127.0.0.1";

        //gestire qui sotto con try catch
        Socket serverSocket = new Socket(host, PORT);

        InputStreamReader socketRx = new InputStreamReader(serverSocket.getInputStream());
        OutputStreamWriter socketTx = new OutputStreamWriter(serverSocket.getOutputStream());

        new SocketClient(new BufferedReader(socketRx), new BufferedWriter(socketTx)).run();
    }




    @Override
    public void showUpdate(Integer number) {
        // TODO Attenzione! Questo può causare data race con il thread dell'interfaccia o un altro thread
        System.out.print("\n= " + number + "\n> ");
    }

    public void reportError(String details) {
        // TODO Attenzione! Questo può causare data race con il thread dell'interfaccia o un altro thread
        System.err.print("\n[ERROR] " + details + "\n> ");
    }


}

