package backend.domain.game_object.creature.monsters;

import backend.domain.game_object.creature.Creature;
import backend.domain.map.MapKeeper;
import backend.domain.util.ObjectPosition;
import backend.domain.util.CreatureStats;

public abstract class UnusualEnemy extends Enemy {
    public UnusualEnemy(CreatureStats stats, ObjectPosition position, MapKeeper map,
                        int hostility) {
        super(stats, position, map, hostility);
    }

    @Override
    public void onAttack(Creature other) {
        return;
    }

    @Override
    public void onDefense(Creature other) {
        defaultDefense(other);
    }
}
