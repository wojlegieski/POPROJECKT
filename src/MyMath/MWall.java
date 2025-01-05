package MyMath;

import java.awt.*;

public class MWall{
    MPoint3d[] points;
    Color color;
    MPoint3d midle;
    public MWall(MPoint3d[] points, Color color) {
        midle=new MPoint3d(0,0,0);
        this.color=color;
        this.points = points;
        for (MPoint3d p : points) {
            midle.x+=p.x/points.length;
            midle.y+=p.y/points.length;
            midle.z+=p.z/points.length;
        }
    }
    public MWall(MPoint3d[] points) {
        midle=new MPoint3d(0,0,0);
        this.points = points;
        this.color = new Color(100, 100, 100);
        for (MPoint3d p : points) {
            midle.x+=p.x/points.length;
            midle.y+=p.y/points.length;
            midle.z+=p.z/points.length;
        }
    }
    public MWall(double[][] ps) {
        midle=new MPoint3d(0,0,0);
        MPoint3d[] points2 = new MPoint3d[ps.length];
        color=new Color(100, 100, 100);
        for (int i = 0; i < ps.length; i++) {
            points2[i] = new MPoint3d(ps[i]);
        }
        this.points = points2;
        for (MPoint3d p : points2) {
            midle.x+=p.x/points2.length;
            midle.y+=p.y/points2.length;
            midle.z+=p.z/points2.length;
        }
    }
    public MWall rotate(float angle) {
        MWall w = new MWall(points.clone(),color);
        for (int i = 0; i < points.length; i++) {
            w.points[i]=points[i].rotateby(angle);
        }
        w.midle=w.midle.rotateby(angle);
        return w;
    }
    public MWall moveby(float x, float y) {
        MWall w = new MWall(points.clone(), color);
        w.midle=w.midle.moveby(x,y,0);
        for (int i = 0; i < points.length; i++) {
            w.points[i]=points[i].moveby(x,y,0);
        }
        return w;
    }
    public MWall[] upscale(){
        MWall[] w = new MWall[points.length];
        for (int i = 0; i < points.length; i++) {
            w[i]=new MWall(new MPoint3d[] {points[i],points[(i+1)%points.length],midle},color);
        }
        return w;
    }
    public MWall[] haleftringe(){
        if(points.length==3){
            MWall[] w = new MWall[2];
            int maxlind=0;
            double dyst=0;
            for(int i=0;i<points.length;i++){
                double a=points[i].diffvec(points[(i+1)%points.length]).abs();
                if(a>dyst){
                    maxlind=i;
                    dyst=points[i].diffvec(points[(i+1)%points.length]).abs();
                }
            }
            MPoint3d midp=new MPoint3d((points[maxlind].x+points[(maxlind+1)%points.length].x)/2,(points[maxlind].y+points[(maxlind+1)%points.length].y)/2,(points[maxlind].z+points[(maxlind+1)%points.length].z)/2);
            w[0]=new MWall(new MPoint3d[] {midp,points[(maxlind)%points.length],points[(maxlind+2)%points.length]},color);
            w[1]=new MWall(new MPoint3d[] {midp,points[(maxlind+1)%points.length],points[(maxlind+2)%points.length]},color);
            return w;
        }
        else throw new TringleExcepton("wall is not tringle");
    }

    public double distance(MPoint3d p1) {
        double dx=midle.x-p1.x;
        double dy=midle.y-p1.y;
        double dz=midle.z-p1.z;
        return Math.sqrt(dx*dx+dy*dy+dz*dz);
    }
    public double compere(MWall w,MPoint3d p1) {
        return distance(p1)-w.distance(p1);
    }
    public MPoint3d[] getPoints(){
        return points;
    }
    public MPoint3d getMidle(){
        return midle;
    }
    public Color getColor(){
        return color;
    }
}
