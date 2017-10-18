/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

        enviarU("prueba", args[0], socket);

        // get response

        String received = recibir(socket);
        System.out.println("----- ip futura: " + received);

        // cerrar socket
        socket.close();





        /**CONECTARSE AL SERVIDOR DE UN DISTRITO**/

        MulticastSocket socketD = new MulticastSocket(4446);
        InetAddress address = InetAddress.getByName(received);
        socketD.joinGroup(address);

        for (int i = 0; i < 5; i++) {

                String received_D = recibir(socketD);
                System.out.println("Quote of the Moment: " + received_D);
        }

        socketD.leaveGroup(address);
        socketD.close();


}


//envia un mensaje a la ip dada por cierto socket
public static void enviarU(String mensaje, String ip_destino, DatagramSocket socket){



        try{
                byte[] buf = new byte[256];

                InetAddress address = InetAddress.getByName(ip_destino);
                buf = mensaje.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445); //viene con puerto de defecto
                socket.send(packet);
        }catch(IOException e) {
                e.printStackTrace();
        }
}

//recibe un mensaje del socket
public static String recibir(DatagramSocket socket){


        try{
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // display response
                String received = new String(packet.getData(), 0, packet.getLength());
                return received;
        }catch(IOException e) {
                e.printStackTrace();
        }
        return "mensajegenerico";
}



}
