import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
				
				if(line.split("&")[0].split("=")[1].equals("msjnuevos")){
					String puerto = line.split("&")[1];
					List<String> list = new ArrayList<String>();
					
					list = extraermensajes(puerto);
					
					for(int i = 0;i< list.size();i++){
			            //System.out.println(list.get(i));
			            out.println(list.get(i));
			            //elminar mensaje enviado***
					}
					
					//out.println("Server OK"); //sacar esta cosa despues
					out.println("fin###");
					
				}
				else if (line.split("&")[3].split("=")[0].equals("mensaje"))
				{
					
					agregarmensaje(line);
					out.println("Server OK");
					
					//Socket s=new Socket("localhost",8020);
					//PrintWriter enviar = new PrintWriter(new OutputStreamWriter(s.getOutputStream()),true);
	            	//BufferedReader b = new BufferedReader ( new InputStreamReader ( s.getInputStream() ) );
	            	
	            	//enviar.println(line);
	            	//String respuesta = b.readLine();
	            	//System.out.println(respuesta);
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
	
	void agregarmensaje(String msj){
		FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            fichero = new FileWriter("mensajes.txt",true);
            pw = new PrintWriter(fichero);
			pw.println(msj);         
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fichero)
              	  fichero.close();
             } catch (Exception e2) {
          	   e2.printStackTrace();
             }
          }
	}
	List<String> extraermensajes(String port)
	{ 
		File fichero = new File("mensajes.txt" );
	    BufferedReader entrada;
	    List<String> list = new ArrayList<String>();
	    
	    FileWriter f = null;
        PrintWriter pw = null;

	    try {
	    	f = new FileWriter("temporal.txt",true);
            pw = new PrintWriter(f);
 
	        entrada = new BufferedReader(new FileReader(fichero));
	        String linea;
	        while(entrada.ready()){
	        	linea = entrada.readLine();
	        	
	        	if(linea.split("&")[1].equals(port)){
	        		list.add(linea);
	        	}else{
	        		pw.println(linea);	
	        	}
	        		
	        }
	        entrada.close();
	        fichero.delete();
	        pw.close();
	        f.close();
	        File archivo=new File("temporal.txt");
	        archivo.renameTo(new File("mensajes.txt"));
	       
	        
	    }catch (IOException e) {
	    	e.printStackTrace();
	    }
	    return list;
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