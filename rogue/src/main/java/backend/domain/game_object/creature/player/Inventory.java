package backend.domain.game_object.creature.player;

import backend.domain.game_object.item.Item;
import backend.domain.game_object.item.elixir.Elixir;
import backend.domain.game_object.item.food.Food;
import backend.domain.game_object.item.scroll.Scroll;
import backend.domain.game_object.item.weapon.Weapon;
import backend.domain.logger.Logger;
import backend.domain.map.FieldTypes;

import java.util.AbstractList;
import java.util.ArrayList;

public class Inventory extends AbstractList<Item> {
    private final int max_size = 8;
    private final ArrayList<Weapon> weapons;
    private final ArrayList<Food> foods;
    private final ArrayList<Elixir> elixirs;
    private final ArrayList<Scroll> scrolls;
    private final ArrayList<Item> items;
    private transient Logger logger;

    public Inventory() {
        weapons = new ArrayList<>();
        foods = new ArrayList<>();
        elixirs = new ArrayList<>();
        scrolls = new ArrayList<>();
        items = new ArrayList<>();
    }

    public boolean add(Item item) {
        if ((item instanceof Weapon) && (weapons.size() < max_size)) {
            weapons.add((Weapon) item);
            return true;
        }
        if ((item instanceof Food) && (foods.size() < max_size)) {
            foods.add((Food) item);
            return true;
        }
        if ((item instanceof Elixir) && (elixirs.size() < max_size)) {
            elixirs.add((Elixir) item);
            return true;
        }
        if ((item instanceof Scroll) && (scrolls.size() < max_size)) {
            scrolls.add((Scroll) item);
            return true;
        }
        logger.addMessage("You have no free inventory slots!");
        return false;
    }

    public ArrayList<Item> getInventory() {
        updateItems();
        return items;
    }

    public void delete(Item item) {
        if ((item instanceof Weapon)) {
            weapons.remove((Weapon) item);
        } else if ((item instanceof Food)) {
            foods.remove((Food) item);
        } else if ((item instanceof Elixir)) {
            elixirs.remove((Elixir) item);
        } else if ((item instanceof Scroll)) {
            scrolls.remove((Scroll) item);
        }
    }

    public void updateItems() {
        if (!items.isEmpty()) {
            items.clear();
        }
        items.addAll(foods);
        items.addAll(weapons);
        items.addAll(elixirs);
        items.addAll(scrolls);
    }

    @Override
    public Item get(int index) {
        return getInventory().get(index);
    }

    public Item get(FieldTypes type, int i) {
        try {
            if (type == FieldTypes.WEAPON) {
                return weapons.get(i);
            }
            if (type == FieldTypes.ELIXIR) {
                return elixirs.get(i);
            }
            if (type == FieldTypes.SCROLL) {
                return scrolls.get(i);
            }
            if (type == FieldTypes.FOOD) {
                return foods.get(i);
            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        return null;
    }

    @Override
    public int size() {
        return getInventory().size();
    }

    public ArrayList<Elixir> getElixirs() {
        return elixirs;
    }

    public ArrayList<Food> getFoods() {
        return foods;
    }

    public ArrayList<Scroll> getScrolls() {
        return scrolls;
    }

    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}

