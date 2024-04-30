package it.polimi.ingsw.util;
import it.polimi.ingsw.model.*;

public class Print {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public Print(){
        System.out.print(ANSI_RESET);
    }

    /**
     * Printing function for text user interface, it prints the selected side of card
     * @param card The card we want to print in the tui
     * @param side the side we want to display, true for front, false for back
     */
    public void cardPrinter(Card card, boolean side) {
        String[] corners = new String[7];
        String def;
        if(card.getClass() == ResourceCard.class) { //printing a resource card
            switch(card.getResource()) {
                case WOLF:
                    corners[4] = "W";
                    System.out.print(ANSI_BLUE);
                    def = ANSI_BLUE;
                    break;
                case LEAF:
                    corners[4] = "L";
                    System.out.print(ANSI_GREEN);
                    def = ANSI_GREEN;
                    break;
                case BUTTERFLY:
                    corners[4] = "B";
                    System.out.print(ANSI_PURPLE);
                    def = ANSI_PURPLE;
                    break;
                case MUSHROOM:
                    corners[4] = "M";
                    System.out.print(ANSI_RED);
                    def = ANSI_RED;
                    break;
                default:
                    corners[4] = "#";
                    def = ANSI_RESET;
                    break;
            }
            if(side) { //front side
                for(int i = 0; i < 4; i++) {
                    switch(card.getFrontCorners()[i].getType()) {
                        case FREE:
                            corners[i] = " ";
                            break;
                        case DEAD:
                            corners[i] = "x";
                            break;
                        case ITEM:
                            switch(card.getFrontCorners()[i].getItem()) {
                                case JAR:
                                    corners[i] = ANSI_WHITE + "J" + def;
                                    break;
                                case SCROLL:
                                    corners[i] = ANSI_WHITE + "S" + def;
                                    break;
                                case PLUME:
                                    corners[i] = ANSI_WHITE + "P" + def;
                                    break;
                                default:
                                    corners[i] = "#";
                                    break;
                            }
                            break;
                        case RESOURCE:
                            switch(card.getFrontCorners()[i].getResource()) {
                                case WOLF:
                                    corners[i] = ANSI_BLUE + "W" + def;
                                    break;
                                case LEAF:
                                    corners[i] = ANSI_GREEN + "L" + def;
                                    break;
                                case BUTTERFLY:
                                    corners[i] = ANSI_PURPLE + "B" + def;
                                    break;
                                case MUSHROOM:
                                    corners[i] = ANSI_RED + "M" + def;
                                    break;
                                default:
                                    corners[i] = "#";
                                    break;
                            }
                            break;
                        default:
                            corners[i] = "#";
                            break;
                    }
                }
                String pts = " ";
                if(card.getPoints() != 0)
                    pts = ANSI_YELLOW + card.getPoints() + def;
                System.out.println(",___________________,");
                if(corners[3].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("| " + corners[3] + " |");
                }
                if(corners[0].equals("x")) {
                    System.out.println("     " + pts + "         |");
                } else {
                    System.out.println("     " + pts + "     | " + corners[0] + " |");
                }
                if(corners[3].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("|---'");
                }
                if(corners[0].equals("x")) {
                    System.out.println("               |");
                } else {
                    System.out.println("           '---|");
                }
                if(corners[2].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("|___,");
                }
                if(corners[1].equals("x")) {
                    System.out.println("               |");
                } else {
                    System.out.println("           ,___|");
                }
                if(corners[2].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("| " + corners[2] + " |");
                }
                if(corners[1].equals("x")) {
                    System.out.println("               |");
                } else {
                    System.out.println("           | " + corners[1] + " |");
                }
                if(corners[2].equals("x")) {
                    System.out.print("!____");
                } else {
                    System.out.print("!___|");
                }
                if(corners[1].equals("x")) {
                    System.out.println("_______________!");
                } else {
                    System.out.println("___________|___!");
                }
            } else { //back side
                System.out.println(",___________________,");
                System.out.println("|   |           |   |");
                System.out.println("|---'           '---|");
                System.out.println("|___,     " + corners[4] + "     ,___|");
                System.out.println("|   |           |   |");
                System.out.println("!___|___________|___!");
            }
        } else if(card.getClass() == GoldCard.class) { //printing a gold card
            switch(card.getResource()) {
                case WOLF:
                    corners[4] = "W";
                    System.out.print(ANSI_BLUE);
                    def = ANSI_BLUE;
                    break;
                case LEAF:
                    corners[4] = "L";
                    System.out.print(ANSI_GREEN);
                    def = ANSI_GREEN;
                    break;
                case BUTTERFLY:
                    corners[4] = "B";
                    System.out.print(ANSI_PURPLE);
                    def = ANSI_PURPLE;
                    break;
                case MUSHROOM:
                    corners[4] = "M";
                    System.out.print(ANSI_RED);
                    def = ANSI_RED;
                    break;
                default:
                    corners[4] = "#";
                    def = ANSI_RESET;
                    break;
            }
            if(side) { //front side
                for(int i = 0; i < 4; i++) {
                    switch(card.getFrontCorners()[i].getType()) {
                        case FREE:
                            corners[i] = " ";
                            break;
                        case DEAD:
                            corners[i] = "x";
                            break;
                        case ITEM:
                            switch(card.getFrontCorners()[i].getItem()) {
                                case JAR:
                                    corners[i] = ANSI_WHITE + "J" + def;
                                    break;
                                case SCROLL:
                                    corners[i] = ANSI_WHITE + "S" + def;
                                    break;
                                case PLUME:
                                    corners[i] = ANSI_WHITE + "P" + def;
                                    break;
                                default:
                                    corners[i] = "#";
                                    break;
                            }
                            break;
                        case RESOURCE:
                            switch(card.getFrontCorners()[i].getResource()) {
                                case WOLF:
                                    corners[i] = ANSI_BLUE + "W" + def;
                                    break;
                                case LEAF:
                                    corners[i] = ANSI_GREEN + "L" + def;
                                    break;
                                case BUTTERFLY:
                                    corners[i] = ANSI_PURPLE + "B" + def;
                                    break;
                                case MUSHROOM:
                                    corners[i] = ANSI_RED + "M" + def;
                                    break;
                                default:
                                    corners[i] = "#";
                                    break;
                            }
                            break;
                        default:
                            corners[i] = "#";
                            break;
                    }
                }
                String reqRes = "";
                int cntRes = 0;
                for(Resource r : Resource.values()) {
                    String singleChar;
                    switch(r) {
                        case WOLF: singleChar = ANSI_BLUE + "W"; break;
                        case BUTTERFLY: singleChar = ANSI_PURPLE + "B"; break;
                        case MUSHROOM: singleChar = ANSI_RED + "M"; break;
                        case LEAF: singleChar = ANSI_GREEN + "L"; break;
                        default: singleChar = "#"; break;
                    }
                    if(((GoldCard)card).countResource(r) == 1) {
                        reqRes = reqRes + singleChar;
                        cntRes++;
                    } else if(((GoldCard)card).countResource(r) > 1) {
                        cntRes += ((GoldCard)card).countResource(r);
                        for(int i = 0; i < ((GoldCard)card).countResource(r); i++)
                            reqRes = singleChar + reqRes;
                    }
                }
                if(cntRes == 4) reqRes = reqRes + " " + def;
                else if(cntRes == 3) reqRes = " " + reqRes + " " + def;
                String pts = ANSI_YELLOW + card.getPoints();
                switch(((GoldCard)card).getPointsType()) {
                    case ITEM:
                        switch(((GoldCard)card).getItem()) {
                            case JAR: pts = pts + " J"; break;
                            case SCROLL: pts = pts + " S"; break;
                            case PLUME: pts = pts + " P"; break;
                            default: pts = pts + " #"; break;
                        }
                        break;
                    case CORNER:
                        pts = pts + " C";
                        break;
                    case SIMPLE:
                        pts = " " + pts + " ";
                        break;
                    default: pts = pts + "#R";
                        break;
                }
                pts = pts + def;
                System.out.println(",___________________,");
                if(corners[3].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("| " + corners[3] + " |");
                }
                if(corners[0].equals("x")) {
                    System.out.println("    " + pts + "        |");
                } else {
                    System.out.println("    " + pts + "    | " + corners[0] + " |");
                }
                if(corners[3].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("|---'");
                }
                if(corners[0].equals("x")) {
                    System.out.println("               |");
                } else {
                    System.out.println("           '---|");
                }
                if(corners[2].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("|___,");
                }
                if(corners[1].equals("x")) {
                    System.out.println("               |");
                } else {
                    System.out.println("           ,___|");
                }
                if(corners[2].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("| " + corners[2] + " |");
                }
                if(corners[1].equals("x")) {
                    System.out.println("   " + reqRes + "       |");
                } else {
                    System.out.println("   " + reqRes + "   | " + corners[1] + " |");
                }
                if(corners[2].equals("x")) {
                    System.out.print("!____");
                } else {
                    System.out.print("!___|");
                }
                if(corners[1].equals("x")) {
                    System.out.println("_______________!");
                } else {
                    System.out.println("___________|___!");
                }
            } else { //back side
                System.out.println(",___________________,");
                System.out.println("|   |           |   |");
                System.out.println("|---'           '---|");
                System.out.println("|___,     " + corners[4] + "     ,___|");
                System.out.println("|   |           |   |");
                System.out.println("!___|___________|___!");
            }
        } else if(card.getClass() == AchievementCard.class) { //printing an achievement card
            def = ANSI_YELLOW;
            System.out.print(def);
            if(side) {
                if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyDiagonal.class) {
                    switch(card.getResource()) {
                        case WOLF:
                            def = ANSI_BLUE;
                            System.out.print(def);
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|      ##     " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    ##         " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|  ##               |");
                            System.out.println("!___________________!");
                            break;
                        case LEAF:
                            def = ANSI_GREEN;
                            System.out.print(def);
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|  ##         " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    ##         " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|      ##           |");
                            System.out.println("!___________________!");
                            break;
                        case MUSHROOM:
                            def = ANSI_RED;
                            System.out.print(def);
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|      ##     " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    ##         " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|  ##               |");
                            System.out.println("!___________________!");
                            break;
                        case BUTTERFLY:
                            def = ANSI_PURPLE;
                            System.out.print(def);
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|  ##         " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    ##         " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|      ##           |");
                            System.out.println("!___________________!");
                            break;
                        default:
                            System.out.println(",___________________,");
                            System.out.println("| # |           | # |");
                            System.out.println("|---'    ACH    '---|");
                            System.out.println("|___,  DIAGONAL ,___|");
                            System.out.println("| # |   ERROR   | # |");
                            System.out.println("!___|___________|___!");
                            break;
                    }
                } else if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyItem.class) {
                    def = ANSI_WHITE;
                    System.out.print(def);
                    switch(((AchievementCard) card).getItem()) {
                        case JAR:
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|     J       " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    J J        " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|                   |");
                            System.out.println("!___________________!");
                            break;
                        case SCROLL:
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|     S       " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    S S        " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|                   |");
                            System.out.println("!___________________!");
                            break;
                        case PLUME:
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|     P       " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    P P        " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|                   |");
                            System.out.println("!___________________!");
                            break;
                        default:
                            System.out.println(",___________________,");
                            System.out.println("| # |           | # |");
                            System.out.println("|---'   ACH     '---|");
                            System.out.println("|___,   ITEM    ,___|");
                            System.out.println("| # |   ERROR   | # |");
                            System.out.println("!___|___________|___!");
                            break;
                    }
                } else if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyLshape.class) {
                    switch(card.getResource()) {
                        case WOLF:
                            def = ANSI_BLUE;
                            System.out.print(def);
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|      " + ANSI_RED + "##" + def + "     " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    ##         " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|    ##             |");
                            System.out.println("!___________________!");
                            break;
                        case LEAF:
                            def = ANSI_GREEN;
                            System.out.print(def);
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|    ##       " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    ##         " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|  " + ANSI_PURPLE + "##" + def + "               |");
                            System.out.println("!___________________!");
                            break;
                        case MUSHROOM:
                            def = ANSI_RED;
                            System.out.print(def);
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|    ##       " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    ##         " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|      " + ANSI_GREEN + "##" + def + "           |");
                            System.out.println("!___________________!");
                            break;
                        case BUTTERFLY:
                            def = ANSI_PURPLE;
                            System.out.print(def);
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|  " + ANSI_BLUE + "##" + def +  "         " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    ##         " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|    ##             |");
                            System.out.println("!___________________!");
                            break;
                        default:
                            System.out.println(",___________________,");
                            System.out.println("| # |           | # |");
                            System.out.println("|---'    ACH    '---|");
                            System.out.println("|___,  L SHAPE  ,___|");
                            System.out.println("| # |   ERROR   | # |");
                            System.out.println("!___|___________|___!");
                            break;
                    }
                } else if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyMixed.class) {
                    def = ANSI_WHITE;
                    System.out.println(def + ",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                    System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                    System.out.println("|             " + ANSI_YELLOW + "'\\./'" + def + " |");
                    System.out.println("|    P J S      " + ANSI_YELLOW + "'" + def + "   |");
                    System.out.println("|                   |");
                    System.out.println("!___________________!");
                } else if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyResource.class) {
                    switch(card.getResource()) {
                        case WOLF:
                            def = ANSI_BLUE;
                            System.out.print(def);
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|     W       " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    W W        " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|                   |");
                            System.out.println("!___________________!");
                            break;
                        case LEAF:
                            def = ANSI_GREEN;
                            System.out.print(def);
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|     L       " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    L L        " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|                   |");
                            System.out.println("!___________________!");
                            break;
                        case MUSHROOM:
                            def = ANSI_RED;
                            System.out.print(def);
                            System.out.print(def);
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|     M       " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    M M        " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|                   |");
                            System.out.println("!___________________!");
                            break;
                        case BUTTERFLY:
                            def = ANSI_PURPLE;
                            System.out.print(def);
                            System.out.println(",_____________" + ANSI_YELLOW + "_____" + def + "_,");
                            System.out.println("|             " + ANSI_YELLOW + "| " + card.getPoints() + " |" + def + " |");
                            System.out.println("|     B       " + ANSI_YELLOW + "'\\./'" + def + " |");
                            System.out.println("|    B B        " + ANSI_YELLOW + "'" + def + "   |");
                            System.out.println("|                   |");
                            System.out.println("!___________________!");
                            break;
                        default:
                            System.out.println(",___________________,");
                            System.out.println("| # |           | # |");
                            System.out.println("|---'    ACH    '---|");
                            System.out.println("|___,  RESOURCE ,___|");
                            System.out.println("| # |   ERROR   | # |");
                            System.out.println("!___|___________|___!");
                            break;
                    }
                } else {
                    System.out.println(",___________________,");
                    System.out.println("| # |           | # |");
                    System.out.println("|---'    ACH    '---|");
                    System.out.println("|___,   ERROR   ,___|");
                    System.out.println("| # |           | # |");
                    System.out.println("!___|___________|___!");
                }
            } else {
                def = ANSI_WHITE;
                System.out.print(def);
                System.out.println(",___________________,");
                System.out.println("|" + ANSI_YELLOW + "        @@@        " + def + "|");
                System.out.println("|" + ANSI_YELLOW + " *    ,@@@@@,    * " + def + "|");
                System.out.println("|" + ANSI_YELLOW + "       °@#@°       " + def + "|");
                System.out.println("|" + ANSI_YELLOW + " <>   , °#° ,   <> " + def + "|");
                System.out.println("!___________________!");
            }
        } else if(card.getClass() == StarterCard.class) { //printing a starter card
            def = ANSI_YELLOW;
            System.out.print(def);
            switch(card.getResource()) {
                case WOLF:
                    corners[4] = ANSI_BLUE + "W" + def;
                    break;
                case LEAF:
                    corners[4] = ANSI_GREEN + "L" + def;
                    break;
                case BUTTERFLY:
                    corners[4] = ANSI_PURPLE + "B" + def;
                    break;
                case MUSHROOM:
                    corners[4] = ANSI_RED + "M" + def;
                    break;
                default:
                    corners[4] = "#";
                    def = ANSI_RESET;
                    break;
            }
            corners[5] = "#";
            if(((StarterCard) card).getSecondResource() != null) switch(((StarterCard) card).getSecondResource()) {
                case WOLF:
                    corners[5] = ANSI_BLUE + "W" + def;
                    break;
                case LEAF:
                    corners[5] = ANSI_GREEN + "L" + def;
                    break;
                case BUTTERFLY:
                    corners[5] = ANSI_PURPLE + "B" + def;
                    break;
                case MUSHROOM:
                    corners[5] = ANSI_RED + "M" + def;
                    break;
                default:
                    corners[5] = "#";
                    break;
            }
            corners[6] = "#";
            if(((StarterCard) card).getThirdResource() != null) switch(((StarterCard) card).getThirdResource()) {
                case WOLF:
                    corners[6] = ANSI_BLUE + "W" + def;
                    break;
                case LEAF:
                    corners[6] = ANSI_GREEN + "L" + def;
                    break;
                case BUTTERFLY:
                    corners[6] = ANSI_PURPLE + "B" + def;
                    break;
                case MUSHROOM:
                    corners[6] = ANSI_RED + "M" + def;
                    break;
                default:
                    corners[6] = "#";
                    break;
            }
            if(side) { //front side
                for(int i = 0; i < 4; i++) {
                    switch(card.getFrontCorners()[i].getType()) {
                        case FREE:
                            corners[i] = " ";
                            break;
                        case DEAD:
                            corners[i] = "x";
                            break;
                        case RESOURCE:
                            switch(card.getFrontCorners()[i].getResource()) {
                                case WOLF:
                                    corners[i] = ANSI_BLUE + "W" + def;
                                    break;
                                case LEAF:
                                    corners[i] = ANSI_GREEN + "L" + def;
                                    break;
                                case BUTTERFLY:
                                    corners[i] = ANSI_PURPLE + "B" + def;
                                    break;
                                case MUSHROOM:
                                    corners[i] = ANSI_RED + "M" + def;
                                    break;
                                default:
                                    corners[i] = "#";
                                    break;
                            }
                            break;
                        default:
                            corners[i] = "#";
                            break;
                    }
                }
                System.out.println(",___________________,");
                if(corners[3].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("| " + corners[3] + " |");
                }
                if(corners[0].equals("x")) {
                    System.out.println("               |");
                } else {
                    System.out.println("           | " + corners[0] + " |");
                }
                if(corners[3].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("|---'");
                }
                if(corners[5].equals("#")) {
                    System.out.print("           ");
                } else {
                    System.out.print("     " + corners[4] + "     ");
                }
                if(corners[0].equals("x")) {
                    System.out.println("    |");
                } else {
                    System.out.println("'---|");
                }
                if(corners[2].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("|___,");
                }
                if(corners[5].equals("#")) {
                    System.out.print("     " + corners[4] + "     ");
                } else {
                    System.out.print("     " + corners[5] + "     ");
                }
                if(corners[1].equals("x")) {
                    System.out.println("    |");
                } else {
                    System.out.println(",___|");
                }
                if(corners[2].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("| " + corners[2] + " |");
                }
                if(corners[6].equals("#")) {
                    System.out.print("           ");
                } else {
                    System.out.print("     " + corners[6] + "     ");
                }
                if(corners[1].equals("x")) {
                    System.out.println("    |");
                } else {
                    System.out.println("| " + corners[1] + " |");
                }
                if(corners[2].equals("x")) {
                    System.out.print("!____");
                } else {
                    System.out.print("!___|");
                }
                if(corners[1].equals("x")) {
                    System.out.println("_______________!");
                } else {
                    System.out.println("___________|___!");
                }
            } else { //back side
                for(int i = 0; i < 4; i++) {
                    switch(card.getBackCorners()[i].getType()) {
                        case FREE:
                            corners[i] = " ";
                            break;
                        case DEAD:
                            corners[i] = "x";
                            break;
                        case RESOURCE:
                            switch(card.getBackCorners()[i].getResource()) {
                                case WOLF:
                                    corners[i] = ANSI_BLUE + "W" + def;
                                    break;
                                case LEAF:
                                    corners[i] = ANSI_GREEN + "L" + def;
                                    break;
                                case BUTTERFLY:
                                    corners[i] = ANSI_PURPLE + "B" + def;
                                    break;
                                case MUSHROOM:
                                    corners[i] = ANSI_RED + "M" + def;
                                    break;
                                default:
                                    corners[i] = "#";
                                    break;
                            }
                            break;
                        default:
                            corners[i] = "#";
                            break;
                    }
                }
                System.out.println(",___________________,");
                if(corners[3].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("| " + corners[3] + " |");
                }
                if(corners[0].equals("x")) {
                    System.out.println("               |");
                } else {
                    System.out.println("           | " + corners[0] + " |");
                }
                if(corners[3].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("|---'");
                }
                if(corners[0].equals("x")) {
                    System.out.println("     O         |");
                } else {
                    System.out.println("     O     '---|");
                }
                if(corners[2].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("|___,");
                }
                if(corners[1].equals("x")) {
                    System.out.println("    O O        |");
                } else {
                    System.out.println("    O O    ,___|");
                }
                if(corners[2].equals("x")) {
                    System.out.print("|    ");
                } else {
                    System.out.print("| " + corners[2] + " |");
                }
                if(corners[1].equals("x")) {
                    System.out.println("     O         |");
                } else {
                    System.out.println("     O     | " + corners[1] + " |");
                }
                if(corners[2].equals("x")) {
                    System.out.print("!____");
                } else {
                    System.out.print("!___|");
                }
                if(corners[1].equals("x")) {
                    System.out.println("_______________!");
                } else {
                    System.out.println("___________|___!");
                }
            }
        } else { //error card
            System.out.println(",___________________,");
            System.out.println("| # |           | # |");
            System.out.println("|---'           '---|");
            System.out.println("|___,   ERROR   ,___|");
            System.out.println("| # |           | # |");
            System.out.println("!___|___________|___!");
        }
        System.out.print(ANSI_RESET);
    }

    /**
     * Printing function for text user interface, it prints the current side of the card
     * it's an overloading of the previous method
     * @param card The card we want to print in the tui
     */
    public void cardPrinter(Card card) {
        cardPrinter(card, card.isFront());
    }
}
