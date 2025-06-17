package backend.domain.util;

import backend.domain.map.Constants;

public final class ObjectPosition {
    private int x;
    private int y;

    public ObjectPosition(int x, int y) throws IllegalArgumentException {
        setX(x);
        setY(y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public void setX(int x) throws IllegalArgumentException {
        if ((x > Constants.WIDTH) || (x < 0)) {
            throw new IllegalArgumentException();
        }
        this.x = x;
    }

    public void setY(int y) throws IllegalArgumentException {
        if ((y > Constants.HEIGHT) || (y < 0)) {
            throw new IllegalArgumentException();
        }
        this.y = y;
    }

}
