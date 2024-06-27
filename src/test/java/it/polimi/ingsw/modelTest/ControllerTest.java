package it.polimi.ingsw.modelTest;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.ClientRmi;
import it.polimi.ingsw.networking.VirtualView;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.rmi.RemoteException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {


    public ArrayList<AchievementCard> getOrderedAchievementDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/AchievementCards.json")) {
            AchievementCard[] tempAchievement = gson.fromJson(reader, AchievementCard[].class);
            ArrayList<AchievementCard> tempDeck = new ArrayList<>();
            for (AchievementCard achievementCard : tempAchievement)
                tempDeck.add(new AchievementCard(achievementCard.getPoints(), achievementCard.getResource(), achievementCard.getStrategyType(), achievementCard.getItem(), achievementCard.getID()));
            return tempDeck;
        } catch (IOException e) {
            return null;
        }
    }

    @Test
    void controllerTest() throws RemoteException {
        Card tempCard1;
        Card tempCard2;
        AchievementCard tempAchievement1;
        AchievementCard tempAchievement2;
        Game testGame = Game.getInstance();
        GameController controller = new GameController();
        controller.setPlayersNumber(2);
        Player fake1 = new Player("Marco");
        VirtualView cli1 = new ClientRmi(null);
        Player fake2 = new Player("Simone");
        VirtualView cli2 = new ClientRmi(null);
        // Player fake3 = new Player("giovanni");
        // VirtualView cli3 = new ClientRmi(null);
        // test of addPlayer method
        controller.addPlayer(fake1 , cli1);
        controller.addPlayer(fake2 , cli2);
        assertEquals(testGame.getPlayersNumber(),2);
        // test of placeCard method
        tempCard1 = fake1.getHand().get(2);
        assertInstanceOf(GoldCard.class, tempCard1); // third card in the hand is a GoldCard
        tempCard2 = fake2.getHand().get(0);
        testGame.setGameState(GameState.GAME);
        testGame.setCurrPlayer(fake1);
        controller.placeCard("Marco",0,true,41,41);
        testGame.setCurrPlayer(fake2);
        controller.placeCard("Simone",0,true,41,41);
        assertNotEquals(fake1.getArea().getSpace(41,41).getCard(),null);
        assertEquals(fake2.getArea().getSpace(41,41).getCard(),tempCard2);
        // test of drawCard method, Marco draws a resourceCard directly from resource deck
        testGame.setCurrPlayer(fake1);
        controller.drawCard("Marco",6);
        assertEquals(fake1.getHand().size(),3);
        // test of setSecretAchievement method
        tempAchievement1 = getOrderedAchievementDeck().get(0);
        controller.setSecretAchievement("Marco",tempAchievement1);
        assertEquals(fake1.getSecretAchievement().get(0),tempAchievement1);
        // test of sendChatMessage method
        Chat chat = Chat.getInstance();
        Message msg = new Message("prova","autore");
        controller.sendChatMessage(msg);
        assertEquals(chat.getLastMessage(),msg);
        // test of getWholeChat() method
        Message msg2 = new Message("prova2","autore2");
        controller.sendChatMessage(msg2);
        Message msx;
        Message msx2;
        msx = controller.getWholeChat().get(0);
        assertEquals(msx,msg);
        msx2 = controller.getWholeChat().get(1);
        assertEquals(msx2,msg2);
        // test of isPlayerInTurn function
        boolean bool = testGame.getCurrPlayer() == testGame.getPlayers().indexOf(fake2);
        assertTrue(bool);
        // test of calculateEndPoints function
        bool = controller.calculateEndPoints();
        assertFalse(bool);
        testGame.setGameState(GameState.FINALSCORE);
        bool = controller.calculateEndPoints();
        assertTrue(bool);
        testGame.disconnection("luca");
        testGame.setGameState(GameState.LOBBY);
        assertFalse(controller.placeCard(fake1.getName(), 1, false, 40, 40));
        assertFalse(controller.drawCard(fake1.getName(), 0));


        testGame.end();
        testGame.nextState();
    }

    @Test
    void starterCardTest() throws RemoteException {
        Game testGame = Game.getInstance();
        GameController controller = new GameController();
        controller.setPlayersNumber(2);
        Player fake1 = new Player("Marco");
        VirtualView cli1 = new ClientRmi(null);
        Player fake2 = new Player("Simone");
        VirtualView cli2 = new ClientRmi(null);
        controller.addPlayer(fake1 , cli1);
        controller.addPlayer(fake2 , cli2);

        controller.setStarterCardSide(fake1.getName(), true);
        controller.setStarterCardSide(fake2.getName(), false);
        for(Player p : testGame.getPlayers()){
            if(p.getName().equalsIgnoreCase(fake1.getName()))
                assertTrue(p.getArea().getSpace(40,40).getCard().isFront());
            else if(p.getName().equalsIgnoreCase(fake2.getName()))
                assertFalse(p.getArea().getSpace(40,40).getCard().isFront());
        }
        controller.disconnection("");
        controller.reconnection("", null, null);

        testGame.end();
        testGame.nextState();
    }

}
