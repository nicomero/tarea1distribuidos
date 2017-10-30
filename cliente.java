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

	//--------------------------------------------------------------------------

	public static void main(String[] args) throws IOException {
		//======================================================
        //	CONECTARSE AL MAIN SERVER
		//======================================================

		HashMap<String,List<String>> capturados = new HashMap<String,List<String>>();
		HashMap<String,List<String>> asesinados = new HashMap<String,List<String>>();

		String ipServer = "";
		String puertoServer = "";
		String nDistrito = "";
		boolean otroDist = true;
		int puertoCliente = 0;

		System.out.println ("[Cliente] Ingresar IP Servidor Central:");
		Scanner entradaScanner = new Scanner (System.in); //Creación de un objeto Scanner
		ipServer = entradaScanner.nextLine (); //Invocamos un método sobre un objeto Scanner

		System.out.println ("[Cliente] Ingresar Puerto Servidor Central:");
		puertoServer = entradaScanner.nextLine ();

		//Crea socket cliente
		DatagramSocket socket = new DatagramSocket();

		while(otroDist){//mientras quiera más distritos

			System.out.println ("[Cliente] Introducir Nombre de Distrito a Investigar:");
			nDistrito = entradaScanner.nextLine ();

			//======================================================
	        //	CONECTARSE AL SERVIDOR CENTRAL
			//======================================================

	        //Envía solicitud de info Multicast al servidor central
	        enviarU(nDistrito, ipServer, puertoServer, socket);

	        //Recibe la información del distrito
	        String received = recibir(socket);
			//[NombreDistrito,ipMulticast,puertoMulticast,ipPeticiones,puertoPeticiones]
			List<String> info = new ArrayList<String>(Arrays.asList(received.split(",")));

			//======================================================
	        //	CONECTARSE AL SERVIDOR DE UN DISTRITO
			//======================================================

			//puerto conexión multicast
			//info.get(2)=puerto multicast distrito
			//info.get(1)=ip multicast distrito
	        MulticastSocket socketD = new MulticastSocket(Integer.parseInt(info.get(2)));
	        InetAddress address = InetAddress.getByName(info.get(1));
	        socketD.joinGroup(address);

			detectarMulti(socketD);//detecta si hay algo escrito en el multicast mediante thread

			interfazCliente(socket, info, entradaScanner, nDistrito, capturados, asesinados);//muestra la interfaz al cliente

	        socketD.leaveGroup(address);
	        socketD.close();
		}//end while
	}

	//--------------------------------------------------------------------------

	public static void interfazCliente(DatagramSocket socket, List<String> info, Scanner entradaScanner, String nDistrito, HashMap<String,List<String>> capturados, HashMap<String,List<String>> asesinados){
		String input;
		boolean masTitan = true;
		int i;
		while(masTitan) {//mientras se quieran hacer acciones en el distrito actual

			System.out.println ("[Cliente]["+nDistrito+"] Consola");
			System.out.println ("[Cliente]["+nDistrito+"] (1) Listar Titanes");
			System.out.println ("[Cliente]["+nDistrito+"] (2) Cambiar Distrito");
			System.out.println ("[Cliente]["+nDistrito+"] (3) Capturar Titan");
			System.out.println ("[Cliente]["+nDistrito+"] (4) Asesinar Titan");
			System.out.println ("[Cliente]["+nDistrito+"] (5) Listar Titanes Capturados");
			System.out.println ("[Cliente]["+nDistrito+"] (6) Listar Titanes Asesinados");

			input = entradaScanner.nextLine();

			//info.get(3)=IP Peticiones
			//info.get(4)=Puerto Peticionoes
			if(input.equals("1")){//Listar titanes
				enviarU(input,info.get(3),info.get(4),socket);
				input=recibir(socket);
				System.out.println("[Cliente]["+nDistrito+"]LISTA DE TITANES:\n" + input);
			}
			else if (input.equals("2")){//cambiar distrito
				masTitan = false;
			}
			else if (input.equals("3")){//Capturar titan
				enviarU(input,info.get(3),info.get(4),socket);
				input=recibir(socket);
				//["",[a,b,c],"",[a,b,c],...]
				List<String> titanesDisponibles = new ArrayList<String>(Arrays.asList(input.split("/")));
				List<String> sublista = new ArrayList<String>();
				HashMap<String,List<String>> idDisponibles = new HashMap<String,List<String>>();
				System.out.println("Elija un ID");
				for(i=0;i<titanesDisponibles.size();i++){
					int b = i%2;
					if(b == 1){
						sublista = Arrays.asList(titanesDisponibles.get(i).split(","));
						idDisponibles.put(sublista.get(0),Arrays.asList(sublista.get(1),sublista.get(2)));
						System.out.println(sublista.get(0)+".- Nombre: "+sublista.get(1)+", Tipo: "+sublista.get(2));
					}
				}
				input = entradaScanner.nextLine();
				while(!idDisponibles.containsKey(input)){
					System.out.println("Por favor ingrese un ID valido");
					input = entradaScanner.nextLine();
				}

				capturados.put(input,idDisponibles.get(input));
				enviarU("3/"+input,info.get(3),info.get(4),socket);
				System.out.println("¡Se ha capturado el titan con éxito!");

			}
			else if (input.equals("4")){//Asesinar titan
				enviarU(input,info.get(3),info.get(4),socket);
				input=recibir(socket);
				//["",[a,b,c],"",[a,b,c],...]
				List<String> titanesDisponibles = new ArrayList<String>(Arrays.asList(input.split("/")));
				List<String> sublista = new ArrayList<String>();
				HashMap<String,List<String>> idDisponibles = new HashMap<String,List<String>>();
				System.out.println("Elija un ID");
				for(i=0;i<titanesDisponibles.size();i++){
					int b = i%2;
					if(b == 1){
						sublista = Arrays.asList(titanesDisponibles.get(i).split(","));
						idDisponibles.put(sublista.get(0),Arrays.asList(sublista.get(1),sublista.get(2)));
						System.out.println(sublista.get(0)+".- Nombre: "+sublista.get(1)+", Tipo: "+sublista.get(2));
					}
				}
				input = entradaScanner.nextLine();
				while(!idDisponibles.containsKey(input)){
					System.out.println("Por favor ingrese un ID valido");
					input = entradaScanner.nextLine();
				}

				asesinados.put(input,idDisponibles.get(input));
				enviarU("4/"+input,info.get(3),info.get(4),socket);
				System.out.println("¡Se ha asesinado el titan con éxito!");

			}
			else if (input.equals("5")){//Listar titanes capturados
				System.out.println("[Cliente]["+nDistrito+"] "+capturados+"\n");
			}
			else if (input.equals("6")){//Listar titanes asesinados
				System.out.println("[Cliente]["+nDistrito+"] "+asesinados+"\n");
			}
			else {
				System.out.println("[Cliente]["+nDistrito+"]Ingrese algo valido");
			}
		}
	}//end interfazCliente

	public static void detectarMulti(final MulticastSocket socketD){
		Thread t = new Thread(new Runnable(){

			public void run(){

				boolean flag = false; //indica si hay fallo en el socket

				while(!flag){//mientras NO halla fallo en el socket

				 	String received_D = recibir(socketD);
					flag = received_D.equals("Fallo");
					if (!flag){
						System.out.println(received_D);
					}
				}
			}
		});
		t.start();
	}//end detectarMulti

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

}//end class cliente
