package MyMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;



public class Model {
    MWall[] walls;
    public Model(MWall[] walls) {
        this.walls = walls;
    }
    public Model(MPoint3d[] points,int[][] ind){
        walls = new MWall[ind.length];
        for(int i=0;i<ind.length;i++){
            MPoint3d[] ps=new MPoint3d[ind[i].length];
            for(int j=0;j<ind[i].length;j++){
                ps[j]=points[i];
            }
            walls[i]=new MWall(ps);
        }
    }
    public Model(double[][] points,int[][] ind){
        walls = new MWall[ind.length];
        for(int i=0;i<ind.length;i++){
            MPoint3d[] ps=new MPoint3d[ind[i].length];
            for(int j=0;j<ind[i].length;j++){
                ps[j]=new MPoint3d(points[ind[i][j]]);
            }
            walls[i]=new MWall(ps);
        }
    }
    public Model(double[][] points,int[][] ind,Color color){
        walls = new MWall[ind.length];
        for(int i=0;i<ind.length;i++){
            MPoint3d[] ps=new MPoint3d[ind[i].length];
            for(int j=0;j<ind[i].length;j++){
                ps[j]=new MPoint3d(points[ind[i][j]]);
            }
            walls[i]=new MWall(ps,color);
        }
    }
    public void upscale(){
        List<MWall> newwals=new ArrayList<MWall>();
        for (int i = 0; i <walls.length ; i++) {
            MWall[] temp=walls[i].upscale();
            for (int j = 0; j < temp.length ; j++) {
                newwals.add(temp[j].haleftringe()[0]);
                newwals.add(temp[j].haleftringe()[1]);
            }
        }
        walls=newwals.toArray(new MWall[newwals.size()]);
    }
    public Model merge(Model m){
        int l1 = walls.length;
        int l2 = m.walls.length;
        MWall[] newWalls = new MWall[l1+l2];
        for(int i=0;i<l1;i++){
            newWalls[i]=walls[i];
        }
        for(int i=0;i<l2;i++){
            newWalls[l1+i]=m.walls[i];
        }
        return new Model(newWalls);
    }
    public MWall[] getWalls() {
        return walls;
    }
    public MWall[] getWallsort(MPoint3d point) {
        return Arrays.stream(walls).sorted(Comparator.comparing(wall -> wall.distance(point))).toArray(MWall[]::new);
    }
    public Model rotate(float angle) {
        MWall[] newWalls = new MWall[walls.length];
        for(int i=0;i<walls.length;i++){
            newWalls[i]=walls.clone()[i].rotate(angle);
        }
        return new Model(newWalls);
    }
    public Model moveby(float x, float y) {
        MWall[] newWalls = new MWall[walls.length];
        for(int i=0;i<walls.length;i++){
            newWalls[i]=walls.clone()[i].moveby(x,y);
        }
        return new Model(newWalls);
    }



}
