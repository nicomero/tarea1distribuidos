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
                        //Recibir el paquete para determinar lo que el cliente quiere
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);

                        String received_D = recibir(packet);
                        System.out.println("Quote of the Moment: " + received_D);

                        //en caso de que quiera ip multicast
                        enviarIp_multi("trost", packet);

                } catch (IOException e) {
                        e.printStackTrace();
                        moreQuotes = false;
                }
        }
        socket.close();
}


//enviar ip multicast
public void enviarIp_multi(String distrito, DatagramPacket packet){

        //extraer puerto y direccion
        InetAddress address = packet.getAddress();
        int port = packet.getPort();

        //enviar ip del distrito que solicito
        enviarU("230.0.0.1", address, port);

}


//enviar un mensaje a la direccion y puerto dados
public void enviarU(String mensaje, InetAddress ip_destino, int port){


        try{
                byte[] buf = new byte[256];

                buf = mensaje.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ip_destino, port);
                socket.send(packet);
        }catch(IOException e) {
                e.printStackTrace();
        }
}


//recibe un mensaje en formato string para retornarlo
public String recibir(DatagramPacket packet){



        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        return received;

}



}
