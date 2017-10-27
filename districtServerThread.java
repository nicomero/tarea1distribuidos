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
	HashMap<Integer,String> titanes=new HashMap<Integer,String>();
	int id = 0;
	int puerto;
	String nDistrito = "";
	String ipMulti = "";
	String ipPeti = "";
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

		System.out.println ("[Distrito "+nDistrito+"] IP Peticiones:");
		ipPeti = entradaEscaner.nextLine (); //Invocamos un método sobre un objeto Scanner

		System.out.println ("[Distrito "+nDistrito+"] Puerto Peticiones:");
		puertoPeti = entradaEscaner.nextLine (); //Invocamos un método sobre un objeto Scanner

        socket = new DatagramSocket(Integer.parseInt(puertoPeti), InetAddress.getByName(ipPeti));
	}

	public void run() {
		//funcion que lee desde consola
		inputLocal();
		//Escucha las instrucciones que da el cliente
		escucharCliente(socket);
	}

	public void inputLocal(){	//funcion que se encarga de leer de consola
		Thread t = new Thread(new Runnable(){
			public void run(){

				Scanner scan = new Scanner(System.in);
				String input;
				try{
					DatagramSocket socket_multi = new DatagramSocket();

					while(true){

						String dString = new Date().toString();

						System.out.println("Escoja opcion [Publicar titan]");
						input = scan.nextLine();

						if(input.equals("Publicar titan")){

							crearTitan();
							System.out.println(Arrays.asList(titanes));

							enviarU(dString+" "+nDistrito, ipMulti, socket_multi);
						}
						else{
							System.out.println("Ingrese mensaje valido");
						}
					}
				}catch(IOException e){
					System.out.println("inputLocal Distrito");
					e.printStackTrace();
				}
				//socket.close();
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

				if (received_D.equals("1")){//cliente quiere ver titanes
					enviarC("Lista de titanes",packet ,socket);
				}
				else if (received_D.equals("3")){//cliente quiere capturar titanes
					enviarC("Info titan capturado",packet ,socket);
				}
				else if (received_D.equals("4")){//cliente quiere matar titanes
					enviarC("Info titan muerto",packet ,socket);
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

	public void crearTitan(){

		Scanner scan = new Scanner(System.in);
		String input;

		System.out.println("Escoga nombre");
		input = scan.nextLine();
		System.out.println("Escoga tipo");
		input = input + "," +scan.nextLine();
		titanes.put(id, input);
		id = id+1;

	}

}//end districtServerThread
