package it.polimi.ingsw.model;

public class ResourceCard extends Card {
    ResourceCard(int points, Resource resource, Corner[] frontCorners, Corner[] backCorners) {
        this.points = points;
        this.resource = resource;
        for (int i = 0; i < frontCorners.length; i++) {
            this.frontCorners[i] = frontCorners[i];
        }
        for (int i = 0; i < backCorners.length; i++) {
            this.backCorners[i] = backCorners[i];
        }
    }
}
