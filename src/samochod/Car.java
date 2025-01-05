package samochod;

public class Car {
    private static final double DRAG_COEFFICIENT = 0.3;
    private static final double AIR_DENSITY = 1.225;
    private static final double TIRE_WIDTH = 1.0;
    private static final double ROLLING_RESISTANCE_COEFF = 0.01;
    private static final double GRAVITY = 9.81;

    private Engine engine;
    private Position position;
    private Gearbox gearbox;
    private boolean engineOn;
    private String registrationNumber;
    private String model;
    private float speed;
    private float facing;
    private float tireRadius;
    private int weight;
    private double wheelBase;

    public Car(Engine engine, Position position, Gearbox gearbox, String registrationNumber, String model, float facing) {
        this.engine = engine;
        this.gearbox = gearbox;
        this.position = position;
        this.engineOn = false;
        this.registrationNumber = registrationNumber;
        this.model = model;
        this.facing = facing;
        this.tireRadius = 40;
        this.weight = 800;
        engine.start();
    }

    public void wlacz() {
        engineOn = true;
        engine.start();
    }

    public void wylacz() {
        engineOn = false;
        engine.stop();
    }

    public int getWaga() {
        return engine.getWaga() + gearbox.getWaga() + weight;
    }

    public float getAktPredkosc() {
        return gearbox.getCurrentRatio() * engine.getCurrentRPM();
    }

    public Position getPozycja() {
        return position;
    }

    public double maxPredkosc() {
        double power = engine.calculateMaxPower();
        return Math.round(Math.pow(power / (DRAG_COEFFICIENT * TIRE_WIDTH * AIR_DENSITY), 1.0 / 3.0));
    }

    public void move(boolean onRoad) {
        double[] dragAndResistance = calculateDragAndResistance(speed);
        double fdrag = dragAndResistance[0];
        double rollingResistance = dragAndResistance[1];

        if (speed >= 0) {
            if (!onRoad && speed > 10) {
                speed -= 0.5;
            }
            speed -= (fdrag + rollingResistance) / getWaga()*0.2;
        } else {
            if (!onRoad && speed < -10) {
                speed += 0.5;
            }
            speed += (fdrag + rollingResistance) / getWaga()*0.2;
        }

        position.movepolar(facing, speed / 4);
        engine.setCurrentRPM((int) (gearbox.getCurrentRatio() * speed / tireRadius * 1000));
    }

    private double[] calculateDragAndResistance(float speed) {
        double fdrag = 0.5 * AIR_DENSITY * DRAG_COEFFICIENT * TIRE_WIDTH * speed * speed;
        double rollingResistance = ROLLING_RESISTANCE_COEFF * getWaga() * GRAVITY;
        return new double[]{fdrag, rollingResistance};
    }

    public void accelerate() {
        if (engineOn) {
        if (engine.getCurrentRPM() < engine.getMaxRPM()) {
            if (engine.getCurrentRPM() < -engine.getMaxRPM()) {
                engine.setCurrentRPM(-engine.getMaxRPM() + 1);
            }
            double force = (engine.getCurrentTorque() / tireRadius) * gearbox.getCurrentRatio() * 2.5;
            double acceleration = force / getWaga();
            speed += acceleration;
        }
        }
    }

    public void brake() {
        if (speed - 0.5 > 0) {
            speed -= 1;
        } else {
            speed = 0;
        }
    }

    public void left() {
        facing -= Math.min(0.03, 0.03 * speed / 15);
    }

    public void right() {
        facing += Math.min(0.03, 0.03 * speed / 15);
    }

    public void stop() {
        speed = 0;
    }

    public float getFacing() {
        return facing;
    }

    public void useClutch() {
        gearbox.engageClutch();
    }

    public void releaseClutch() {
        gearbox.disengageClutch();
    }

    public void shiftUp() {
        gearbox.upshift();
    }

    public void shiftDown() {
        gearbox.downshift();
    }

    @Override
    public String toString() {
        return "rpm:" + engine.getCurrentRPM() + "\n" + "speed:" + (int) speed + "\n" + "gear:" + gearbox.getCurrentGear();
    }

    public int getObroty() {
        return engine.getCurrentRPM();
    }
}