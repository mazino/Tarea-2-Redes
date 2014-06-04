import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;

import java.io.File;
import java.io.FileOutputStream;
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
	static int fichero = 0;
	PrintStream out; //Canal de escritura
	BufferedReader in; //Canal de Lectura
	
	 private Server(Socket sc) {
	        scliente = sc;
	 }
	
	public void run() // implementamos el metodo run
	{	
		//System.out.println(fichero);
		
			try {

					//Obtengo una referencia a los canales de escritura y lectura del socket cliente
					in = new BufferedReader( new InputStreamReader ( scliente.getInputStream() ) );
					out = new PrintStream ( scliente.getOutputStream() );
					
					String line = "";
					while ((line = in.readLine()) != null) {//Se lee lo que escribio el socket cliente en el canal de lectura
						System.out.println("Mensaje recibido: " + line);
						
						if (line.split("&")[0].equals("mensaje"))
						{	
							agregarmensaje(line);
							out.println("Server OK");
						}
						else if(line.split("&")[0].equals("msjnuevos")){
							String puerto = line.split("&")[1];
							String ip = line.split("&")[2];
							
							List<String> list = new ArrayList<String>();
							//System.out.println("puerto: "+ puerto +"   IP="+ip);
							list = extraermensajes(puerto,ip);
							
							for(int i = 0;i< list.size();i++){
					            //System.out.println(list.get(i));
					            out.println(list.get(i));
					            //elminar mensaje enviado***
							}
							
							//out.println("Server OK"); //sacar esta cosa despues
							out.println("fin###");
							
						}
						else if(line.split("&")[0].equals("archivo")){	
							recibirfichero();
							
						}
	
					}
				out.close();
				in.close();
				
				scliente.close();
				
			} catch (IOException e) {
				System.out.println("No puedo crear el socket");
			}
	}
	
	void recibirfichero(){
		out.println("OK");
		BufferedInputStream bis;
		BufferedOutputStream bos;
		byte[] receivedData;
		int in;
	    String file;

		try{
			 //Buffer de 1024 bytes
			 receivedData = new byte[1024];
			 bis = new BufferedInputStream(scliente.getInputStream());
			 DataInputStream dis=new DataInputStream(scliente.getInputStream());
			 //Recibimos el nombre del fichero
			 file = dis.readUTF();
			 file = file.substring(file.indexOf('\\')+1,file.length());
			 //Para guardar fichero recibido
			 bos = new BufferedOutputStream(new FileOutputStream(file));
			 while ((in = bis.read(receivedData)) != -1){
				 bos.write(receivedData,0,in);
			 }
			 
			 bos.close();
			 dis.close();
			 
		 
		 }catch (Exception e ) {
			 System.err.println(e);
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
	List<String> extraermensajes(String port,String ip)
	{ 
		File fichero = null;
	    BufferedReader entrada = null;
	    List<String> list = new ArrayList<String>();
	    
	    FileWriter f = null;
        PrintWriter pw = null;

	    try {
	    	fichero = new File("mensajes.txt");
	    	if ( !fichero.exists())
	    	{	
	    		//Si el archivo contacto no existe se crea vacio
	    		fichero.createNewFile(); 
	    	}
	    	f = new FileWriter("temporal.txt",true);
            pw = new PrintWriter(f);
 
	        entrada = new BufferedReader(new FileReader(fichero));
	        String linea;
	        while(entrada.ready()){
	        	linea = entrada.readLine();
	        	
	        	if(linea.split("&")[2].equals(ip) && linea.split("&")[3].equals(port)){ 
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