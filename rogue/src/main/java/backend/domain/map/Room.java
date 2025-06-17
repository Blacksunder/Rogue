package backend.domain.map;

import java.util.ArrayList;

public class Room {
    private int width;
    private int height;
    private int xAngle;
    private int yAngle;
    private ArrayList<Door> doors;
    private ArrayList<DoorSide> sides;
    private boolean cornerRoom;
    private boolean endRoom;
    private boolean startRoom;

    public Room(int width, int height, int xAngle, int yAngle, ArrayList<DoorSide> sides, boolean corner) {
        this.width = width;
        this.height = height;
        this.xAngle = xAngle;
        this.yAngle = yAngle;
        doors = new ArrayList<>();
        this.sides = sides;
        cornerRoom = corner;
        endRoom = false;
        startRoom = false;
    }

    public int getXAngle() {
        return xAngle;
    }

    public int getYAngle() {
        return yAngle;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Door> getDoors() {
        return doors;
    }

    public ArrayList<DoorSide> getSides() {
        return sides;
    }

    public boolean isCornerRoom() {
        return cornerRoom;
    }

    public boolean isEndRoom() {
        return endRoom;
    }

    public void setEndRoom(boolean endRoom) {
        this.endRoom = endRoom;
    }

    public boolean isStartRoom() {
        return startRoom;
    }

    public void setStartRoom(boolean startRoom) {
        this.startRoom = startRoom;
    }
}
