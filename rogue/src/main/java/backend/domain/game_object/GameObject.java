package backend.domain.game_object;

import backend.domain.map.Constants;
import backend.domain.map.FieldTypes;
import backend.domain.map.MapKeeper;
import backend.domain.util.ObjectPosition;
import backend.domain.logger.Logger;

import java.util.ArrayList;

public class GameObject {
    private ObjectPosition position;
    private transient MapKeeper map;
    private String name;
    private boolean isDead = false;
    private transient Logger logger;

    public GameObject(ObjectPosition position) {
        this.position = position;
    }

    public GameObject(ObjectPosition position, MapKeeper map) {
        this.position = position;
        this.map = map;
        this.name = "";
    }

    public ObjectPosition getPosition() {
        return position;
    }

    public void setPosition(ObjectPosition position) {
        this.position = position;
    }

    protected MapKeeper getMap() {
        return map;
    }

    public void setMap(MapKeeper map) {
        this.map = map;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDead(boolean dead) {
        this.isDead = dead;
    }

    protected int[] getClosestRandomCell() {
        int[] arr = new int[]{-1, 1};
        ArrayList<int[]> a = new ArrayList<>();
        for (int i : arr) {
            for (int j : arr) {
                try {
                    if ((getMap().getField()[getPosition().getY() + i][getPosition().getX() + j] == FieldTypes.ROOM_FLOOR ||
                            getMap().getField()[getPosition().getY() + i][getPosition().getX() + j] == FieldTypes.ROAD)) {
                        a.add(new int[]{getPosition().getY() + i, getPosition().getX() + j});
                    }
                } catch (IndexOutOfBoundsException ignored) {

                }
            }
        }
        if (!a.isEmpty()) {
            return a.get(Constants.random(0, a.size() - 1));
        }
        return new int[]{getPosition().getY(), getPosition().getX()};
    }

    public boolean isDead() {
        return isDead;
    }

    public String getName() {
        return name;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }
}

