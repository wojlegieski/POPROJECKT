package MyMath;

import java.awt.*;

public class Modleling {
    public static Model rectangle(double h, double w, double l, MPoint3d pos, Color color) {
        double[][] vertices = {{-l/2 + pos.getY(), -w/2 + pos.getX(), pos.getZ()},
                {-l/2 + pos.getY(), w/2 + pos.getX(), pos.getZ()},
                {l/2 + pos.getY(), w/2 + pos.getX(), pos.getZ()},
                {l/2 + pos.getY(), -w/2 + pos.getX(), pos.getZ()},
                {-l/2 + pos.getY(), -w/2 + pos.getX(), pos.getZ()+h},
                {-l/2 + pos.getY(), w/2 + pos.getX(), pos.getZ()+h},
                {l/2 + pos.getY(), w/2 + pos.getX(), pos.getZ()+h},
                {l/2 + pos.getY(), -w/2 + pos.getX(), pos.getZ()+h}};
        int[][] faces = {{0, 1, 2, 3}, {4, 5, 6, 7}, {0, 1, 5, 4}, {1, 2, 6, 5}, {2, 3, 7, 6}, {3, 0, 4, 7}};
        return new Model(vertices, faces,color);
    }
}
