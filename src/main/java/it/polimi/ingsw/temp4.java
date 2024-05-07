package it.polimi.ingsw;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.Print;
import com.google.gson.*;


import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class temp4 {
    public static void main(String[] args) {

        Gson gson = new Gson();
        Gson gson1 = new Gson();
        Print printer = new Print();

        // RESOURCE CARDS
        try (Reader reader = new FileReader("src/main/resources/ResourceCards.json")) {
            // convert JSON file to resource card array
            ResourceCard[] res = gson.fromJson(reader, ResourceCard[].class);
            ArrayList<ResourceCard> ris = new ArrayList<ResourceCard>();
            Corner[] frontCorners;
            Corner[] backCorners;
            // print cards
            for(int j = 0; j < res.length; j++){
                ris.add(res[j]);
                System.out.println("------------ LETTURA CARTA RISORSA " + (j+1));
                System.out.println("points: " + ris.get(j).getPoints() + "\nresource: " +
                        ris.get(j).getResource() + "\nfront: " +
                        ris.get(j).isFront());
                frontCorners = ris.get(j).getFrontCorners();
                for(int i = 0; i < 4; i++){
                    System.out.println("angolo " + i + ": " + frontCorners[i].getType() + " " +
                            frontCorners[i].getResource() + " " +
                            frontCorners[i].getItem() + " " +
                            "visible: " + frontCorners[i].isVisible());
                }
                backCorners = ris.get(j).getBackCorners();
                for(int i = 0; i < 4; i++){
                    System.out.println("angolo " + i + ": " + backCorners[i].getType() + " " +
                            backCorners[i].getResource() + " " +
                            backCorners[i].getItem() + " " +
                            "visible: " + backCorners[i].isVisible());
                }
                printer.largeCardBothSidesPrinter(ris.get(j));
                System.out.println("\n########################################\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // GOLD CARDS
        try (Reader reader = new FileReader("src/main/resources/GoldCards.json")) {
            // convert JSON file to resource card array
            GoldCard[] gold = gson.fromJson(reader, GoldCard[].class);
            ArrayList<GoldCard> gld = new ArrayList<GoldCard>();
            Corner[] frontCorners;
            Corner[] backCorners;
            // print cards
            for(int j = 0; j < gold.length; j++){
                gld.add(gold[j]);
                System.out.println("------------ LETTURA CARTA ORO " + (j+1));
                System.out.println("points: " + gld.get(j).getPoints() + "\npoints type: "  + gld.get(j).getPointsType() + "\nresource: " +
                        gld.get(j).getResource() + "\nfront: " +
                        gld.get(j).isFront());
                System.out.println("required item: " + gld.get(j).getItem());
                System.out.println("required wolfs: " + gld.get(j).countResource(Resource.WOLF));
                System.out.println("required butterflies: " + gld.get(j).countResource(Resource.BUTTERFLY));
                System.out.println("required mushrooms: " + gld.get(j).countResource(Resource.MUSHROOM));
                System.out.println("required leafs: " + gld.get(j).countResource(Resource.LEAF));
                frontCorners = gld.get(j).getFrontCorners();
                for(int i = 0; i < 4; i++){
                    System.out.println("angolo " + i + ": " + frontCorners[i].getType() + " " +
                            frontCorners[i].getResource() + " " +
                            frontCorners[i].getItem() + " " +
                            "visible: " + frontCorners[i].isVisible());
                }
                backCorners = gld.get(j).getBackCorners();
                for(int i = 0; i < 4; i++){
                    System.out.println("angolo " + i + ": " + backCorners[i].getType() + " " +
                            backCorners[i].getResource() + " " +
                            backCorners[i].getItem() + " " +
                            "visible: " + backCorners[i].isVisible());
                }
                printer.largeCardBothSidesPrinter(gld.get(j));
                System.out.println("\n########################################\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // STARTER CARDS
        try (Reader reader = new FileReader("src/main/resources/StarterCards.json")) {
            // convert JSON file to starter card array
            StarterCard[] start = gson.fromJson(reader, StarterCard[].class);
            ArrayList<StarterCard> strt = new ArrayList<StarterCard>();
            Corner[] frontCorners;
            Corner[] backCorners;
            // print cards
            for(int j = 0; j < start.length; j++){
                strt.add(start[j]);
                System.out.println("------------ LETTURA CARTA INIZIALE " + (j+1));
                System.out.println("1st resource: " + strt.get(j).getResource() + "\n2nd resource: " + strt.get(j).getSecondResource() +
                        "\n3rd resource: " + strt.get(j).getThirdResource() + "\nfront: " + strt.get(j).isFront());
                frontCorners = strt.get(j).getFrontCorners();
                for(int i = 0; i < 4; i++){
                    System.out.println("angolo " + i + ": " + frontCorners[i].getType() + " " +
                            frontCorners[i].getResource() + " " +
                            frontCorners[i].getItem() + " " +
                            "visible: " + frontCorners[i].isVisible());
                }
                backCorners = strt.get(j).getBackCorners();
                for(int i = 0; i < 4; i++){
                    System.out.println("angolo " + i + ": " + backCorners[i].getType() + " " +
                            backCorners[i].getResource() + " " +
                            backCorners[i].getItem() + " " +
                            "visible: " + backCorners[i].isVisible());
                }
                printer.largeCardBothSidesPrinter(strt.get(j));
                System.out.println("\n########################################\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //ACHIEVEMENT CARDS
        try (Reader reader = new FileReader("src/main/resources/AchievementCards.json")) {
            // convert JSON file to starter card array
            AchievementCard[] achi = gson1.fromJson(reader, AchievementCard[].class);
            ArrayList<AchievementCard> ach = new ArrayList<AchievementCard>();
            // print cards
            for(int j = 0; j < achi.length; j++){
                ach.add(new AchievementCard(achi[j].getPoints(), achi[j].getResource(), achi[j].getStrategyType(), achi[j].getItem()));
                System.out.println("------------ LETTURA CARTA OBIETTIVO " + (j+1));
                System.out.println("resource: " + ach.get(j).getResource() + "\nitem: " + ((AchievementCard)ach.get(j)).getItem()  + "\npoints: " + ach.get(j).getPoints());
                System.out.print("strategy: ");
                if(((AchievementCard)ach.get(j)).getStrategy().getClass() == ConcreteStrategyDiagonal.class) {
                    System.out.println("diagonal");
                } else if(((AchievementCard)ach.get(j)).getStrategy().getClass() == ConcreteStrategyLshape.class) {
                    System.out.println("L shape");
                } else if(((AchievementCard)ach.get(j)).getStrategy().getClass() == ConcreteStrategyMixed.class) {
                    System.out.println("mixed");
                } else if(((AchievementCard)ach.get(j)).getStrategy().getClass() == ConcreteStrategyItem.class) {
                    System.out.println("item");
                } else if(((AchievementCard)ach.get(j)).getStrategy().getClass() == ConcreteStrategyResource.class) {
                    System.out.println("resource");
                } else {
                    System.out.println("error");
                }
                printer.largeCardBothSidesPrinter(ach.get(j));
                System.out.println("\n########################################\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        System.out.println(AsciiArt);
    }
}
