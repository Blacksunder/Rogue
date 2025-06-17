package backend.domain.map;

public class Door {
    private int x;
    private int y;
    private DoorSide side;
    private int roomNumber;

    public Door(int x, int y, DoorSide side, int roomNumber) {
        this.x = x;
        this.y = y;
        this.side = side;
        this.roomNumber = roomNumber;
    }

    public DoorSide getSide() {
        return side;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
