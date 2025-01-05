package MyMath;

import java.awt.*;
import java.awt.geom.Point2D;

public class MPoint3d {
    double x;
    double y;
    double z;
    public MPoint3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public MPoint3d(double[] ps) {
        if (ps == null || ps.length != 3) {
            throw new IllegalArgumentException("The array must contain 3 elements.");
        }
        x = ps[0];
        y = ps[1];
        z = ps[2];
    }

    public MPoint3d moveby(float dx, float dy, float dz) {
        MPoint3d p = new  MPoint3d(x+dx,y+dy,z+dz);
        return p;
    }
    public MPoint3d moveby(MVector3D v) {
        return new MPoint3d(x+v.x,y+v.y,z+v.z);
    }

    public MPoint3d rotateby(float angle) {
        MPoint3d p = new  MPoint3d(x,y,z);
        p.x=x*Math.cos(angle)-y*Math.sin(angle);
        p.y=y*Math.cos(angle)+x*Math.sin(angle);
        return p;
    }
    private MPoint3d findpoap(double d,MVector3D facing,MPoint3d ps) {
        MVector3D pr=ps.diffvec(this);
        double t=-(facing.x*x+facing.y*y+facing.z*z+d)/(facing.x*pr.x+facing.y*pr.y+facing.z*pr.z);
        MPoint3d poap=new MPoint3d(x + pr.x*t,y + pr.y*t,z + pr.z*t);
        return poap;
    }

    public Point2D toPoint(MPoint3d c, float focallength, MVector3D facing, MPoint3d top, int width, int height) {
        MPoint3d ps = new MPoint3d(c.x, c.y, c.z).moveby(facing.setlength(-focallength));
        double d = -(c.x * facing.x + c.y * facing.y + c.z * facing.z);
        MVector3D poap = this.findpoap(d, facing, ps).diffvec(c);
        MVector3D vtop = top.findpoap(d, facing, ps).diffvec(c);
        MVector3D midlevec = vtop.vecp(facing);
        if (this.diffvec(ps.moveby(facing)).abs() > this.diffvec(ps.moveby(facing.revese())).abs()) {
            return new Point2D.Double(10000, 10000);
        }
        double r = poap.abs();
        double agl = poap.agl(midlevec, vtop);
        Point2D p2 = new Point2D.Double(r * Math.cos(agl) + width / 2d, r * Math.sin(agl) + height / 2d);
        return p2;
    }
    public Point2D toPoint(MPoint3d c, float focallength,MPoint3d facingpint,MPoint3d top,int width,int height) {
        MVector3D facing=facingpint.diffvec(c);
        return toPoint(c,focallength,facing,top,width,height);
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
    public MVector3D diffvec(MPoint3d p) {
        return new MVector3D(x- p.x,y-p.y,z- p.z);
    }
    public MPoint3d toshadow(MPoint3d light) {
        MPoint3d p = new MPoint3d(0, 0, 0);
        if (light.z > this.z) {
            double t = (-this.z) / (this.z - light.z);
            p.x = this.x + (this.x - light.x) * t;
            p.y = this.y + (this.y - light.y) * t;
            p.z = 0;
        } else {
            p.x = this.x;
            p.y = this.y;
            p.z = 0;
        }
        return p;
    }
    public MPoint3d add(MPoint3d p) {
        return new MPoint3d(x+p.x,y+p.y,z+p.z);
    }

    @Override
    public String toString() {
        return "MPoint3d [x=" + x + ", y=" + y + ", z=" + z + "]";
    }
}
