import MyMath.*;
import track.Checkpoint;
import track.Road;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;


public class    GamePanel extends JPanel {
    Model model;
    MPoint3d light;
    float h;
    MPoint3d c;
    MVector3D facing;
    float darklightratio=0.01f;
    int revdec=5000000;
    boolean odl=true;
    String idk;
    MPoint3d top;
    Checkpoint[] checkpoints;
    Road[] roads;
    boolean vec;
    GamePanel() {
        this.setLayout(new BorderLayout());

    }

    public void updateScene(Model model, MPoint3d light, float h, MPoint3d c,MPoint3d facingpoint,MPoint3d top) {
        this.model = model;
        this.light = light;
        this.h = h;
        this.c = c;
        this.facing = facingpoint.diffvec(c);
        this.repaint();
        this.top = top;
    }
    public void updateScene(Model model, MPoint3d light, float h, MPoint3d c,MVector3D facing,MPoint3d top) {
        this.model = model;
        this.light = light;
        this.h = h;
        this.c = c;
        this.facing = facing;
        this.repaint();
        this.top = top;
    }



    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if(model!=(null)){
            renderScene(g);
        }
        if(idk!=null){
            g2d.setColor(Color.black);
            int i =0;
            g2d.setFont(new Font("Arial", Font.TRUETYPE_FONT, 20));
            for(String s:idk.split("\n")) {
                g2d.drawString(s, 100, 100+i*30);
                i++;
            }

        }
    }

    public void renderScene(Graphics g) {
        this.invalidate();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setBackground(new Color(50,150,50));
        g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
        if(roads!=null){
            for (Road road:roads){
                Polygon p = new Polygon();
                for(MPoint3d p1:road.getPoints()){
                    Point2D poi = p1.toPoint(c,h, facing,top,getWidth(),getHeight());
                    p.addPoint((int) poi.getX(),(int) poi.getY());
                }
                g2d.setColor(Color.gray);
                g2d.fillPolygon(p);
            }
        }
        if(checkpoints!=null){
            for (Checkpoint checkpoint:checkpoints){
                Polygon p = new Polygon();
                for(MPoint3d p1:checkpoint.getPoints()){
                    Point2D poi = p1.toPoint(c,h, facing,top,getWidth(),getHeight());
                    p.addPoint((int) poi.getX(),(int) poi.getY());
                }
                if(checkpoint.drivedon()) {
                    g2d.setColor(new Color(0x00FF2D));
                }else{
                    g2d.setColor(checkpoint.getColor());
                }
                g2d.fillPolygon(p);
            }
        }
        MWall[] ws=model.getWallsort(c.add(facing.setlength(h).revese()));
        g2d.setColor(new Color(0,0,0));
        for(MWall w:ws) {
            Polygon p = new Polygon();
            MPoint3d[] pos = w.getPoints();
            for (MPoint3d p1 : pos) {
                p.addPoint((int) p1.toshadow(light).toPoint(c,h, facing,top,getWidth(),getHeight()).getX(),(int) p1.toshadow(light).toPoint(c,h, facing,top,getWidth(),getHeight()).getY());
            }
            g2d.fillPolygon(p);
        }
        for (int i = ws.length-1; i >=0; i--) {
            MWall wall = ws[i];
            Polygon polygon = new Polygon();
            MPoint3d[] points = wall.getPoints();
            for(MPoint3d p : points){
                polygon.addPoint((int) p.toPoint(c,h, facing,top,getWidth(),getHeight()).getX(),(int) p.toPoint(c,h, facing,top,getWidth(),getHeight()).getY());
            }
            MPoint3d focallength=c.add(facing.setlength(h).revese());
            MVector3D v1=points[0].diffvec(points[1]);
            MVector3D v2=points[0].diffvec(points[2]);
            MVector3D vl=wall.getMidle().diffvec(light);
            MVector3D vs=v1.vecp(v2);
            if(wall.getMidle().add(vs).diffvec(focallength).abs()<wall.getMidle().add(vs.revese()).diffvec(focallength).abs()){
                vs=vs.revese();
            }
            Color finacolor;
            int red=wall.getColor().getRed();
            int green=wall.getColor().getGreen();
            int blue=wall.getColor().getBlue();
            int alpha=wall.getColor().getAlpha();
            if (odl) {
                finacolor = new Color((int) (red*darklightratio+red*(1-darklightratio) * Math.min(Math.max(vs.vcos(vl), 0) / (vl.abs() * vl.abs()) * revdec, 1)), (int) (green*darklightratio+green*(1-darklightratio) * Math.min(Math.max(vs.vcos(vl), 0) / (vl.abs() * vl.abs()) * revdec, 1)), (int) (blue*darklightratio+blue*(1-darklightratio)* Math.min(Math.max(vs.vcos(vl), 0) / (vl.abs() * vl.abs()) * revdec, 1)),alpha);
            }
            else {
                finacolor = new Color((int) (red*darklightratio+red*(1-darklightratio)* Math.max(vs.vcos(vl),0)), (int) (green*darklightratio+green*(1-darklightratio)* Math.max(vs.vcos(vl),0)), (int) (blue*darklightratio+blue*(1-darklightratio)* Math.max(vs.vcos(vl),0)),alpha);
            }

            g2d.setColor(finacolor);
            g2d.fillPolygon(polygon);

        }
        this.validate();

    }
    public void setRoads(Road[] roads) {this.roads = roads;}
    public void setcheckpoints(Checkpoint[] checkpoints) {
        this.checkpoints = checkpoints;
    }
    public void setString(String s){
        idk=s;
    }
    public void setVec(boolean vec) {
        this.vec = vec;
    }
    public void endScreen(double time) {
        Graphics g2d = this.getGraphics();
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2d.setColor(Color.white);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.drawString("You finished the race in " + time + " seconds!", 100, 100);
        g2d.drawString("Pres enter to reset",100,200);
    }
}
