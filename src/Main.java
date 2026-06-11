import MyMath.*;
import samochod.*;
import track.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    static int TARGET_FPS = 90;
    static int FRAME_SKIP = 1;
    static long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS;
    static int ROAD_SCALE = 300;
    static int MODEL_UPSCALE_COUNT = 2;
    static Position startPositon=new Position(-300,150);
    static float startAngle=(float) Math.PI;
    static int targetLapsMap = 10;
    static float MAX_DIST = 3000;

    public static void main(String[] args) {
        int port = 5005;
        String mapMode = "5";

        if (args.length >= 2) {
            port = Integer.parseInt(args[0]);
            mapMode = args[1];
        }

        List<String> mapSequence = new ArrayList<>();

        if (mapMode.equals("all")) {
            mapSequence.add("1");
            mapSequence.add("7");
            mapSequence.add("10");
            mapSequence.add("9");
            mapSequence.add("8");
            mapSequence.add("6");
            mapSequence.add("2");
            mapSequence.add("3");
            mapSequence.add("4");
            mapSequence.add("5");
        }
        else {
            // Domyślnie tylko mapa 5
            mapSequence.add("5");
        }

        int currentMapIndex = 0;


        boolean allFilesExist = true;
        for (String suffix : mapSequence) {
            File checkFile = new File("src/maps/checkpoints" + suffix + ".txt");
            File roadsFile = new File("src/maps/roads" + suffix + ".txt");

            if (!checkFile.exists()) {
                System.err.println("BŁĄD: Brakuje pliku " + checkFile.getPath());
                allFilesExist = false;
            }
            if (!roadsFile.exists()) {
                System.err.println("BŁĄD: Brakuje pliku " + roadsFile.getPath());
                allFilesExist = false;
            }
        }

        if (!allFilesExist) {
            System.err.println("KRYTYCZNY BŁĄD: Nie znaleziono wszystkich wymaganych plików map. Zatrzymuję program!");
            return;
        }

        String currentMapSuffix = mapSequence.get(currentMapIndex);

        List<MPoint3d> lidarHitPoints = new ArrayList<>();
        boolean shouldReset = false; //TODO impement rest on button
        boolean wasEKeyNotPressed = true;
        boolean wasQKeyNotPressed = true;
        boolean wasTabNotPressed = true;
        int currentCamera = 0;
        int cameraCount = 2;
        int laps = 1;
        int curentlap=0;
        int curentwins=0;

        Car maluch = new Car.Builder()
                .setPosition(startPositon.copy())
                .setFacing(startAngle)
                .build();
        Model carModel = carModeling();
        String checkpointsFile = "src/maps/checkpoints" + currentMapSuffix + ".txt";
        String roadsFile = "src/maps/roads" + currentMapSuffix + ".txt";
        Checkpoint[] checkpoints = loadCheckpoints(checkpointsFile);
        Checkpoint meta = checkpoints[checkpoints.length-1];
        ArrayList<Road> roads = loadMapRoad(roadsFile);
        Area trackArea = mergeRoadsToSingleArea(roads);
        MPoint3d light = new MPoint3d(500, 100, 200);
        GameFrame a = new GameFrame();;
        a.setCheckpints(checkpoints);
        a.setRoads(roads.toArray(new Road[roads.size()]));
        boolean end = false;
//            ToneGenerator toneThread = new ToneGenerator();
//            toneThread.start();
        boolean onroad=true;
        List<Position> povbuffor = new ArrayList<>();
        maluch.turnOn();
        double stime = System.nanoTime();

        portConnect bridge = new portConnect();
        bridge.connect(port);
        boolean remoteUp = false;
        boolean remoteDown = false;
        boolean remoteLeft = false;
        boolean remoteRight = false;
        int iterrator = 0;

        int allCheckpoints = checkpoints.length;
        boolean isWaitingForReset = false;
        int waitFramesCount = 0;
        while (true) {
            long startTime = System.nanoTime();
            double time = ( System.nanoTime() - stime)/1_000_000_000;
//                toneThread.setFrequency(maluch.getObroty()/60);

            int zaliczoneCheckpoints = 0;
            float distToTarget = MAX_DIST;
            end = true;

            for(Checkpoint c : checkpoints) {
                if(c!=meta) {
                    c.isin(maluch.getPosition());
                    if (c.drivedon()) {
                        zaliczoneCheckpoints++;
                    } else {
                        end = false;

                        float currentDist = getDist(
                                maluch.getPosition().getX(), maluch.getPosition().getY(),
                                c.getPosition().getX(), c.getPosition().getY()
                        );

                        if (currentDist < distToTarget) {
                            distToTarget = currentDist;
                        }
                    }
                }
            }
            if(end) {
                meta.isin(maluch.getPosition());

                float currentDist = getDist(
                        maluch.getPosition().getX(), maluch.getPosition().getY(),
                        meta.getPosition().getX(), meta.getPosition().getY()
                );

                if (currentDist < distToTarget) {
                    distToTarget = currentDist;
                }

                if(meta.drivedon()) {
                    curentlap++;
                    if(curentlap==laps) {

                                isWaitingForReset = true;
                                waitFramesCount = 20;


//                                    toneThread=new ToneGenerator();
//                                    toneThread.start();
//                                    break;
//                                }
//                            }

                    }

                }
            }





            if (isWaitingForReset) {
                waitFramesCount--;
                if (waitFramesCount <= 0) {
                    isWaitingForReset = false;
                    curentwins++;
                    for (Checkpoint c : checkpoints) {
                        c.setwason(false);
                    }
                    if(curentwins==targetLapsMap) {
                        currentMapIndex=(currentMapIndex+1)%mapSequence.size();
                        currentMapSuffix = mapSequence.get(currentMapIndex);
                        checkpointsFile = "src/maps/checkpoints" + currentMapSuffix + ".txt";
                        roadsFile = "src/maps/roads" + currentMapSuffix + ".txt";
                        checkpoints = loadCheckpoints(checkpointsFile);
                        meta = checkpoints[checkpoints.length-1];
                        roads = loadMapRoad(roadsFile);
                        a.setCheckpints(checkpoints);
                        allCheckpoints = checkpoints.length;
                        a.setRoads(roads.toArray(new Road[roads.size()]));
                        trackArea = mergeRoadsToSingleArea(roads);
                        curentwins=0;
                    }



                    end = false;
                    reset(maluch);
                    stime = System.nanoTime();
                    curentlap=0;
                }
            }


            Position tpow = new Position(0,0);
            tpow.movepolar(maluch.getFacing(),3);
            povbuffor.add(tpow);
            Position pov;
            if (povbuffor.size() >= 15) {
                povbuffor.removeFirst();
            }
            pov = povbuffor.getFirst();
            switch (currentCamera) {
                case 0:
                    a.render(carModel.rotate(maluch.getFacing()).moveby(maluch.getPosition().getX(),
                                    maluch.getPosition().getY()), light, 450,
                            new MPoint3d(maluch.getPosition().getX() - 20, maluch.getPosition().getY(), 80),  //camera
                            new MPoint3d(maluch.getPosition().getX() + 20, maluch.getPosition().getY(), 20),   //facing
                            new MPoint3d(maluch.getPosition().getX() + 1, maluch.getPosition().getY(), 300));  //top
                    break;
                case 1:
                    a.renderv(carModel.rotate(maluch.getFacing()).moveby(maluch.getPosition().getX(),
                                    maluch.getPosition().getY()), light, 450,
                            new MPoint3d(maluch.getPosition().getX() - 20, maluch.getPosition().getY(), 80),  //camera
                            new MVector3D(pov.getX(),pov.getY(),-3),   //facing
                            new MPoint3d(maluch.getPosition().getX() + 1, maluch.getPosition().getY(), 300));  //top
                    break;
            }


            float[] angles = {-2.4f,-1.57f, -0.78f, -0.35f, 0, 0.35f, 0.78f, 1.57f,2.4f, (float) Math.PI}; // Kąty lidaru
            StringBuilder lidarDisplay = new StringBuilder();
            StringBuilder lidarJson = new StringBuilder("[");

            lidarHitPoints.clear();

            for (int i = 0; i < angles.length; i++) {
                int degrees = (int) Math.toDegrees(angles[i]);
                float dist =  getDistanceToEdge(maluch.getPosition(), maluch.getFacing() + angles[i], trackArea);
                lidarJson.append((int) dist);
                lidarDisplay.append(String.format("%d° : %.1f\n", degrees, dist));
                if (i < angles.length - 1) lidarJson.append(",");
                Position hitPos = new Position(maluch.getPosition().getX(), maluch.getPosition().getY());
                if (onroad){
                    hitPos.movepolar(maluch.getFacing() + angles[i], dist);}
                else{
                    hitPos.movepolar(maluch.getFacing() + angles[i], -dist);
                }

                lidarHitPoints.add(hitPos.toMPoint3d());
            }
            a.setLidarHitPoints(lidarHitPoints);



            lidarJson.append("]");


            if (isWaitingForReset) {
                onroad = true;
            }
            String jsonMsg = String.format(java.util.Locale.US,
                    "{\"x\":%.2f, \"y\":%.2f, \"rpm\":%d, \"speed\":%.2f, \"lidar\":%s, \"onRoad\":%b, \"checkpoints\":%d, \"targetDist\":%.2f, \"allCheckpoints\":%d}\n",
                    maluch.getPosition().getX(),
                    maluch.getPosition().getY(),
                    maluch.getObroty(),
                    maluch.getSpeed(),
                    lidarJson,
                    onroad,
                    zaliczoneCheckpoints,
                    distToTarget,
                    allCheckpoints
            );

            if (maluch.getObroty() > 6700){
                maluch.useClutch();
                maluch.shiftUp();
                maluch.releaseClutch();
            }
            if (maluch.getObroty()<5200 && maluch.getCurrentGear()>1) {
                maluch.useClutch();
                maluch.shiftDown();
                maluch.releaseClutch();
            }

            if(iterrator ==0){bridge.sendData(jsonMsg + "\n");

                String cmd;
                while ((cmd = bridge.receiveData()) != null) {
                    cmd = cmd.trim();
                    if (cmd.equals("RESET")) {
                        if (isWaitingForReset) {
                            curentwins++;
                            if(curentwins >= targetLapsMap) {
                                currentMapIndex = (currentMapIndex + 1) % mapSequence.size();
                                currentMapSuffix = mapSequence.get(currentMapIndex);
                                checkpointsFile = "src/maps/checkpoints" + currentMapSuffix + ".txt";
                                roadsFile = "src/maps/roads" + currentMapSuffix + ".txt";
                                checkpoints = loadCheckpoints(checkpointsFile);
                                meta = checkpoints[checkpoints.length-1];
                                roads = loadMapRoad(roadsFile);
                                a.setCheckpints(checkpoints);
                                allCheckpoints = checkpoints.length;
                                a.setRoads(roads.toArray(new Road[roads.size()]));
                                trackArea = mergeRoadsToSingleArea(roads);
                                curentwins = 0;
                            }
                        }
                        reset(maluch);
                        remoteUp = false; remoteDown = false;
                        remoteLeft = false; remoteRight = false;
                        stime = System.nanoTime();
                        for(Checkpoint c:checkpoints){
                            c.setwason(false);
                        }
                        curentlap = 0;
                        isWaitingForReset = false;
                        continue;
                    }

                    switch(cmd) {
                        case "UP_ON":      remoteUp = true; break;
                        case "UP_OFF":     remoteUp = false; break;
                        case "DOWN_ON":    remoteDown = true; break;
                        case "DOWN_OFF":   remoteDown = false; break;
                        case "LEFT_ON":    remoteLeft = true; break;
                        case "LEFT_OFF":   remoteLeft = false; break;
                        case "RIGHT_ON":   remoteRight = true; break;
                        case "RIGHT_OFF":  remoteRight = false; break;
                    }
                }
            }

            a.gamePanel.arrowUp = remoteUp;
            a.gamePanel.arrowDown = remoteDown;
            a.gamePanel.arrowLeft = remoteLeft;
            a.gamePanel.arrowRight = remoteRight;
            String displayStats = String.format(
                    "--- CAR STATS ---\n" +
                            "TIME: %.1f\n" +
                            "SPEED: %.2f | RPM: %d\n" +
                            "ON ROAD: %b\n" +
                            "CP: %d / %d | DIST: %.2f\n" +
                            "--- LIDAR ---\n" +
                            "%s",
                    time,
                    maluch.getSpeed(),
                    maluch.getObroty(),
                    onroad,
                    zaliczoneCheckpoints,
                    allCheckpoints,
                    distToTarget,
                    lidarDisplay
            );
            iterrator = (iterrator + 1) % FRAME_SKIP;
            a.setText(displayStats);
            if (remoteUp) {
                maluch.accelerate();
            }
            if (remoteDown) {
                maluch.brake();
            }
            if (remoteLeft) {
                maluch.left();
            }
            if (remoteRight) {
                maluch.right();
            }
            if (a.isShiftPressed()) {
                maluch.useClutch();
            } else maluch.releaseClutch();
            if (a.isQkeyPressed()) {
                if (wasQKeyNotPressed) {
                    maluch.shiftDown();
                    wasQKeyNotPressed = false;
                }
            } else {
                wasQKeyNotPressed = true;
            }
            if (a.isTapkeyPressed()){
                if (wasTabNotPressed){
                    currentCamera=(currentCamera+1)%cameraCount;
                    wasTabNotPressed = false;
                }
            }
            else {wasTabNotPressed = true;}
            if (a.isEkeyPressed()) {
                if (wasEKeyNotPressed) {
                    maluch.shiftUp();
                    wasEKeyNotPressed = false;
                }
            } else {
                wasEKeyNotPressed = true;
            }
            for (Road road : roads) {
                if(road.isPointInsidePolygon(maluch.getPosition())){
                    onroad = true;
                    break;
                }
                onroad = false;
            }
            maluch.move(onroad);
            long elapsedTime = System.nanoTime() - startTime;
            long sleepTime = OPTIMAL_TIME - elapsedTime;


            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    static Checkpoint[] setCheckpoints(){
        return new Checkpoint[]{
                new Checkpoint(new Position(900,-1325),50,400),
                new Checkpoint(new Position(1500,-800),200,50),
                new Checkpoint(new Position(-100,-700),50,200),
                new Checkpoint(new Position(675,700),40,125),
                new Checkpoint(new Position(-700,-300),200,50),
                new Checkpoint(new Position(-150,150),50,200,Color.red)
        };
    }
    static ArrayList<Road> mapRoad(){
        ArrayList<Road> roads = new ArrayList<>();
        roads.add(Roadcreator.straight(new Point2D.Double(750,300),100,600).upscale(ROAD_SCALE));
        roads.add(Roadcreator.trun(new Point2D.Double(-600,50),400).revesey().upscale(ROAD_SCALE));
        roads.add(Roadcreator.trun(new Point2D.Double(-600,-800),400).upscale(ROAD_SCALE));
        roads.add(Roadcreator.straight(new Point2D.Double(-700,-375),200,450).upscale(ROAD_SCALE));
        roads.add(Roadcreator.trun(new Point2D.Double(-200,-800),400).revesex().upscale(ROAD_SCALE));
        roads.add(Roadcreator.straight(new Point2D.Double(0,-700),400,200).revesex().upscale(ROAD_SCALE));
        roads.add(Roadcreator.longtrun(new Point2D.Double(600,-1000),800).revesey().upscale(ROAD_SCALE));
        roads.add(Roadcreator.longutrun(new Point2D.Double(1200,-1600),800).flip().upscale(ROAD_SCALE));
        roads.add(Roadcreator.straight(new Point2D.Double(1500,-1000),200,800).upscale(ROAD_SCALE));
        roads.add(Roadcreator.longtrun(new Point2D.Double(1200,-200),800).revesey().upscale(ROAD_SCALE));
        roads.add(Roadcreator.longutrun(new Point2D.Double(600,700),400).flip().revesey().upscale(ROAD_SCALE));
        roads.add(Roadcreator.trun(new Point2D.Double(400,500),200).revesex().upscale(ROAD_SCALE));
        roads.add(Roadcreator.trun(new Point2D.Double(200,400),200).revesey().upscale(ROAD_SCALE));
        roads.add(Roadcreator.trun(new Point2D.Double(100,200),200).revesex().upscale(ROAD_SCALE));
        roads.add(Roadcreator.narrowing(new Point2D.Double(-200,150),100,200,400).upscale(ROAD_SCALE));
        return roads;
    }




    static float getDist(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }


    static ArrayList<Road> loadMapRoad(String filename) {
        ArrayList<Road> roads = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(" ");
                String type = parts[0].toLowerCase();
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                Road road = null;
                switch (type) {
                    case "straight":
                        road = Roadcreator.straight(new Point2D.Double(x, y), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                        break;
                    case "trun":
                        road = Roadcreator.trun(new Point2D.Double(x, y), Integer.parseInt(parts[3]));
                        break;
                    case "longtrun":
                        road = Roadcreator.longtrun(new Point2D.Double(x, y), Integer.parseInt(parts[3]));
                        break;
                    case "longutrun":
                        road = Roadcreator.longutrun(new Point2D.Double(x, y), Integer.parseInt(parts[3]));
                        break;
                    case "narrowing":
                        road = Roadcreator.narrowing(new Point2D.Double(x, y), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
                        break;
                }

                if (road != null) {
                    for (String part : parts) {
                        String mod = part.toLowerCase();
                        if (mod.equals("revesex")) road = road.revesex();
                        else if (mod.equals("revesey")) road = road.revesey();
                        else if (mod.equals("revesexy")) road = road.revesexy();
                        else if (mod.equals("flip")) road = road.flip();
                    }
                    road = road.upscale(ROAD_SCALE);
                    roads.add(road);
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd wczytywania trasy z pliku: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Błąd formatu liczby w pliku trasy: " + e.getMessage());
        }
        return roads;
    }



    static Checkpoint[] loadCheckpoints(String filename) {
        ArrayList<Checkpoint> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Ignoruj puste linie i komentarze (#)
                if (line.trim().isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",");
                float x = Float.parseFloat(parts[0].trim());
                float y = Float.parseFloat(parts[1].trim());
                int w = Integer.parseInt(parts[2].trim());
                int h = Integer.parseInt(parts[3].trim());

                // Sprawdzanie, czy zdefiniowano kolor (7 parametrów)
                if (parts.length >= 7) {
                    int r = Integer.parseInt(parts[4].trim());
                    int g = Integer.parseInt(parts[5].trim());
                    int b = Integer.parseInt(parts[6].trim());
                    list.add(new Checkpoint(new Position(x, y), w, h, new Color(r, g, b)));
                } else {
                    list.add(new Checkpoint(new Position(x, y), w, h));
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd wczytywania checkpointów z pliku: " + e.getMessage());
        }
        return list.toArray(new Checkpoint[0]);
    }

//        ustawianie koloru
    static Model carModeling(){
        Model carModel = Modleling.rectangle(10, 20, 20, new MPoint3d(0, 0, 20), new Color(50, 80, 255, 132));
        carModel = carModel.merge(Modleling.rectangle(15, 20, 60, new MPoint3d(0, 0, 5), Color.red));
//            for (int i = 0; i <MODEL_UPSCALE_COUNT; i++) {
//                carModel.upscale();
//            }
        carModel = carModel.merge(Modleling.rectangle(10, 5, 10, new MPoint3d(10, 20, 0), Color.BLACK));
        carModel = carModel.merge(Modleling.rectangle(10, 5, 10, new MPoint3d(-10, 20, 0), Color.BLACK));
        carModel = carModel.merge(Modleling.rectangle(10, 5, 10, new MPoint3d(-10, -20, 0), Color.BLACK));
        carModel = carModel.merge(Modleling.rectangle(10, 5, 10, new MPoint3d(10, -20, 0), Color.BLACK));
        return carModel;
    }

    static void reset(Car car){
        car.setPosition(startPositon.copy());
        car.setFacing(startAngle);
        car.stop()  ;
        car.setGear(1);
    }

    public static float getDistanceToEdge(Position carPos, float angle, Area trackArea) {
        float maxDist = 2000.0f; // Maksymalny zasięg "wzroku"
        float step = 5.0f;      // Dokładność pomiaru

        boolean startsOnTrack = trackArea.contains(carPos.getX(), carPos.getY());

        for (float d = 0; d < maxDist; d += step) {
            Position checkPoint = new Position(carPos.getX(), carPos.getY());
            checkPoint.movepolar(angle, d);
            boolean currentPointOnTrack = trackArea.contains(checkPoint.getX(), checkPoint.getY());

            if (startsOnTrack) {
                if (!currentPointOnTrack) {
                    return d;
                }
            } else {

                if (currentPointOnTrack) {
                    return -d;
                }
            }
        }
        if (startsOnTrack) {
            return maxDist;
        } else {
            return -maxDist;
        }
    }

    static Area mergeRoadsToSingleArea(ArrayList<Road> roads) {
        Area combinedArea = new Area();

        for (Road r : roads) {
            Point2D.Double[] poly = r.getPolygon();
            if (poly.length > 0) {
                Path2D.Double path = new Path2D.Double();
                path.moveTo(poly[0].getX(), poly[0].getY());

                for (int i = 1; i < poly.length; i++) {
                    path.lineTo(poly[i].getX(), poly[i].getY());
                }
                path.closePath();
                combinedArea.add(new Area(path));
            }
        }
        return combinedArea;
    }
}