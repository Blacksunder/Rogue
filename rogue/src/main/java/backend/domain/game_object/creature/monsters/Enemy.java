package backend.domain.game_object.creature.monsters;

import backend.domain.game_object.creature.Creature;
import backend.domain.game_object.item.Item;
import backend.domain.map.Constants;
import backend.domain.map.MapKeeper;
import backend.domain.util.CreatureConstants;
import backend.domain.util.ObjectPosition;
import backend.domain.util.CreatureStats;
import backend.domain.wavealgo.WaveAlgo;

import java.util.Stack;

/**
 * Базовый класс реализующий логику, совпадающую для всех врагов
 */
public abstract class Enemy extends Creature {
    private final int hostility;
    private int award;

    public Enemy(CreatureStats stats, ObjectPosition position, MapKeeper map, int hostility) {
        super(stats, position, map);
        int tmpMaxHealth = map.getSessionRecord().getMaxLevel() > 3 ? CreatureConstants.getBonusStats(map) : 0;
        this.getBaseStats().plus(new CreatureStats(tmpMaxHealth, tmpMaxHealth, Constants.random(0, Math.min(map.getPlayer().getCurrentLevel(), 10)), Constants.random(0, Math.min(map.getPlayer().getCurrentLevel(), 10))));
        this.normalize();
        this.hostility = hostility;
        this.award = Constants.random(stats.getMaxHealth() + stats.getPower() * 10, stats.getMaxHealth() + stats.getPower() * 10 + stats.getAgility() * 5);
    }

    /**
     * Перемещение врага если он не заагрен
     */
    public abstract void specificMove();

    /**
     * Перемещение врага если он заагрен
     *
     * @param next_y Координаты следующей в кратчайшем пути клетки поля(для функции move())
     * @param next_x
     */
    public void
    specificMove(int next_y, int next_x) {

        super.getPosition().setX(next_x);
        super.getPosition().setY(next_y);
    }

    public void setAward(int award) {
        this.award = award;
    }

    @Override
    public void move() {
        WaveAlgo waveAlgo = new WaveAlgo(super.getMap().getField(), super.getPosition().getX(),
                super.getPosition().getY());
        try {
            Stack<int[]> stack = waveAlgo.findPath();
            if (stack.size() > hostility) {
                super.setDir(Constants.getRandomDirection());
                specificMove();
            } else {
                stack.pop();
                if (!stack.isEmpty()) {
                    int[] point;
                    point = stack.pop();
                    specificMove(point[0], point[1]);
                } else {
                    super.fight(getMap().getPlayer());
                }
            }
        } catch (IllegalAccessError e) {
            super.setDir(Constants.getRandomDirection());
            specificMove();
        }
    }

    public void onDeath() {
        getMap().getPlayer().addXp(award / 5);
        getMap().getPlayer().addAward(award);
    }

    public String toString() {
        return super.toString() + " Award: " + this.award + " " + getBaseStats();
    }

    /**
     * Функция, приводящая врага к нормальному виду
     */
    private void normalize() {
        if (getBaseStats().getMaxHealth() > CreatureConstants.EnemyMaxPossibleHealth) {
            getBaseStats().setMaxHealth(CreatureConstants.EnemyMaxPossibleHealth);
        }
        getBaseStats().setHP(getBaseStats().getMaxHealth());
        if (getBaseStats().getAgility() + getBaseStats().getPower() > CreatureConstants.MaxPossibleSumOfParams) {
            getBaseStats().setPower(CreatureConstants.MaxPossibleSumOfParams - getBaseStats().getAgility());
        }
    }
}
