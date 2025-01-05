package track;
import MyMath.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import samochod.Position;
public class Road {
    Point2D.Double[] polygon;
    Point2D pos;
    public Road(Point2D.Double[] polygon,Point2D pos) {
        this.polygon = polygon;
        this.pos = pos;
    }

    public boolean isPointInsidePolygon(Position point) {
        Point2D.Double p = new Point2D.Double(point.getX(), point.getY());
        return isPointInsidePolygon(p);
    }


        public boolean isPointInsidePolygon(Point2D.Double point) {
        int intersections = 0;
        int n = polygon.length;

        for (int i = 0; i < n; i++) {
            Point2D.Double vertex1 = polygon[i];
            Point2D.Double vertex2 = polygon[(i + 1) % n];
            if (isRayIntersectingEdge(point, vertex1, vertex2)) {
                intersections++;
            }
        }
        return (intersections % 2 != 0);
    }
    private boolean isRayIntersectingEdge(Point2D.Double point, Point2D.Double vertex1, Point2D.Double vertex2) {
        if (vertex1.y > vertex2.y) {
            Point2D.Double temp = vertex1;
            vertex1 = vertex2;
            vertex2 = temp;
        }
        if (point.y <= vertex1.y || point.y > vertex2.y) {
            return false;
        }
        if (point.x >= Math.max(vertex1.x, vertex2.x)) {
            return false;
        }
        if (point.x < Math.min(vertex1.x, vertex2.x)) {
            return true;
        }
        double intersectionX = vertex1.x + (point.y - vertex1.y) * (vertex2.x - vertex1.x) / (vertex2.y - vertex1.y);
        return point.x < intersectionX;
    }
    public Road revesex(Point2D.Double pos) {
        Point2D.Double[] newpolygon = new Point2D.Double[polygon.length];
        for (int i = 0; i < polygon.length; i++) {
            Point2D.Double p = polygon[i];
            newpolygon[i] = new Point2D.Double(-p.getX()+2*pos.getX(), p.getY());
        }
        return new Road(newpolygon, pos);
    }
    public Road  revesey(Point2D.Double pos) {
        Point2D.Double[] newpolygon = new Point2D.Double[polygon.length];
        for (int i = 0; i < polygon.length; i++) {
            Point2D.Double p = polygon[i];
            newpolygon[i] = new Point2D.Double(p.getX(), -p.getY()+2*pos.getY());
        }
        return new Road(newpolygon, pos);
    }
    public Road revesexy(Point2D.Double pos) {
        return this.revesex(pos).revesey(pos);
    }
    public Road revesex() {
        Point2D.Double d2pos=new Point2D.Double(pos.getX(),pos.getY());
        return revesex(d2pos);
    }
    public Road revesey() {
        Point2D.Double d2pos=new Point2D.Double(pos.getX(),pos.getY());
        return revesey(d2pos);
    }
    public Road revesexy() {
        return this.revesex().revesey();
    }


    public MPoint3d[] getPoints(){
        MPoint3d[] points = new MPoint3d[polygon.length];
        for (int i = 0; i < polygon.length; i++) {
            Point2D.Double p = polygon[i];
            points[i] = new MPoint3d(p.getX(), p.getY(), 0);
        }
        return points;
    }
    public Road upscale(int a) {
        List<Point2D.Double> newpolygon = new ArrayList<>();
        newpolygon.add(polygon[0]);
        int n=polygon.length;
        for (int i = 0; i < n; i++) {
            if(Math.abs(polygon[i].getX()-polygon[(i+1)%n].getX())>a || Math.abs(polygon[i].getY()-polygon[(i+1)%n].getY())>a) {
                double newx=(polygon[i].getX()+polygon[(i+1)%n].getX())/2;
                double newy=(polygon[i].getY()+polygon[(i+1)%n].getY())/2;
                newpolygon.add(new Point2D.Double(newx,newy));
            }
            if(i<n-1) {
                newpolygon.add(polygon[i+1]);
            }
        }
        return new Road(newpolygon.toArray(new Point2D.Double[newpolygon.size()]),pos);
    }
    public Road  flip(Point2D.Double pos) {
        Point2D.Double[] newpolygon = new Point2D.Double[polygon.length];
        for (int i = 0; i < polygon.length; i++) {
            Point2D.Double p = polygon[i];
            newpolygon[i] = new Point2D.Double(p.getY()-pos.getY()+pos.getX(), p.getX()-pos.getX()+pos.getY());
        }
        return new Road(newpolygon, pos);
    }
    public Road  flip() {
        Point2D.Double d2pos=new Point2D.Double(pos.getX(),pos.getY());
        return flip(d2pos);
    }

}

