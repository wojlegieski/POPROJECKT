import MyMath.MPoint3d;
import MyMath.MVector3D;
import MyMath.Model;
import samochod.Car;
import samochod.Position;


public class GraphcSystem {
    private  GameFrame gameFrame;
    private MPoint3d light;
    float focalLength;

    public GraphcSystem(GameFrame gameFrame, MPoint3d light, float focalLength) {
        this.gameFrame = gameFrame;
        this.focalLength=focalLength;
        this.light=light;
    }
    public void render(Car car, int currentCamera, double time, Position lastPosition){
        Model carModel=car.getModel();
        gameFrame.setText("time"+String.format("%.1f\n",time)+car);

        MVector3D v = new MVector3D(lastPosition.getX(),lastPosition.getY(),0).revese().setlength(car.getCurrentSpeed()*2);
        switch (currentCamera){
            case 0:
                gameFrame.render(
                        carModel.rotate(car.getFacing()).moveby(car.getPosition().getX(),car.getPosition().getY())
                        ,light,focalLength,
                        new MPoint3d(car.getPosition().getX() - 20, car.getPosition().getY(), 80),  //camera
                        new MPoint3d(car.getPosition().getX() + 20, car.getPosition().getY(), 20),   //facing
                        new MPoint3d(car.getPosition().getX() + 1, car.getPosition().getY(), 300));  //top
                break;
            case 1:
                gameFrame.renderv(carModel.rotate(car.getFacing()).moveby(car.getPosition().getX(),
                                car.getPosition().getY()), light, focalLength-car.getCurrentSpeed()*3,
                        new MPoint3d(car.getPosition().getX(), car.getPosition().getY(), 80),  //camera
                        new MVector3D(lastPosition.getX(),lastPosition.getY(),-3),   //facing
                        new MPoint3d(car.getPosition().getX()+ 1, car.getPosition().getY(), 300));  //top
                break;
            case 2:
                gameFrame.renderv(carModel.rotate(car.getFacing()).moveby(car.getPosition().getX(),
                                car.getPosition().getY()), light, focalLength-car.getCurrentSpeed()*3,
                        new MPoint3d(car.getPosition().getX()+v.getX(), car.getPosition().getY()+v.getY(), Math.max(20,80-car.getCurrentSpeed())),  //camera
                        new MVector3D(lastPosition.getX(),lastPosition.getY(),-3),   //facing
                        new MPoint3d(car.getPosition().getX()+v.getX()+ 1, car.getPosition().getY()+v.getY(), 300));  //top
                break;

        }

    }

    public void displayEndScreen(double time) {
        gameFrame.endScreen(time);
    }
}
