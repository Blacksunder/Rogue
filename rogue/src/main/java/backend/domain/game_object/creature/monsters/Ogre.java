package backend.domain.game_object.creature.monsters;

import backend.domain.game_object.creature.Creature;
import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureConstants;
import backend.domain.util.ObjectPosition;
import backend.domain.util.CreatureStats;
import backend.domain.util.Modifiers;

public class Ogre extends UnusualEnemy {
    private static final int ogreHostility = Modifiers.MEDIUM;
    private static final int ogreAgility = Modifiers.LOW;
    private static final int ogrePower = Modifiers.VERY_HIGH;
    private static final int ogreMaxHealth = CreatureConstants.EnemyHealthMultiplier * Modifiers.VERY_HIGH;
    private boolean counterAttacks = false;

    public Ogre(ObjectPosition pos, MapKeeper map) {
        super(new CreatureStats(ogreMaxHealth, ogreMaxHealth, ogrePower, ogreAgility), pos, map,
                ogreHostility);
        setName("Ogre");
    }

    public void onAttack(Creature other) {
        if (!isStunned()) {
            setStunned(true);
        } else {
            setStunned(false);
            counterAttacks = true;
        }
    }

    @Override
    public void onDefense(Creature other) {
        super.defaultDefense(other);
        if (counterAttacks && !isStunned()) {
            fight(other);
            counterAttacks = false;
        }
    }

    @Override
    public void specificMove() {
        if (!isStunned()) {
            super.moveToDir();
            super.moveToDir();
            setStunned(true);
        } else {
            setStunned(false);
        }

    }

}
