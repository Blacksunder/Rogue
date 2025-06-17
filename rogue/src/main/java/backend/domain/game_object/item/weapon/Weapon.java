package backend.domain.game_object.item.weapon;

import backend.domain.game_object.creature.player.Player;
import backend.domain.game_object.item.Item;
import backend.domain.map.MapKeeper;
import backend.domain.util.ObjectPosition;

public abstract class Weapon extends Item {
    private int power;
    private final int critical_chance;

    public Weapon(ObjectPosition position, MapKeeper map, int current_level, int critical_chance, int power) {
        super(position, map, current_level, null);
        this.critical_chance = critical_chance;
        this.power = power;
    }

    public abstract int onHit();

    @Override
    public void onUse() {
        if (getParent() != null && getParent() instanceof Player) {
            ((Player)getParent()).setCurrentWeapon(this);
            equipWeapon();
        }
        getLogger().addMessage("Now you are using " + getName());
    }

    protected void setPower(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }

    public int getCriticalChance() {
        return critical_chance;
    }

    @Override
    public String toString() {
        return super.toString() + " critical chance: " + getCriticalChance() + " power: " + getPower();
    }
}
