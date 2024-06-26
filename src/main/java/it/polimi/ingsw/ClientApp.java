package it.polimi.ingsw;


import it.polimi.ingsw.networking.Client;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.*;

public class ClientApp {

    public static void main(String[] args) throws NotBoundException, IOException {
        String AsciiArt =
                "  ______                   __                            __    __              __                                   __  __            \n"+
                " /      \\                 /  |                          /  \\  /  |            /  |                                 /  |/  |           \n"+
                "/$$$$$$  |  ______    ____$$ |  ______   __    __       $$  \\ $$ |  ______   _$$ |_    __    __   ______   ______  $$ |$$/   _______  \n"+
                "$$ |  $$/  /      \\  /    $$ | /      \\ /  \\  /  |      $$$  \\$$ | /      \\ / $$   |  /  |  /  | /      \\ /      \\ $$ |/  | /       | \n"+
                "$$ |      /$$$$$$  |/$$$$$$$ |/$$$$$$  |$$  \\/$$/       $$$$  $$ | $$$$$$  |$$$$$$/   $$ |  $$ |/$$$$$$  |$$$$$$  |$$ |$$ |/$$$$$$$/  \n"+
                "$$ |   __ $$ |  $$ |$$ |  $$ |$$    $$ | $$  $$<        $$ $$ $$ | /    $$ |  $$ | __ $$ |  $$ |$$ |  $$/ /    $$ |$$ |$$ |$$      \\  \n"+
                "$$ \\__/  |$$ \\__$$ |$$ \\__$$ |$$$$$$$$/  /$$$$  \\       $$ |$$$$ |/$$$$$$$ |  $$ |/  |$$ \\__$$ |$$ |     /$$$$$$$ |$$ |$$ | $$$$$$  | \n"+
                "$$    $$/ $$    $$/ $$    $$ |$$       |/$$/ $$  |      $$ | $$$ |$$    $$ |  $$  $$/ $$    $$/ $$ |     $$    $$ |$$ |$$ |/     $$/  \n"+
                " $$$$$$/   $$$$$$/   $$$$$$$/  $$$$$$$/ $$/   $$/       $$/   $$/  $$$$$$$/    $$$$/   $$$$$$/  $$/       $$$$$$$/ $$/ $$/ $$$$$$$/   \n";
        boolean checkChoice = false;
        boolean portFlag = false;
        boolean nicknameOk = false;
        Scanner scan = new Scanner(System.in);
        String line;
        int connectionChoice = 0;
        int portChoice = 7171;
        String ipChoice = null;
        String ip = "127.0.0.1";
        String nickname = null;
        int guiChoice = 0;
        System.out.println(AsciiArt);
        do {
            System.out.println("> Choose the interface:");
            System.out.println("   [1] TUI");
            System.out.println("   [2] GUI");
            try {
                scan = new Scanner(System.in);
                line = scan.nextLine();
                guiChoice = Integer.parseInt(line);
                if (guiChoice == 1 || guiChoice == 2) {
                    checkChoice = true;
                } else {
                    System.out.println("> Please enter a valid number.");
                }
            } catch (NoSuchElementException | NumberFormatException ignored) {
                System.err.println("> Invalid input. Please enter a numeric value.");
            }
        } while(!checkChoice);

        checkChoice = false;

        do {
            System.out.println("> Choose the connection method:");
            System.out.println("   [1] RMI Connection");
            System.out.println("   [2] Socket Connection");
            try {
                scan = new Scanner(System.in);
                line = scan.nextLine();
                connectionChoice = Integer.parseInt(line);
                if(connectionChoice == 1 || connectionChoice == 2) {
                    checkChoice = true;
                } else {
                    System.out.println("> Please enter a valid number.");
                }
            } catch (NoSuchElementException | NumberFormatException ignored) {
                System.err.println("> Invalid input. Please enter a numeric value.");
            }
        } while(!checkChoice);

        checkChoice = false;

        do {
            System.out.println("> Select port (0 for default): ");
            try {
                scan = new Scanner(System.in);
                line = scan.nextLine();
                portChoice = Integer.parseInt(line);
                if (portChoice == 0) {
                    if (connectionChoice == 1) //RMI
                        portChoice = 6969;
                    else //Socket
                        portChoice = 7171;
                    checkChoice = true;
                } else if (portChoice > 0 && portChoice <= 65535) {
                    checkChoice = true;
                } else {
                    System.out.println("> Invalid port number. Please enter a value between 1 and 65535.");
                }
            } catch (NoSuchElementException | NumberFormatException ignored) {
                System.err.println("> Invalid input. Please enter a numeric value.");
            }
        } while(!checkChoice);

        checkChoice = false;

        do {
            System.out.println("> Select ip (0 for localhost): ");
            try {
                scan = new Scanner(System.in);
                line = scan.nextLine();
                ipChoice = line;
                if(!ipChoice.contains("."))
                    checkChoice = false;
                else
                    checkChoice = true;
                if (ipChoice.equals("0")) {
                    ipChoice = "127.0.0.1";
                    checkChoice = true;
                }
            } catch (NoSuchElementException | NumberFormatException ignored) {
                System.out.println("> Invalid port number. Please enter a value between 1 and 65535.");
            }
        } while(!checkChoice);

        checkChoice = false;

        do {
            System.out.println("> Enter Nickname: ");
            try {
                scan = new Scanner(System.in);
                nickname = scan.nextLine().trim();
                checkChoice = !nickname.isEmpty();
            } catch (NoSuchElementException | NumberFormatException ignored) { }
        } while(!checkChoice);

        Client c = new Client(connectionChoice, portChoice, ipChoice, (guiChoice != 1), nickname);
    }

}

