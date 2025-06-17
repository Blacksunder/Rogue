package backend.domain.game_object.creature.monsters;

import backend.domain.game_object.creature.Creature;
import backend.domain.map.Constants;
import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureConstants;
import backend.domain.util.CreatureStats;
import backend.domain.util.Modifiers;
import backend.domain.util.ObjectPosition;

import java.util.ArrayList;

public class Mimic extends DefaultEnemy {
    private static final int mimicHostility = Modifiers.LOW;
    private static final int mimicAgility = Modifiers.HIGH;
    private static final int mimicPower = Modifiers.LOW;
    private static final int mimicMaxHealth = CreatureConstants.EnemyHealthMultiplier * Modifiers.HIGH;

    public Mimic(ObjectPosition position, MapKeeper map) {
        super(new CreatureStats(mimicMaxHealth, mimicMaxHealth, mimicPower, mimicAgility),
                position, map, mimicHostility);
        setName(Constants.getRandomName());
    }

    private void changeNameIfNot() {
        if (!getName().contentEquals("Mimic")) {
            setName("Mimic");
        }
    }

    @Override
    public void specificMove() {
        return;
    }

    @Override
    public void specificMove(int next_y, int next_x) {
        changeNameIfNot();
        super.specificMove(next_y, next_x);
    }

    @Override
    public void onAttack(Creature other) {
        changeNameIfNot();
        super.onAttack(other);
    }
}
