package MyMath;

import java.awt.*;

public class Modleling {
    public static Model rectangle(double h, double w, double l, MPoint3d pos, Color color) {
        double[][] vertices = {{-l/2 + pos.getY(), -w/2 + pos.getX(), pos.getZ()}, //0
                {-l/2 + pos.getY(), w/2 + pos.getX(), pos.getZ()},                 //1
                {l/2 + pos.getY(), w/2 + pos.getX(), pos.getZ()},                  //2
                {l/2 + pos.getY(), -w/2 + pos.getX(), pos.getZ()},                 //3
                {-l/2 + pos.getY(), -w/2 + pos.getX(), pos.getZ()+h},              //4
                {-l/2 + pos.getY(), w/2 + pos.getX(), pos.getZ()+h},               //5
                {l/2 + pos.getY(), w/2 + pos.getX(), pos.getZ()+h},                //6
                {l/2 + pos.getY(), -w/2 + pos.getX(), pos.getZ()+h}};              //7
        int[][] faces = {{0, 1, 2, 3}, {4, 5, 6, 7}, {0, 1, 5, 4}, {1, 2, 6, 5}, {2, 3, 7, 6}, {3, 0, 4, 7}};
        return new Model(vertices, faces,color);
    }
}
