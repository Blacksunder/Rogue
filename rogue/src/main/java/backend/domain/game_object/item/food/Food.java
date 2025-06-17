package backend.domain.game_object.item.food;

import backend.domain.game_object.item.Item;
import backend.domain.map.MapKeeper;
import backend.domain.util.ObjectPosition;

public class Food extends Item {
    private int hp;

    public Food(ObjectPosition position, MapKeeper map, int current_level) {
        super(position, map, current_level, null);
    }

    @Override
    public void onUse() {
        if (getParent() != null) {
            getParent().getBaseStats().setHP(getParent().getBaseStats().getHP()+hp);
        }
        setParent(null);
        setDead(true);
        getLogger().addMessage("You have eaten " + getName());
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    @Override
    public String toString() {
        return super.toString() + " bonus health: " + hp;
    }
}

