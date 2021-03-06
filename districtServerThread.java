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
	protected boolean more = true;
	HashMap<Integer,List<String>> titanes = new HashMap<Integer,List<String>>();
	int puerto;
	String ipServer = "";
	String puertoServer = "";
	String nDistrito = "";
	String ipMulti = "";
	String ipPeti = "";
	String puertoMulti = "";
	String puertoPeti = "";
	String claveSecreta = "Xba1SS7lbAXmMe09aE12X2x";

	public districtServerThread() throws IOException {
		System.out.println ("[Distrito] Ingresar IP Servidor Central:");
		Scanner entradaScanner = new Scanner (System.in); //Creación de un objeto Scanner
		ipServer = entradaScanner.nextLine (); //Invocamos un método sobre un objeto Scanner

		System.out.println ("[Distrito] Ingresar Puerto Servidor Central:");
		puertoServer = entradaScanner.nextLine ();

		System.out.println ("[Distrito] Nombre Distrito:");
		nDistrito = entradaScanner.nextLine ();

		System.out.println ("[Distrito "+nDistrito+"] IP Multicast:");
		ipMulti = entradaScanner.nextLine ();

		System.out.println ("[Distrito "+nDistrito+"] Puerto Multicast:");
		puertoMulti = entradaScanner.nextLine ();

		System.out.println ("[Distrito "+nDistrito+"] IP Peticiones:");
		ipPeti = entradaScanner.nextLine ();

		System.out.println ("[Distrito "+nDistrito+"] Puerto Peticiones:");
		puertoPeti = entradaScanner.nextLine ();

        socket = new DatagramSocket(Integer.parseInt(puertoPeti), InetAddress.getByName(ipPeti));
	}

	public void run() {
		//funcion que lee desde consola
		inputLocal();
		//Envía actualizaciones de titanes periodicamente mediante multicast
		enviarActualizaciones();
		//Escucha las instrucciones que da el cliente
		escucharCliente(socket);
	}

	public void enviarActualizaciones(){
		Thread t = new Thread(new Runnable(){
			public void run(){
				try{
					DatagramSocket socket_multi = new DatagramSocket();

					while(true){
						//Enviar periódicamente cada 40 segundos
						try {
							sleep(40*1000);

							String correo = "";
							for(Map.Entry m:titanes.entrySet()){
								correo += m.getKey()+" "+m.getValue()+"\n";
							}
							enviarU("ACTUALIZACION:\n"+correo, ipMulti, socket_multi);
		                } catch (InterruptedException e) { }
					}
				}catch(IOException e){
					System.out.println("enviarActualizaciones Distrito");
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	public void inputLocal(){	//funcion que se encarga de leer de consola
		Thread t = new Thread(new Runnable(){
			public void run(){

				Scanner scan = new Scanner(System.in);
				String input;
				try{
					DatagramSocket socket_multi = new DatagramSocket();

					while(true){

						List<String> titan ;

						System.out.println("Escoja opcion [Publicar Titan]");
						input = scan.nextLine();

						if(input.equals("Publicar Titan")){
							int id = crearTitan();
							titan = titanes.get(id);
							enviarU("Aparece nuevo Titan! "+titan.get(0)+", tipo "+titan.get(1)+", ID "+Integer.toString(id), ipMulti, socket_multi);
						}
						else{
							System.out.println("Ingrese mensaje valido");
						}
					}
				}catch(IOException e){
					System.out.println("inputLocal Distrito");
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	public void escucharCliente(DatagramSocket socket){
		try{
			while (more) {
				byte[] buf = new byte[256];
				//Recibir el paquete para determinar lo que el cliente quiere
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);

				String received_D = recibir(packet);//recibir peticiones del cliente
				System.out.println("mensaje recibido: " + received_D);

				String correo = "";
				List<List<String>> listado = new ArrayList<List<String>>();

				if (received_D.equals("1")){//cliente quiere ver titanes
					for(Map.Entry m:titanes.entrySet()){
   						correo += m.getKey()+" "+m.getValue()+"\n";
  					}
					enviarC(correo, packet, socket);
				}
				else if (received_D.equals("3")){//cliente quiere capturar titanes
					//Pasando listas a string, preferible no entender.
					Iterator it = titanes.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry)it.next();
						if(!titanes.get(pair.getKey()).get(1).equals("Excentrico")){
							listado.add(Arrays.asList(pair.getKey().toString(),titanes.get(pair.getKey()).get(0),titanes.get(pair.getKey()).get(1)));
						}
					}
					System.out.println(listado);
					for (List<String> s : listado){
						correo += s + "";
					}
					String a;
					a = correo.replaceAll("\\[", "/").replaceAll("]", "/");
					correo = a.replaceAll(" ", "");

					System.out.println(correo);
					enviarC(correo, packet ,socket);
				}
				else if (received_D.contains("3/")){//cliente captura titan
					List<String> capturando = new ArrayList<String>(Arrays.asList(received_D.split("/")));
					titanes.remove(Integer.parseInt(capturando.get(1)));
				}
				else if (received_D.equals("4")){//cliente quiere matar titanes
					//Pasando listas a string, preferible no entender.
					Iterator it = titanes.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry)it.next();
						if(!titanes.get(pair.getKey()).get(1).equals("Cambiante")){
							listado.add(Arrays.asList(pair.getKey().toString(),titanes.get(pair.getKey()).get(0),titanes.get(pair.getKey()).get(1)));
						}
					}
					System.out.println(listado);
					for (List<String> s : listado){
						correo += s + "";
					}
					String a;
					a = correo.replaceAll("\\[", "/").replaceAll("]", "/");
					correo = a.replaceAll(" ", "");

					System.out.println(correo);
					enviarC(correo, packet ,socket);
				}
				else if (received_D.contains("4/")){//cliente mata titan
					List<String> asesinando = new ArrayList<String>(Arrays.asList(received_D.split("/")));
					titanes.remove(Integer.parseInt(asesinando.get(1)));
				}
			}
			socket.close();
		}catch(IOException e){
			System.out.println("run distrito");
			e.printStackTrace();
		}
	}

	//enviar mensajes a multicast
	public void enviarU(String mensaje, String ip_destino, DatagramSocket socket){
        try{
            byte[] buf = new byte[256];

            InetAddress address = InetAddress.getByName(ip_destino);
            buf = mensaje.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(puertoMulti));
            socket.send(packet);
        }catch(IOException e) {
            System.out.println("enviarU Distrito");
            e.printStackTrace();
        }
	}

	//enviar mensajes a cliente
	public void enviarC(String mensaje, DatagramPacket packet, DatagramSocket socket){
		try{
			byte[] buf = new byte[256];
			//extraer puerto y direccion
			InetAddress address = packet.getAddress();
			int port = packet.getPort();

			buf = mensaje.getBytes();
			DatagramPacket paquete = new DatagramPacket(buf, buf.length, address, port);
			socket.send(paquete);
		}catch(IOException e) {
			System.out.println("enviarC Distrito");
			e.printStackTrace();
		}
	}

	//recibe un mensaje en formato string para retornarlo
	public String recibir(DatagramPacket packet){

	    // display response
	    String received = new String(packet.getData(), 0, packet.getLength());
	    return received;
	}

	//recibe un mensaje del socket
	public static String recibirSocket(DatagramSocket socket){
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

	public int crearTitan(){
		try{
			socket = new DatagramSocket();

			Scanner scan = new Scanner(System.in);
			String input;
			List<String> valores = new ArrayList<String>();

			System.out.println("[Distrito "+nDistrito+"] Introducir Nombre");
			input = scan.nextLine();
			valores.add(input);
			System.out.println("[Distrito "+nDistrito+"] Introducir Tipo");

			while(true){
				System.out.println("1.- Normal");
				System.out.println("2.- Excentrico");
				System.out.println("3.- Cambiante");
				input = scan.nextLine();

				if(input.equals("1")){
					valores.add("Normal");
					break ;
				}
				else if(input.equals("2")){
					valores.add("Excentrico");
					break;
				}
				else if(input.equals("3")){
					valores.add("Cambiante");
					break;
				}
				System.out.println("Ingrese una respuesta valida");
			}

			//Pide id sincronizado al servidor central
			byte[] buf = new byte[256];
			InetAddress address = InetAddress.getByName(ipServer);
			String puerto_destino = puertoServer;
			buf = claveSecreta.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(puerto_destino));
			socket.send(packet);

			int id = Integer.parseInt(recibirSocket(socket));
			titanes.put(id, valores);

			System.out.println("[Distrito "+nDistrito+"] Se ha publicado el Titán: "+titanes.get(id).get(0));

			System.out.println("**************");
			System.out.println("ID: "+id);
			System.out.println("Nombre: "+titanes.get(id).get(0));
			System.out.println("Tipo: "+titanes.get(id).get(1));
			System.out.println("**************");

			return id;
		}catch(IOException e) {
			System.out.println("crearTitan Distrito");
			e.printStackTrace();
		}
		return -1;
	}

}//end districtServerThread
