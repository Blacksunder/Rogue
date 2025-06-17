package backend.domain.util;

import backend.domain.map.Constants;

public final class CreatureStats {
    private int maxHealth = 0;
    private int hp = 0;
    private int power = 0;
    private int agility = 0;

    public CreatureStats() {

    }

    public CreatureStats(CreatureStats other) {
        maxHealth = other.maxHealth;
        hp = other.hp;
        power = other.power;
        agility = other.agility;
    }

    public CreatureStats(int max_health, int hp, int power, int agility) throws IllegalArgumentException {
        setMaxHealth(max_health);
        setHP(hp);
        setPower(power);
        setAgility(agility);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHP() {
        return hp;
    }

    public int getPower() {
        return power;
    }

    public int getAgility() {
        return agility;
    }

    public void setHP(int hp) {
        this.hp = Math.min(hp, maxHealth);
    }

    public void setMaxHealth(int maxHealth) throws IllegalArgumentException {
        if (maxHealth < 0) {
            throw new IllegalArgumentException();
        }
        this.maxHealth = maxHealth;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public void plus(CreatureStats other) {
        setAgility(getAgility() + other.getAgility());
        setPower(getPower() + other.getPower());
        setMaxHealth(getMaxHealth() + other.getMaxHealth());
        setHP(getHP() + other.getHP());
    }

    @Override
    public String toString() {
        String maxHealthString = (getMaxHealth() == 0) ? "" : "bonus max health: " + getMaxHealth() + " ";
        String hpString = getHP() == 0 ? "" : " bonus health: " + getHP() + " ";
        String powerString = getPower() == 0 ? "" : " bonus power: " + getPower() + " ";
        String agilityString = getAgility() == 0 ? "" : " bonus agility: " + getAgility() + " ";
        return maxHealthString + hpString + powerString + agilityString;
    }
}

