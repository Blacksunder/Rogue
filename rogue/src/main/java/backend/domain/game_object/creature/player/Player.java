package backend.domain.game_object.creature.player;

import backend.domain.game_object.creature.Creature;
import backend.domain.game_object.item.Item;
import backend.domain.game_object.item.weapon.Weapon;
import backend.domain.map.Constants;
import backend.domain.map.FieldTypes;
import backend.domain.map.MapKeeper;
import backend.domain.util.*;

import java.util.ArrayList;
import java.util.List;


public class Player extends Creature {
    Inventory inventory;
    private static final int defaultMaxHealth = CreatureConstants.PlayerHealthMultiplier * Modifiers.HIGH;
    private static final int defaultPower = Modifiers.HIGH;
    private static final int defaultAgility = Modifiers.HIGH;
    private int currentCounterattackChance = 0;
    transient private Weapon current_weapon = null;
    private int level = 1;
    private int xp = 0;
    private int treasures = 0;

    public Player(ObjectPosition position, MapKeeper map) {
        super(new CreatureStats(defaultMaxHealth, defaultMaxHealth, defaultPower, defaultAgility),
                position, map);
        inventory = new Inventory();
        setName("Player");
    }

    private void fightMonsterOnCoords(int finalY, int finalX) {
        fight(getMap().getMonsters().stream().
                filter(monster ->
                        monster.getPosition().getY() == finalY
                                && monster.getPosition().getX() == finalX).toList().get(0));

    }

    private void pickItemOnCoords(int finalY, int finalX) {
        List<Item> foundItem = getMap().getItems().stream().filter(item ->
                (item.getPosition().getY() == finalY
                        && item.getPosition().getX() == finalX)).toList();
        if (!foundItem.isEmpty()) {
            Item tmp = foundItem.get(0);
            if (inventory.add(tmp)) {
                tmp.onPick(this);
            }
        }
    }


    private boolean tryToAttackMonster() {
        Direction dir = getDir();
        int y = getPosition().getY();
        int x = getPosition().getX();
        boolean flag = false;
        switch (dir) {
            case UP:
                if ((y > 0) && (Constants.isMonster(getMap().getField()[y - 1][x]))) {
                    fightMonsterOnCoords(y - 1, x);
                    flag = true;
                }
                break;
            case DOWN:
                if ((y < Constants.HEIGHT - 1) && (Constants.isMonster(getMap().getField()[y + 1][x]))) {
                    fightMonsterOnCoords(y + 1, x);
                    flag = true;
                }
                break;
            case LEFT:
                if ((x > 0) && (Constants.isMonster(getMap().getField()[y][x - 1]))) {
                    fightMonsterOnCoords(y, x - 1);
                    flag = true;
                }
                break;
            case RIGHT:
                if ((x < Constants.WIDTH - 1) && (Constants.isMonster(getMap().getField()[y][x + 1]))) {
                    fightMonsterOnCoords(y, x + 1);
                    flag = true;
                }
                break;
        }
        return flag;
    }

    public void addXp(int xp) {
        this.xp += xp;
    }

    private boolean tryToPickItem() {
        Direction dir = getDir();
        int y = getPosition().getY();
        int x = getPosition().getX();
        boolean flag = false;
        switch (dir) {
            case UP:
                if ((y > 0) && (Constants.isItem(getMap().getField()[y - 1][x]))) {
                    pickItemOnCoords(y - 1, x);
                    flag = true;
                }
                break;
            case DOWN:
                if ((y < Constants.HEIGHT - 1) && (Constants.isItem(getMap().getField()[y + 1][x]))) {
                    pickItemOnCoords(y + 1, x);
                    flag = true;
                }
                break;
            case LEFT:
                if ((x > 0) && (Constants.isItem(getMap().getField()[y][x - 1]))) {
                    pickItemOnCoords(y, x - 1);
                    flag = true;
                }
                break;
            case RIGHT:
                if ((x < Constants.WIDTH - 1) && (Constants.isItem(getMap().getField()[y][x + 1]))) {
                    pickItemOnCoords(y, x + 1);
                    flag = true;
                }
                break;
        }
        return flag;
    }

    public void updateInventory() {
        if (!inventory.isEmpty()) {
            ArrayList<Item> tmp = getInventory().getInventory();
            for (Item item : tmp) {
                if (item.getParent() == null) inventory.delete(item);
                item.updateCoords();
            }
        }
        inventory.updateItems();
    }

    @Override
    public void move() {
        tickStats();
        if (isStunned()) {
            setStunned(false);
            return;
        }
        if (!tryToAttackMonster()) {
            if (tryToPickItem()) {
                getMap().refreshMap();
            }
            super.moveToDir();
            updateInventory();
        }
        updateLevel();
    }

    @Override
    public void onDefense(Creature other) {
        defaultDefense(other);
        if (Constants.random(0, 100) < currentCounterattackChance) {
            super.fight(other);
        }
    }

    public int getCurrentLevel() {
        return level;
    }

    public int getXp() {
        return this.xp;
    }

    public Inventory getInventory() {
        return inventory;
    }

    /**
     * При атаке добавляются статы текущего оружия
     *
     * @param other
     */
    public void onAttack(Creature other) {
        if (current_weapon != null) {
            addTempStats(new Pair<Integer, CreatureStats>(1,
                    new CreatureStats(0, 0, current_weapon.onHit(), 0)));
        }
    }

    private void updateLevel() {
        if (xp > getCurrentLevelMaxXP()) {
            xp -= getCurrentLevelMaxXP();
            level++;
            getBaseStats().setMaxHealth(getBaseStats().getMaxHealth() + 25);
            getBaseStats().setPower(getBaseStats().getPower() + 1);
            getBaseStats().setAgility(getBaseStats().getAgility() + 1);
        }
    }

    public void useItem(FieldTypes type, int i) {
        if (!inventory.isEmpty()) {
            Item tmp = inventory.get(type, i);
            if (tmp != null) {
                tmp.onUse();
            }
            updateLevel();
            updateInventory();
        }
    }

    public void setCurrentWeapon(Weapon item) {
        if (current_weapon != null) {
            current_weapon.unequipWeapon();
        }
        this.current_weapon = item;
    }

    public void setDir(Direction dir) {
        super.setDir(dir);
    }

    public void addAward(int award) {
        treasures += award;
    }

    public void throwWeapon(Item item) {
        if (item != null) {
            item.setParent(null);
            item.onThrow();
            if (current_weapon == item) {
                item.unequipWeapon();
                current_weapon = null;
                getLogger().addMessage("Now you fight without any weapon");
            }
        }
        updateInventory();
    }

    public void throwWeapon(FieldTypes type, int i) {
        Item tmp = inventory.get(type, i);
        throwWeapon(tmp);
    }

    public int getCurrentLevelMaxXP() {
        return level * CreatureConstants.PlayerXPRate;
    }

    public int getTreasures() {
        return treasures;
    }
}

