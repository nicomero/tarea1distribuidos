import java.io.*;
import java.net.*;
import java.util.*;

public class mainServerThread extends Thread {

protected DatagramSocket socket = null;
protected BufferedReader in = null;
protected boolean moreQuotes = true;
//protected List<String> distritos = new ArrayList<String>("Trost","Shiganshina");

public mainServerThread() throws IOException {
        this("QuoteServerThread");
}

public mainServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
}

public void run() {

        while (moreQuotes) {
                try {
                        byte[] buf = new byte[256];

                        // receive request
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);

                        //entregar ip a cliente
                        String ipCity = "127.0.0.1";
                        buf = ipCity.getBytes();

                        // send the response to the client at "address" and "port"
                        InetAddress address = packet.getAddress();
                        int port = packet.getPort();

                        packet = new DatagramPacket(buf, buf.length, address, port);
                        socket.send(packet);

                } catch (IOException e) {
                        e.printStackTrace();
                        moreQuotes = false;
                }
        }
        socket.close();
}

}
