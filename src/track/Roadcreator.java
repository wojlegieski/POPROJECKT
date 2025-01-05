package track;

import java.awt.geom.Point2D;

public class Roadcreator {
    public static Road straight(Point2D pos, int length, int width){
        Point2D.Double p1 = new Point2D.Double(pos.getX()-length/2, pos.getY()-width/2);
        Point2D.Double p2 = new Point2D.Double(pos.getX()+length/2, pos.getY()-width/2);
        Point2D.Double p3 = new Point2D.Double(pos.getX()+length/2, pos.getY()+width/2);
        Point2D.Double p4 = new Point2D.Double(pos.getX()-length/2, pos.getY()+width/2);
        return new Road(new Point2D.Double[] {p1, p2, p3, p4},pos);
    }
    public static Road trun(Point2D pos,int width){
        Point2D.Double p1 = new Point2D.Double(pos.getX()+width/2, pos.getY()-width/2);
        Point2D.Double p2 = new Point2D.Double(pos.getX()+width/2, pos.getY());
        Point2D.Double p3 = new Point2D.Double(pos.getX(), pos.getY()+width/2);
        Point2D.Double p4 = new Point2D.Double(pos.getX()-width/2, pos.getY()+width/2);
        return new Road(new Point2D.Double[] {p1, p2, p3, p4},pos);
    }
    public static Road longtrun(Point2D pos,int width){
        int a = width/8;
        Point2D.Double p0 = new Point2D.Double(pos.getX()-width/2+3*a, pos.getY()-width/2+a);
        Point2D.Double p1 = new Point2D.Double(pos.getX()-width/2, pos.getY()-width/2);
        Point2D.Double p2 = new Point2D.Double(pos.getX()-width/2, pos.getY()-width/2+2*a);
        Point2D.Double p3 = new Point2D.Double(pos.getX()-width/2+3*a, pos.getY()-width/2+3*a);
        Point2D.Double p4 = new Point2D.Double(pos.getX()+width/2-3*a, pos.getY()+width/2-3*a);
        Point2D.Double p5 = new Point2D.Double(pos.getX()+width/2-2*a, pos.getY()+width/2);
        Point2D.Double p6 = new Point2D.Double(pos.getX()+width/2, pos.getY()+width/2);
        Point2D.Double p7 = new Point2D.Double(pos.getX()+width/2-a, pos.getY()+width/2-3*a);
        return new Road(new Point2D.Double[] {p0,p1, p2, p3, p4,p5,p6,p7},pos);
    }
    public static Road longutrun(Point2D pos,int width){
        int a = width/8;
        Point2D.Double p0 = new Point2D.Double(pos.getX()-width/4+a, pos.getY()-width/2+2*a);
        Point2D.Double p1 = new Point2D.Double(pos.getX()+width/4, pos.getY()-width/2);
        Point2D.Double p2 = new Point2D.Double(pos.getX()+width/4, pos.getY()-width/2+2*a);
        Point2D.Double p3 = new Point2D.Double(pos.getX(), pos.getY());
        Point2D.Double p4 = new Point2D.Double(pos.getX()+width/4, pos.getY()+width/2-2*a);
        Point2D.Double p5 = new Point2D.Double(pos.getX()+width/4, pos.getY()+width/2);
        Point2D.Double p6 = new Point2D.Double(pos.getX()-width/4+a, pos.getY()+width/2-2*a);
        Point2D.Double p7 = new Point2D.Double(pos.getX()-width/4, pos.getY());
        return new Road(new Point2D.Double[] {p0,p1, p2, p3, p4,p5,p6,p7},pos);
    }
    public static Road narrowing(Point2D pos,int widthstart,int widthend,int length ){
        Point2D.Double p1 = new Point2D.Double(pos.getX()-length/2, pos.getY()-widthend/2);
        Point2D.Double p2 = new Point2D.Double(pos.getX()+length/2, pos.getY()-widthstart/2);
        Point2D.Double p3 = new Point2D.Double(pos.getX()+length/2, pos.getY()+widthstart/2);
        Point2D.Double p4 = new Point2D.Double(pos.getX()-length/2, pos.getY()+widthend/2);
        return new Road(new Point2D.Double[] {p1, p2, p3, p4},pos);
    }

}
