package it.polimi.ingsw.model;

public class Space {
    public Card card;
    private boolean free;
    private boolean dead;
    private boolean checked;

    public Space(boolean free, boolean dead) {
        this.free = free;
        this.dead = dead;
        checked = false;
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
