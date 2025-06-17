package backend;

import backend.domain.game_object.creature.monsters.Enemy;
import backend.domain.game_object.creature.player.Player;
import backend.domain.game_object.item.Item;
import backend.domain.game_object.item.weapon.Weapon;
import backend.domain.map.FieldTypes;
import backend.domain.map.MapGenerator;
import backend.domain.map.MapKeeper;
import backend.domain.map.Room;
import backend.datalayer.*;
import backend.domain.util.Direction;
import backend.domain.logger.Logger;

import java.util.ArrayList;

public class Model {
    private MapKeeper mapKeeper;
    private MapGenerator mapGenerator;
    private SaveLoader fileLoader;
    private GameSession currentGame;
    private Logger logger;

    public Model() {
        mapKeeper = new MapKeeper();
        mapGenerator = new MapGenerator(mapKeeper);
        currentGame = new GameSession(mapKeeper);
        fileLoader = new SaveLoader(currentGame);
        logger = new Logger();
        mapKeeper.setLogger(logger);
    }

    public ArrayList<String> getTopRecords() {
        return fileLoader.getTopRecords();
    }

    public ArrayList<Enemy> getMonsters() {
        return mapKeeper.getMonsters();
    }

    public void makeStep() {
        logger.updateMessagesAge();
        logger.clearOldMessages();
        if (!mapKeeper.checkForStairs()) {
            mapKeeper.makeStep();
        } else {
            goToNextLevel();
            saveGame();
        }
    }

    public FieldTypes[][] getFieldToPrint() {
        return mapKeeper.getFieldToPrint();
    }

    public void movePlayer(Direction direction) {
        mapKeeper.movePlayer(direction);
    }

    public void saveGame() {
        fileLoader.save();
    }

    public boolean loadGame() {
        boolean success = fileLoader.load();
        if (success) {
            currentGame.setLoadedData(mapKeeper);
            mapKeeper.refreshMap();
            mapKeeper.giveLoggerToObjects();
        }
        return success;
    }

    public void createNewGame() {
        fileLoader.deleteSave();
        mapGenerator.createMap();
        currentGame.updateData(mapKeeper);
        mapKeeper.giveLoggerToObjects();
    }

    public void goToNextLevel() {
        mapGenerator.createMap();
        currentGame.updateData(mapKeeper);
        mapKeeper.giveLoggerToObjects();
    }


    public Player getPlayer() {
        return mapKeeper.getPlayer();
    }

    public ArrayList<String> getMessages() {
        return logger.getMessages();
    }

    public void addRecord() {
        fileLoader.saveRecord();
        fileLoader.deleteSave();
    }

    public boolean isVictory() {
        return currentGame.getSessionRecord().getMaxLevel() == 21;
    }

    public boolean isGameOver() {
        return currentGame.getPlayer().getStats().getHP() <= 0;
    }

    public void useItem(Item item) {
        item.onUse();
        mapKeeper.getPlayer().updateInventory();
    }

    public void setEmptyHands() {
        mapKeeper.getPlayer().setCurrentWeapon(null);
        logger.addMessage("Now you fight without any weapon");
    }

    public void nullSession() {
        mapKeeper.setPlayer(null);
        mapKeeper.setSessionRecord(new SessionRecord(0, 0, 0));
        logger.fullClear();
    }

    public int getDungeonLevel() {
        return mapKeeper.getSessionRecord().getMaxLevel();
    }
}
