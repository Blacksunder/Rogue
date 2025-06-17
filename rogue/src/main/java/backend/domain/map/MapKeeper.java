package backend.domain.map;

import backend.datalayer.SessionRecord;
import backend.domain.game_object.GameObject;
import backend.domain.game_object.creature.Creature;
import backend.domain.game_object.creature.monsters.Enemy;
import backend.domain.game_object.creature.monsters.Ghost;
import backend.domain.game_object.creature.player.Player;
import backend.domain.game_object.item.Item;
import backend.domain.game_object.item.elixir.Elixir;
import backend.domain.game_object.item.scroll.Scroll;
import backend.domain.game_object.item.food.Food;
import backend.domain.game_object.item.weapon.Weapon;
import backend.domain.util.Direction;
import backend.domain.util.ObjectPosition;
import backend.domain.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class MapKeeper {
    private ArrayList<Room> rooms;
    private FieldTypes[][] field;
    private VisibilityType[][] visibilityField;
    private FieldTypes[][] fieldToPrint;
    private ArrayList<Enemy> monsters;
    private ArrayList<Weapon> weapons;
    private ArrayList<Elixir> elixirs;
    private ArrayList<Food> foods;
    private ArrayList<Scroll> scrolls;
    private ArrayList<Integer> yRoadCoords;
    private ArrayList<Integer> xRoadCoords;
    private Player player;
    private ArrayList<Item> items;
    private GameObject stairs;
    private SessionRecord sessionRecord;
    private transient Logger logger;

    public MapKeeper() {
        rooms = new ArrayList<>();
        field = new FieldTypes[Constants.HEIGHT][Constants.WIDTH];
        visibilityField = new VisibilityType[Constants.HEIGHT][Constants.WIDTH];
        for (int y = 0; y < Constants.HEIGHT; ++y) {
            for (int x = 0; x < Constants.WIDTH; ++x) {
                visibilityField[y][x] = VisibilityType.NOT_FOUND;
            }
        }
        fieldToPrint = new FieldTypes[Constants.HEIGHT][Constants.WIDTH];
        monsters = new ArrayList<>();
        weapons = new ArrayList<>();
        elixirs = new ArrayList<>();
        foods = new ArrayList<>();
        scrolls = new ArrayList<>();
        items = new ArrayList<>();
        yRoadCoords = new ArrayList<>();
        xRoadCoords = new ArrayList<>();
        sessionRecord = new SessionRecord(0, 0, 0);
    }

    public FieldTypes[][] getField() {
        return field;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public ArrayList<Enemy> getMonsters() {
        return monsters;
    }

    public void setMonsters(ArrayList<Enemy> monsters) {
        this.monsters = monsters;
    }

    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(ArrayList<Weapon> weapons) {
        this.weapons = weapons;
    }

    public ArrayList<Elixir> getElixirs() {
        return elixirs;
    }

    public void setElixirs(ArrayList<Elixir> elixirs) {
        this.elixirs = elixirs;
    }

    public ArrayList<Food> getFoods() {
        return foods;
    }

    public void setFoods(ArrayList<Food> foods) {
        this.foods = foods;
    }

    public ArrayList<Scroll> getScrolls() {
        return scrolls;
    }

    public void setScrolls(ArrayList<Scroll> scrolls) {
        this.scrolls = scrolls;
    }

    public FieldTypes[][] getFieldToPrint() {
        fillFieldToPrint();
        return fieldToPrint;
    }

    public ArrayList<Integer> getyRoadCoords() {
        return yRoadCoords;
    }

    public void setyRoadCoords(ArrayList<Integer> yRoadCoords) {
        this.yRoadCoords = yRoadCoords;
    }

    public ArrayList<Integer> getxRoadCoords() {
        return xRoadCoords;
    }

    public void setxRoadCoords(ArrayList<Integer> xRoadCoords) {
        this.xRoadCoords = xRoadCoords;
    }

    public VisibilityType[][] getVisibilityField() {
        return visibilityField;
    }

    public void setVisibilityField(VisibilityType[][] visibilityField) {
        this.visibilityField = visibilityField;
    }

    public SessionRecord getSessionRecord() {
        return sessionRecord;
    }

    public void setSessionRecord(SessionRecord sessionRecord) {
        this.sessionRecord = sessionRecord;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public GameObject getStairs() {
        return stairs;
    }

    public void setStairs(GameObject stairs) {
        this.stairs = stairs;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public void giveLoggerToObjects() {
        items.forEach(x -> x.setLogger(logger));
        monsters.forEach(x->x.setLogger(logger));
        player.setLogger(logger);
        player.getInventory().setLogger(logger);
    }

    public void movePlayer(Direction direction) {
        player.setDir(direction);
        player.move();
    }

    public void addRoadCeil(int yRoad, int xRoad) {
        yRoadCoords.add(yRoad);
        xRoadCoords.add(xRoad);
    }

    public void fillField() {
        convertRoomsToField();
        addStairs();
        addStartPlayerPosition();
    }

    public void convertRoomsToField() {
        int num = 0;
        for (Room room : rooms) {
            try {
                setRoomFloor(room);
                setWalls(room);
                setDoors(room);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Room " + num + " can't be set");
            }
            ++num;
        }
    }

    public void setWalls(Room room) {
        int x = room.getXAngle();
        int y = room.getYAngle();
        for ( ; y < room.getYAngle() + room.getHeight() - 1; ++y) {
            field[y][x] = FieldTypes.VERTICAL_WALL;
            field[y][x + room.getWidth() - 1] = FieldTypes.VERTICAL_WALL;
        }
        for ( ; x < room.getXAngle() + room.getWidth(); ++x) {
            field[y][x] = FieldTypes.HORIZONTAL_WALL;
            field[room.getYAngle()][x] = FieldTypes.HORIZONTAL_WALL;
        }
        field[room.getYAngle()][room.getXAngle()] = FieldTypes.LEFT_UP_CORNER;
        field[room.getYAngle()][room.getXAngle() + room.getWidth() - 1] = FieldTypes.RIGHT_UP_CORNER;
        field[room.getYAngle() + room.getHeight() - 1][room.getXAngle()] = FieldTypes.LEFT_DOWN_CORNER;
        field[room.getYAngle() + room.getHeight() - 1][room.getXAngle() + room.getWidth() - 1] =
                FieldTypes.RIGHT_DOWN_CORNER;
    }

    public void setRoomFloor(Room room) {
        for (int y = room.getYAngle() + 1; y < room.getHeight() + room.getYAngle() - 1; ++y) {
            for (int x = room.getXAngle() + 1; x < room.getWidth() + room.getXAngle() - 1; ++x) {
                field[y][x] = FieldTypes.ROOM_FLOOR;
            }
        }
    }

    public void setDoors(Room room) {
        for (Door door : room.getDoors()) {
            field[door.getY()][door.getX()] = FieldTypes.DOOR;
        }
    }

    public void setRails() {
        for (int x = 0; x < Constants.WIDTH - 1; ++x) {
            field[0][x] = field[Constants.SECTOR_HEIGHT - 1][x] =
                    field[Constants.SECTOR_HEIGHT * 2 - 1][x] =
                    field[Constants.SECTOR_HEIGHT * 3 - 1][x] = FieldTypes.RAILS;
        }
        for (int y = 0; y < Constants.HEIGHT; ++y) {
            field[y][0] = field[y][Constants.SECTOR_WIDTH - 1] =
                    field[y][Constants.SECTOR_WIDTH * 2 - 1] =
                    field[y][Constants.SECTOR_WIDTH * 3 - 1] = FieldTypes.RAILS;
        }
    }

    public void convertRoadsToField() {
        for (int i = 0; i < yRoadCoords.size(); ++i) {
            field[yRoadCoords.get(i)][xRoadCoords.get(i)] = FieldTypes.ROAD;
        }
    }

    public void convertMonstersToField() {
        for (Enemy monster : monsters) {
            int y = monster.getPosition().getY();
            int x = monster.getPosition().getX();
            switch (monster.getName()) {
                case "Zombie" -> field[y][x] = FieldTypes.ZOMBIE;
                case "Vampire" -> field[y][x] = FieldTypes.VAMPIRE;
                case "Ghost" -> field[y][x] = FieldTypes.GHOST;
                case "Ogre" -> field[y][x] = FieldTypes.OGRE;
                case "Snake" -> field[y][x] = FieldTypes.SNAKE;
                case "Mimic" -> field[y][x] = FieldTypes.MIMIC;
                case "Scroll" -> field[y][x] = FieldTypes.SCROLL;
                case "Elixir" -> field[y][x] = FieldTypes.ELIXIR;
                case "Food" -> field[y][x] = FieldTypes.FOOD;
                case "Weapon" -> field[y][x] = FieldTypes.WEAPON;
            }
        }
    }

    public void convertWeaponsToField() {
        for (Weapon weapon : weapons) {
            if (weapon.getParent() == null) {
                field[weapon.getPosition().getY()][weapon.getPosition().getX()] = FieldTypes.WEAPON;
            }
        }
    }

    public void convertElixirsToField() {
        for (Elixir elixir : elixirs) {
            if (elixir.getParent() == null) {
                field[elixir.getPosition().getY()][elixir.getPosition().getX()] = FieldTypes.ELIXIR;
            }
        }
    }

    public void convertFoodsToField() {
        for (Food food : foods) {
            if (food.getParent() == null) {
                field[food.getPosition().getY()][food.getPosition().getX()] = FieldTypes.FOOD;
            }
        }
    }

    public void convertScrollsToField() {
        for (Scroll scroll : scrolls) {
            if (scroll.getParent() == null) {
                field[scroll.getPosition().getY()][scroll.getPosition().getX()] = FieldTypes.SCROLL;
            }
        }
    }

    public void convertPlayerToField() {
        field[player.getPosition().getY()][player.getPosition().getX()] = FieldTypes.PLAYER;
    }

    public void convertStairsToField() {
        field[stairs.getPosition().getY()][stairs.getPosition().getX()] = FieldTypes.STAIRS;
    }

    public void clearMap() {
        for (Room room : rooms) {
            room.getDoors().clear();
            room.getSides().clear();
        }
        rooms.clear();
        monsters.clear();
        List<Weapon> weaponsToClear = weapons.stream().filter(x -> x.getParent() == null).toList();
        weapons.removeAll(weaponsToClear);
        List<Elixir> elixirsToCLear = elixirs.stream().filter(x -> x.getParent() == null).toList();
        elixirs.removeAll(elixirsToCLear);
        List<Food> foodsToClear = foods.stream().filter(x -> x.getParent() == null).toList();
        foods.removeAll(foodsToClear);
        List<Scroll> scrollsToClear = scrolls.stream().filter(x -> x.getParent() == null).toList();
        scrolls.removeAll(scrollsToClear);
        items.removeAll(weaponsToClear);
        items.removeAll(elixirsToCLear);
        items.removeAll(foodsToClear);
        items.removeAll(scrollsToClear);
        yRoadCoords.clear();
        xRoadCoords.clear();
        for (int y = 0; y < Constants.HEIGHT; ++y) {
            for (int x = 0; x < Constants.WIDTH; ++x) {
                field[y][x] = FieldTypes.EMPTY;
                visibilityField[y][x] = VisibilityType.NOT_FOUND;
            }
        }
    }

    public void addStairs() {
        int endRoomNumber = -1;
        int countRooms = 0;
        for (Room room : rooms) {
            if (room.isEndRoom()) {
                endRoomNumber = countRooms;
            }
            ++countRooms;
        }
        if (endRoomNumber >= 0) {
            int yStartFloor = rooms.get(endRoomNumber).getYAngle() + 1;
            int xStartFloor = rooms.get(endRoomNumber).getXAngle() + 1;
            int floorHeight = rooms.get(endRoomNumber).getHeight() - 2;
            int floorWidth = rooms.get(endRoomNumber).getWidth() - 2;
            int yStairs = yStartFloor + floorHeight / 2;
            int xStairs = xStartFloor + floorWidth / 2;
            stairs = new GameObject(new ObjectPosition(xStairs, yStairs));
            field[yStairs][xStairs] = FieldTypes.STAIRS;
        } else {
            System.out.println("End room can't be found");
        }
    }

    public void addStartPlayerPosition() {
        int startRoomNumber = -1;
        int countRooms = 0;
        for (Room room : rooms) {
            if (room.isStartRoom()) {
                startRoomNumber = countRooms;
            }
            ++countRooms;
        }
        if (startRoomNumber >= 0) {
            int yStartFloor = rooms.get(startRoomNumber).getYAngle() + 1;
            int xStartFloor = rooms.get(startRoomNumber).getXAngle() + 1;
            int floorHeight = rooms.get(startRoomNumber).getHeight() - 2;
            int floorWidth = rooms.get(startRoomNumber).getWidth() - 2;
            int yStart = yStartFloor + floorHeight / 2;
            int xStart = xStartFloor + floorWidth / 2;
            field[yStart][xStart] = FieldTypes.PLAYER;
            if (player == null) {
                player = new Player(new ObjectPosition(xStart, yStart),
                        this);
            } else {
                player.setPosition(new ObjectPosition(xStart, yStart));
            }
        } else {
            System.out.println("Start room can't be found");
        }
    }

    public void refreshMap() {
        for (int y = 0; y < Constants.HEIGHT; ++y) {
            for (int x = 0; x < Constants.WIDTH; ++x) {
                field[y][x] = FieldTypes.EMPTY;
            }
        }
        convertRoomsToField();
        convertRoadsToField();
        convertStairsToField();
        updateItemLists();
        convertWeaponsToField();
        convertElixirsToField();
        convertFoodsToField();
        convertScrollsToField();
        convertMonstersToField();
        convertPlayerToField();
    }

    public void updateVisibility() {
        hideField();
        discoverPlayerRoom();
        discoverCloseCeils();
    }

    public void discoverPlayerRoom() {
        boolean inRoom = false;
        for (Room room : rooms) {
            int yStart = room.getYAngle();
            int xStart = room.getXAngle();
            int yEnd = yStart + room.getHeight();
            int xEnd = xStart + room.getWidth();
            int yPlayer = player.getPosition().getY();
            int xPlayer = player.getPosition().getX();
            if (!inRoom && yPlayer >= yStart && yPlayer < yEnd && xPlayer >= xStart
                    && xPlayer < xEnd) {
                discoverRoom(room);
                inRoom = true;
            }
        }
    }

    public void hideField() {
        for (int y = 0; y < Constants.HEIGHT; ++y) {
            for (int x = 0; x < Constants.WIDTH; ++x) {
                if (visibilityField[y][x] == VisibilityType.CAN_SEE) {
                    visibilityField[y][x] = VisibilityType.FOUND;
                }
            }
        }
    }

    public void discoverRoom(Room room) {
        for (int y = room.getYAngle(); y < room.getYAngle() + room.getHeight(); ++y) {
            for (int x = room.getXAngle(); x < room.getXAngle() + room.getWidth(); ++x) {
                visibilityField[y][x] = VisibilityType.CAN_SEE;
            }
        }
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void discoverCloseCeils() {
        for (int y = player.getPosition().getY() - 1; y <= player.getPosition().getY() + 1; ++y) {
            for (int x = player.getPosition().getX() - 1; x <= player.getPosition().getX() + 1; ++x) {
                if ((y == player.getPosition().getY() || x == player.getPosition().getX()) &&
                        canBeDiscovered(y, x) && visibilityField[y][x] != VisibilityType.CAN_SEE) {
                    visibilityField[y][x] = VisibilityType.CAN_SEE;                    
                }
            }
        }
    }
    
    public boolean canBeDiscovered(int y, int x) {
        boolean canBeDiscovered = false;
        for (int i = 0; !canBeDiscovered && i < xRoadCoords.size(); ++i) {
            if (y == yRoadCoords.get(i) && x == xRoadCoords.get(i)) {
                canBeDiscovered = true;
            }
        }
        for (int i = 0; !canBeDiscovered && i < rooms.size(); ++i) {
            for (int j = 0; !canBeDiscovered && j < rooms.get(i).getDoors().size(); ++j) {
                Door door = rooms.get(i).getDoors().get(j);
                if (y == door.getY() && x == door.getX()) {
                    canBeDiscovered = true;
                }
            }
        }
        return canBeDiscovered;
    }

    public void fillFieldToPrint() {
        updateVisibility();
        for (int y = 0; y < Constants.HEIGHT; ++y) {
            for (int x = 0; x < Constants.WIDTH; ++x) {
                fieldToPrint[y][x] = getTypeForCeil(y, x);
            }
        }
    }

    public FieldTypes getTypeForCeil(int y, int x) {
        FieldTypes type = FieldTypes.EMPTY;
        boolean set = false;
        if (visibilityField[y][x] == VisibilityType.NOT_FOUND) {
            set = true;
        } else if (field[y][x] == FieldTypes.GHOST &&
                visibilityField[y][x] == VisibilityType.CAN_SEE) {
            if (checkGhostVisibility(y, x)) {
                type = field[y][x];
            } else {
                type = defineLowerType(y, x);
            }
            set = true;
        } else if (visibilityField[y][x] == VisibilityType.CAN_SEE) {
            type = field[y][x];
            set = true;
        }
        for (int i = 0; !set && i < rooms.size(); ++i) {
            for (int j = 0; !set && j < rooms.get(i).getDoors().size(); ++j) {
                if (y == rooms.get(i).getDoors().get(j).getY() &&
                        x == rooms.get(i).getDoors().get(j).getX()) {
                    type = FieldTypes.DOOR;
                    set = true;
                }
            }
        }
        if (!set && visibilityField[y][x] == VisibilityType.FOUND &&
                (field[y][x] == FieldTypes.HORIZONTAL_WALL ||
                        field[y][x] == FieldTypes.VERTICAL_WALL ||
                        field[y][x] == FieldTypes.RIGHT_DOWN_CORNER ||
                        field[y][x] == FieldTypes.RIGHT_UP_CORNER ||
                        field[y][x] == FieldTypes.LEFT_DOWN_CORNER ||
                        field[y][x] == FieldTypes.LEFT_UP_CORNER)) {
            type = field[y][x];
            set = true;
        }
        for (int i = 0; !set && i < xRoadCoords.size(); ++i) {
            if (y == yRoadCoords.get(i) && x == xRoadCoords.get(i)) {
                type = FieldTypes.ROAD;
                set = true;
            }
        }
        return type;
    }

    public boolean checkGhostVisibility(int y, int x) {
        boolean isVisible = true;
        for (Enemy enemy : monsters) {
            if (enemy instanceof Ghost && enemy.getPosition().getY() == y &&
                enemy.getPosition().getX() == x) {
                Ghost monster = (Ghost) enemy;
                if (monster.isInvisible()) {
                    isVisible = false;
                }
            }
        }
        return isVisible;
    }

    public FieldTypes defineLowerType(int y, int x) {
        FieldTypes type = FieldTypes.ROOM_FLOOR;
        boolean found = false;
        for (int i = 0; !found && i < items.size(); ++i) {
            if (items.get(i).getPosition().getY() == y && items.get(i).getPosition().getX() == x) {
                if (items.get(i) instanceof Food) {
                    type = FieldTypes.FOOD;
                } else if (items.get(i) instanceof Weapon) {
                    type = FieldTypes.WEAPON;
                } else if (items.get(i) instanceof Elixir) {
                    type = FieldTypes.ELIXIR;
                } else if (items.get(i) instanceof Scroll) {
                    type = FieldTypes.SCROLL;
                }
                found = true;
            }
        }
        for (int i = 0; !found && i < xRoadCoords.size(); ++i) {
            if (xRoadCoords.get(i) == x && yRoadCoords.get(i) == y) {
                type = FieldTypes.ROAD;
                found = true;
            }
        }
        return type;
    }

    public Player getPlayer() { return player; }

    public void fillMainItemList() {
        items.addAll(weapons);
        items.addAll(foods);
        items.addAll(scrolls);
        items.addAll(elixirs);
    }

    public void updateItemLists() {
        List<Elixir> usedElixirs = elixirs.stream().filter(GameObject::isDead).toList();
        elixirs.removeAll(usedElixirs);
        List<Food> usedFoods = foods.stream().filter(GameObject::isDead).toList();
        foods.removeAll(usedFoods);
        List<Scroll> usedScrolls = scrolls.stream().filter(GameObject::isDead).toList();
        scrolls.removeAll(usedScrolls);
        items.removeAll(usedElixirs);
        items.removeAll(usedFoods);
        items.removeAll(usedScrolls);
    }

    public void makeStep() {
        List<Enemy> deadEnemies = monsters.stream().filter(Creature::isDead).toList();
        for (Enemy enemy : deadEnemies) {
            enemy.onDeath();
            monsters.remove(enemy);
            sessionRecord.setKills(sessionRecord.getKills() + 1);
            sessionRecord.setCoins(player.getTreasures());
            logger.addMessage(enemy.getName() + " is killed!");
        }
        refreshMap();
        for (Enemy monster : monsters) {
            monster.move();
            refreshMap();
        }
    }

    public boolean checkForStairs() {
       return player.getPosition().getX() == stairs.getPosition().getX() &&
               player.getPosition().getY() == stairs.getPosition().getY();
    }
}
