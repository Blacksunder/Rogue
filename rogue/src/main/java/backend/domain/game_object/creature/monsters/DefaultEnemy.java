package backend.domain.game_object.creature.monsters;

import backend.domain.game_object.creature.Creature;
import backend.domain.map.MapKeeper;
import backend.domain.util.ObjectPosition;
import backend.domain.util.CreatureStats;

public abstract class DefaultEnemy extends Enemy {
    private boolean isVisible = true;

    public DefaultEnemy(CreatureStats stats, ObjectPosition position, MapKeeper map, int hostility) {
        super(stats, position, map, hostility);
    }

    @Override
    public void specificMove() {
        super.moveToDir();

    }

    @Override
    public void onAttack(Creature other) {
        if (isInvisible()) {
            changeVisibility();
        }
    }

    @Override
    public void onDefense(Creature other) {
        defaultDefense(other);
    }

    public void changeVisibility() {
        isVisible = !isVisible;
    }

    public boolean isInvisible() {
        return !isVisible;
    }
}
