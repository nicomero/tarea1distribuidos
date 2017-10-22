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

public class districtServerThread extends Thread {

	private long FIVE_SECONDS = 5000;
	protected DatagramSocket socket = null;
	protected BufferedReader in = null;
	protected boolean moreQuotes = true;
	int puerto;
	String nDistrito = "";
	String ipMulti = "";
	String puertoMulti = "";
	String puertoPeti = "";

	public districtServerThread() throws IOException {
		System.out.println ("[Distrito] Nombre Distrito:");
		Scanner entradaEscaner = new Scanner (System.in); //Creación de un objeto Scanner
		nDistrito = entradaEscaner.nextLine (); //Invocamos un método sobre un objeto Scanner

		System.out.println ("[Distrito "+nDistrito+"] IP Multicast:");
		ipMulti = entradaEscaner.nextLine (); //Invocamos un método sobre un objeto Scanner

		System.out.println ("[Distrito "+nDistrito+"] Puerto Multicast:");
		puertoMulti = entradaEscaner.nextLine (); //Invocamos un método sobre un objeto Scanner

		System.out.println ("[Distrito "+nDistrito+"] Puerto Peticiones:");
		puertoPeti = entradaEscaner.nextLine (); //Invocamos un método sobre un objeto Scanner

        socket = new DatagramSocket(Integer.parseInt(puertoPeti));
	}

	public void run() {
		inputLocal();
        while (moreQuotes) {



            String dString = new Date().toString();

            enviarU(dString+" "+nDistrito, ipMulti, socket);

            // sleep for a while
            try {
                sleep((long)(Math.random() * FIVE_SECONDS));
            } catch (InterruptedException e) { }
        }
        socket.close();
	}

	public void enviarU(String mensaje, String ip_destino, DatagramSocket socket){
        try{
            byte[] buf = new byte[256];

            InetAddress address = InetAddress.getByName(ip_destino);
            buf = mensaje.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(puertoMulti));
            socket.send(packet);
        }catch(IOException e) {
            System.out.println("ilprob");
            e.printStackTrace();
        }
	}

	public void inputLocal(){
		Thread t = new Thread(new Runnable(){
			public void run(){
				Scanner scan = new Scanner(System.in);
				String input;

				while(true){
					System.out.println("escoga opcion [Publicar titan]");
					input = scan.nextLine();

					if(input.equals("Publicar titan")){
						System.out.println("Todo bien c:");
					}
				}
			}
		});
		t.start();
	}

}//end districtServerThread
