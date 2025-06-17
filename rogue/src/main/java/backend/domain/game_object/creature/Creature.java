package backend.domain.game_object.creature;

import backend.domain.game_object.GameObject;
import backend.domain.map.Constants;
import backend.domain.map.MapKeeper;
import backend.domain.util.*;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Базовый класс реализующий логику существа на карте
 */
public abstract class Creature extends GameObject {
    private final CreatureStats stats;
    private ArrayList<Pair<Integer, CreatureStats>> tempStats;
    private boolean stunned = false;
    private Direction dir;

    public Direction getDir() {
        return dir;
    }

    public Creature(CreatureStats stats, ObjectPosition position, MapKeeper map) {
        super(position, map);
        this.stats = stats;
        this.tempStats = new ArrayList<>();
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public CreatureStats getStats() {
        if (!isStunned()) {
            CreatureStats tmp = new CreatureStats(stats.getMaxHealth(), stats.getHP(), stats.getPower(), stats.getAgility());
            if (!tempStats.isEmpty()) {
                for (Pair<Integer, CreatureStats> temp : tempStats) {
                    tmp.plus(temp.getValue());
                }
            }
            return tmp;
        }
        else {
            return new CreatureStats(stats.getMaxHealth(), stats.getHP(), 0, 0);
        }
    }

    public CreatureStats getBaseStats() {
        return stats;
    }


    private int getEvadeChance() {
        int evadeChance = stats.getAgility() * 4;
        if (evadeChance > 50) evadeChance = 50;
        return evadeChance;
    }

    public int getDamage() {
        return getStats().getPower();
    }

    public void fight(Creature other) {
        if (other == null) {
            return;
        }
        if (other.getEvadeChance() < Constants.random(0, 100)) {
            getLogger().addMessage(getName() + " attacked " + other.getName());
            onAttack(other);
            other.onDefense(this);
        } else {
            getLogger().addMessage(getName() + " missed " + other.getName());
        }

    }

    public void tickStats() {
        Iterator<Pair<Integer, CreatureStats>> iterator = tempStats.iterator();
        while (iterator.hasNext()) {
            Pair<Integer, CreatureStats> temp = iterator.next();
            int newKey = temp.getKey() - 1;
            temp.setKey(newKey);
            if (newKey <= 0) {
                iterator.remove();
            }
        }
    }

    /**
     * Абстрактный метод по перемещению существа на карте
     */
    public abstract void move();

    public abstract void onAttack(Creature other);

    public abstract void onDefense(Creature other);

    public void defaultDefense(Creature other) {
        int hp = getBaseStats().getHP() - other.getDamage();
        getBaseStats().setHP(hp);
        setDead(getStats().getHP() < 0);
    }

    public boolean isStunned() {
        return stunned;
    }

    public CreatureStats getBonusStats() {
        CreatureStats tmp = new CreatureStats();
        if (!tempStats.isEmpty()) {
            for (Pair<Integer, CreatureStats> temp : tempStats) {
                tmp.plus(temp.getValue());
            }
        }
        return tmp;
    }

    protected void moveToDir() {
        Direction dir = getDir();
        int x = getPosition().getX();
        int y = getPosition().getY();
        switch (dir) {
            case UP:
                if ((y > 0) && (Constants.isMovable(getMap().getField()[y - 1][x]))) {
                    y--;
                }
                break;
            case DOWN:
                if ((y < Constants.HEIGHT) && (Constants.isMovable(getMap().getField()[y + 1][x]))) {
                    y++;
                }
                break;
            case LEFT:
                if ((x > 0) && (Constants.isMovable(getMap().getField()[y][x - 1]))) {
                    x--;
                }
                break;
            case RIGHT:
                if ((x < Constants.WIDTH) && (Constants.isMovable(getMap().getField()[y][x + 1]))) {
                    x++;
                }
                break;
        }
        getPosition().setY(y);
        getPosition().setX(x);
    }

    public void addTempStats(Pair<Integer, CreatureStats> temp) {
        this.tempStats.add(temp);
    }

    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }

    @Override
    public String toString() {
        return getName() + " " + "X " + getPosition().getX() + " Y " + getPosition().getY() + " HP " + getStats().getHP();
    }
}
