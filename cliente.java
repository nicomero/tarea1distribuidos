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
		//======================================================
        //	CONECTARSE AL MAIN SERVER
		//======================================================

		String ipServer = "";
		String puertoServer = "";
		String nDistrito = "";
		boolean otroDist = true;

		System.out.println ("[Cliente] Ingresar IP Servidor Central:");
		Scanner entradaEscaner = new Scanner (System.in); //Creación de un objeto Scanner
		ipServer = entradaEscaner.nextLine (); //Invocamos un método sobre un objeto Scanner

		System.out.println ("[Cliente] Ingresar Puerto Servidor Central:");
		puertoServer = entradaEscaner.nextLine ();

		while(otroDist){ //mientras quiera más distritos

			boolean masTitan = true;

			System.out.println ("[Cliente] Introducir Nombre de Distrito a Investigar:");
			nDistrito = entradaEscaner.nextLine ();

	        //Envía solicitud de info Multicast al servidor central
	        DatagramSocket socket = new DatagramSocket();
	        enviarU(nDistrito, ipServer, puertoServer, socket);

	        //Recibe la información del distrito
	        String received = recibir(socket);
			//[NombreDistrito,ipMulticast,puertoMulticast,ipPeticiones,puertoPeticiones]
			List<String> info = new ArrayList<String>(Arrays.asList(received.split(",")));
			System.out.println(info);
	        // cerrar socket
	        socket.close();

			//======================================================
	        //	CONECTARSE AL SERVIDOR DE UN DISTRITO
			//======================================================

			//puerto conexión multicast
			//info.get(2)=puerto multicast distrito
			//info.get(1)=ip multicast distrito
	        MulticastSocket socketD = new MulticastSocket(Integer.parseInt(info.get(2)));
	        InetAddress address = InetAddress.getByName(info.get(1));
	        socketD.joinGroup(address);

			socket = new DatagramSocket();
			detectarMulti(socketD);//detecta si hay algo escrito en el multicast

			String input;

	        while(masTitan) {//mientras se quieran hacer acciones en el distrito actual

	            System.out.println ("[Cliente] Consola");
				System.out.println ("[Cliente] (1) Listar Titanes");
				System.out.println ("[Cliente] (2) Cambiar Distrito");
				System.out.println ("[Cliente] (3) Capturar Titan");
				System.out.println ("[Cliente] (4) Asesinar Titan");
				System.out.println ("[Cliente] (5) Listar Titanes Capturados");
				System.out.println ("[Cliente] (6) Listar Titanes Asesinados");

				input = entradaEscaner.nextLine();
				//info.get(3)=IP Peticiones
				//info.get(4)=Puerto Peticionoes

				if(input.equals("1")){//Listar titanes
					enviarU(input,info.get(3),info.get(4),socket);
					input=recibir(socket);
					System.out.println ("[Cliente] Lo que llego fue:--" + input);
				}
				else if (input.equals("2")){//cambiar distrito
					masTitan = false;
				}
				else if (input.equals("3")){//Capturar titan
					enviarU(input,info.get(3),info.get(4),socket);
					input=recibir(socket);
					System.out.println ("[Cliente] Lo que llego fue:--" + input);
				}
				else if (input.equals("4")){//Asesinar titan
					enviarU(input,info.get(3),info.get(4),socket);
					input=recibir(socket);
					System.out.println ("[Cliente] Lo que llego fue:--" + input);
				}
				else if (input.equals("5")){//Listar titanes capturados
					System.out.println ("[Cliente] Titanes capturados");
				}
				else if (input.equals("6")){//Listar titanes asesinados
					System.out.println ("[Cliente] Titanes asesinados");
				}
				else {
					System.out.println ("[Cliente] Ingrese algo valido");
				}
	        }

	        socketD.leaveGroup(address);
	        socketD.close();
			socket.close();
		}//end while
	}

	//envia un mensaje a la ip dada por cierto socket
	public static void enviarU(String mensaje, String ip_destino, String puerto_destino, DatagramSocket socket){

        try{
            byte[] buf = new byte[256];
            InetAddress address = InetAddress.getByName(ip_destino);
            buf = mensaje.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(puerto_destino));
            socket.send(packet);
        }catch(IOException e) {
			System.out.println("enviarU cliente");
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
        }catch(IOException e) {}
        return "Fallo";
	}

	public static void detectarMulti(MulticastSocket socketD){
		Thread t = new Thread(new Runnable(){

			public void run(){

				boolean flag = false; //indica si hay fallo en el socket

				while(!flag){//mientras NO halla fallo en el socket

					String received_D = recibir(socketD);
					flag = received_D.equals("Fallo");
					if (!flag){
						System.out.println("Date: " + received_D);
					}
				}
			}
		});
		t.start();
	}

}//end class cliente
