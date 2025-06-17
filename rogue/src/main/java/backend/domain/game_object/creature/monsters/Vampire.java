package backend.domain.game_object.creature.monsters;

import backend.domain.game_object.creature.Creature;
import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureConstants;
import backend.domain.util.ObjectPosition;
import backend.domain.util.CreatureStats;
import backend.domain.util.Modifiers;

public class Vampire extends DefaultEnemy {
    private static final int vampireHostility = Modifiers.HIGH;
    private static final int vampireAgility = Modifiers.HIGH;
    private static final int vampirePower = Modifiers.MEDIUM;
    private static final int vampireMaxHealth = CreatureConstants.EnemyHealthMultiplier * Modifiers.HIGH;
    private boolean wasAttacked = false;

    public Vampire(ObjectPosition position, MapKeeper map) {
        super(new CreatureStats(vampireMaxHealth, vampireMaxHealth, vampirePower, vampireAgility),
                position, map, vampireHostility);
        setName("Vampire");
    }

    @Override
    public void onDefense(Creature other) {
        if (!wasAttacked) {
            wasAttacked = true;
        } else {
            super.defaultDefense(other);
        }
    }

    @Override
    public void onAttack(Creature other) {
        if (other.getBaseStats().getMaxHealth() >= 3) {
            other.getBaseStats().setMaxHealth(other.getStats().getMaxHealth() - 2);
        }
    }
}
