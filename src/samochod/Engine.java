package samochod;

public class Engine extends CarPart {
    private static final int DEFAULT_MAX_RPM = 7000;
    private static final int IDLE_RPM = 500;
    private static final int STOP_RPM_THRESHOLD = 200;
    private static final double POWER_CONSTANT = 2d * Math.PI / 60d;

    private int maxRPM;
    private int currentRPM;
    private double[] torqueCurve;

    public Engine(int maxRPM, String name, int weight, int price, double[] torqueCurve) {
        super(name, weight, price);
        this.maxRPM = maxRPM;
        this.torqueCurve = torqueCurve;
        this.currentRPM = 0;
    }

    public Engine(String name, int weight, int price) {
        super(name, weight, price);
        this.maxRPM = DEFAULT_MAX_RPM;
        this.torqueCurve = initializeTorqueCurve(this.maxRPM);
    }

    private double[] initializeTorqueCurve(int maxRPM) {
        double[] torqueCurve = new double[maxRPM];
        for (int rpm = 0; rpm < maxRPM; rpm++) {
            double a = rpm * 0.001;
            torqueCurve[rpm] = -0.02 * a * a * a + 0.5 * a * a + 80 * a + 200;
        }
        return torqueCurve;
    }

    public void start() {
        if (currentRPM == 0) {
            currentRPM = IDLE_RPM;
        }
    }

    public void stop() {
        currentRPM = 0;
    }

    public void increaseRPM(float rpmIncrease) {
        if (currentRPM + rpmIncrease < maxRPM) {
            currentRPM += rpmIncrease;
        }
    }

    public void decreaseRPM(float rpmDecrease) {
        if (currentRPM - rpmDecrease < STOP_RPM_THRESHOLD) {
            stop();
        } else if (currentRPM - rpmDecrease < IDLE_RPM) {
            currentRPM = IDLE_RPM;
        } else {
            currentRPM -= rpmDecrease;
        }
    }

    public int getCurrentRPM() {
        return currentRPM;
    }

    public double calculateMaxPower() {
        double maxPower = 0;
        for (int rpm = 0; rpm < torqueCurve.length; rpm++) {
            double power = torqueCurve[rpm] * rpm * POWER_CONSTANT;
            if (power > maxPower) {
                maxPower = power;
            }
        }
        return maxPower;
    }

    public double getCurrentTorque() {
        return torqueCurve[Math.abs(currentRPM)];
    }
    public void setCurrentRPM(int currentRPM) {
        this.currentRPM = currentRPM;
    }
    public int getMaxRPM() {
        return maxRPM;
    }
}
