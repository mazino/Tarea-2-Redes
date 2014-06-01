import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
	
	Socket scliente; //Socket cliente
	
	PrintStream out; //Canal de escritura
	BufferedReader in; //Canal de Lectura
	
	 private Server(Socket sc) {
	        scliente = sc;
	 }
	
	public void run() // implementamos el metodo run
	{
		try {
			//Obtengo una referencia a los canales de escritura y lectura del socket cliente
			in = new BufferedReader( new InputStreamReader ( scliente.getInputStream() ) );
			out = new PrintStream ( scliente.getOutputStream() );
			
			String line = "";
			while ((line = in.readLine()) != null) {//Se lee lo que escribio el socket cliente en el canal de lectura
				System.out.println("Mensaje recibido: " + line);
				//Escribo en canal de escritura el mismo mensaje recibido
				out.println(line);
				
				if (line.equals("mensaje"))
				{
					//Guardar el mensaje en un archivo de texto para enviar cuando
					//el cliente pida datos 
				}
				if (line.equals("by")) {
					break;
				}
			}
		
			out.close();
			in.close();
			
			scliente.close();
			
		} catch (IOException e) {
			System.out.println("No puedo crear el socket");
		}
	}

public static void main(String[] args) throws IOException {
		 
	
		ServerSocket servidor = new ServerSocket(8080);
		while(true )  // buscar forma para finalizar el servidor
		{
			 Server nuevoserver = new Server(servidor.accept()); 
			 nuevoserver.start();
		}
		
		//servidor.close();

	}

}