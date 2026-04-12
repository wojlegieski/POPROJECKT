import java.io.*;
import java.net.*;

public class portConnect {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void connect(int port) {
        try {
            System.out.println("Java: Oczekiwanie na połączenie z Pythonem na porcie " + port + "...");
            serverSocket = new ServerSocket(port);
            // Program zatrzyma się tutaj, dopóki nie uruchomisz skryptu Python
            clientSocket = serverSocket.accept();

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Java: Połączono z Pythonem!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(String data) {
        if (out != null) out.print(data); // Wysyłamy JSON
    }

    public String receiveData() {
        try {
            if (in != null && in.ready()) {
                return in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}