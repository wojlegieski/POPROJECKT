package samochod;

public class Position {
    float x;
    float y;
    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public void movepolar(float x, float y) {
        this.x += Math.cos(x)*y;
        this.y += Math.sin(x)*y;
    }
    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "[x=" + x + ", y=" + y + "]";
    }

    public Position copy() {
        return new Position(x,y);
    }
}