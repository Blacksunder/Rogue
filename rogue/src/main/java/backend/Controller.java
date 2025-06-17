package backend;

import backend.domain.game_object.creature.monsters.Enemy;
import backend.domain.game_object.creature.player.Player;
import backend.domain.game_object.item.Item;
import backend.domain.game_object.item.weapon.Weapon;
import backend.domain.map.FieldTypes;
import backend.domain.map.Room;
import backend.domain.util.Direction;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    private Model model;

    public Controller(Model model) {
        this.model = model;
    }

    public List<String> getTopRecords() {
        return model.getTopRecords();
    }

    public ArrayList<Enemy> getMonsters() {
        return model.getMonsters();
    }

    public void makeStep() {
        model.makeStep();
    }

    public FieldTypes[][] getFieldToPrint() {
        return model.getFieldToPrint();
    }

    public void movePlayer(Direction direction) {
        model.movePlayer(direction);
    }

    public void saveGame() {
        model.saveGame();
    }

    public Player getPlayer() {
        return model.getPlayer();
    }

    public ArrayList<String> getMessages() {
        return model.getMessages();
    }

    public void addRecord() {
        model.addRecord();
    }

    public boolean isVictory() {
        return model.isVictory();
    }

    public boolean isGameOver() {
        return model.isGameOver();
    }

    public boolean loadGame() {
        return model.loadGame();
    }

    public void createNewGame() {
        model.createNewGame();
    }

    public void useItem(Item item) {
        model.useItem(item);
    }

    public void setEmptyHands() {
        model.setEmptyHands();
    }

    public void nullSession() {
        model.nullSession();
    }

    public int getDungeonLevel() {
        return model.getDungeonLevel();
    }
}
