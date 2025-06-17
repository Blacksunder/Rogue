package backend.datalayer;

import backend.domain.game_object.GameObject;
import backend.domain.game_object.creature.monsters.Enemy;
import backend.domain.game_object.creature.player.Player;
import backend.domain.game_object.item.Item;
import backend.domain.game_object.item.elixir.Elixir;
import backend.domain.game_object.item.scroll.Scroll;
import backend.domain.game_object.item.food.Food;
import backend.domain.game_object.item.weapon.Weapon;
import backend.domain.map.*;

import java.util.ArrayList;
import java.util.List;

public class GameSession {
    private SessionRecord sessionRecord;
    private GameObject stairs;
    private ArrayList<Room> rooms;
    private ArrayList<Enemy> monsters;
    private ArrayList<Weapon> weapons;
    private ArrayList<Elixir> elixirs;
    private ArrayList<Food> foods;
    private ArrayList<Scroll> scrolls;
    private ArrayList<Integer> yRoadCoords;
    private ArrayList<Integer> xRoadCoords;
    private Player player;
    private VisibilityType[][] visibilityField;

    public GameSession(MapKeeper mapKeeper) {
        updateData(mapKeeper);
    }

    public void updateData(MapKeeper mapKeeper) {
        rooms = mapKeeper.getRooms();
        monsters = mapKeeper.getMonsters();
        weapons = mapKeeper.getWeapons();
        elixirs = mapKeeper.getElixirs();
        foods = mapKeeper.getFoods();
        scrolls = mapKeeper.getScrolls();
        yRoadCoords = mapKeeper.getyRoadCoords();
        xRoadCoords = mapKeeper.getxRoadCoords();
        player = mapKeeper.getPlayer();
        visibilityField = mapKeeper.getVisibilityField();
        sessionRecord = mapKeeper.getSessionRecord();
        stairs = mapKeeper.getStairs();
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public void setyRoadCoords(ArrayList<Integer> yRoadCoords) {
        this.yRoadCoords = yRoadCoords;
    }

    public void setxRoadCoords(ArrayList<Integer> xRoadCoords) {
        this.xRoadCoords = xRoadCoords;
    }

    public void setVisibilityField(VisibilityType[][] visibilityField) {
        this.visibilityField = visibilityField;
    }

    public ArrayList<Integer> getyRoadCoords() {
        return yRoadCoords;
    }

    public ArrayList<Integer> getxRoadCoords() {
        return xRoadCoords;
    }

    public VisibilityType[][] getVisibilityField() {
        return visibilityField;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public SessionRecord getSessionRecord() {
        return sessionRecord;
    }

    public void setSessionRecord(SessionRecord sessionRecord) {
        this.sessionRecord = sessionRecord;
    }

    public void setLoadedData(MapKeeper mapKeeper) {
        mapKeeper.setSessionRecord(sessionRecord);
        mapKeeper.setStairs(stairs);
        mapKeeper.setRooms(rooms);
        mapKeeper.setyRoadCoords(yRoadCoords);
        mapKeeper.setxRoadCoords(xRoadCoords);
        mapKeeper.setVisibilityField(visibilityField);
        mapKeeper.setMonsters(monsters);

        List<Weapon> pickedWeapons = weapons.stream().filter(Item::isPicked).toList();
        weapons.removeAll(pickedWeapons);
        List<Elixir> pickedElixirs = elixirs.stream().filter(Item::isPicked).toList();
        elixirs.removeAll(pickedElixirs);
        List<Food> pickedFoods = foods.stream().filter(Item::isPicked).toList();
        foods.removeAll(pickedFoods);
        List<Scroll> pickedScrolls = scrolls.stream().filter(Item::isPicked).toList();
        scrolls.removeAll(pickedScrolls);

        mapKeeper.setPlayer(player);
        player.setMap(mapKeeper);
        player.getInventory().forEach(x -> x.setParent(player));
        player.getInventory().getWeapons().stream().filter(Item::isEquiped).
                forEach(x -> player.setCurrentWeapon(x));

        weapons.addAll(player.getInventory().getWeapons());
        elixirs.addAll(player.getInventory().getElixirs());
        foods.addAll(player.getInventory().getFoods());
        scrolls.addAll(player.getInventory().getScrolls());

        mapKeeper.setElixirs(elixirs);
        mapKeeper.setScrolls(scrolls);
        mapKeeper.setWeapons(weapons);
        mapKeeper.setFoods(foods);

        mapKeeper.getItems().clear();
        mapKeeper.getItems().addAll(weapons);
        mapKeeper.getItems().addAll(elixirs);
        mapKeeper.getItems().addAll(scrolls);
        mapKeeper.getItems().addAll(weapons);
        mapKeeper.getItems().addAll(foods);

        mapKeeper.getItems().forEach(x -> x.setMap(mapKeeper));
        mapKeeper.getMonsters().forEach(x -> x.setMap(mapKeeper));

        mapKeeper.refreshMap();
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

    public Player getPlayer() {
        return player;
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
}
