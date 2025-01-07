package samochod;
import MyMath.Model;

public class Car {
    private static final double DRAG_COEFFICIENT = 0.3;
    private static final double AIR_DENSITY = 1.225;
    private static final double TIRE_WIDTH = 1.0;
    private static final double ROLLING_RESISTANCE_COEFF = 0.01;
    private static final double GRAVITY = 9.81;
    private static final double DEFAULT_SPEED_REDUCTION_THRESHOLD = 200;
    private static final double DEFAULT_ROAD_SPEED_REDUCTION = 0.5;

    private Engine engine;
    private Position position;
    private Gearbox gearbox;
    private boolean engineOn;
    private String registrationNumber;
    private String modelName;
    private float speed;
    private float facing;
    private float tireRadius;
    private int weight;
    private Model model;

    public Car(Builder builder) {
        this.engine = builder.engine;
        this.gearbox = builder.gearbox;
        this.position = builder.position;
        this.engineOn = false;
        this.registrationNumber = builder.registrationNumber;
        this.modelName = builder.modelName;
        this.facing = builder.facing;
        this.tireRadius = builder.tireRadius;
        this.weight = builder.weight;
        this.model=builder.model;
    }

    public void turnOff() {
        engineOn = false;
        engine.stop();
    }

    public void turnOn() {
        engineOn = true;
        engine.start();
    }

    public int getTotalWeight() {
        return engine.getWaga() + gearbox.getWaga() + weight;
    }

    public float getCurrentSpeed() {
        return speed;}

    public Position getPosition() {
        return position;
    }

    public double calculateMaxSpeed() {
        double power = engine.calculateMaxPower();
        return Math.round(Math.pow(power / (DRAG_COEFFICIENT * TIRE_WIDTH * AIR_DENSITY), 1.0 / 3.0));
    }

    public void setGear(int gear) {
        gearbox.setCurrentGear(gear);
    }

    public void move(boolean onRoad) {
        double dragForce = calculateDragForce(speed);
        double rollingResistance = calculateRollingResistance();

        if (speed >= 0) {
            if (!onRoad && speed > DEFAULT_SPEED_REDUCTION_THRESHOLD) {
                speed -= DEFAULT_ROAD_SPEED_REDUCTION;
            }
            speed -= (dragForce + rollingResistance) / getTotalWeight() * 0.2;
        } else {
            if (!onRoad && speed < -DEFAULT_SPEED_REDUCTION_THRESHOLD) {
                speed += DEFAULT_ROAD_SPEED_REDUCTION;
            }
            speed += (dragForce + rollingResistance) / getTotalWeight() * 0.2;
        }
        position.movepolar(facing, speed / 4);
        engine.setCurrentRPM((int) (gearbox.getCurrentRatio() * speed / tireRadius * 1000));
    }

    private double calculateDragForce(float speed) {
        return 0.5 * AIR_DENSITY * DRAG_COEFFICIENT * TIRE_WIDTH * speed * speed;
    }

    private double calculateRollingResistance() {
        return ROLLING_RESISTANCE_COEFF * getTotalWeight() * GRAVITY;
    }

    public void accelerate() {
        if (engineOn && engine.getCurrentRPM() < engine.getMaxRPM()) {
            if (engine.getCurrentRPM() < -engine.getMaxRPM()) {
                engine.setCurrentRPM(-engine.getMaxRPM() + 1);
            }
            double force = (engine.getCurrentTorque() / tireRadius) * gearbox.getCurrentRatio() * 2.5;
            double acceleration = force / getTotalWeight();
            speed += acceleration;
        }
    }

    public void brake() {
        speed = Math.max(0, speed - 1);
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
    public Model getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "rpm:" + engine.getCurrentRPM() + "\n"
                + "speed:" + (int) speed + "\n"
                + "gear:" + gearbox.getCurrentGear();
    }

    public int getObroty() {
        return engine.getCurrentRPM();
    }
    public void setPosition(Position position) {
        this.position = position;
    }
    public void setFacing(float facing) {
        this.facing = facing;
    }

    public static class Builder {
        private Engine engine;
        private Position position;
        private Gearbox gearbox;
        private String registrationNumber;
        private String modelName;
        private float facing;
        private float tireRadius;
        private int weight;
        private Model model;
        public Builder(Model model) {
            engine=new Engine("basic",400,500);
            gearbox=new Gearbox("basic",200,1000);
            registrationNumber="ABC123";
            modelName="Basic Car";
            facing=0;
            position=new Position(0,0);
            tireRadius=40;
            weight=800;
            this.model=model;
        }

        public Builder setEngine(Engine engine) {
            this.engine = engine;
            return this;
        }
        public Builder setPosition(Position position) {
            this.position = position;
            return this;
        }
        public Builder setGearbox(Gearbox gearbox) {
            this.gearbox = gearbox;
            return this;
        }
        public Builder setRegistrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
            return this;
        }
        public Builder setModelName(String modelName) {
            this.modelName = modelName;
            return this;
        }
        public Builder setFacing(float facing) {
            this.facing = facing;
            return this;
        }
        public Builder setTireRadius(float tireRadius) {
            this.tireRadius = tireRadius;
            return this;
        }
        public Builder setWeight(int weight) {
            this.weight = weight;
            return this;
        }
        public Car build() {
            return new Car(this);
        }
    }

}