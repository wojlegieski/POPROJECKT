package track;

import MyMath.MPoint3d;
import samochod.Position;

import java.awt.*;

public class Checkpoint {
    Position position;
    int leanth;
    int with;
    Color color;
    boolean drivedon;
    public Checkpoint(Position position, int leanth, int with, Color color) {
        this.position = position;
        this.leanth = leanth;
        this.with = with;
        this.color = color;
        drivedon = false;
    }
    public Checkpoint(Position position, int leanth, int with) {
        this.position = position;
        this.leanth = leanth;
        this.with = with;
        this.color = Color.cyan;
        drivedon = false;
    }
    public Position getPozycja() {
        return position;
    }
    public void isin(Position position) {
        if ((this.position.getX()-leanth/2< position.getX())&
                (this.position.getX()+leanth/2> position.getX())&
                (this.position.getY()-with/2< position.getY())&
                (this.position.getY()+with/2> position.getY())) {
            drivedon = true;
        }
    }
    public MPoint3d[] getPoints(){
        return new MPoint3d[]{new MPoint3d(position.getX()-leanth/2, position.getY()-with/2, 0),
                new MPoint3d(position.getX()+leanth/2, position.getY()-with/2, 0),
                new MPoint3d(position.getX()+leanth/2, position.getY()+with/2, 0),
                new MPoint3d(position.getX()-leanth/2, position.getY()+with/2, 0),};
    }
    public boolean drivedon() {
        return drivedon;
    }
    public Color getColor() {
        return color;
    }
    public void setwason(boolean was){
        drivedon=was;
    }
}
