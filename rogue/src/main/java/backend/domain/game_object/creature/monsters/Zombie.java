package backend.domain.game_object.creature.monsters;

import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureConstants;
import backend.domain.util.ObjectPosition;
import backend.domain.util.CreatureStats;
import backend.domain.util.Modifiers;


public class Zombie extends DefaultEnemy {
    private static final int zombieHostility = Modifiers.MEDIUM;
    private static final int zombieAgility = Modifiers.LOW;
    private static final int zombiePower = Modifiers.MEDIUM;
    private static final int zombieMaxHealth = CreatureConstants.EnemyHealthMultiplier * Modifiers.HIGH;

    public Zombie(ObjectPosition position, MapKeeper map) {
        super(new CreatureStats(zombieMaxHealth, zombieMaxHealth, zombiePower, zombieAgility),
                position, map, zombieHostility);
        setName("Zombie");
    }
}
