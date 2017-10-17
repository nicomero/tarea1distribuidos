import java.io.*;
import java.net.*;
import java.util.*;

public class cliente {

public static void main(String[] args) throws IOException {

        //recordar de agregar la ip
        if (args.length != 1) {
                System.out.println("Usage: java cliente <hostname>");
                return;
        }

        /**CONECTARSE AL MAIN SERVER PARA OBTENER DISTRITO**/

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        // send request
        byte[] buf = new byte[256];
        InetAddress address = InetAddress.getByName(args[0]);

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("ip futura: " + received);

        // cerrar socket
        socket.close();



        /**CONECTARSE AL SERVIDOR DE UN DISTRITO**/

        MulticastSocket socketD = new MulticastSocket(4446);
        address = InetAddress.getByName(received);
        socketD.joinGroup(address);

        for (int i = 0; i < 5; i++) {

                //byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                socketD.receive(packet);



                String received_D = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Quote of the Moment: " + received_D);
        }

        socketD.leaveGroup(address);
        socketD.close();




}


}
