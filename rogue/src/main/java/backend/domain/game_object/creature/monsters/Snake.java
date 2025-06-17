package backend.domain.game_object.creature.monsters;

import backend.domain.game_object.creature.Creature;
import backend.domain.map.Constants;
import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureConstants;
import backend.domain.util.ObjectPosition;
import backend.domain.util.CreatureStats;
import backend.domain.util.Modifiers;

import java.util.ArrayList;

public class Snake extends UnusualEnemy {
    private static final int snakeHostility = Modifiers.MEDIUM;
    private static final int snakeAgility = Modifiers.VERY_HIGH;
    private static final int snakePower = Modifiers.VERY_HIGH;
    private static final int snakeMaxHealth = CreatureConstants.EnemyHealthMultiplier * Modifiers.MEDIUM;
    private static final int chanceToSleep = 30;

    public Snake(ObjectPosition pos, MapKeeper map) {
        super(new CreatureStats(snakeMaxHealth, snakeMaxHealth, snakePower, snakeAgility), pos, map,
                snakeHostility);
        setName("Snake");
    }

    @Override
    public void onAttack(Creature other) {
        if (Constants.random(0, 100) < chanceToSleep) {
            getLogger().addMessage(getName() + " stunned " + other.getName());
            other.setStunned(true);
        }
    }

    @Override
    public void specificMove() {
        int[] tmp = getClosestRandomCell();
        specificMove(tmp[0], tmp[1]);
    }

}
