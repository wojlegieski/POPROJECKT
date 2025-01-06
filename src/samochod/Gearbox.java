package samochod;

public class Gearbox extends CarPart {
    private static final float[] DEFAULT_RATIOS = {3.5f, 2.23f, 1.52f, 1.14f, 0.97f, 0.87f};
    private Clutch clutch;
    private int currentGear;
    private int totalGears;
    private float[] gearRatios;
    private float finalDriveRatio;

    // Constructor with default clutch and ratios
    public Gearbox(String name, int weight, int price) {
        this(name, weight, price, new Clutch("basic", 10, 204), DEFAULT_RATIOS);
    }

    // Constructor with custom clutch and ratios
    public Gearbox(String name, int weight, int price, Clutch clutch, float[] gearRatios) {
        super(name, weight, price);
        this.clutch = clutch;
        this.gearRatios = gearRatios;
        this.totalGears = gearRatios.length; // Dynamically calculate total gears
        this.currentGear = 1;
        this.finalDriveRatio = 4.3f;
    }


    public float getCurrentRatio() {
        if (clutch.isEngaged()) {
            return 0;
        }

        if (currentGear > 0) {
            return gearRatios[currentGear - 1]*finalDriveRatio;
        } else if (currentGear == 0) {
            return 0;
        }
        return -gearRatios[0]*finalDriveRatio;
    }

    public int getCurrentGear() {
        return currentGear;
    }

    public void downshift() {
        try {
            if (!clutch.isEngaged()) {
                throw new GearboxException("Clutch is not engaged!");
            }
            if (currentGear > -1) {
                currentGear--;
            }
        } catch (GearboxException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setCurrentGear(int gear){
        currentGear = gear;
    }

    public void upshift() {
        try {
            if (!clutch.isEngaged()) {
                throw new GearboxException("Clutch is not engaged!");
            }
            if (currentGear < totalGears) {
                currentGear++;
            }
        } catch (GearboxException e) {
            System.out.println(e.getMessage());
        }
    }

    public void engageClutch(){
        clutch.engage();
    }
    public void disengageClutch(){
        clutch.disengage();
    }
    public boolean isClutchEngaged(){
        return clutch.isEngaged();
    }



}
