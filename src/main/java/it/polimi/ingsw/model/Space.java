package it.polimi.ingsw.model;

public class Space {
    private Card card;
    private boolean free;
    private boolean dead;
    private boolean checked;

    public Space(Card card, boolean free, boolean dead, boolean checked) {
        this.card = card;
        this.free = free;
        this.dead = dead;
        this.checked = checked;
    }

//GETTER
    public Card getCard() {
        return card;
    }

    public boolean isFree() {
        return free;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isChecked() {
        return checked;
    }

//SETTER

    public void setCard(Card card) {
        this.card = card;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
