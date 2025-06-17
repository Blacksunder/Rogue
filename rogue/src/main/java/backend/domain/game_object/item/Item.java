package backend.domain.game_object.item;

import backend.domain.game_object.GameObject;
import backend.domain.game_object.creature.Creature;
import backend.domain.map.Constants;
import backend.domain.map.MapKeeper;
import backend.domain.util.Modifiers;
import backend.domain.util.ObjectPosition;

public abstract class Item extends GameObject {
    private final int quantity;
    private final int default_luck = 1;
    private boolean isPicked = false;
    private boolean isEquiped = false;
    private transient Creature parent;

    public Item(ObjectPosition position, MapKeeper map, int current_level, Creature parent) {
        super(position, map);
        this.parent = parent;
        if (Constants.random(0, 100) < default_luck + map.getPlayer().getCurrentLevel() + map.getSessionRecord().getMaxLevel()) {
            quantity = Modifiers.VERY_HIGH;
        } else {
            quantity = Modifiers.LOW;
        }
    }

    public Creature getParent() {
        return parent;
    }

    public void setParent(Creature parent) {
        this.parent = parent;
    }

    public final int getQuantity() {
        return quantity;
    }

    public abstract void onUse();

    public void onPick(Creature other) {
        isPicked = true;
        this.parent = other;
        getLogger().addMessage("You've picked up " + getName());
    }

    public void updateCoords() {
        if (this.parent != null) {
            this.getPosition().setY(parent.getPosition().getY());
            this.getPosition().setX(parent.getPosition().getX());
        }
    }

    public boolean isPicked() {
        return isPicked;
    }

    public void onThrow() {
        isPicked = false;
    }

    @Override
    public String toString() {
        String quantityModifier = quantity == Modifiers.VERY_HIGH ? "Great" : "Medium";
        return quantityModifier + " " + getName();
    }

    public void unequipWeapon() {
        isEquiped = false;
    }

    public void equipWeapon() {
        isEquiped = true;
    }

    public boolean isEquiped() {
        return isEquiped;
    }
}

