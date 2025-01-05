package samochod;

public abstract class CarPart {
    String nazwa;
    int waga;
    int cena;
    public CarPart(String nazwa, int waga, int cena) {
        this.nazwa = nazwa;
        this.waga = waga;
        this.cena = cena;
    }
    public String getNazwa() {
        return nazwa;
    }
    public int getWaga() {
        return waga;
    }
    public int getCena() {
        return cena;
    }

}
