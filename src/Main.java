import MyMath.*;
import samochod.*;
import track.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class Main {
    static int TARGET_FPS = 60;
    static long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS;
    static int ROAD_SCALE = 300;
    static int MODEL_UPSCALE_COUNT = 2;
    static Position startPositon=new Position(-300,150);
    static float startAngle=(float) Math.PI;

    public static void main(String[] args) {


        boolean wasEKeyNotPressed = true;
        boolean wasQKeyNotPressed = true;
        boolean wasTabNotPressed = true;
        int currentCamera = 0;
        int cameraCount = 2;
        int laps = 2;
        int curentlap=0;

        Car maluch = new Car.Builder()
                .setPosition(startPositon.copy())
                .setFacing(startAngle)
                .build();
        Model carModel = carModeling();
        Checkpoint[] checkpoints = setCheckpoints();
        Checkpoint meta = checkpoints[checkpoints.length-1];
        ArrayList<Road> roads = mapRoad();
        MPoint3d light = new MPoint3d(500, 100, 200);
        GameFrame a = new GameFrame();;
        a.setCheckpints(checkpoints);
        a.setRoads(roads.toArray(new Road[roads.size()]));
        boolean end = false;
        ToneGenerator toneThread = new ToneGenerator();
        toneThread.start();
        boolean onroad=true;
        List<Position> povbuffor = new ArrayList<>();
        maluch.turnOn();
        double stime = System.nanoTime();

        while (true) {
            long startTime = System.nanoTime();
            double time = (System.nanoTime() - stime)/1_000_000_000;
            toneThread.setFrequency(maluch.getObroty()/60);
            for(Checkpoint c : checkpoints) {
                if(c!=meta) {
                    c.isin(maluch.getPosition());
                }
            }
            for(Checkpoint c : checkpoints) {
                if(c!=meta) {
                    if (!c.drivedon()) {
                        end = false;
                        break;
                    }
                    end = true;
                }
            }
            if(end) {
                meta.isin(maluch.getPosition());
                if(meta.drivedon()==true) {
                    curentlap++;
                    for (Checkpoint c : checkpoints) {
                        c.setwason(false);
                    }
                    if(curentlap==laps) {
                        toneThread.stopEngine();
                        a.endScreen(time);
                        while (true) {
                            System.out.println();
                            if(a.isEnterkeyPressed()){
                                end = false;
                                reset(maluch);
                                stime = System.nanoTime();
                                curentlap=0;
                                toneThread=new ToneGenerator();
                                toneThread.start();
                                break;
                            }
                        }

                    }

                }
            }

            Position tpow = new Position(0,0);
            tpow.movepolar(maluch.getFacing(),3);
            povbuffor.add(tpow);
            Position pov;
            if (povbuffor.size() >= 15) {
                povbuffor.removeFirst();
            }
            pov = povbuffor.getFirst();
            switch (currentCamera) {
                case 0:
                    a.render(carModel.rotate(maluch.getFacing()).moveby(maluch.getPosition().getX(),
                                    maluch.getPosition().getY()), light, 450,
                            new MPoint3d(maluch.getPosition().getX() - 20, maluch.getPosition().getY(), 80),  //camera
                            new MPoint3d(maluch.getPosition().getX() + 20, maluch.getPosition().getY(), 20),   //facing
                            new MPoint3d(maluch.getPosition().getX() + 1, maluch.getPosition().getY(), 300));  //top
                    break;
                case 1:
                    a.renderv(carModel.rotate(maluch.getFacing()).moveby(maluch.getPosition().getX(),
                                    maluch.getPosition().getY()), light, 450,
                            new MPoint3d(maluch.getPosition().getX(), maluch.getPosition().getY(), 80),  //camera
                            new MVector3D(pov.getX(),pov.getY(),-3),   //facing
                            new MPoint3d(maluch.getPosition().getX() + 1, maluch.getPosition().getY(), 300));  //top
                    break;
            }

            a.setText(String.format("%.1f",time)+maluch);
            if (a.isUpPressed()) {
                maluch.accelerate();
            }
            if (a.isDownPressed()) {
                maluch.brake();
            }
            if (a.isLeftPressed()) {
                maluch.left();
            }
            if (a.isRightPressed()) {
                maluch.right();
            }
            if (a.isShiftPressed()) {
                maluch.useClutch();
            } else maluch.releaseClutch();
            if (a.isQkeyPressed()) {
                if (wasQKeyNotPressed) {
                    maluch.shiftDown();
                    wasQKeyNotPressed = false;
                }
            } else {
                wasQKeyNotPressed = true;
            }
            if (a.isTapkeyPressed()){
                if (wasTabNotPressed){
                    currentCamera=(currentCamera+1)%cameraCount;
                    wasTabNotPressed = false;
                }
            }
            else {wasTabNotPressed = true;}
            if (a.isEkeyPressed()) {
                if (wasEKeyNotPressed) {
                    maluch.shiftUp();
                    wasEKeyNotPressed = false;
                }
            } else {
                wasEKeyNotPressed = true;
            }
            for (Road road : roads) {
                if(road.isPointInsidePolygon(maluch.getPosition())){
                    onroad = true;
                    break;
                }
                onroad = false;
            }
            maluch.move(onroad);
            long elapsedTime = System.nanoTime() - startTime;
            long sleepTime = OPTIMAL_TIME - elapsedTime;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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

    static void reset(Car car){
        car.setPosition(startPositon.copy());
        car.setFacing(startAngle);
        car.stop();
        car.setGear(1);
    }
}