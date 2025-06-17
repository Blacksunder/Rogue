package backend.datalayer;

import backend.domain.game_object.creature.monsters.Enemy;
import backend.domain.game_object.item.Item;
import backend.domain.game_object.item.elixir.Elixir;
import backend.domain.game_object.item.scroll.Scroll;
import backend.domain.game_object.item.food.Food;
import backend.domain.game_object.item.weapon.Weapon;
import backend.domain.map.Constants;
import backend.domain.map.Door;
import backend.domain.map.Room;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SaveLoader {

    private GameSession gameSession;

    public SaveLoader(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public boolean load() {
        boolean canBeLoaded;
        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapterFactory(new EnemyAdapterFactory())
                .registerTypeAdapterFactory(new ItemAdapterFactory());
        Gson gson = builder.create();
        try (FileReader reader = new FileReader("save.json")) {
            GameSession loaded = gson.fromJson(reader, GameSession.class);
            gameSession.setSessionRecord(loaded.getSessionRecord());
            gameSession.setRooms(loaded.getRooms());
            gameSession.setVisibilityField(loaded.getVisibilityField());
            gameSession.setyRoadCoords(loaded.getyRoadCoords());
            gameSession.setxRoadCoords(loaded.getxRoadCoords());
            gameSession.setMonsters(loaded.getMonsters());
            gameSession.setWeapons(loaded.getWeapons());
            gameSession.setElixirs(loaded.getElixirs());
            gameSession.setFoods(loaded.getFoods());
            gameSession.setScrolls(loaded.getScrolls());
            gameSession.setPlayer(loaded.getPlayer());
            gameSession.setStairs(loaded.getStairs());
            canBeLoaded = checkForCorrectLoading();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            canBeLoaded = false;
        }
        return canBeLoaded;
    }

    public void save() {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        try(FileWriter writer = new FileWriter("save.json")) {
            writer.write(gson.toJson(gameSession));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteSave() {
        try(FileWriter writer = new FileWriter("save.json")) {
            writer.write(" ");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean checkForCorrectLoading() {
        boolean correct;
        try {
            correct = checkNumParams() && checkRooms() && checkRoads() && checkVisibilityField() &&
                    checkEnemies() && checkItems() && checkPlayer() && checkStairs();
        } catch (Exception e) {
            System.out.println("Invalid file");
            correct = false;
        }
        return correct;
    }

    public boolean checkNumParams() {
        return gameSession.getSessionRecord().getCoins() >= 0 &&
                gameSession.getSessionRecord().getMaxLevel() >= 1 &&
                gameSession.getSessionRecord().getMaxLevel() <= 21 &&
                gameSession.getSessionRecord().getKills() >= 0;
    }

    public boolean checkRooms() {
        boolean correct = gameSession.getRooms().size() == 9;

        for (int n = 0; correct && n < gameSession.getRooms().size(); ++n) {
            Room room = gameSession.getRooms().get(n);
            correct = room.getYAngle() >= 0 && room.getYAngle() < Constants.HEIGHT &&
                    room.getXAngle() >= 0 && room.getXAngle() < Constants.WIDTH &&
                    room.getWidth() >= Constants.MIN_ROOM_WIDTH &&
                    room.getWidth() <= Constants.MAX_ROOM_WIDTH &&
                    room.getHeight() >= Constants.MIN_ROOM_HEIGHT &&
                    room.getHeight() <= Constants.MAX_ROOM_HEIGHT;
            for (int i = 0; correct && i < room.getDoors().size(); ++i) {
                Door door = room.getDoors().get(i);
                correct = door.getX() >= 0 && door.getX() < Constants.WIDTH && door.getY() >= 0 &&
                        door.getY() < Constants.HEIGHT;
            }
        }

        return correct;
    }

    public boolean checkVisibilityField() {
        boolean correct = gameSession.getVisibilityField().length == Constants.HEIGHT;
        for (int i = 0; correct && i < Constants.HEIGHT; ++i) {
            correct = gameSession.getVisibilityField()[i].length == Constants.WIDTH;
        }
        return correct;
    }

    public boolean checkRoads() {
        boolean correct = gameSession.getxRoadCoords().size() == gameSession.getyRoadCoords().size();
        for (int i = 0; correct && i < gameSession.getyRoadCoords().size(); ++i) {
            correct = gameSession.getyRoadCoords().get(i) >= 0 &&
                    gameSession.getyRoadCoords().get(i) < Constants.HEIGHT &&
                    gameSession.getxRoadCoords().get(i) >= 0 &&
                    gameSession.getxRoadCoords().get(i) < Constants.WIDTH;
        }
        return correct;
    }

    public boolean checkEnemies() {
        boolean correct = true;
        for (int i = 0; correct && i < gameSession.getMonsters().size(); ++i) {
            Enemy enemy = gameSession.getMonsters().get(i);
            correct = enemy.getPosition().getX() >= 0 && enemy.getPosition().getX() < Constants.WIDTH &&
                    enemy.getPosition().getY() >= 0 && enemy.getPosition().getY() < Constants.HEIGHT;
        }
        return correct;
    }

    public boolean checkItems() {
        boolean correct = true;
        for (int i = 0; correct && i < gameSession.getWeapons().size(); ++i) {
            Weapon weapon = gameSession.getWeapons().get(i);
            correct = weapon.getPosition().getX() >= 0 && weapon.getPosition().getX() < Constants.WIDTH &&
                    weapon.getPosition().getY() >= 0 && weapon.getPosition().getY() < Constants.HEIGHT;
        }
        for (int i = 0; correct && i < gameSession.getElixirs().size(); ++i) {
            Elixir elixir = gameSession.getElixirs().get(i);
            correct = elixir.getPosition().getX() >= 0 && elixir.getPosition().getX() < Constants.WIDTH &&
                    elixir.getPosition().getY() >= 0 && elixir.getPosition().getY() < Constants.HEIGHT;
        }
        for (int i = 0; correct && i < gameSession.getFoods().size(); ++i) {
            Food food = gameSession.getFoods().get(i);
            correct = food.getPosition().getX() >= 0 && food.getPosition().getX() < Constants.WIDTH &&
                    food.getPosition().getY() >= 0 && food.getPosition().getY() < Constants.HEIGHT;
        }
        for (int i = 0; correct && i < gameSession.getScrolls().size(); ++i) {
            Scroll scroll = gameSession.getScrolls().get(i);
            correct = scroll.getPosition().getX() >= 0 && scroll.getPosition().getX() < Constants.WIDTH &&
                    scroll.getPosition().getY() >= 0 && scroll.getPosition().getY() < Constants.HEIGHT;
        }
        return correct;
    }

    public boolean checkPlayer() {
        boolean correct = gameSession.getPlayer().getPosition().getX() >= 0 &&
                gameSession.getPlayer().getPosition().getX() < Constants.WIDTH &&
                gameSession.getPlayer().getPosition().getY() >= 0 &&
                gameSession.getPlayer().getPosition().getY() < Constants.HEIGHT &&
                gameSession.getPlayer().getXp() >= 0 && gameSession.getPlayer().getCurrentLevel() >= 1;
        for (int i = 0; correct && i < gameSession.getPlayer().getInventory().size(); ++i) {
            Item item = gameSession.getPlayer().getInventory().get(i);
            correct = item.getPosition().getX() >= 0 && item.getPosition().getX() < Constants.WIDTH &&
                    item.getPosition().getY() >= 0 && item.getPosition().getY() < Constants.HEIGHT;
        }
        return correct;
    }

    public boolean checkStairs() {
        return gameSession.getStairs().getPosition().getX() >= 0 &&
                gameSession.getStairs().getPosition().getX() < Constants.WIDTH &&
                gameSession.getStairs().getPosition().getY() >= 0 &&
                gameSession.getStairs().getPosition().getY() < Constants.HEIGHT;
    }

    public ArrayList<String> getTopRecords() {
        return loadRecordKeeper().getTopRecords();
    }

    public void saveRecord() {
        RecordKeeper recordKeeper = loadRecordKeeper();
        recordKeeper.addRecord(gameSession.getSessionRecord());
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        try(FileWriter writer = new FileWriter("records.json")) {
            gson.toJson(recordKeeper, writer);
        } catch (IOException e) {
            // invalid writing, ignored
        }
    }

    public RecordKeeper loadRecordKeeper() {
        RecordKeeper recordKeeper = new RecordKeeper();
        Gson gson = new Gson();
        try(FileReader reader = new FileReader("records.json")) {
            RecordKeeper loaded = gson.fromJson(reader, RecordKeeper.class);
            if (loaded != null) {
                recordKeeper = loaded;
            }
        } catch (IOException e) {
            // invalid read, ignored
        }
        return recordKeeper;
    }
}
