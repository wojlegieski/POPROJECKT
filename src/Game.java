import samochod.Car;
import samochod.Position;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private GameFrame gameFrame;
    private GraphcSystem graphcSystem;
    private InputSystem inputSystem;
    private Car car;
    private Position startPosition;
    private float startAngle;
    private double lap;
    private int cameraCount=2;
    private int[] currentCamera={0};

    public Game(GraphcSystem graphcSystem,Car car, Position startPosition,float startAngle,int laps) {
        gameFrame = new GameFrame();
        this.graphcSystem = graphcSystem;
        this.inputSystem = new InputSystem();
        this.car = car;
        this.startPosition = startPosition;
        this.startAngle = startAngle;
        lap=laps;
    }
    public void start(Car car, Position startPosition,float startAngle){
        GameFrame gameFrame=new GameFrame();
        int currentLap=0;
        List<Position> povbuffor = new ArrayList<>();
        while(true){
            inputSystem.handleInput(car,gameFrame,cameraCount,currentCamera);
            Position tpow = new Position(0,0);
            tpow.movepolar(car.getFacing(),3);
            povbuffor.add(tpow);
            Position pov;
            if (povbuffor.size() >= 15) {
                povbuffor.removeFirst();
            }
            pov = povbuffor.getFirst();

        }


    }

}
