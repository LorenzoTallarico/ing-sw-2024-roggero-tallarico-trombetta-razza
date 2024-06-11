package it.polimi.ingsw.networkingProva;

import it.polimi.ingsw.networking.action.Action;

import java.io.IOException;
import java.rmi.RemoteException;

public class Consumer {


    public void showAction(Action act) throws IOException{

    }

    public String getNickname(){
        return "";
    }
    public boolean getOnline(){
        return false;
    };
    public boolean getGui() {
        return false;
    }

    void setOnline(boolean b){

    }

    void setNickname(String nick) {

    }

    void setPing(boolean b){

    }

    public boolean getPing(){
        return false;
    }

    public String getNicknameFirst() {
        return "";
    }
}
