package backend.domain.game_object.item.food;

import backend.domain.map.MapKeeper;
import backend.domain.util.ObjectPosition;

public class Burger extends Food{
    private final int default_hp = 15;

    public Burger(ObjectPosition position, MapKeeper map, int current_level) {
        super(position, map, current_level);
        setHp(default_hp * getQuantity() + current_level);
        setName("Burger");
    }
}
