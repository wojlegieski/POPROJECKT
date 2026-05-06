import java.io.*;
import java.net.*;

public class portConnect {
    private DatagramSocket socket;
    private InetAddress clientAddress;
    private int clientPort;

    public void connect(int port) {
        try {
            System.out.println("Java: Oczekiwanie na pakiety UDP na porcie " + port + "...");
            socket = new DatagramSocket(port);
            // W UDP nie ma accept(). Adres klienta poznamy przy pierwszym odebranym pakiecie.
            socket.setSoTimeout(10); // Mały timeout, żeby nie blokować pętli gry
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendData(String data) {
        if (socket != null && clientAddress != null) {
            try {
                byte[] buffer = data.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                socket.send(packet);
            } catch (IOException e) {
                System.err.println("Błąd wysyłania pakietu UDP: " + e.getMessage());
            }
        }
    }

    public String receiveData() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            this.clientAddress = packet.getAddress();
            this.clientPort = packet.getPort();

            return new String(packet.getData(), 0, packet.getLength()).trim();
        } catch (SocketTimeoutException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}