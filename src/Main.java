import MyMath.*;
import samochod.*;
import track.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class Main {

    static int ROAD_SCALE = 300;
    static int MODEL_UPSCALE_COUNT = 1;
    static Position startPositon=new Position(-300,150);
    static float startAngle=(float) Math.PI;

    public static void main(String[] args) {
        Car maluch = new Car.Builder(carModeling())
                .setPosition(startPositon.copy())
                .setFacing(startAngle)
                .build();
        Checkpoint[] checkpoints = setCheckpoints();
        ArrayList<Road> roads = mapRoad();
        MPoint3d light = new MPoint3d(500, 100, 2000);
        Game game= new Game(maluch,startPositon,startAngle,2,roads,checkpoints,light,450);
        game.start();

    }



    static Checkpoint[] setCheckpoints(){
        return new Checkpoint[]{
                new Checkpoint(new Position(900,-1325),50,400),
                new Checkpoint(new Position(1500,-800),200,50),
                new Checkpoint(new Position(-100,-700),50,200),
                new Checkpoint(new Position(675,700),40,125),
                new Checkpoint(new Position(-700,-300),200,50),
                new Checkpoint(new Position(-300,150),50,200,Color.red)
        };
    }
    static ArrayList<Road> mapRoad(){
        ArrayList<Road> roads = new ArrayList<>();
        roads.add(Roadcreator.straight(new Point2D.Double(750,300),100,600).upscale(ROAD_SCALE));
        roads.add(Roadcreator.trun(new Point2D.Double(-600,50),400).revesey().upscale(ROAD_SCALE));
        roads.add(Roadcreator.trun(new Point2D.Double(-600,-800),400).upscale(ROAD_SCALE));
        roads.add(Roadcreator.straight(new Point2D.Double(-700,-375),200,450).upscale(ROAD_SCALE));
        roads.add(Roadcreator.trun(new Point2D.Double(-200,-800),400).revesex().upscale(ROAD_SCALE));
        roads.add(Roadcreator.straight(new Point2D.Double(0,-700),400,200).revesex().upscale(ROAD_SCALE));
        roads.add(Roadcreator.longtrun(new Point2D.Double(600,-1000),800).revesey().upscale(ROAD_SCALE));
        roads.add(Roadcreator.longutrun(new Point2D.Double(1200,-1600),800).flip().upscale(ROAD_SCALE));
        roads.add(Roadcreator.straight(new Point2D.Double(1500,-1000),200,800).upscale(ROAD_SCALE));
        roads.add(Roadcreator.longtrun(new Point2D.Double(1200,-200),800).revesey().upscale(ROAD_SCALE));
        roads.add(Roadcreator.longutrun(new Point2D.Double(600,700),400).flip().revesey().upscale(ROAD_SCALE));
        roads.add(Roadcreator.trun(new Point2D.Double(400,500),200).revesex().upscale(ROAD_SCALE));
        roads.add(Roadcreator.trun(new Point2D.Double(200,400),200).revesey().upscale(ROAD_SCALE));
        roads.add(Roadcreator.trun(new Point2D.Double(100,200),200).revesex().upscale(ROAD_SCALE));
        roads.add(Roadcreator.narrowing(new Point2D.Double(-200,150),100,200,400).upscale(ROAD_SCALE));
        return roads;
    }
    static Model carModeling(){
        Model carModel = Modleling.rectangle(10, 20, 20, new MPoint3d(0, 0, 20), new Color(50, 80, 255, 132));
        carModel = carModel.merge(Modleling.rectangle(15, 20, 60, new MPoint3d(0, 0, 5), Color.red));
        for (int i = 0; i <MODEL_UPSCALE_COUNT; i++) {
            carModel.upscale();
        }
        carModel = carModel.merge(Modleling.rectangle(10, 5, 10, new MPoint3d(10, 20, 0), Color.BLACK));
        carModel = carModel.merge(Modleling.rectangle(10, 5, 10, new MPoint3d(-10, 20, 0), Color.BLACK));
        carModel = carModel.merge(Modleling.rectangle(10, 5, 10, new MPoint3d(-10, -20, 0), Color.BLACK));
        carModel = carModel.merge(Modleling.rectangle(10, 5, 10, new MPoint3d(10, -20, 0), Color.BLACK));
        return carModel;
    }
}