package backend.domain.map;

import backend.domain.game_object.creature.monsters.*;
import backend.domain.game_object.item.elixir.*;
import backend.domain.game_object.item.scroll.AgilityScroll;
import backend.domain.game_object.item.scroll.MaxHealthScroll;
import backend.domain.game_object.item.scroll.PowerScroll;
import backend.domain.game_object.item.scroll.Scroll;
import backend.domain.game_object.item.food.Apple;
import backend.domain.game_object.item.food.Burger;
import backend.domain.game_object.item.food.Food;
import backend.domain.game_object.item.food.Pizza;
import backend.domain.game_object.item.weapon.Axe;
import backend.domain.game_object.item.weapon.Katana;
import backend.domain.game_object.item.weapon.Sword;
import backend.domain.game_object.item.weapon.Weapon;
import backend.domain.util.*;

import java.util.ArrayList;
import java.util.List;

public class MapGenerator {
    private MapKeeper mapKeeper;
    
    public MapGenerator(MapKeeper mapKeeper) {
        this.mapKeeper = mapKeeper;
    }

    public void createMap() {
        generateMap();
        mapKeeper.refreshMap();
    }

    public void generateMap() {
        do {
            mapKeeper.clearMap();
            generateRoomsLayout();
            generateDoors();
            mapKeeper.setRails();
        } while (!generateRoads());
        chooseEndAndStartRooms();
        mapKeeper.getSessionRecord().setMaxLevel(mapKeeper.getSessionRecord().getMaxLevel() + 1);
        mapKeeper.fillField();
        generateEnemies();
        generateWeapons();
        generateElixirs();
        generateFood();
        generateScrolls();
        mapKeeper.fillMainItemList();
        mapKeeper.refreshMap();
        mapKeeper.getLogger().addMessage("Welcome to the level "
                + mapKeeper.getSessionRecord().getMaxLevel());
    }
    
    public void generateRoomsLayout() {
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int firstBorderX = x * Constants.SECTOR_WIDTH;
                int secondBorderX = firstBorderX + Constants.SECTOR_WIDTH - 1;
                int firstBorderY = y * Constants.SECTOR_HEIGHT;
                int secondBorderY = firstBorderY + Constants.SECTOR_HEIGHT - 1;
                int width = Constants.random(Constants.MIN_ROOM_WIDTH, Constants.MAX_ROOM_WIDTH);
                int height = Constants.random(Constants.MIN_ROOM_HEIGHT, Constants.MAX_ROOM_HEIGHT);
                int xAngle = Constants.random(firstBorderX + 2, secondBorderX - 2 - width);
                int yAngle = Constants.random(firstBorderY + 2, secondBorderY - 2 - height);
                ArrayList<DoorSide> possibleSides = new ArrayList<>();
                if (y != 0) {
                    possibleSides.add(DoorSide.UP);
                }
                if (y != 2) {
                    possibleSides.add(DoorSide.DOWN);
                }
                if (x != 0) {
                    possibleSides.add(DoorSide.LEFT);
                }
                if (x != 2) {
                    possibleSides.add(DoorSide.RIGHT);
                }
                boolean corner = (x == 0 && y == 0) || (x == 2 && y == 2) || (x == 0 && y == 2)
                        || (x == 2 && y == 0);
                mapKeeper.getRooms().add(new Room(width, height, xAngle, yAngle, possibleSides, corner));
            }
        }
    }

    public void generateDoors() {
        int roomNumber = 0;
        for (Room room : mapKeeper.getRooms()) {
            int doors = Constants.random(Constants.MIN_DOORS, Constants.MAX_DOORS);
            if (room.isCornerRoom()) {
                doors = 2;
            }
            ArrayList<DoorSide> sides = room.getSides();
            int sidesAmount = sides.size();
            for (int i = 0; i < sidesAmount - doors; ++i) {
                int side = Constants.random(0, sides.size() - 1);
                sides.remove(side);
            }
            for (DoorSide side : sides) {
                room.getDoors().add(createDoor(room, side, roomNumber));
            }
            ++roomNumber;
        }
    }

    public Door createDoor(Room room, DoorSide side, int roomNumber) {
        int xDoor = 0;
        int yDoor = 0;
        switch (side) {
            case UP:
                yDoor = room.getYAngle();
                xDoor = Constants.random(room.getXAngle() + 1, room.getXAngle() + room.getWidth() - 2);
                break;
            case DOWN:
                yDoor = room.getYAngle() + room.getHeight() - 1;
                xDoor = Constants.random(room.getXAngle() + 1, room.getXAngle() + room.getWidth() - 2);
                break;
            case LEFT:
                yDoor = Constants.random(room.getYAngle() + 1, room.getYAngle() + room.getHeight() - 2);
                xDoor = room.getXAngle();
                break;
            case RIGHT:
                yDoor = Constants.random(room.getYAngle() + 1, room.getYAngle() + room.getHeight() - 2);
                xDoor = room.getXAngle() + room.getWidth() - 1;
                break;
        }
        return new Door(xDoor, yDoor, side, roomNumber);
    }

    public boolean generateRoads() {
        for (Room room : mapKeeper.getRooms()) {
            for (Door door : room.getDoors()) {
                connectRoadsToRails(door);
            }
        }
        connectRoadsOnRails();
        boolean generated = checkForLoop();
        if (!generated) {
            generated = checkForConnect();
        }
        return generated;
    }

    public void connectRoadsToRails(Door door) {
        switch (door.getSide()) {
            case RIGHT, LEFT -> {
                int yRoad = door.getY();
                int xRoad = door.getX();
                if (door.getSide() == DoorSide.RIGHT) {
                    ++xRoad;
                } else {
                    --xRoad;
                }
                while (mapKeeper.getField()[yRoad][xRoad] != FieldTypes.RAILS &&
                        mapKeeper.getField()[yRoad][xRoad] != FieldTypes.ROAD) {
                    mapKeeper.getField()[yRoad][xRoad] = FieldTypes.ROAD;
                    mapKeeper.addRoadCeil(yRoad, xRoad);
                    if (door.getSide() == DoorSide.RIGHT) {
                        ++xRoad;
                    } else {
                        --xRoad;
                    }
                }
                mapKeeper.getField()[yRoad][xRoad] = FieldTypes.ROAD;
                mapKeeper.addRoadCeil(yRoad, xRoad);
            } case UP, DOWN -> {
                int yRoad = door.getY();
                int xRoad = door.getX();
                if (door.getSide() == DoorSide.DOWN) {
                    ++yRoad;
                } else {
                    --yRoad;
                }
                while (mapKeeper.getField()[yRoad][xRoad] != FieldTypes.RAILS &&
                        mapKeeper.getField()[yRoad][xRoad] != FieldTypes.ROAD) {
                    mapKeeper.getField()[yRoad][xRoad] = FieldTypes.ROAD;
                    mapKeeper.addRoadCeil(yRoad, xRoad);
                    if (door.getSide() == DoorSide.DOWN) {
                        ++yRoad;
                    } else {
                        --yRoad;
                    }
                }
                mapKeeper.getField()[yRoad][xRoad] = FieldTypes.ROAD;
                mapKeeper.addRoadCeil(yRoad, xRoad);
            }
        }
    }

    public void connectRoadsOnRails() {
        boolean connectedFirstCol = connectVertical(Constants.SECTOR_WIDTH - 1);
        boolean connectedFirstLine = connectHorizontal(Constants.SECTOR_HEIGHT - 1);
        boolean connectedSecondCol = connectVertical(Constants.SECTOR_WIDTH * 2 - 1);
        boolean connectedSecondLine = connectHorizontal(Constants.SECTOR_HEIGHT * 2 - 1);
        if (!connectedFirstCol) {
            connectVerticalAgain(Constants.SECTOR_WIDTH - 1);
        }
        if (!connectedFirstLine) {
            connectHorizontalAgain(Constants.SECTOR_HEIGHT - 1);
        }
        if (!connectedSecondCol) {
            connectVerticalAgain(Constants.SECTOR_WIDTH * 2 - 1);
        }
        if (!connectedSecondLine) {
            connectHorizontalAgain(Constants.SECTOR_HEIGHT * 2 - 1);
        }

    }

    public boolean connectVertical(int x) {
        boolean connected = false;
        int firstRoadY = 0;
        int secondRoadY = 0;
        for (int i = 0; i < Constants.HEIGHT; ++i) {
            if (mapKeeper.getField()[i][x] == FieldTypes.ROAD && firstRoadY == 0) {
                firstRoadY = i;
            } else if (mapKeeper.getField()[i][x] == FieldTypes.ROAD && secondRoadY == 0) {
                secondRoadY = i;
            }
            if (firstRoadY != 0 && secondRoadY != 0) {
                for (int j = firstRoadY + 1; j < secondRoadY; ++j) {
                    mapKeeper.getField()[j][x] = FieldTypes.ROAD;
                    mapKeeper.addRoadCeil(j, x);
                }
                firstRoadY = secondRoadY = 0;
            }
        }
        if (firstRoadY == 0 && secondRoadY == 0) {
            connected = true;
        }
        return connected;
    }

    public boolean connectHorizontal(int y) {
        boolean connected = false;
        int firstRoadX = 0;
        int secondRoadX = 0;
        for (int i = 0; i < Constants.WIDTH; ++i) {
            if (mapKeeper.getField()[y][i] == FieldTypes.ROAD && firstRoadX == 0) {
                firstRoadX = i;
            } else if (mapKeeper.getField()[y][i] == FieldTypes.ROAD && secondRoadX == 0) {
                secondRoadX = i;
            }
            if (firstRoadX != 0 && secondRoadX != 0) {
                for (int j = firstRoadX + 1; j < secondRoadX; ++j) {
                    mapKeeper.getField()[y][j] = FieldTypes.ROAD;
                    mapKeeper.addRoadCeil(y, j);
                }
                firstRoadX = secondRoadX = 0;
            }
        }
        if (firstRoadX == 0 && secondRoadX == 0) {
            connected = true;
        }
        return connected;
    }

    public void connectVerticalAgain(int x) {
        int firstRoadY = 0;
        int secondRoadY = 0;
        for (int i = Constants.HEIGHT - 1; i >= 0; --i) {
            if (mapKeeper.getField()[i][x] == FieldTypes.ROAD && firstRoadY == 0) {
                firstRoadY = i;
            } else if (mapKeeper.getField()[i][x] == FieldTypes.ROAD && secondRoadY == 0) {
                secondRoadY = i;
            }
            if (firstRoadY != 0 && secondRoadY != 0) {
                for (int j = firstRoadY - 1; j >= secondRoadY; --j) {
                    mapKeeper.getField()[j][x] = FieldTypes.ROAD;
                    mapKeeper.addRoadCeil(j, x);
                }
                i = 0;
            }
        }
    }

    public void connectHorizontalAgain(int y) {
        int firstRoadX = 0;
        int secondRoadX = 0;
        for (int i = Constants.WIDTH - 1; i >= 0; --i) {
            if (mapKeeper.getField()[y][i] == FieldTypes.ROAD && firstRoadX == 0) {
                firstRoadX = i;
            } else if (mapKeeper.getField()[y][i] == FieldTypes.ROAD && secondRoadX == 0) {
                secondRoadX = i;
            }
            if (firstRoadX != 0 && secondRoadX != 0) {
                for (int j = firstRoadX - 1; j >= secondRoadX; --j) {
                    mapKeeper.getField()[y][j] = FieldTypes.ROAD;
                    mapKeeper.addRoadCeil(y, j);
                }
                i = 0;
            }
        }
    }

    public boolean checkForConnect() {
        ArrayList<Integer> connectedRooms = new ArrayList<>();
        connectedRooms.add(0);
        for (int i = 0; i < connectedRooms.size(); ++i) {
            for (Door door : mapKeeper.getRooms().get(connectedRooms.get(i)).getDoors()) {
                tryToConnectDoor(door, connectedRooms);
            }
        }
        List<Integer> sortedConnectedRooms = connectedRooms.stream().sorted().toList();
        boolean connected = sortedConnectedRooms.size() == 9;
        for (int i = 1; connected && i < sortedConnectedRooms.size(); ++i) {
            if (sortedConnectedRooms.get(i) != sortedConnectedRooms.get(i - 1) + 1) {
                connected = false;
            }
        }
        return connected;
    }

    public void tryToConnectDoor(Door door, ArrayList<Integer> connectedRooms) {
        int currentY = door.getY();
        int currentX = door.getX();
        int velocityY = 0;
        int velocityX = 0;
        switch (door.getSide()) {
            case LEFT -> velocityX = -1;
            case RIGHT -> velocityX = 1;
            case UP -> velocityY = -1;
            case DOWN -> velocityY = 1;
        }
        DoorSide currentDirection = door.getSide();
        currentY += velocityY * 2;
        currentX += velocityX * 2;
        move(currentY, currentX, velocityY, velocityX, currentDirection, connectedRooms);
    }

    public void move(int currentY, int currentX, int velocityY, int velocityX, DoorSide direction,
                     ArrayList<Integer> connectedRooms) {
        boolean crossRoad = false;
        while (scanDirections(currentY, currentX) != 1 && !crossRoad) {
            if (scanDirections(currentY, currentX) > 2) {
                turnUp(currentY, currentX, direction, connectedRooms);
                turnLeft(currentY, currentX, direction, connectedRooms);
                turnDown(currentY, currentX, direction, connectedRooms);
                turnRight(currentY, currentX, direction, connectedRooms);
                crossRoad = true;
            } else if (canMoveFurther(currentY, currentX, velocityY, velocityX)) {
                currentY += velocityY;
                currentX += velocityX;
            } else if (scanDirections(currentY, currentX) == 2) {
                direction = findDirection(currentY, currentX, direction);
                switch (direction) {
                    case UP -> {
                        velocityY = -1;
                        velocityX = 0;
                    }
                    case DOWN -> {
                        velocityY = 1;
                        velocityX = 0;
                    }
                    case LEFT -> {
                        velocityY = 0;
                        velocityX = -1;
                    }
                    case RIGHT -> {
                        velocityY = 0;
                        velocityX = 1;
                    }
                }
            }
        }
        if (!crossRoad && !connectedRooms.contains(defineRoomNumber(currentY, currentX))) {
            connectedRooms.add(defineRoomNumber(currentY, currentX));
        }
    }

    public boolean canMoveFurther(int y, int x, int velocityY, int velocityX) {
        return mapKeeper.getField()[y + velocityY][x + velocityX] == FieldTypes.ROAD;
    }

    public void turnLeft(int y, int x, DoorSide direction, ArrayList<Integer> connectedRooms) {
        if (direction != DoorSide.RIGHT && mapKeeper.getField()[y][x - 1] == FieldTypes.ROAD) {
            int velocityY = 0;
            int velocityX = -1;
            --x;
            move(y, x, velocityY, velocityX, DoorSide.LEFT, connectedRooms);
        }
    }

    public void turnRight(int y, int x, DoorSide direction, ArrayList<Integer> connectedRooms) {
        if (direction != DoorSide.LEFT && mapKeeper.getField()[y][x + 1] == FieldTypes.ROAD) {
            int velocityY = 0;
            int velocityX = 1;
            ++x;
            move(y, x, velocityY, velocityX, DoorSide.RIGHT, connectedRooms);
        }
    }

    public void turnUp(int y, int x, DoorSide direction, ArrayList<Integer> connectedRooms) {
        if (direction != DoorSide.DOWN && mapKeeper.getField()[y - 1][x] == FieldTypes.ROAD) {
            int velocityY = -1;
            int velocityX = 0;
            --y;
            move(y, x, velocityY, velocityX, DoorSide.UP, connectedRooms);
        }
    }

    public void turnDown(int y, int x, DoorSide direction, ArrayList<Integer> connectedRooms) {
        if (direction != DoorSide.UP && mapKeeper.getField()[y + 1][x] == FieldTypes.ROAD) {
            int velocityY = 1;
            int velocityX = 0;
            ++y;
            move(y, x, velocityY, velocityX, DoorSide.DOWN, connectedRooms);
        }
    }

    public DoorSide findDirection(int y, int x, DoorSide direction) {
        DoorSide foundDirection = direction;
        if (mapKeeper.getField()[y - 1][x] == FieldTypes.ROAD && direction != DoorSide.DOWN) {
            foundDirection = DoorSide.UP;
        } else if (mapKeeper.getField()[y + 1][x] == FieldTypes.ROAD && direction != DoorSide.UP) {
            foundDirection = DoorSide.DOWN;
        } else if (mapKeeper.getField()[y][x - 1] == FieldTypes.ROAD && direction != DoorSide.RIGHT) {
            foundDirection = DoorSide.LEFT;
        } else if (mapKeeper.getField()[y][x + 1] == FieldTypes.ROAD && direction != DoorSide.LEFT) {
            foundDirection = DoorSide.RIGHT;
        }
        return foundDirection;
    }

    public int scanDirections(int y, int x) {
        int directions = 0;
        for (int i = y - 1 ; i <= y + 1; ++i) {
            for (int j = x - 1; j <= x + 1; ++j) {
                if ((i == y || j == x) && mapKeeper.getField()[i][j] == FieldTypes.ROAD) {
                    ++directions;
                }
            }
        }
        return --directions;
    }

    public int defineRoomNumber(int y, int x) {
        y /= Constants.SECTOR_HEIGHT;
        x /= Constants.SECTOR_WIDTH;
        return x + y * 3;
    }
    
    public void chooseEndAndStartRooms() {
        ArrayList<Integer> rooms = new ArrayList<>();
        for (int i = 0; i < 9; ++i) {
            rooms.add(i);
        }
        int roomNumber = Constants.random(0, rooms.size() - 1);
        mapKeeper.getRooms().get(rooms.get(roomNumber)).setEndRoom(true);
        rooms.remove(rooms.get(roomNumber));
        roomNumber = Constants.random(0, rooms.size() - 1);
        mapKeeper.getRooms().get(rooms.get(roomNumber)).setStartRoom(true);
    }

    public boolean checkForLoop() {
        boolean loop = true;
        for (int y = Constants.SECTOR_HEIGHT - 1; loop && y <= Constants.SECTOR_HEIGHT * 2 - 1; ++y) {
            if (mapKeeper.getField()[y][Constants.SECTOR_WIDTH - 1] != FieldTypes.ROAD ||
                mapKeeper.getField()[y][Constants.SECTOR_WIDTH * 2 - 1] != FieldTypes.ROAD) {
                loop = false;
            }
        }
        for (int x = Constants.SECTOR_WIDTH - 1; loop && x <= Constants.SECTOR_WIDTH * 2 - 1; ++x) {
            if (mapKeeper.getField()[Constants.SECTOR_HEIGHT - 1][x] != FieldTypes.ROAD ||
                mapKeeper.getField()[Constants.SECTOR_HEIGHT * 2 - 1][x] != FieldTypes.ROAD) {
                loop = false;
            }
        }
        return loop;
    }

    public void generateEnemies() {
        int minNumber = (int)Math.sqrt(mapKeeper.getSessionRecord().getMaxLevel() * Constants.ENEMY_MIN_MODIFIER);
        int maxNumber = (int)Math.sqrt(mapKeeper.getSessionRecord().getMaxLevel() * Constants.ENEMY_MAX_MODIFIER);
        double numberOfEnemies = Constants.random(minNumber, maxNumber);
        numberOfEnemies *= countDifficulty();
        for (int i = 0; i < (int) numberOfEnemies; ++i) {
            int freeSpace = countFreeSpace();
            int positionNumber = Constants.random(0, freeSpace - 1);
            ObjectPosition position = setPosition(positionNumber);
            int enemyType = Constants.random(FieldTypes.ZOMBIE.ordinal(), FieldTypes.MIMIC.ordinal());
            Enemy monster = null;
            if (enemyType == FieldTypes.ZOMBIE.ordinal()) {
                monster = new Zombie(position, mapKeeper);
                mapKeeper.getField()[position.getY()][position.getX()] = FieldTypes.ZOMBIE;
            } else if (enemyType == FieldTypes.VAMPIRE.ordinal()) {
                monster = new Vampire(position, mapKeeper);
                mapKeeper.getField()[position.getY()][position.getX()] = FieldTypes.VAMPIRE;
            } else if (enemyType == FieldTypes.GHOST.ordinal()) {
                monster = new Ghost(position, mapKeeper, defineRoomNumber(position.getY(), position.getX()));
                mapKeeper.getField()[position.getY()][position.getX()] = FieldTypes.GHOST;
            } else if (enemyType == FieldTypes.OGRE.ordinal()) {
                monster = new Ogre(position, mapKeeper);
                mapKeeper.getField()[position.getY()][position.getX()] = FieldTypes.OGRE;
            } else if (enemyType == FieldTypes.SNAKE.ordinal()) {
                monster = new Snake(position, mapKeeper);
                mapKeeper.getField()[position.getY()][position.getX()] = FieldTypes.SNAKE;
            } else if (enemyType == FieldTypes.MIMIC.ordinal()) {
                monster = new Mimic(position, mapKeeper);
                mapKeeper.getField()[position.getY()][position.getX()] = FieldTypes.MIMIC;
            }
            mapKeeper.getMonsters().add(monster);
        }
    }

    public int countFreeSpace() {
        int space = 0;
        for (Room room : mapKeeper.getRooms()) {
            for (int y = room.getYAngle() + 1; !room.isStartRoom() &&
                    y < room.getYAngle() + room.getHeight() - 1; ++y) {
                for (int x = room.getXAngle() + 1; x < room.getXAngle() + room.getWidth() - 1; ++x) {
                    if (mapKeeper.getField()[y][x] == FieldTypes.ROOM_FLOOR) {
                        ++space;
                    }
                }
            }
        }
        return space;
    }

    public ObjectPosition setPosition(int positionNumber) {
        int ceilNumber = 0;
        int foundY = 0;
        int foundX = 0;
        boolean found = false;
        for (Room room : mapKeeper.getRooms()) {
            for (int y = room.getYAngle() + 1; !room.isStartRoom() && !found &&
                    y < room.getYAngle() + room.getHeight() - 1; ++y) {
                for (int x = room.getXAngle() + 1; !found &&
                        x < room.getXAngle() + room.getWidth() - 1; ++x) {
                    if (mapKeeper.getField()[y][x] == FieldTypes.ROOM_FLOOR
                            && ceilNumber != positionNumber) {
                        ++ceilNumber;
                    } else if (mapKeeper.getField()[y][x] == FieldTypes.ROOM_FLOOR) {
                        foundY = y;
                        foundX = x;
                        found = true;
                    }
                }
            }
        }
        return new ObjectPosition(foundX, foundY);
    }
    
    public void generateWeapons() {
        double numberOfWeapons = Constants.random(Constants.WEAPON_MIN_MODIFIER, Constants.WEAPON_MAX_MODIFIER);
        numberOfWeapons /= countDifficulty();
        for (int i = 0; i < (int) numberOfWeapons; ++i) {
            int space = countFreeSpace();
            int positionNumber = Constants.random(0, space - 1);
            ObjectPosition position = setPosition(positionNumber);
            int weaponType = Constants.random(ItemType.AXE.ordinal(), ItemType.SWORD.ordinal());
            Weapon weapon = null;
            if (weaponType == ItemType.AXE.ordinal()) {
                weapon = new Axe(position, mapKeeper, mapKeeper.getSessionRecord().getMaxLevel());
            } else if (weaponType == ItemType.KATANA.ordinal()) {
                weapon = new Katana(position, mapKeeper, mapKeeper.getSessionRecord().getMaxLevel());
            } else if (weaponType == ItemType.SWORD.ordinal()) {
                weapon = new Sword(position, mapKeeper, mapKeeper.getSessionRecord().getMaxLevel());
            }
            mapKeeper.getWeapons().add(weapon);
            mapKeeper.getField()[position.getY()][position.getX()] = FieldTypes.WEAPON;
        }
    }

    public void generateElixirs() {
        double numberOfElixirs = Constants.random(Constants.ELIXIR_MIN_MODIFIER, Constants.ELIXIR_MAX_MODIFIER);
        numberOfElixirs /= countDifficulty();
        for (int i = 0; i < (int) numberOfElixirs; ++i) {
            int space = countFreeSpace();
            int positionNumber = Constants.random(0, space - 1);
            ObjectPosition position = setPosition(positionNumber);
            int elixirType = Constants.random(ItemType.AGILITY_ELIXIR.ordinal(), ItemType.POWER_ELIXIR.ordinal());
            Elixir elixir = null;
            if (elixirType == ItemType.AGILITY_ELIXIR.ordinal()) {
                elixir = new AgilityElixir(position, mapKeeper, mapKeeper.getSessionRecord().getMaxLevel());
            } else if (elixirType == ItemType.HEALTH_ELIXIR.ordinal()) {
                elixir = new HealthElixir(position, mapKeeper, mapKeeper.getSessionRecord().getMaxLevel());
            } else if (elixirType == ItemType.MAX_HEALTH_ELIXIR.ordinal()) {
                elixir = new MaxHealthElixir(position, mapKeeper, mapKeeper.getSessionRecord().getMaxLevel());
            } else if (elixirType == ItemType.POWER_ELIXIR.ordinal()) {
                elixir = new PowerElixir(position, mapKeeper, mapKeeper.getSessionRecord().getMaxLevel());
            }
            mapKeeper.getElixirs().add(elixir);
            mapKeeper.getField()[position.getY()][position.getX()] = FieldTypes.ELIXIR;
        }
    }

    public void generateFood() {
        double numberOfFood = Constants.random(Constants.FOOD_MIN_MODIFIER, Constants.FOOD_MAX_MODIFIER);
        numberOfFood /= countDifficulty();
        for (int i = 0; i < (int) numberOfFood; ++i) {
            int space = countFreeSpace();
            int positionNumber = Constants.random(0, space - 1);
            ObjectPosition position = setPosition(positionNumber);
            int foodType = Constants.random(ItemType.APPLE.ordinal(), ItemType.PIZZA.ordinal());
            Food food = null;
            if (foodType == ItemType.APPLE.ordinal()) {
                food = new Apple(position, mapKeeper, mapKeeper.getSessionRecord().getMaxLevel());
            } else if (foodType == ItemType.BURGER.ordinal()) {
                food = new Burger(position, mapKeeper, mapKeeper.getSessionRecord().getMaxLevel());
            } else if (foodType == ItemType.PIZZA.ordinal()) {
                food = new Pizza(position, mapKeeper, mapKeeper.getSessionRecord().getMaxLevel());
            }
            mapKeeper.getFoods().add(food);
            mapKeeper.getField()[position.getY()][position.getX()] = FieldTypes.FOOD;
        }
    }

    public void generateScrolls() {
        double numberOfScrolls = Constants.random(Constants.SCROLL_MIN_MODIFIER, Constants.SCROLL_MAX_MODIFIER);
        numberOfScrolls /= countDifficulty();
        for (int i = 0; i < (int) numberOfScrolls; ++i) {
            int space = countFreeSpace();
            int positionNumber = Constants.random(0, space - 1);
            ObjectPosition position = setPosition(positionNumber);
            int scrollType = Constants.random(ItemType.AGILITY_SCROLL.ordinal(), ItemType.POWER_SCROLL.ordinal());
            Scroll scroll = null;
            if (scrollType == ItemType.AGILITY_SCROLL.ordinal()) {
                scroll = new AgilityScroll(position, mapKeeper, mapKeeper.getSessionRecord().getMaxLevel());
            } else if (scrollType == ItemType.MAX_HEALTH_SCROLL.ordinal()) {
                scroll = new MaxHealthScroll(position, mapKeeper, mapKeeper.getSessionRecord().getMaxLevel());
            } else if (scrollType == ItemType.POWER_SCROLL.ordinal()) {
                scroll = new PowerScroll(position, mapKeeper, mapKeeper.getSessionRecord().getMaxLevel());
            }
            mapKeeper.getScrolls().add(scroll);
            mapKeeper.getField()[position.getY()][position.getX()] = FieldTypes.SCROLL;
        }
    }

    public double countDifficulty() {
        int maxLevel = mapKeeper.getSessionRecord().getMaxLevel();
        int kills = mapKeeper.getSessionRecord().getKills();
        double killsExpected = (double) (maxLevel * maxLevel) / 3;
        if (maxLevel == 1) {
            kills = 1;
            killsExpected = 1;
        }
        double balance = killsExpected / kills;
        if (balance > 1.5) {
            balance = 1.5;
        } else if (balance < 0.5) {
            balance = 0.5;
        }
        return balance;
    }
}
