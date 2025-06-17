package backend.domain.map;

import backend.domain.util.Direction;

import java.util.Arrays;

public class Constants {
    public static final int WIDTH = 90;
    public static final int HEIGHT = 30;
    public static final int SECTOR_WIDTH = WIDTH / 3;
    public static final int SECTOR_HEIGHT = HEIGHT / 3;
    public static final int MIN_ROOM_WIDTH = 5; // can't be less than 5
    public static final int MIN_ROOM_HEIGHT = 5; // can't be less than 5
    public static final int MAX_ROOM_WIDTH = 20; // can't be more than (sector width - 4)
    public static final int MAX_ROOM_HEIGHT = 6; // can't be more than (sector height - 4)
    public static final int MIN_DOORS = 1;
    public static final int MAX_DOORS = 3;
    public static final int ENEMY_MAX_MODIFIER = 100;
    public static final int ENEMY_MIN_MODIFIER = 50;
    public static final int WEAPON_MAX_MODIFIER = 3;
    public static final int WEAPON_MIN_MODIFIER = 0;
    public static final int ELIXIR_MAX_MODIFIER = 5;
    public static final int ELIXIR_MIN_MODIFIER = 1;
    public static final int SCROLL_MAX_MODIFIER = 3;
    public static final int SCROLL_MIN_MODIFIER = 0;
    public static final int FOOD_MAX_MODIFIER = 5;
    public static final int FOOD_MIN_MODIFIER = 1;

    public static int random(int min, int max) {
        max -= min;
        ++max;
        return (int) (Math.random() * max) + min;
    }

    public static Direction getRandomDirection() {
        Direction dir = Direction.UP;
        switch (Constants.random(0, 3)) {
            case 1 -> dir = Direction.RIGHT;
            case 2 -> dir = Direction.DOWN;
            case 3 -> dir = Direction.LEFT;
        }
        return dir;
    }

    public static boolean isMovable(FieldTypes cell) {
        FieldTypes[] tmp = {FieldTypes.DOOR, FieldTypes.ROAD, FieldTypes.ROOM_FLOOR, FieldTypes.STAIRS};
        return Arrays.stream(tmp).toList().contains(cell) || isItem(cell);
    }

    public static boolean isMonster(FieldTypes cell) {
        FieldTypes[] tmp = {FieldTypes.OGRE, FieldTypes.ZOMBIE, FieldTypes.VAMPIRE, FieldTypes.GHOST, FieldTypes.SNAKE, FieldTypes.MIMIC};
        return Arrays.stream(tmp).toList().contains(cell);
    }

    public static boolean isItem(FieldTypes cell) {
        FieldTypes[] tmp = {FieldTypes.WEAPON, FieldTypes.SCROLL, FieldTypes.ELIXIR, FieldTypes.FOOD};
        return Arrays.stream(tmp).toList().contains(cell);
    }
    public static String getRandomName() {
        String[] names = {"Weapon", "Food", "Elixir", "Scroll"};
        return names[Constants.random(0, names.length - 1)];
    }
}
