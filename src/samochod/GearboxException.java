package samochod;

class GearboxException extends Exception {
    // Parameterless Constructor
    public GearboxException() {}

    // Constructor that accepts a message
    public GearboxException(String message)
    {
        super(message);
    }
}
