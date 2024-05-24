package it.polimi.ingsw.util;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.Arrays;

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
    public static final String ANSI_BOLD = "\033[1m";
    public static final String ANSI_BOLD_RESET = "\033[0m";
    public static final String ANSI_ITALIC = "\u001B[3m";

    /**
     * Custom printer for Text User Interface, when initialized resets the color on the terminal
     */
    public Print(){
        System.out.print(ANSI_RESET);
    }

    /**
     * Printing function for text user interface, it prints the whole playground
     * eventually with row's and column's indexes
     * @param area The playground object with bounds and the matrix of the space
     * @param coords Boolean to set true if we want to print also the coords
     * @param spaces Boolean to set true if we want 2 empty rows and 2 empty columns
     * at the borders of the grid
     */
    public static void playgroundPrinter(Playground area, boolean coords, boolean spaces) {
        int east, west, north, south;
        if(spaces) {
            east = area.getEastBound() == 0 ? 80 : area.getEastBound() + 1;
            west = area.getWestBound() == 0 ? 80 : area.getWestBound() - 1;
            north = area.getNorthBound() == 0 ? 0 : area.getNorthBound() - 1;
            south = area.getSouthBound() == 0 ? 80 : area.getSouthBound() + 1;
        } else {
            west = area.getWestBound();
            east = area.getEastBound();
            south = area.getSouthBound();
            north = area.getNorthBound();
        }
        String[] resultArr = playgroundToString(area);
        String result = "";
        if(coords && !spaces) {
            result = "      " + ANSI_BOLD;
            for(int i = west; i <= east; i++){
                if(i < 10)
                    result = result.concat("    " + i);
                else
                    result = result.concat("   " + i);
            }
            result = result.concat(ANSI_BOLD_RESET + "\n");
            for (int i = 0, row = north, wait = 0; i < resultArr.length; i++, wait++) {
                if(wait == 2)
                    wait = 0;
                if(wait == 1) {
                    if (row < 10)
                        result = result.concat(ANSI_BOLD + "   " + row + "  " + ANSI_BOLD_RESET);
                    else
                        result = result.concat(ANSI_BOLD + "  " + row + "  " + ANSI_BOLD_RESET);
                    row++;
                } else {
                    result = result.concat("      ");
                }
                result = result.concat(resultArr[i] + "\n");
            }
        } else if(coords && spaces) {
            result = "      " + ANSI_BOLD;
            for(int i = west; i <= east; i++){
                if(i < 10)
                    result = result.concat("    " + i);
                else
                    result = result.concat("   " + i);
            }
            if(north >= 0) {
                result = result.concat(ANSI_BOLD_RESET + "\n");
                if(north >= 10)
                    result = result.concat("\n" + ANSI_BOLD + "  " + north + "  "+ ANSI_BOLD_RESET + "\n");
                else
                    result = result.concat("\n" + ANSI_BOLD + "   " + north + "  "+ ANSI_BOLD_RESET + "\n");
            } else {
                north--;
            }
            for (int i = 0, row = north+1, wait = 0; i < resultArr.length; i++, wait++) {
                if(wait == 2)
                    wait = 0;
                if(wait == 1) {
                    if (row < 10)
                        result = result.concat(ANSI_BOLD + "   " + row + "  " + ANSI_BOLD_RESET);
                    else
                        result = result.concat(ANSI_BOLD + "  " + row + "  " + ANSI_BOLD_RESET);
                    row++;
                } else {
                    result = result.concat("      ");
                }
                result = result.concat("     " + resultArr[i] + "\n");
            }
            if(south < 80)
                if(south >= 10)
                    result = result.concat(ANSI_BOLD + "  " + south + "  "+ ANSI_BOLD_RESET + "\n\n");
                else
                    result = result.concat(ANSI_BOLD + "   " + south + "  "+ ANSI_BOLD_RESET + "\n\n");

        } else if(!coords && spaces) {
            result = result.concat("\n\n");
            for (int i = 0; i < resultArr.length; i++) {
                result = result.concat("     " + resultArr[i] + "\n");
            }
            result = result.concat("\n\n");
        } else { //no cords no spaces
            for (int i = 0; i < resultArr.length; i++) {
                result = result.concat(resultArr[i] + "\n");
            }
        }
        System.out.print(result);
    }

    /**
     * Printing function for text user interface, it prints the whole playground with row's
     * and column's indexes and border space, it's an overloading of the previous method
     * @param area The playground object with bounds and the matrix of the space
     */
    public static void playgroundPrinter(Playground area) {
        playgroundPrinter(area, true, true);
    }

    /**
     * Printing function for text user interface, it prints the selected side of card
     * using non-regular ascii characters
     * @param card The card we want to print in the tui
     * @param side The side we want to display, true for front, false for back
     */
    public static void cardPrinter(Card card, boolean side) {
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
                if(corners[3].equals("x")) {
                    System.out.print("╭────");
                } else {
                    System.out.print("╭───┬");
                }
                if(corners[0].equals("x")) {
                    System.out.println("───────────────╮");
                } else {
                    System.out.println("───────────┬───╮");
                }
                if(corners[3].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("│ " + corners[3] + " │");
                }
                if(corners[0].equals("x")) {
                    System.out.println("     " + pts + "         │");
                } else {
                    System.out.println("     " + pts + "     │ " + corners[0] + " │");
                }
                if(corners[3].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("├───╯");
                }
                if(corners[0].equals("x")) {
                    System.out.println("               │");
                } else {
                    System.out.println("           ╰───┤");
                }
                if(corners[2].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("├───╮");
                }
                if(corners[1].equals("x")) {
                    System.out.println("               │");
                } else {
                    System.out.println("           ╭───┤");
                }
                if(corners[2].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("│ " + corners[2] + " │");
                }
                if(corners[1].equals("x")) {
                    System.out.println("               │");
                } else {
                    System.out.println("           │ " + corners[1] + " │");
                }
                if(corners[2].equals("x")) {
                    System.out.print("╰────");
                } else {
                    System.out.print("╰───┴");
                }
                if(corners[1].equals("x")) {
                    System.out.println("───────────────╯");
                } else {
                    System.out.println("───────────┴───╯");
                }
            } else { //back side
                System.out.println("╭───┬───────────┬───╮");
                System.out.println("│   │           │   │");
                System.out.println("├───╯           ╰───┤");
                System.out.println("├───╮     " + corners[4] + "     ╭───┤");
                System.out.println("│   │           │   │");
                System.out.println("╰───┴───────────┴───╯");
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
                if(corners[3].equals("x")) {
                    System.out.print("╭────");
                } else {
                    System.out.print("╭───┬");
                }
                if(corners[0].equals("x")) {
                    System.out.println("───────────────╮");
                } else {
                    System.out.println("───────────┬───╮");
                }
                if(corners[3].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("│ " + corners[3] + " │");
                }
                if(corners[0].equals("x")) {
                    System.out.println("    " + pts + "        │");
                } else {
                    System.out.println("    " + pts + "    │ " + corners[0] + " │");
                }
                if(corners[3].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("├───╯");
                }
                if(corners[0].equals("x")) {
                    System.out.println("               │");
                } else {
                    System.out.println("           ╰───┤");
                }
                if(corners[2].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("├───╮");
                }
                if(corners[1].equals("x")) {
                    System.out.println("               │");
                } else {
                    System.out.println("           ╭───┤");
                }
                if(corners[2].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("│ " + corners[2] + " │");
                }
                if(corners[1].equals("x")) {
                    System.out.println("   " + reqRes + "       │");
                } else {
                    System.out.println("   " + reqRes + "   │ " + corners[1] + " │");
                }
                if(corners[2].equals("x")) {
                    System.out.print("╰────");
                } else {
                    System.out.print("╰───┴");
                }
                if(corners[1].equals("x")) {
                    System.out.println("───────────────╯");
                } else {
                    System.out.println("───────────┴───╯");
                }
            } else { //back side
                System.out.println("╭───┬───────────┬───╮");
                System.out.println("│   │           │   │");
                System.out.println("├───╯           ╰───┤");
                System.out.println("├───╮     " + corners[4] + "     ╭───┤");
                System.out.println("│   │           │   │");
                System.out.println("╰───┴───────────┴───╯");
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
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│      ██     " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│  ██               │");
                            System.out.println("╰───────────────────╯");
                            break;
                        case LEAF:
                            def = ANSI_GREEN;
                            System.out.print(def);
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│  ██         " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│      ██           │");
                            System.out.println("╰───────────────────╯");
                            break;
                        case MUSHROOM:
                            def = ANSI_RED;
                            System.out.print(def);
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│      ██     " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│  ██               │");
                            System.out.println("╰───────────────────╯");
                            break;
                        case BUTTERFLY:
                            def = ANSI_PURPLE;
                            System.out.print(def);
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│  ██         " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│      ██           │");
                            System.out.println("╰───────────────────╯");
                            break;
                        default:
                            System.out.println("╭───┬───────────┬───╮");
                            System.out.println("│ # │           │ # │");
                            System.out.println("├───╯    ACH    ╰───┤");
                            System.out.println("├───╮  DIAGONAL ╭───┤");
                            System.out.println("│ # │   ERROR   │ # │");
                            System.out.println("╰───┴───────────┴───╯");
                            break;
                    }
                } else if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyItem.class) {
                    def = ANSI_WHITE;
                    System.out.print(def);
                    switch(((AchievementCard) card).getItem()) {
                        case JAR:
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│     J       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    J J        " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│                   │");
                            System.out.println("╰───────────────────╯");
                            break;
                        case SCROLL:
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│     S       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    S S        " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│                   │");
                            System.out.println("╰───────────────────╯");
                            break;
                        case PLUME:
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│     P       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    P P        " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│                   │");
                            System.out.println("╰───────────────────╯");
                            break;
                        default:
                            System.out.println("╭───┬───────────┬───╮");
                            System.out.println("│ # │           │ # │");
                            System.out.println("├───╯   ACH     ╰───┤");
                            System.out.println("├───╮   ITEM    ╭───┤");
                            System.out.println("│ # │   ERROR   │ # │");
                            System.out.println("╰───┴───────────┴───╯");
                            break;
                    }
                } else if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyLshape.class) {
                    switch(card.getResource()) {
                        case WOLF:
                            def = ANSI_BLUE;
                            System.out.print(def);
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│      " + ANSI_RED + "██" + def + "     " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│    ██             │");
                            System.out.println("╰───────────────────╯");
                            break;
                        case LEAF:
                            def = ANSI_GREEN;
                            System.out.print(def);
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│    ██       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│  " + ANSI_PURPLE + "██" + def + "               │");
                            System.out.println("╰───────────────────╯");
                            break;
                        case MUSHROOM:
                            def = ANSI_RED;
                            System.out.print(def);
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│    ██       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│      " + ANSI_GREEN + "██" + def + "           │");
                            System.out.println("╰───────────────────╯");
                            break;
                        case BUTTERFLY:
                            def = ANSI_PURPLE;
                            System.out.print(def);
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│  " + ANSI_BLUE + "██" + def +  "         " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│    ██             │");
                            System.out.println("╰───────────────────╯");
                            break;
                        default:
                            System.out.println("╭───┬───────────┬───╮");
                            System.out.println("│ # │           │ # │");
                            System.out.println("├───╯    ACH    ╰───┤");
                            System.out.println("├───╮  L SHAPE  ╭───┤");
                            System.out.println("│ # │   ERROR   │ # │");
                            System.out.println("╰───┴───────────┴───╯");
                            break;
                    }
                } else if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyMixed.class) {
                    def = ANSI_WHITE;
                    System.out.println(def + "╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                    System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                    System.out.println("│             " + ANSI_YELLOW + "'\\./'" + def + " │");
                    System.out.println("│    P J S      " + ANSI_YELLOW + "'" + def + "   │");
                    System.out.println("│                   │");
                    System.out.println("╰───────────────────╯");
                } else if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyResource.class) {
                    switch(card.getResource()) {
                        case WOLF:
                            def = ANSI_BLUE;
                            System.out.print(def);
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│     W       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    W W        " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│                   │");
                            System.out.println("╰───────────────────╯");
                            break;
                        case LEAF:
                            def = ANSI_GREEN;
                            System.out.print(def);
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│     L       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    L L        " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│                   │");
                            System.out.println("╰───────────────────╯");
                            break;
                        case MUSHROOM:
                            def = ANSI_RED;
                            System.out.print(def);
                            System.out.print(def);
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│     M       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    M M        " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│                   │");
                            System.out.println("╰───────────────────╯");
                            break;
                        case BUTTERFLY:
                            def = ANSI_PURPLE;
                            System.out.print(def);
                            System.out.println("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            System.out.println("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            System.out.println("│     B       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            System.out.println("│    B B        " + ANSI_YELLOW + "'" + def + "   │");
                            System.out.println("│                   │");
                            System.out.println("╰───────────────────╯");
                            break;
                        default:
                            System.out.println("╭───┬───────────┬───╮");
                            System.out.println("│ # │           │ # │");
                            System.out.println("├───╯    ACH    ╰───┤");
                            System.out.println("├───╮  RESOURCE ╭───┤");
                            System.out.println("│ # │   ERROR   │ # │");
                            System.out.println("╰───┴───────────┴───╯");
                            break;
                    }
                } else {
                    System.out.println("╭───┬───────────┬───╮");
                    System.out.println("│ # │           │ # │");
                    System.out.println("├───╯    ACH    ╰───┤");
                    System.out.println("├───╮   ERROR   ╭───┤");
                    System.out.println("│ # │           │ # │");
                    System.out.println("╰───┴───────────┴───╯");
                }
            } else {
                def = ANSI_WHITE;
                System.out.print(def);
                System.out.println("╭───────────────────╮");
                System.out.println("│" + ANSI_YELLOW + "        @@@        " + def + "│");
                System.out.println("│" + ANSI_YELLOW + " *    ,@@@@@,    * " + def + "│");
                System.out.println("│" + ANSI_YELLOW + "       °@#@°       " + def + "│");
                System.out.println("│" + ANSI_YELLOW + " <>   , °#° ,   <> " + def + "│");
                System.out.println("╰───────────────────╯");
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
                if(corners[3].equals("x")) {
                    System.out.print("╭────");
                } else {
                    System.out.print("╭───┬");
                }
                if(corners[0].equals("x")) {
                    System.out.println("───────────────╮");
                } else {
                    System.out.println("───────────┬───╮");
                }
                if(corners[3].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("│ " + corners[3] + " │");
                }
                if(corners[0].equals("x")) {
                    System.out.println("               │");
                } else {
                    System.out.println("           │ " + corners[0] + " │");
                }
                if(corners[3].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("├───╯");
                }
                if(corners[5].equals("#")) {
                    System.out.print("           ");
                } else {
                    System.out.print("     " + corners[4] + "     ");
                }
                if(corners[0].equals("x")) {
                    System.out.println("    │");
                } else {
                    System.out.println("╰───┤");
                }
                if(corners[2].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("├───╮");
                }
                if(corners[5].equals("#")) {
                    System.out.print("     " + corners[4] + "     ");
                } else {
                    System.out.print("     " + corners[5] + "     ");
                }
                if(corners[1].equals("x")) {
                    System.out.println("    │");
                } else {
                    System.out.println("╭───┤");
                }
                if(corners[2].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("│ " + corners[2] + " │");
                }
                if(corners[6].equals("#")) {
                    System.out.print("           ");
                } else {
                    System.out.print("     " + corners[6] + "     ");
                }
                if(corners[1].equals("x")) {
                    System.out.println("    │");
                } else {
                    System.out.println("│ " + corners[1] + " │");
                }
                if(corners[2].equals("x")) {
                    System.out.print("╰────");
                } else {
                    System.out.print("╰───┴");
                }
                if(corners[1].equals("x")) {
                    System.out.println("───────────────╯");
                } else {
                    System.out.println("───────────┴───╯");
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
                if(corners[3].equals("x")) {
                    System.out.print("╭────");
                } else {
                    System.out.print("╭───┬");
                }
                if(corners[0].equals("x")) {
                    System.out.println("───────────────╮");
                } else {
                    System.out.println("───────────┬───╮");
                }
                if(corners[3].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("│ " + corners[3] + " │");
                }
                if(corners[0].equals("x")) {
                    System.out.println("               │");
                } else {
                    System.out.println("           │ " + corners[0] + " │");
                }
                if(corners[3].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("├───╯");
                }
                if(corners[0].equals("x")) {
                    System.out.println("     O         │");
                } else {
                    System.out.println("     O     ╰───┤");
                }
                if(corners[2].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("├───╮");
                }
                if(corners[1].equals("x")) {
                    System.out.println("    O O        │");
                } else {
                    System.out.println("    O O    ╭───┤");
                }
                if(corners[2].equals("x")) {
                    System.out.print("│    ");
                } else {
                    System.out.print("│ " + corners[2] + " │");
                }
                if(corners[1].equals("x")) {
                    System.out.println("     O         │");
                } else {
                    System.out.println("     O     │ " + corners[1] + " │");
                }
                if(corners[2].equals("x")) {
                    System.out.print("╰────");
                } else {
                    System.out.print("╰───┴");
                }
                if(corners[1].equals("x")) {
                    System.out.println("───────────────╯");
                } else {
                    System.out.println("───────────┴───╯");
                }
            }
        } else { //error card
            System.out.println("╭───┬───────────┬───╮");
            System.out.println("│ # │           │ # │");
            System.out.println("├───╯           ╰───┤");
            System.out.println("├───╮   ERROR   ╭───┤");
            System.out.println("│ # │           │ # │");
            System.out.println("╰───┴───────────┴───╯");
        }
        System.out.print(ANSI_RESET);
    }

    /**
     * Printing function for text user interface, it prints the current side of the card
     * with non-regular ascii characters, it's an overloading of the previous method
     * @param card The card we want to print in the tui
     */
    public static void cardPrinter(Card card) {
        cardPrinter(card, card.isFront());
    }

    /**
     * Printing function for text user interface, it prints the selected side of card
     * using regular ascii characters
     * @param card The card we want to print in the tui
     * @param side The side we want to display, true for front, false for back
     */
    public static void basicCardPrinter(Card card, boolean side) {
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
     * with regular ascii characters, it's an overloading of the previous method
     * @param card The card we want to print in the tui
     */
    public static void basicCardPrinter(Card card) {
        cardPrinter(card, card.isFront());
    }


    /**
     * Given a Card, this function return an array of 3 strings
     * representing the current side of the card
     * @param card The card we want to convert in strings
     */
    private static String[] smallCardToString(Card card) {
        String[] line = {"", "", ""};
        String w = ANSI_BLUE + "W" + ANSI_RESET;
        String l = ANSI_GREEN + "L" + ANSI_RESET;
        String b = ANSI_PURPLE + "B" + ANSI_RESET;
        String m = ANSI_RED + "M" + ANSI_RESET;
        String j = ANSI_WHITE + "J" + ANSI_RESET;
        String p = ANSI_WHITE + "P" + ANSI_RESET;
        String s = ANSI_WHITE + "S" + ANSI_RESET;
        String def;
        String res;
        Corner[] corner = card.isFront() ? card.getFrontCorners() : card.getBackCorners();
        String[] cc = new String[4];
        for(int i = 0; i < 4; i++) {
            switch(corner[i].getType()) {
                case ITEM:
                    switch(corner[i].getItem()) {
                        case JAR:
                            cc[i] = j;
                            break;
                        case SCROLL:
                            cc[i] = s;
                            break;
                        case PLUME:
                            cc[i] = p;
                            break;
                        default:
                            cc[i] = "@";
                            break;
                    }
                    break;
                case RESOURCE:
                    switch(corner[i].getResource()) {
                        case WOLF:
                            cc[i] = w;
                            break;
                        case LEAF:
                            cc[i] = l;
                            break;
                        case MUSHROOM:
                            cc[i] = m;
                            break;
                        case BUTTERFLY:
                            cc[i] = b;
                            break;
                        default:
                            cc[i] = "@";
                            break;
                    }
                    break;
                case FREE:
                    switch(i) {
                        case 0:
                            cc[i] = "╮";
                            break;
                        case 1:
                            cc[i] = "╯";
                            break;
                        case 2:
                            cc[i] = "╰";
                            break;
                        case 3:
                            cc[i] = "╭";
                            break;
                        default:
                            cc[i] = "@";
                            break;
                    }
                    break;
                case DEAD:
                    cc[i] = "᙭";
                    break;
                default:
                    cc[i] = "@";
                    break;
            }
        }
        switch (card.getResource()) {
            case BUTTERFLY:
                res = b;
                def = ANSI_PURPLE;
                break;
            case MUSHROOM:
                res = m;
                def = ANSI_RED;
                break;
            case LEAF:
                res = l;
                def = ANSI_GREEN;
                break;
            case WOLF:
                res = w;
                def = ANSI_BLUE;
                break;
            default:
                res = "@";
                def = ANSI_WHITE;
                break;
        }
        if(card.getClass() == ResourceCard.class || card.getClass() == GoldCard.class) {
            // line 1 / 3
            if(corner[3].isVisible())
                line[0] += def + cc[3] + def + "─";
            line[0] += def + "───";
            if(corner[0].isVisible())
                line[0] += def + "─" + cc[0];
            // line 2 / 3
            if(card.isFront())
                line[1] += def + "│     │";
            else
                line[1] += def + "│  " + res + def + "  │";
            // line 3 / 3
            if(corner[2].isVisible())
                line[2] += def + cc[2] + def + "─";
            line[2] += def + "───";
            if(corner[1].isVisible())
                line[2] += def + "─" + cc[1];
        } else if(card.getClass() == StarterCard.class) {
            def = ANSI_YELLOW;
            int qres = 1;
            String scndres = "°", thrdres = "°";
            if(((StarterCard) card).getSecondResource() != null) {
                qres++;
                switch (((StarterCard) card).getSecondResource()) {
                    case BUTTERFLY:
                        scndres = b;
                        break;
                    case MUSHROOM:
                        scndres = m;
                        break;
                    case LEAF:
                        scndres = l;
                        break;
                    case WOLF:
                        scndres = w;
                        break;
                    default:
                        scndres = "@";
                        break;
                }
            }
            if(((StarterCard) card).getThirdResource() != null) {
                qres++;
                switch (((StarterCard) card).getThirdResource()) {
                    case BUTTERFLY:
                        thrdres = b;
                        break;
                    case MUSHROOM:
                        thrdres = m;
                        break;
                    case LEAF:
                        thrdres = l;
                        break;
                    case WOLF:
                        thrdres = w;
                        break;
                    default:
                        thrdres = "@";
                        break;
                }
            }
            // line 1 / 3 starter
            if(corner[3].isVisible())
                line[0] += def + cc[3] + def + "─";
            line[0] += def + "───";
            if(corner[0].isVisible())
                line[0] += def + "─" + cc[0];
            // line 2 / 3 starter
            if(card.isFront()) {
                switch(qres) {
                    case 1:
                        line[1] = def + "│  " + res + def + "  │";
                        break;
                    case 2:
                        line[1] = def + "│ " + res + " " + scndres + def + " │";
                        break;
                    case 3:
                        line[1] = def + "│" + res + " " + scndres + " " + thrdres + def +"│";
                        break;
                    default:
                        line[1] = def + "│@ @ @│";
                        break;
                }
            } else
                line[1] += def + "│     │";
            // line 3 / 3 starter
            if(corner[2].isVisible())
                line[2] += def + cc[2] + def + "─";
            line[2] += def + "───";
            if(corner[1].isVisible())
                line[2] += def + "─" + cc[1];
        } else { // achievement card ??
            line[0] = "╭─────╮";
            line[1] = "│ERROR│";
            line[2] = "╰─────╯";
        }
        line[0] += ANSI_RESET;
        line[1] += ANSI_RESET;
        line[2] += ANSI_RESET;
        return line;
    }

    /**
     * Given a card, the function print its current side in
     * a small version
     * @param card The card we want to print
     */
    public static void smallCardPrinter(Card card) {
        String[] result = smallCardToString(card);
        System.out.println(result[0] + "\n" + result[1] + "\n" + result[2]);
    }

    /**
     * Given a playground this function converts it in an array of string
     * @param area The playground we want to convert
     * @return an array of strings
     */
    private static String[] playgroundToString(Playground area) {
        Space space;
        Card card;
        String[] text;
        int x, y;
        int west = area.getWestBound();
        int east = area.getEastBound();
        int south = area.getSouthBound();
        int north = area.getNorthBound();
        int height = south - north + 1;
        int width = east - west + 1;
        int cHeight = (height * 2) + 1;     //number of chars in a column
        int cWidth = (width * 5) + 2;       //number of chars in a row

        //debugging
        /*
        for(int row = north; row <= south; row++) {
            for(int col = west; col <= east; col++) {
                space = area.getSpace(row, col);
                System.out.print("row: " + row + ", col: " + col);
                if(!space.isFree() && !space.isDead()) { //space is card, not free, not dead
                    card = space.getCard();
                    System.out.print(", " + (card.getClass() == StarterCard.class ? ANSI_YELLOW : ANSI_CYAN) + "CARD" + ANSI_RESET + ", ");
                    for(Corner c : (card.isFront() ? card.getFrontCorners() : card.getBackCorners()))
                        System.out.print((c.isVisible() ? Print.ANSI_GREEN + "V" : Print.ANSI_RED + "N") + Print.ANSI_RESET);
                    System.out.print(" ");
                } else if(space.isDead()) { //space is dead
                    System.out.print(", " + ANSI_BLACK + "DEAD" + ANSI_RESET + ",      ");
                } else { //space is free but not dead
                    System.out.print(", FREE,      ");
                }
            }
            System.out.println(";");
        }
    */
    //initializing the stringbuilder array
        StringBuilder[] line = new StringBuilder[cHeight];
        for(int i = 0; i  < cHeight; i++)
            line[i] = new StringBuilder();

    //algorithm
        for(int col = west; col <= east; col++) {
            for(int row = north; row <= south; row++) {
                y = row - north;
                x = col - west;
                space = area.getSpace(row, col);
                if(!space.isFree() && !space.isDead()) { //space is card, not free, not dead
                    card = space.getCard();
                    text = smallCardToString(card);
                //first line
                    line[y*2].append(text[0]);
                //second line
                    line[(y*2)+1].append(text[1]);
                //third line
                    line[(y*2)+2].append(text[2]);
                } else if(space.isDead()) { //space is dead
                //first line
                    if(y == 0) {
                        if(x == 0)
                            line[y*2].append("     ");
                        else
                            line[y*2].append("   ");
                    }
                //second line
                    if(x == 0)
                        line[(y*2)+1].append("     ");
                    else
                        line[(y*2)+1].append("   ");
                //third line
                    if(y == south - north) {
                        if(x == 0)
                            line[(y*2)+2].append("     ");
                        else
                            line[(y*2)+2].append("   ");
                    }
                } else { //space is free but not dead
                //first line
                    if(y == 0) { //top row
                            line[y*2].append("       ");
                    } else { //check if there is card on top left
                        if(x == 0) //most left
                            line[y*2].append("     ");
                        else {
                            if(!area.getSpace(row-1,col-1).isFree())
                                line[y*2].append("   ");
                            else
                                line[y*2].append("     ");
                        }
                    }
                //second line
                    line[(y*2)+1].append("       ");
                //third line
                    if(y == south - north) // last row
                        line[(y*2)+2].append("       ");
                    else {
                        if(!area.getSpace(row+1,col-1).isFree())
                            line[(y*2)+2].append("   ");
                        else
                            line[(y*2)+2].append("     ");
                    }
                }
            }
        }

    //converting the stringbuilder[] to string[]
        String[] result = new String[cHeight];
        for(int i = 0; i < cHeight; i++) {
            //System.out.println(" - riga " + i  + " " + (line[i].length() == 0 ? "empty" : "full"));
            if(line[i].length() == 0) System.out.println(" - riga " + i  + " is empty");
            result[i] = line[i].toString();
        }
        return result;
    }

    /**
     * Given a Card, this function return an array of 6 strings representing the card
     * @param card The card we want to convert in strings
     * @param side Boolean for the side we want elaborate, true for front, false for back.
     */
    private static String[] largeCardToString(Card card, boolean side) {
        String[] result = new String[6];
        String[] corners = new String[7];
        String def;
        if(card.getClass() == ResourceCard.class) { //printing a resource card
            switch(card.getResource()) {
                case WOLF:
                    corners[4] = "W";
                    Arrays.fill(result, ANSI_BLUE);
                    def = ANSI_BLUE;
                    break;
                case LEAF:
                    corners[4] = "L";
                    Arrays.fill(result, ANSI_GREEN);
                    def = ANSI_GREEN;
                    break;
                case BUTTERFLY:
                    corners[4] = "B";
                    Arrays.fill(result, ANSI_PURPLE);
                    def = ANSI_PURPLE;
                    break;
                case MUSHROOM:
                    corners[4] = "M";
                    Arrays.fill(result, ANSI_RED);
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
                if(corners[3].equals("x")) {
                    result[0] = result[0].concat("╭────");
                } else {
                    result[0] = result[0].concat("╭───┬");
                }
                if(corners[0].equals("x")) {
                    result[0] = result[0].concat("───────────────╮");
                } else {
                    result[0] = result[0].concat("───────────┬───╮");
                }
                if(corners[3].equals("x")) {
                    result[1] = result[1].concat("│    ");
                } else {
                    result[1] = result[1].concat("│ " + corners[3] + " │");
                }
                if(corners[0].equals("x")) {
                    result[1] = result[1].concat("     " + pts + "         │");
                } else {
                    result[1] = result[1].concat("     " + pts + "     │ " + corners[0] + " │");
                }
                if(corners[3].equals("x")) {
                    result[2] = result[2].concat("│    ");
                } else {
                    result[2] = result[2].concat("├───╯");
                }
                if(corners[0].equals("x")) {
                    result[2] = result[2].concat("               │");
                } else {
                    result[2] = result[2].concat("           ╰───┤");
                }
                if(corners[2].equals("x")) {
                    result[3] = result[3].concat("│    ");
                } else {
                    result[3] = result[3].concat("├───╮");
                }
                if(corners[1].equals("x")) {
                    result[3] = result[3].concat("               │");
                } else {
                    result[3] = result[3].concat("           ╭───┤");
                }
                if(corners[2].equals("x")) {
                    result[4] = result[4].concat("│    ");
                } else {
                    result[4] = result[4].concat("│ " + corners[2] + " │");
                }
                if(corners[1].equals("x")) {
                    result[4] = result[4].concat("               │");
                } else {
                    result[4] = result[4].concat("           │ " + corners[1] + " │");
                }
                if(corners[2].equals("x")) {
                    result[5] = result[5].concat("╰────");
                } else {
                    result[5] = result[5].concat("╰───┴");
                }
                if(corners[1].equals("x")) {
                    result[5] = result[5].concat("───────────────╯");
                } else {
                    result[5] = result[5].concat("───────────┴───╯");
                }
            } else { //back side
                result[0] = result[0].concat("╭───┬───────────┬───╮");
                result[1] = result[1].concat("│   │           │   │");
                result[2] = result[2].concat("├───╯           ╰───┤");
                result[3] = result[3].concat("├───╮     " + corners[4] + "     ╭───┤");
                result[4] = result[4].concat("│   │           │   │");
                result[5] = result[5].concat("╰───┴───────────┴───╯");
            }
        } else if(card.getClass() == GoldCard.class) { //printing a gold card
            switch(card.getResource()) {
                case WOLF:
                    corners[4] = "W";
                    Arrays.fill(result, ANSI_BLUE);
                    def = ANSI_BLUE;
                    break;
                case LEAF:
                    corners[4] = "L";
                    Arrays.fill(result, ANSI_GREEN);
                    def = ANSI_GREEN;
                    break;
                case BUTTERFLY:
                    corners[4] = "B";
                    Arrays.fill(result, ANSI_PURPLE);
                    def = ANSI_PURPLE;
                    break;
                case MUSHROOM:
                    corners[4] = "M";
                    Arrays.fill(result, ANSI_RED);
                    def = ANSI_RED;
                    break;
                default:
                    corners[4] = "#";
                    Arrays.fill(result, ANSI_RESET);
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
                if(corners[3].equals("x")) {
                    result[0] = result[0].concat("╭────");
                } else {
                    result[0] = result[0].concat("╭───┬");
                }
                if(corners[0].equals("x")) {
                    result[0] = result[0].concat("───────────────╮");
                } else {
                    result[0] = result[0].concat("───────────┬───╮");
                }
                if(corners[3].equals("x")) {
                    result[1] = result[1].concat("│    ");
                } else {
                    result[1] = result[1].concat("│ " + corners[3] + " │");
                }
                if(corners[0].equals("x")) {
                    result[1] = result[1].concat("    " + pts + "        │");
                } else {
                    result[1] = result[1].concat("    " + pts + "    │ " + corners[0] + " │");
                }
                if(corners[3].equals("x")) {
                    result[2] = result[2].concat("│    ");
                } else {
                    result[2] = result[2].concat("├───╯");
                }
                if(corners[0].equals("x")) {
                    result[2] = result[2].concat("               │");
                } else {
                    result[2] = result[2].concat("           ╰───┤");
                }
                if(corners[2].equals("x")) {
                    result[3] = result[3].concat("│    ");
                } else {
                    result[3] = result[3].concat("├───╮");
                }
                if(corners[1].equals("x")) {
                    result[3] = result[3].concat("               │");
                } else {
                    result[3] = result[3].concat("           ╭───┤");
                }
                if(corners[2].equals("x")) {
                    result[4] = result[4].concat("│    ");
                } else {
                    result[4] = result[4].concat("│ " + corners[2] + " │");
                }
                if(corners[1].equals("x")) {
                    result[4] = result[4].concat("   " + reqRes + "       │");
                } else {
                    result[4] = result[4].concat("   " + reqRes + "   │ " + corners[1] + " │");
                }
                if(corners[2].equals("x")) {
                    result[5] = result[5].concat("╰────");
                } else {
                    result[5] = result[5].concat("╰───┴");
                }
                if(corners[1].equals("x")) {
                    result[5] = result[5].concat("───────────────╯");
                } else {
                    result[5] = result[5].concat("───────────┴───╯");
                }
            } else { //back side
                result[0] = result[0].concat("╭───┬───────────┬───╮");
                result[1] = result[1].concat("│   │           │   │");
                result[2] = result[2].concat("├───╯           ╰───┤");
                result[3] = result[3].concat("├───╮     " + corners[4] + "     ╭───┤");
                result[4] = result[4].concat("│   │           │   │");
                result[5] = result[5].concat("╰───┴───────────┴───╯");
            }
        } else if(card.getClass() == AchievementCard.class) { //printing an achievement card
            def = ANSI_YELLOW;
            Arrays.fill(result, def);
            if(side) {
                if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyDiagonal.class) {
                    switch(card.getResource()) {
                        case WOLF:
                            def = ANSI_BLUE;
                            Arrays.fill(result, def);
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│      ██     " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│  ██               │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        case LEAF:
                            def = ANSI_GREEN;
                            Arrays.fill(result, def);
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│  ██         " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│      ██           │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        case MUSHROOM:
                            def = ANSI_RED;
                            Arrays.fill(result, def);
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│      ██     " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│  ██               │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        case BUTTERFLY:
                            def = ANSI_PURPLE;
                            Arrays.fill(result, def);
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│  ██         " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│      ██           │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        default:
                            result[0] = result[0].concat("╭───┬───────────┬───╮");
                            result[1] = result[1].concat("│ # │           │ # │");
                            result[2] = result[2].concat("├───╯    ACH    ╰───┤");
                            result[3] = result[3].concat("├───╮  DIAGONAL ╭───┤");
                            result[4] = result[4].concat("│ # │   ERROR   │ # │");
                            result[5] = result[5].concat("╰───┴───────────┴───╯");
                            break;
                    }
                } else if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyItem.class) {
                    def = ANSI_WHITE;
                    Arrays.fill(result, def);
                    switch(((AchievementCard) card).getItem()) {
                        case JAR:
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│     J       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    J J        " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│                   │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        case SCROLL:
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│     S       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    S S        " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│                   │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        case PLUME:
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│     P       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    P P        " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│                   │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        default:
                            result[0] = result[0].concat("╭───┬───────────┬───╮");
                            result[1] = result[1].concat("│ # │           │ # │");
                            result[2] = result[2].concat("├───╯   ACH     ╰───┤");
                            result[3] = result[3].concat("├───╮   ITEM    ╭───┤");
                            result[4] = result[4].concat("│ # │   ERROR   │ # │");
                            result[5] = result[5].concat("╰───┴───────────┴───╯");
                            break;
                    }
                } else if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyLshape.class) {
                    switch(card.getResource()) {
                        case WOLF:
                            def = ANSI_BLUE;
                            Arrays.fill(result, def);
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│      " + ANSI_RED + "██" + def + "     " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│    ██             │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        case LEAF:
                            def = ANSI_GREEN;
                            Arrays.fill(result, def);
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│    ██       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│  " + ANSI_PURPLE + "██" + def + "               │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        case MUSHROOM:
                            def = ANSI_RED;
                            Arrays.fill(result, def);
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│    ██       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│      " + ANSI_GREEN + "██" + def + "           │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        case BUTTERFLY:
                            def = ANSI_PURPLE;
                            Arrays.fill(result, def);
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│  " + ANSI_BLUE + "██" + def +  "         " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    ██         " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│    ██             │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        default:
                            result[0] = result[0].concat("╭───┬───────────┬───╮");
                            result[1] = result[1].concat("│ # │           │ # │");
                            result[2] = result[2].concat("├───╯    ACH    ╰───┤");
                            result[3] = result[3].concat("├───╮  L SHAPE  ╭───┤");
                            result[4] = result[4].concat("│ # │   ERROR   │ # │");
                            result[5] = result[5].concat("╰───┴───────────┴───╯");
                            break;
                    }
                } else if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyMixed.class) {
                    def = ANSI_WHITE;
                    Arrays.fill(result, def);
                    result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                    result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                    result[2] = result[2].concat("│             " + ANSI_YELLOW + "'\\./'" + def + " │");
                    result[3] = result[3].concat("│    P J S      " + ANSI_YELLOW + "'" + def + "   │");
                    result[4] = result[4].concat("│                   │");
                    result[5] = result[5].concat("╰───────────────────╯");
                } else if(((AchievementCard)card).getStrategy().getClass() == ConcreteStrategyResource.class) {
                    switch(card.getResource()) {
                        case WOLF:
                            def = ANSI_BLUE;
                            Arrays.fill(result, def);
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│     W       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    W W        " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│                   │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        case LEAF:
                            def = ANSI_GREEN;
                            Arrays.fill(result, def);
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│     L       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    L L        " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│                   │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        case MUSHROOM:
                            def = ANSI_RED;
                            Arrays.fill(result, def);
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│     M       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    M M        " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│                   │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        case BUTTERFLY:
                            def = ANSI_PURPLE;
                            Arrays.fill(result,def);
                            result[0] = result[0].concat("╭─────────────" + ANSI_YELLOW + "┬───┬" + def + "─╮");
                            result[1] = result[1].concat("│             " + ANSI_YELLOW + "│ " + card.getPoints() + " │" + def + " │");
                            result[2] = result[2].concat("│     B       " + ANSI_YELLOW + "'\\./'" + def + " │");
                            result[3] = result[3].concat("│    B B        " + ANSI_YELLOW + "'" + def + "   │");
                            result[4] = result[4].concat("│                   │");
                            result[5] = result[5].concat("╰───────────────────╯");
                            break;
                        default:
                            result[0] = result[0].concat("╭───┬───────────┬───╮");
                            result[1] = result[1].concat("│ # │           │ # │");
                            result[2] = result[2].concat("├───╯    ACH    ╰───┤");
                            result[3] = result[3].concat("├───╮  RESOURCE ╭───┤");
                            result[4] = result[4].concat("│ # │   ERROR   │ # │");
                            result[5] = result[5].concat("╰───┴───────────┴───╯");
                            break;
                    }
                } else {
                    result[0] = result[0].concat("╭───┬───────────┬───╮");
                    result[1] = result[1].concat("│ # │           │ # │");
                    result[2] = result[2].concat("├───╯    ACH    ╰───┤");
                    result[3] = result[3].concat("├───╮   ERROR   ╭───┤");
                    result[4] = result[4].concat("│ # │           │ # │");
                    result[5] = result[5].concat("╰───┴───────────┴───╯");
                }
            } else {
                def = ANSI_WHITE;
                Arrays.fill(result, def);
                result[0] = result[0].concat("╭───────────────────╮");
                result[1] = result[1].concat("│" + ANSI_YELLOW + "        @@@        " + def + "│");
                result[2] = result[2].concat("│" + ANSI_YELLOW + " *    ,@@@@@,    * " + def + "│");
                result[3] = result[3].concat("│" + ANSI_YELLOW + "       °@#@°       " + def + "│");
                result[4] = result[4].concat("│" + ANSI_YELLOW + " <>   , °#° ,   <> " + def + "│");
                result[5] = result[5].concat("╰───────────────────╯");
            }
        } else if(card.getClass() == StarterCard.class) { //printing a starter card
            def = ANSI_YELLOW;
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
                Arrays.fill(result, def);
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
                if(corners[3].equals("x")) {
                    result[0] = result[0].concat("╭────");
                } else {
                    result[0] = result[0].concat("╭───┬");
                }
                if(corners[0].equals("x")) {
                    result[0] = result[0].concat("───────────────╮");
                } else {
                    result[0] = result[0].concat("───────────┬───╮");
                }
                if(corners[3].equals("x")) {
                    result[1] = result[1].concat("│    ");
                } else {
                    result[1] = result[1].concat("│ " + corners[3] + " │");
                }
                if(corners[0].equals("x")) {
                    result[1] = result[1].concat("               │");
                } else {
                    result[1] = result[1].concat("           │ " + corners[0] + " │");
                }
                if(corners[3].equals("x")) {
                    result[2] = result[2].concat("│    ");
                } else {
                    result[2] = result[2].concat("├───╯");
                }
                if(corners[5].equals("#")) {
                    result[2] = result[2].concat("           ");
                } else {
                    result[2] = result[2].concat("     " + corners[4] + "     ");
                }
                if(corners[0].equals("x")) {
                    result[2] = result[2].concat("    │");
                } else {
                    result[2] = result[2].concat("╰───┤");
                }
                if(corners[2].equals("x")) {
                    result[3] = result[3].concat("│    ");
                } else {
                    result[3] = result[3].concat("├───╮");
                }
                if(corners[5].equals("#")) {
                    result[3] = result[3].concat("     " + corners[4] + "     ");
                } else {
                    result[3] = result[3].concat("     " + corners[5] + "     ");
                }
                if(corners[1].equals("x")) {
                    result[3] = result[3].concat("    │");
                } else {
                    result[3] = result[3].concat("╭───┤");
                }
                if(corners[2].equals("x")) {
                    result[4] = result[4].concat("│    ");
                } else {
                    result[4] = result[4].concat("│ " + corners[2] + " │");
                }
                if(corners[6].equals("#")) {
                    result[4] = result[4].concat("           ");
                } else {
                    result[4] = result[4].concat("     " + corners[6] + "     ");
                }
                if(corners[1].equals("x")) {
                    result[4] = result[4].concat("    │");
                } else {
                    result[4] = result[4].concat("│ " + corners[1] + " │");
                }
                if(corners[2].equals("x")) {
                    result[5] = result[5].concat("╰────");
                } else {
                    result[5] = result[5].concat("╰───┴");
                }
                if(corners[1].equals("x")) {
                    result[5] = result[5].concat("───────────────╯");
                } else {
                    result[5] = result[5].concat("───────────┴───╯");
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
                if(corners[3].equals("x")) {
                    result[0] = "╭────";
                } else {
                    result[0] = "╭───┬";
                }
                if(corners[0].equals("x")) {
                    result[0] = result[0].concat("───────────────╮");
                } else {
                    result[0] = result[0].concat("───────────┬───╮");
                }
                if(corners[3].equals("x")) {
                    result[1] = "│    ";
                } else {
                    result[1] = "│ " + corners[3] + " │";
                }
                if(corners[0].equals("x")) {
                    result[1] = result[1].concat("               │");
                } else {
                    result[1] = result[1].concat("           │ " + corners[0] + " │");
                }
                if(corners[3].equals("x")) {
                    result[2] = "│    ";
                } else {
                    result[2] = "├───╯";
                }
                if(corners[0].equals("x")) {
                    result[2] = result[2].concat("     O         │");
                } else {
                    result[2] = result[2].concat("     O     ╰───┤");
                }
                if(corners[2].equals("x")) {
                    result[3] = "│    ";
                } else {
                    result[3] = "├───╮";
                }
                if(corners[1].equals("x")) {
                    result[3] = result[3].concat("    O O        │");
                } else {
                    result[3] = result[3].concat("    O O    ╭───┤");
                }
                if(corners[2].equals("x")) {
                    result[4] = "│    ";
                } else {
                    result[4] = "│ " + corners[2] + " │";
                }
                if(corners[1].equals("x")) {
                    result[4] = result[4].concat("     O         │");
                } else {
                    result[4] = result[4].concat("     O     │ " + corners[1] + " │");
                }
                if(corners[2].equals("x")) {
                    result[5] = "╰────";
                } else {
                    result[5] = "╰───┴";
                }
                if(corners[1].equals("x")) {
                    result[5] = result[5].concat("───────────────╯");
                } else {
                    result[5] = result[5].concat("───────────┴───╯");
                }
                for(int i = 0; i < result.length; i++)
                    result[i] = (def).concat(result[i]);
            }
        } else { //error card
            result[0] = "╭───┬───────────┬───╮";
            result[1] = "│ # │           │ # │";
            result[2] = "├───╯           ╰───┤";
            result[3] = "├───╮   ERROR   ╭───┤";
            result[4] = "│ # │           │ # │";
            result[5] = "╰───┴───────────┴───╯";
        }
        for(int i = 0; i < result.length; i++)
            result[i] = result[i].concat(ANSI_RESET);
        return result;
    }

    /**
     * Given a Card, this function return an array of 6 strings representing
     * the current side of the card, or the front if it's an achievement
     * It's an overloading of the previous method
     * @param card The card we want to convert in strings
     */
    private static String[] largeCardToString(Card card) {
        if(card.getClass() == AchievementCard.class)
            return largeCardToString(card, true);
        else
            return largeCardToString(card, card.isFront());
    }

    /**
     * Printing function for text user interface, it prints the selected side of card
     * using non-regular ascii characters
     * @param card The card we want to print in the tui
     * @param side The side we want to display, true for front, false for back
     */
    public static void largeCardPrinter(Card card, boolean side) {
        String[] lines = largeCardToString(card, side);
        String result = "";
        for (String line : lines)
            result = result.concat(line + "\n");
        System.out.println(result);
    }

    /**
     * Printing function for text user interface, it prints the current side of the card
     * with non-regular ascii characters, it's an overloading of the previous method
     * @param card The card we want to print in the tui
     */
    public static void largeCardPrinter(Card card) {
        largeCardPrinter(card, card.isFront());
    }


    /**
     * Printing function for text user interface, it prints both sides of the card
     * with non-regular ascii characters
     * @param card The card we want to print in the tui
     */
    public static void largeCardBothSidesPrinter(Card card) {
        String[] front = largeCardToString(card, true);
        String[] back = largeCardToString(card, false);
        String toPrint = "";
        for(int i = 0; i < front.length; i++)
            toPrint = toPrint.concat(front[i] + " " + ANSI_RESET + back[i] + "\n");
        System.out.print(toPrint);
    }

    /**
     * Printing function for text user interface, given an arraylist of achievement
     * cards, it prints them all in a single row, showing only the front side
     * @param achCards The arraylist containing the cards we want to be printed
     */
    public static void inLineAchievementPrinter(ArrayList<AchievementCard> achCards) {
        String[] lines = new String[6];
        String[] temp;
        Arrays.fill(lines, "");
        for(int i = 0; i < achCards.size(); i++) {
            temp = largeCardToString(achCards.get(i), true);
            for(int j = 0; j < 6; j++)
                lines[j] = lines[j].concat(temp[j] + "   " + ANSI_RESET);
        }
        String result = "";
        for(int i = 0; i < 6; i++)
            result = result.concat(lines[i] + "\n");
        System.out.print(result);
    }

    /**
     * Printing function for text user interface, given an arraylist of cards,
     * it prints them all in a single row showing their current side
     * @param cards The arraylist containing the cards we want to be printed
     */
    public static void inLineLargeCardsPrinter(ArrayList<Card> cards) {
        String[] lines = new String[6];
        String[] temp;
        Arrays.fill(lines, "");
        for(int i = 0; i < cards.size(); i++) {
            temp = largeCardToString(cards.get(i));
            for(int j = 0; j < 6; j++)
                lines[j] = lines[j].concat(temp[j] + "   " + ANSI_RESET);
        }
        String result = "";
        for(int i = 0; i < 6; i++)
            result = result.concat(lines[i] + "\n");
        System.out.print(result);
    }

    /**
     * Given an array of cards, representing the hand of the player and an
     * array of achievements, representing the secret and the common achievements,
     * This method prints all the cards in two rows, one for each side
     * @param cards The cards representing the hand of the player
     * @param achievements The achievement cards representing the achievement of the player
     */
    public static void largeHandPrinter(ArrayList<Card> cards, ArrayList<AchievementCard> achievements) {
        String[] lines = new String[6];
        String[] temp;
        String result = "";
        if(cards != null && !cards.isEmpty()) {
            // first row
            Arrays.fill(lines, "       ");
            result = "       " + ANSI_BOLD;
            for(int i = 0; i < cards.size(); i++) {
                result = result.concat("       " + (i+1) + "° Card" + "       " + "   ");
            }
            if(achievements != null && !achievements.isEmpty())
                result = result.concat("             " + "Secret achievement and common achievements" + "              ");
            result = result.concat(ANSI_BOLD_RESET + "\n");
            lines[2] = ANSI_BOLD + " Front " + ANSI_BOLD_RESET;
            lines[3] = ANSI_BOLD + "  side " + ANSI_BOLD_RESET;
            for(int i = 0; i < cards.size(); i++) {
                temp = largeCardToString(cards.get(i), true);
                for(int j = 0; j < 6; j++)
                    lines[j] = lines[j].concat(temp[j] + "   " + ANSI_RESET);
            }
            if(achievements != null && !achievements.isEmpty())
                for(int i = 0; i < achievements.size(); i++) {
                    temp = largeCardToString(achievements.get(i), true);
                    for(int j = 0; j < 6; j++)
                        lines[j] = lines[j].concat(temp[j] + "   " + ANSI_RESET);
                }
            for(int i = 0; i < 6; i++) {
                result = result.concat(lines[i] + "\n");
            }
            //second row
            Arrays.fill(lines, "       ");
            lines[2] = ANSI_BOLD + "  Back " + ANSI_BOLD_RESET;
            lines[3] = ANSI_BOLD + "  side " + ANSI_BOLD_RESET;
            for(int i = 0; i < cards.size(); i++) {
                temp = largeCardToString(cards.get(i), false);
                for(int j = 0; j < 6; j++)
                    lines[j] = lines[j].concat(temp[j] + "   " + ANSI_RESET);
            }
            for(int i = 0; i < 6; i++) {
                result = result.concat(lines[i] + "\n");
            }
        } else if(achievements != null && !achievements.isEmpty()){ //just achievements ? should be an error in the game
            Arrays.fill(lines, "");
            for(int i = 0; i < achievements.size(); i++) {
                temp = largeCardToString(achievements.get(i), true);
                for(int j = 0; j < 6; j++)
                    lines[j] = lines[j].concat(temp[j] + "   " + ANSI_RESET);
            }
            result = ANSI_BOLD;
            result = result.concat("             " + "Secret achievement and common achievements" + "              ");
            result = result.concat(ANSI_BOLD_RESET + "\n");
            for(int i = 0; i < 6; i++) {
                result = result.concat(lines[i] + "\n");
            }
        } else
            result = ANSI_BOLD + "It seems you don't have card in your hand.\n" + ANSI_BOLD_RESET;
        System.out.print(result);
    }

    /**
     * Given a player's name, this function return the special tui chars for its color
     * @param player the name of the player we want to obtain the color from
     * @param list arraylist of the players in the game
     * @param single player asking for color
     * @return String of chars representing the color for tui
     */
    public static String getPlayerColor(String player, ArrayList<Player> list, Player single) {
        Player tmp = null;
        String color;
        if(player.equalsIgnoreCase(single.getName()))
            tmp = single;
        else
            for(Player p : list)
                if(player.equalsIgnoreCase(p.getName()))
                    tmp = p;
        if(tmp == null)
            color = "";
        else
            switch(tmp.getColor()) {
                case RED:
                    color = ANSI_RED;
                    break;
                case BLUE:
                    color = ANSI_BLUE;
                    break;
                case GREEN:
                    color = ANSI_GREEN;
                    break;
                case YELLOW:
                    color = ANSI_YELLOW;
                    break;
                case NONE:
                default:
                    color = "";
                    break;
            }
        return color;
    }

    /**
     * Given all the cards on the table, this function prints em for the tui
     * @param commonGold arraylist representing the gold cards
     * @param commonResource arraylist representing the resource cards
     * @param goldDeck resource of the first card on top of the gold deck
     * null if the deck is empty
     * @param resourceDeck resource of the first card on top of the resource deck
     * null if the deck is empty
     */
    public static void drawChoicePrinter(ArrayList<GoldCard> commonGold, ArrayList<ResourceCard> commonResource, Resource goldDeck, Resource resourceDeck) {
        String result = ANSI_BOLD;
        String[] lines = new String[6];
        Arrays.fill(lines, "");
        String spaces;
        String[] gDeck = new String[8];
        String[] rDeck = new String[8];
        String[] tempLines;
        if(resourceDeck != null || goldDeck != null) {
            int cSpaces = 22 * (commonGold.size() + commonResource.size());
            spaces = "";
            for(int i = 0; i < cSpaces; i++)
                spaces = spaces.concat(" ");
            result = result.concat(spaces);
            if(goldDeck != null) {
                result = result.concat("      5: Gold deck      ");
                gDeck = inlineDeckPrinter(goldDeck);
            }
            if(resourceDeck != null) {
                result = result.concat("    6: Resource deck");
                rDeck = inlineDeckPrinter(resourceDeck);
            }
            result = result.concat(ANSI_BOLD_RESET);
            result = result.concat("\n" + spaces);
            if(goldDeck != null)
                result = result.concat(gDeck[0] + " ");
            if(resourceDeck != null)
                result = result.concat(rDeck[0]);
            result = result.concat("\n");

        }
        result = result.concat(ANSI_BOLD);
        for(int i = 0; i < commonGold.size(); i++) {
            result = result.concat("    " + (i+1) + ": Gold card      ");
        }
        for(int i = 0; i < commonResource.size(); i++) {
            result = result.concat("  " + (i+3) + ": Resource card    ");
        }
        result = result.concat(ANSI_BOLD_RESET);
        if(goldDeck != null)
            result = result.concat(gDeck[1] + " ");
        if(resourceDeck != null)
            result = result.concat(rDeck[1]);
        result = result.concat("\n");
        if(!commonGold.isEmpty()) {
            for(int j = 0; j < commonGold.size(); j++) {
                tempLines = largeCardToString(commonGold.get(j), true);
                for (int i = 0; i < 6; i++) {
                    lines[i] = lines[i].concat(tempLines[i] + " ");
                }
            }
        }
        if(!commonResource.isEmpty()) {
            for(int j = 0; j < commonResource.size(); j++) {
                tempLines = largeCardToString(commonResource.get(j), true);
                for (int i = 0; i < 6; i++) {
                    lines[i] = lines[i].concat(tempLines[i] + " ");
                }
            }
        }
        if(goldDeck != null)
            for (int i = 0; i < 6; i++)
                lines[i] = lines[i].concat(gDeck[i+2] + " ");
        if(resourceDeck != null)
            for (int i = 0; i < 6; i++)
                lines[i] = lines[i].concat(rDeck[i+2] + " ");
        for(int i = 0; i < 6; i++)
            result = result.concat(lines[i] + "\n");
        System.out.print(result);
    }

    /**
     * Given a resource this function will return an array of string representing
     * a deck of back-sided cards with the top one having the right resource
     * @param resource the resource of the top card on the deck
     * @return the array of string representing the deck
     */
    private static String[] inlineDeckPrinter(Resource resource) {
        String c = "#";
        String def = "";
        if(resource != null)
            switch(resource) {
                case WOLF:
                    c = "W";
                    def = ANSI_BLUE;
                    break;
                case LEAF:
                    c = "L";
                    def = ANSI_GREEN;
                    break;
                case BUTTERFLY:
                    c = "B";
                    def = ANSI_PURPLE;
                    break;
                case MUSHROOM:
                    c = "M";
                    def = ANSI_RED;
                    break;
                default:
                    c = "#";
                    break;
            }
        String[] result = new String[8];
        result[0] = ANSI_RESET + "  ╭───┬───────────┬───╮";
        result[1] = ANSI_RESET + " ╭┴──┬┴──────────┬┴──╮│";
        result[2] = def + "╭┴──┬┴──────────┬┴──╮" + ANSI_RESET + "│┤";
        result[3] = def + "│   │           │   │" + ANSI_RESET + "┤┤";
        result[4] = def + "├───╯           ╰───┤" + ANSI_RESET + "┤│";
        result[5] = def + "├───╮     " + c + "     ╭───┤" + ANSI_RESET + "├╯";
        result[6] = def + "│   │           │   ├" + ANSI_RESET + "╯ ";
        result[7] = def + "╰───┴───────────┴───╯  " + ANSI_RESET;
        return result;
    }

    /**
     * Method that prints an ascii art "YOU WON" or "YOU LOST" for the tui
     * @param color String of chars for a specific color, null or "" for none
     */
    public static void resultAsciiArt(boolean win, String color) {
        if(color == null)
            color = "";
        String youWon = "                   __      __  ______   __    __        __       __   ______   __    __ \n" +
                "                  /  \\    /  |/      \\ /  |  /  |      /  |  _  /  | /      \\ /  \\  /  |\n" +
                "                  $$  \\  /$$//$$$$$$  |$$ |  $$ |      $$ | / \\ $$ |/$$$$$$  |$$  \\ $$ |\n" +
                "                   $$  \\/$$/ $$ |  $$ |$$ |  $$ |      $$ |/$  \\$$ |$$ |  $$ |$$$  \\$$ |\n" +
                "                    $$  $$/  $$ |  $$ |$$ |  $$ |      $$ /$$$  $$ |$$ |  $$ |$$$$  $$ |\n" +
                "                     $$$$/   $$ |  $$ |$$ |  $$ |      $$ $$/$$ $$ |$$ |  $$ |$$ $$ $$ |\n" +
                "                      $$ |   $$ \\__$$ |$$ \\__$$ |      $$$$/  $$$$ |$$ \\__$$ |$$ |$$$$ |\n" +
                "                      $$ |   $$    $$/ $$    $$/       $$$/    $$$ |$$    $$/ $$ | $$$ |\n" +
                "                      $$/     $$$$$$/   $$$$$$/        $$/      $$/  $$$$$$/  $$/   $$/ \n";
        String youLost = "                   __      __  ______   __    __        __         ______    ______   ________ \n" +
                "                  /  \\    /  |/      \\ /  |  /  |      /  |       /      \\  /      \\ /        |\n" +
                "                  $$  \\  /$$//$$$$$$  |$$ |  $$ |      $$ |      /$$$$$$  |/$$$$$$  |$$$$$$$$/ \n" +
                "                   $$  \\/$$/ $$ |  $$ |$$ |  $$ |      $$ |      $$ |  $$ |$$ \\__$$/    $$ |   \n" +
                "                    $$  $$/  $$ |  $$ |$$ |  $$ |      $$ |      $$ |  $$ |$$      \\    $$ |   \n" +
                "                     $$$$/   $$ |  $$ |$$ |  $$ |      $$ |      $$ |  $$ | $$$$$$  |   $$ |   \n" +
                "                      $$ |   $$ \\__$$ |$$ \\__$$ |      $$ |_____ $$ \\__$$ |/  \\__$$ |   $$ |   \n" +
                "                      $$ |   $$    $$/ $$    $$/       $$       |$$    $$/ $$    $$/    $$ |   \n" +
                "                      $$/     $$$$$$/   $$$$$$/        $$$$$$$$/  $$$$$$/   $$$$$$/     $$/    \n";
        System.out.println("\n" + color + ANSI_BOLD + (win ? youWon : youLost) + ANSI_BOLD_RESET + ANSI_RESET + "\n");
    }

    /**
     * This method prints the scoreboard for the tui showing the points of each player
     * @param players the arraylist of players playing the game
     */
    public static void scoreboardPrinter(ArrayList<Player> players) {
        int[] pos = new int[players.size()];
        String[] x = new String[players.size()];
        for(int i = 0; i < players.size(); i++) {
            pos[i] = players.get(i).getPoints();
            switch(players.get(i).getColor()) {
                case YELLOW:
                    x[i] = ANSI_YELLOW + "╳" + ANSI_RESET;
                    break;
                case GREEN:
                    x[i] = ANSI_GREEN + "╳" + ANSI_RESET;
                    break;
                case RED:
                    x[i] = ANSI_RED + "╳" + ANSI_RESET;
                    break;
                case BLUE:
                    x[i] = ANSI_BLUE + "╳" + ANSI_RESET;
                    break;
                case NONE:
                default:
                    x[i] = "@";
            }
        }
        String[] lines = new String[11];
    //first row
        Arrays.fill(lines, "    ");
        lines[0] = lines[0].concat("╔═══════════════════════════════╣" + ANSI_BOLD + "SCOREBOARD" + ANSI_BOLD_RESET + "╠════════════════════════════════╗");
    //20s
        lines[1] = lines[1].concat("║   ");
        lines[2] = lines[2].concat("║ ╭─");
        lines[3] = lines[3].concat("║ │ ");
        for(int i = 20; i <= 29; i++) {
        //upper
            if(pos[0] == i)
                lines[1] = lines[1].concat("┌" + x[0]);
            else
                lines[1] = lines[1].concat("┌─");
            if(pos[1] == i)
                lines[1] = lines[1].concat("──" + x[1] + "┐ ");
            else
                lines[1] = lines[1].concat("───┐ ");
        //middle
            if(i != 29)
                lines[2] = lines[2].concat("┤ " + i + " ├─");
            else
                lines[2] = lines[2].concat("┤ 29 │");
        //lower
            if(players.size() >= 3 && pos[2] == i)
                lines[3] = lines[3].concat("└" + x[2]);
            else
                lines[3] = lines[3].concat("└─");
            if(players.size() >= 4 && pos[3] == i)
                lines[3] = lines[3].concat("──" + x[3] + "┘ ");
            else
                lines[3] = lines[3].concat("───┘ ");
        }
        lines[1] = lines[1].concat("  ║");
        lines[2] = lines[2].concat("   ║");
        lines[3] = lines[3].concat("  ║");
    //10s
        lines[4] = lines[4].concat("║ │ ");
        lines[5] = lines[5].concat("║ ╰─");
        lines[6] = lines[6].concat("║   ");
        for(int i = 19; i >= 10; i--) {
            //upper
            if(pos[0] == i)
                lines[4] = lines[4].concat("┌" + x[0]);
            else
                lines[4] = lines[4].concat("┌─");
            if(pos[1] == i)
                lines[4] = lines[4].concat("──" + x[1] + "┐ ");
            else
                lines[4] = lines[4].concat("───┐ ");
            //middle
            lines[5] = lines[5].concat("┤ " + i + " ├─");
            //lower
            if(players.size() >= 3 && pos[2] == i)
                lines[6] = lines[6].concat("└" + x[2]);
            else
                lines[6] = lines[6].concat("└─");
            if(players.size() >= 4 && pos[3] == i)
                lines[6] = lines[6].concat("──" + x[3] + "┘ ");
            else
                lines[6] = lines[6].concat("───┘ ");
        }
        lines[4] = lines[4].concat("  ║");
        lines[5] = lines[5].concat("╮ ║");
        lines[6] = lines[6].concat("│ ║");
    //0s
        lines[7] = lines[7].concat("║        ");
        lines[8] = lines[8].concat("║        ");
        lines[9] = lines[9].concat("║        ");
        for(int i = 0; i <= 9; i++) {
            //upper
            if(pos[0] == i)
                lines[7] = lines[7].concat("┌" + x[0]);
            else
                lines[7] = lines[7].concat("┌─");
            if(pos[1] == i)
                lines[7] = lines[7].concat("─" + x[1] + "┐ ");
            else
                lines[7] = lines[7].concat("──┐ ");
            //middle
            if(i != 0)
                lines[8] = lines[8].concat("┤ " + i + " ├─");
            else
                lines[8] = lines[8].concat("│ 0 ├─");
            //lower
            if(players.size() >= 3 && pos[2] == i)
                lines[9] = lines[9].concat("└" + x[2]);
            else
                lines[9] = lines[9].concat("└─");
            if(players.size() >= 4 && pos[3] == i)
                lines[9] = lines[9].concat("─" + x[3] + "┘ ");
            else
                lines[9] = lines[9].concat("──┘ ");
        }
        lines[7] = lines[7].concat("     │ ║");
        lines[8] = lines[8].concat("─────╯ ║");
        lines[9] = lines[9].concat("       ║");
    //last row
        lines[10] = lines[10].concat("╚═══════════════════════════════════════════════════════════════════════════╝");
    //names on the right
        for(int i = 0; i < players.size(); i++)
            lines[2+(i*2)] = lines[2+(i*2)].concat("   "+ x[i] + " " + players.get(i).getName() + ": " + pos[i] + "pts");
    //printing all together
        String result = "\n";
        for(int i = 0; i < 11; i++)
            result = result.concat(lines[i] + "\n");
        System.out.println(result);
    }

    /**
     * This method prints the scoreboard for the tui showing the points of each player
     * It's an overloading of the previous method, to use for clients that
     * save the own player in a different variable
     * @param players the arraylist of players playing the game
     * @param single player invoking the method
     */
    public static void scoreboardPrinter(ArrayList<Player> players, Player single) {
        ArrayList<Player> temp = new ArrayList<>();
        temp.add(single);
        temp.addAll(players);
        scoreboardPrinter(temp);
    }

}
