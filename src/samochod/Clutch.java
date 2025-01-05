package samochod;

public class Clutch extends CarPart {
    private boolean isEngaged;
    public Clutch(String nazwa, int waga, int cena) {
        super(nazwa, waga, cena);
        isEngaged = false;
    }
    public void engage(){
        isEngaged = true;
    }
    public void disengage(){
        isEngaged = false;
    }
    public boolean isEngaged(){
        return isEngaged;
    }
}
