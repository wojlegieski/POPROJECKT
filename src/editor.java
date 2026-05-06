import MyMath.*;
import samochod.*;
import track.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.Collections;
import java.io.IOException;

public class editor {
    static int TARGET_FPS = 90;
    static int FRAME_SKIP = 1;
    static long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS;
    static int ROAD_SCALE = 300;
    static int MODEL_UPSCALE_COUNT = 2;
    static Position startPositon=new Position(-300,150);
    static float startAngle=(float) Math.PI;

    // NOWA METODA: Skanuje folder w poszukiwaniu pasujących par plików
    static List<String> scanAvailableMaps(String folderPath) {
        List<String> availableIds = new ArrayList<>();
        File folder = new File(folderPath);

        // Jeśli folder nie istnieje, zwracamy pustą listę
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Katalog " + folderPath + " nie istnieje!");
            return availableIds;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();

                // Szukamy plików zaczynających się od "roads" i kończących na ".txt"
                if (name.startsWith("roads") && name.endsWith(".txt")) {
                    // Wyciągamy unikalny ID/Sufiks (np. z "roads1_test.txt" robi się "1_test")
                    String id = name.substring(5, name.length() - 4);

                    // Sprawdzamy, czy istnieje do niego "brat" w postaci pliku checkpoints
                    File checkFile = new File(folder, "checkpoints" + id + ".txt");
                    if (checkFile.exists()) {
                        availableIds.add(id);
                    } else {
                        System.out.println("UWAGA: Znaleziono drogę " + name + ", ale brakuje pliku checkpoints" + id + ".txt!");
                    }
                }
            }
        }

        // Sortujemy alfabetycznie, by przełączanie map było spójne i przewidywalne
        Collections.sort(availableIds);
        return availableIds;
    }


    public static void main(String[] args) {
        // --- 1. SKANOWANIE MAP PRZED STARTEM ---
        List<String> mapList = scanAvailableMaps("src/maps/");

        if (mapList.isEmpty()) {
            System.err.println("BŁĄD KRYTYCZNY: Nie znaleziono żadnych map (par plików roads + checkpoints) w src/maps/!");
            return; // Zatrzymujemy program, bo nie mamy co rysować
        }

        System.out.println("Znaleziono mapy: " + mapList);
        int currentMapIndex = 0; // Wskaźnik na element w mapList

        // Ustawiamy 10 FPS – idealne dla edytora, nie obciąży procesora
        int TARGET_FPS = 10;
        long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS;

        GameFrame a = new GameFrame();
        MPoint3d light = new MPoint3d(500, 100, 200);

        // Wrzucamy "ducha" modelu samochodu
        Model dummyCar = carModeling();

        float camX = -300;
        float camY = 150;
        float camZ = 1500;

        boolean wasTabNotPressed = true;
        boolean wasShiftNotPressed = true;

        System.out.println("Uruchomiono Edytor Map (10 FPS). Edytuj pliki tekstowe i zapisz (Ctrl+S), aby zobaczyć zmiany!");

        while (true) {
            long startTime = System.nanoTime();

            // --- 2. LOGIKA PRZEŁĄCZANIA ZABEZPIECZONA PRZED BŁĘDAMI ---
            if (a.isTapkeyPressed()) { // Przycisk TAB
                if (wasTabNotPressed) {
                    // Operator % sprawia, że po ostatniej mapie wracamy na początek (Index 0)
                    currentMapIndex = (currentMapIndex + 1) % mapList.size();
                    wasTabNotPressed = false;
                }
            } else {
                wasTabNotPressed = true;
            }

            if (a.isShiftPressed()) { // Przycisk SHIFT
                if (wasShiftNotPressed) {
                    // Przechodzimy wstecz. Jeśli jesteśmy na indeksie 0, przeskoczy na koniec listy
                    currentMapIndex = (currentMapIndex - 1 + mapList.size()) % mapList.size();
                    wasShiftNotPressed = false;
                }
            } else {
                wasShiftNotPressed = true;
            }

            // --- 3. GENEROWANIE NAZW PLIKÓW NA BAZIE INDEXU ---
            String currentMapId = mapList.get(currentMapIndex); // Pobiera np. "1", "2_test"
            String checkpointsFile = "src/maps/checkpoints" + currentMapId + ".txt";
            String roadsFile = "src/maps/roads" + currentMapId + ".txt";

            // WCZYTYWANIE PLIKÓW
            Checkpoint[] checkpoints = loadCheckpoints(checkpointsFile);
            ArrayList<Road> roads = loadMapRoad(roadsFile);

            a.setCheckpints(checkpoints);
            a.setRoads(roads.toArray(new Road[0]));

            // Pasek informacyjny (pokazuje ID, Index i aktualną kamerę)
            a.setText(String.format("MAPA [%d/%d]: '%s' | Kam: X: %.0f, Y: %.0f",
                    currentMapIndex + 1, mapList.size(), currentMapId, camX, camY));

            // Obsługa prostej kamery strzałkami i klawiszami Q/E
            if (a.isUpPressed()) camX += 40;
            if (a.isDownPressed()) camX -= 40;
            if (a.isLeftPressed()) camY += 40;
            if (a.isRightPressed()) camY -= 40;
            if (a.isQkeyPressed()) camZ += 40; // Oddalenie
            if (a.isEkeyPressed()) camZ -= 40; // Przybliżenie

            // Renderowanie
            a.render(
                    dummyCar.moveby(startPositon.getX(), startPositon.getY()),
                    light,
                    450,
                    new MPoint3d(camX, camY, camZ),
                    new MPoint3d(camX, camY, 0),
                    new MPoint3d(camX + 1, camY, camZ)
            );

            // Utrzymanie 10 FPS
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
                path.closePath(); // Zamykamy kształt pojedynczej drogi
                combinedArea.add(new Area(path));
            }
        }
        return combinedArea;
    }
}
