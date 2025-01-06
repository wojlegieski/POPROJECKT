import samochod.Car;


public class InputSystem {
    private boolean wasEKeyNotPressed = true;
    private boolean wasQKeyNotPressed = true;
    private boolean wasTabNotPressed = true;

    public void handleInput(Car car, GameFrame gameFrame, int cameraCount, int[] currentCamera) {
        if (gameFrame.isUpPressed()) {
            car.accelerate();
        }
        if (gameFrame.isDownPressed()) {
            car.brake();
        }
        if (gameFrame.isLeftPressed()) {
            car.left();
        }
        if (gameFrame.isRightPressed()) {
            car.right();
        }
        if (gameFrame.isShiftPressed()) {
            car.useClutch();
        } else {
            car.releaseClutch();
        }
        if (gameFrame.isQkeyPressed()) {
            if (wasQKeyNotPressed) {
                car.shiftDown();
                wasQKeyNotPressed = false;
            }
        } else {
            wasQKeyNotPressed = true;
        }
        if (gameFrame.isEkeyPressed()) {
            if (wasEKeyNotPressed) {
                car.shiftUp();
                wasEKeyNotPressed = false;
            }
        } else {
            wasEKeyNotPressed = true;
        }
        if (gameFrame.isTapkeyPressed()) {
            if (wasTabNotPressed) {
                currentCamera[0] = (currentCamera[0] + 1) % cameraCount;
                wasTabNotPressed = false;
            }
        } else {
            wasTabNotPressed = true;
        }
    }
}
