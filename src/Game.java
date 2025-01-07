import MyMath.MPoint3d;
import samochod.Car;
import samochod.Position;
import track.Checkpoint;
import track.Road;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private GameFrame gameFrame;
    private GraphcSystem graphcSystem;
    private InputSystem inputSystem;
    private Car car;
    private Position startPosition;
    static int TARGET_FPS = 60;
    static long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS;
    private float startAngle;
    private double laps;
    private int cameraCount=2;
    private int[] currentCamera={0};
    private int currentLap;
    private List<Road> roads;
    private Checkpoint[] checkpoints;
    Checkpoint meta;
    ToneGenerator toneThread;

    public Game(Car car, Position startPosition, float startAngle, int laps, List<Road> roads, Checkpoint[] checkpoints, MPoint3d light, float focalLength) {
        gameFrame = new GameFrame();
        this.graphcSystem = new GraphcSystem(gameFrame,light,focalLength);
        this.inputSystem = new InputSystem();
        this.car = car;
        this.roads=roads;
        this.checkpoints=checkpoints;
        this.startPosition = startPosition;
        this.startAngle = startAngle;
        this.laps=laps;
        currentCamera[0]=0;
        meta=checkpoints[checkpoints.length-1];
    }
    public void start(){
        List<Position> povbuffor = new ArrayList<>();
        toneThread = new ToneGenerator();
        toneThread.start();
        gameFrame.setRoads(roads.toArray(new Road[roads.size()]));
        gameFrame.setCheckpints(checkpoints);

        double startTime = System.nanoTime();
        inicjalize();
        while(true){
            long frameTime = System.nanoTime();
            double time = (System.nanoTime() - startTime)/1_000_000_000;
            inputSystem.handleInput(car,gameFrame,cameraCount,currentCamera);
            car.move(isOnRoads(car));
            Position tpow = new Position(0,0);
            tpow.movepolar(car.getFacing(),3);
            povbuffor.add(tpow);
            Position pov;
            if (povbuffor.size() >= 15) {
                povbuffor.removeFirst();
            }
            toneThread.setFrequency(car.getObroty()/60f);
            pov = povbuffor.getFirst();
            checkpointCheck(car);
            graphcSystem.render(car,currentCamera[0],time,pov);
            if(checkLapEnd(car)){
                nextLap();
                if(endOfLaps()){
                    toneThread.stopEngine();
                    graphcSystem.displayEndScreen(time);
                    while (true) {
                        System.out.println();
                        if(gameFrame.isEnterkeyPressed()){
                            inicjalize();
                            break;
                        }
                    }
                }
            }
            long elapsedTime = System.nanoTime() - frameTime;
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

    private void inicjalize() {
        car.setPosition(startPosition.copy());
        car.setFacing(startAngle);
        car.stop();
        car.setGear(1);
        currentLap = 0;
        toneThread=new ToneGenerator();
        toneThread.start();
    }
    private boolean endOfLaps(){
        return currentLap==laps;
    }
    private void nextLap(){
        currentLap++;
        for(Checkpoint c : checkpoints){
            c.setwason(false);
        }
    }
    private void checkpointCheck(Car car){
        for (Checkpoint c : checkpoints) {
            if(c!=meta) {
                c.isin(car.getPosition());
            }
        }
    }
    private boolean checkLapEnd(Car car){
        boolean metacheck = true;
        for (Checkpoint c : checkpoints) {
            if(c!=meta&!c.drivedon()) {
                return false;
            }
        }
        meta.isin(car.getPosition());
        return meta.drivedon();
    }
    private void setMetaindex(int index){
        try {
            if (index < checkpoints.length - 1) {
                meta = checkpoints[index];
            } else {
                throw new IndexOutOfBoundsException("index of meta have to be in checkpoints bounds");
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println(e.getMessage());
        }
    }
    private boolean isOnRoads(Car car){
        for(Road r : roads){
            if (r.isPointInsidePolygon(car.getPosition())){
                return true;
            }
        }
        return false;
    }
}
