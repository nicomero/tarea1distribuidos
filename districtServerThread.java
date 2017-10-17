import java.io.*;
import java.net.*;
import java.util.*;

public class districtServerThread extends Thread {

private long FIVE_SECONDS = 5000;
protected DatagramSocket socket = null;
protected BufferedReader in = null;
protected boolean moreQuotes = true;

public districtServerThread() throws IOException {
        this("MulticastServerThread");
}

public districtServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);

}



public void run() {
        while (moreQuotes) {
                try {
                        byte[] buf = new byte[256];

                        // construct quote
                        String dString = null;
                        dString = new Date().toString();

                        buf = dString.getBytes();

                        // send it
                        InetAddress group = InetAddress.getByName("230.0.0.1");
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
                        socket.send(packet);

                        // sleep for a while
                        try {
                                sleep((long)(Math.random() * FIVE_SECONDS));
                        } catch (InterruptedException e) { }
                } catch (IOException e) {
                        e.printStackTrace();
                        moreQuotes = false;
                }
        }
        socket.close();
}
}
