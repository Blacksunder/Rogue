package backend.domain.game_object.item.weapon;

import backend.domain.map.Constants;
import backend.domain.map.MapKeeper;
import backend.domain.util.Modifiers;
import backend.domain.util.ObjectPosition;

public class Sword extends Weapon{
    private static final int critical_chance = 8;
    private static final int crit_modifier = 3;
    private static final int default_power = Modifiers.LOW;

    public Sword(ObjectPosition position, MapKeeper map, int current_level) {
        super(position, map, current_level, critical_chance, default_power);
        setPower(getQuantity() + default_power);
        setName("Sword");
    }

    public int onHit() {
        if (Constants.random(0, 100) < critical_chance) {
            return crit_modifier * getPower();
        }
        return getPower();
    }
}
