package core.domain;

public final class VillageParameters {
    private int happiness;
    private int protection;
    private int food;
    private int faith;
    private int housing;
    private int craftsmanship;

    public VillageParameters() {
        this(50, 0, 50, 0, 0, 0);
    }

    public VillageParameters(int happiness, int protection, int food, int faith, int housing, int craftsmanship) {
        this.happiness = clamp(happiness);
        this.protection = clamp(protection);
        this.food = clamp(food);
        this.faith = clamp(faith);
        this.housing = clamp(housing);
        this.craftsmanship = clamp(craftsmanship);
    }

    public int getHappiness() {
        return happiness;
    }

    public int getProtection() {
        return protection;
    }

    public int getFood() {
        return food;
    }

    public int getFaith() {
        return faith;
    }

    public int getHousing() {
        return housing;
    }

    public int getCraftsmanship() {
        return craftsmanship;
    }

    public void setHappiness(int happiness) {
        this.happiness = clamp(happiness);
    }

    public void setProtection(int protection) {
        this.protection = clamp(protection);
    }

    public void setFood(int food) {
        this.food = clamp(food);
    }

    public void setFaith(int faith) {
        this.faith = clamp(faith);
    }

    public void setHousing(int housing) {
        this.housing = clamp(housing);
    }

    public void setCraftsmanship(int craftsmanship) {
        this.craftsmanship = clamp(craftsmanship);
    }

    public VillageParameters copy() {
        return new VillageParameters(happiness, protection, food, faith, housing, craftsmanship);
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
