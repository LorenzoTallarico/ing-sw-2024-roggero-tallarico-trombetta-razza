package it.polimi.ingsw.networking.socket;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;

public class SocketClient implements VirtualViewSocket {
    final BufferedReader input;
//    final ServerProxy server;

    final static int PORT = 1235;

    protected SocketClient(BufferedReader input, BufferedWriter output) {
        this.input = input;
//        this.server = new ServerProxy(output);
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
                    this.showValue(Integer.parseInt(input.readLine()));
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

            if (command == 0) {
                //server.reset();
            } else {
                //server.add(command);
            }
        }
    }



    public static void main(String[] args) throws IOException {

        //Qui dobbiamo connetterci al server, però non abbiamo il registry quindi dobbiamo connetterci al socket del server
        // per capire dove connetterci usiamo passiamo negli argomenti le informazioni necessarie

        String host = "127.0.0.1";
        Socket serverSocket = new Socket(host, PORT);

        InputStreamReader socketRx = new InputStreamReader(serverSocket.getInputStream());
        OutputStreamWriter socketTx = new OutputStreamWriter(serverSocket.getOutputStream());

        new SocketClient(new BufferedReader(socketRx), new BufferedWriter(socketTx)).run();
    }





    public void showValue(Integer number) {
        // TODO Attenzione! Questo può causare data race con il thread dell'interfaccia o un altro thread
        System.out.print("\n= " + number + "\n> ");
    }

    @Override
    public void showUpdate(Integer number) {

    }

    public void reportError(String details) {
        // TODO Attenzione! Questo può causare data race con il thread dell'interfaccia o un altro thread
        System.err.print("\n[ERROR] " + details + "\n> ");
    }


}

