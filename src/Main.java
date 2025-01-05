import MyMath.*;
import samochod.*;
import track.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS;
        boolean reset=false ;
        boolean ewasnotpers = true;
        boolean qwasnorpres = true;
        boolean tapwaspressed = true;
        int curentcamera=0;
        int cameras =2;
        Model model = Modleling.rectangle(10, 20, 20, new MPoint3d(0, 0, 20), new Color(50, 80, 255, 132));
        model = model.merge(Modleling.rectangle(15, 20, 60, new MPoint3d(0, 0, 5), Color.red));
        for (int i = 0; i <1; i++) {
            model.upscale();
        }
        model = model.merge(Modleling.rectangle(10, 5, 10, new MPoint3d(10, 20, 0), Color.BLACK));
        model = model.merge(Modleling.rectangle(10, 5, 10, new MPoint3d(-10, 20, 0), Color.BLACK));
        model = model.merge(Modleling.rectangle(10, 5, 10, new MPoint3d(-10, -20, 0), Color.BLACK));
        model = model.merge(Modleling.rectangle(10, 5, 10, new MPoint3d(10, -20, 0), Color.BLACK));
        Car maluch = new Car(new Engine("fk9ak", 90, 2134),
                new Position(-300, 150),
                new Gearbox("da", 123, 142),
                "sa1241", "fiat 126p",
                (float) Math.PI
        );



        int laps = 1;
        Checkpoint[] checkpoints = new Checkpoint[]{
                new Checkpoint(new Position(900,-1325),50,400),
                new Checkpoint(new Position(1500,-800),200,50),
                new Checkpoint(new Position(-100,-700),50,200),
                new Checkpoint(new Position(675,700),40,125),
                new Checkpoint(new Position(-700,-300),200,50),
                new Checkpoint(new Position(-300,150),50,200,Color.red)
        };
        Checkpoint meta = checkpoints[checkpoints.length-1];




        ArrayDeque<Road> roads = new ArrayDeque<>();
        roads.add(Roadcreator.straight(new Point2D.Double(750,300),100,600).upscale(300));
        roads.add(Roadcreator.trun(new Point2D.Double(-600,50),400).revesey().upscale(300));
        roads.add(Roadcreator.trun(new Point2D.Double(-600,-800),400).upscale(300));
        roads.add(Roadcreator.straight(new Point2D.Double(-700,-375),200,450).upscale(300));
        roads.add(Roadcreator.trun(new Point2D.Double(-200,-800),400).revesex().upscale(300));
        roads.add(Roadcreator.straight(new Point2D.Double(0,-700),400,200).revesex().upscale(300));
        roads.add(Roadcreator.longtrun(new Point2D.Double(600,-1000),800).revesey().upscale(300));
        roads.add(Roadcreator.longutrun(new Point2D.Double(1200,-1600),800).flip().upscale(300));
        roads.add(Roadcreator.straight(new Point2D.Double(1500,-1000),200,800).upscale(300));
        roads.add(Roadcreator.longtrun(new Point2D.Double(1200,-200),800).revesey().upscale(300));
        roads.add(Roadcreator.longutrun(new Point2D.Double(600,700),400).flip().revesey().upscale(300));
        roads.add(Roadcreator.trun(new Point2D.Double(400,500),200).revesex().upscale(300));
        roads.add(Roadcreator.trun(new Point2D.Double(200,400),200).revesey().upscale(300));
        roads.add(Roadcreator.trun(new Point2D.Double(100,200),200).revesex().upscale(300));
        roads.add(Roadcreator.narrowing(new Point2D.Double(-200,150),100,200,400).upscale(300));
        MPoint3d light = new MPoint3d(500, 100, 200);
        double stime = System.nanoTime();
        GameFrame a = new GameFrame();
        a.setCheckpints(checkpoints);
        a.setRoads(roads.toArray(new Road[roads.size()]));
        boolean end = false;
        ToneGenerator toneThread = new ToneGenerator();
        toneThread.start();
        boolean onroad=true;
        int curentlap=0;
        List<Position> povbuffor = new ArrayList<>();
        maluch.wlacz();



        while (true) {
            long startTime = System.nanoTime();
            double time = (System.nanoTime() - stime)/1_000_000_000;
            toneThread.setFrequency(maluch.getObroty()/60);
            for(Checkpoint c : checkpoints) {
                if(c!=meta) {
                    c.isin(maluch.getPozycja());
                }
            }
            for(Checkpoint c : checkpoints) {
                if(c!=meta) {
                    if (c.drivedon() == false) {
                        end = false;
                        break;
                    }
                    end = true;
                }
            }
            if(end) {
                meta.isin(maluch.getPozycja());
                if(meta.drivedon()==true) {
                    curentlap++;
                    if(curentlap==laps) {
                        a.endScreen(time);
                        toneThread.stopEngine();
                        break;
                    }
                    else{
                        for (Checkpoint c : checkpoints) {
                            c.setwason(false);
                        }
                    }

                }
            }



            Position tpow = new Position(0,0);
            tpow.movepolar(maluch.getFacing(),3);
            povbuffor.add(tpow);
            Position pov;
            if (povbuffor.size() >= 10) {
                povbuffor.removeFirst();
            }
            pov = povbuffor.getFirst();
            switch (curentcamera) {
                case 0:
                    a.render(model.rotate(maluch.getFacing()).moveby(maluch.getPozycja().getX(),
                                    maluch.getPozycja().getY()), light, 450,
                        new MPoint3d(maluch.getPozycja().getX() - 20, maluch.getPozycja().getY(), 80),  //camera
                        new MPoint3d(maluch.getPozycja().getX() + 20, maluch.getPozycja().getY(), 20),   //facing
                        new MPoint3d(maluch.getPozycja().getX() + 1, maluch.getPozycja().getY(), 300));  //top
                    break;
                case 1:
                    a.renderv(model.rotate(maluch.getFacing()).moveby(maluch.getPozycja().getX(),
                                    maluch.getPozycja().getY()), light, 450,
                            new MPoint3d(maluch.getPozycja().getX() - 20, maluch.getPozycja().getY(), 80),  //camera
                            new MVector3D(pov.getX(),pov.getY(),-3),   //facing
                            new MPoint3d(maluch.getPozycja().getX() + 1, maluch.getPozycja().getY(), 300));  //top
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
                if (qwasnorpres) {
                    maluch.shiftDown();
                    qwasnorpres = false;
                }
            } else {
                qwasnorpres = true;
            }
            if (a.isTapkeyPressed()){
                if (tapwaspressed){
                    curentcamera=(curentcamera+1)%cameras;
                    tapwaspressed = false;
                }
            }
            else {tapwaspressed = true;}
            if (a.isEkeyPressed()) {
                if (ewasnotpers) {
                    maluch.shiftUp();
                    ewasnotpers = false;
                }
            } else {
                ewasnotpers = true;
            }

            for (Road road : roads) {
                if(road.isPointInsidePolygon(maluch.getPozycja())){
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
}
