import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
	
	private static ServerSocket servidor;
	static int puerto = 8080;
	Socket scliente; //Socket cliente
	static int fichero = 0;
	PrintStream out; //Canal de escritura
	BufferedReader in; //Canal de Lectura
	
	 private Server(Socket sc) {
	        scliente = sc;
	 }
	
	public void run() 
	{	
		try {
			//Se obtiene una referencia a los canales de escritura y lectura del socket cliente
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
					list = extraermensajes(puerto,ip);
					
					for(int i = 0;i< list.size();i++){            
			            out.println(list.get(i));
			            
					}
					out.println("fin###");
					
				}
				else if(line.split("&")[0].equals("archivo")){	
					recibirfichero(line);
					
				}else if(line.split("&")[0].equals("ficherosnuevos")){
					String puerto = line.split("&")[1];		
					String ip = line.split("&")[2];
					
					List<String> archivos = new ArrayList<String>();
					archivos = extraerfichero(ip,puerto);
					for(int i = 0;i< archivos.size();i++){
						
						System.out.println(archivos.get(i));
						out.println(archivos.get(i));       
			            
					}
					out.println("fin###");
				}
				else if (line.split("&")[0].equals("enviar-archivo")){
					
					mandarfichero(line);
					out.println("fin###");
					
				}

			}
			out.close();
			in.close();
			
			scliente.close();
				
			} catch (IOException e) {
				System.out.println("No puedo crear el socket");
			}
	}
	
	void mandarfichero(String archivo){
		 archivo = archivo.split("-")[1];
		 BufferedInputStream bis;
		 BufferedOutputStream bos;
		 int in;
		 byte[] byteArray;
		 int puerto = Integer.parseInt(archivo.split("&")[3].split("=")[1]);

		try{
			
			 final File localFile = new File(archivo);
			 Socket client = new Socket(archivo.split("&")[2].split("=")[1], puerto); //cambiar localhost por archivo.split("&")[2].split("=")[1]
			 
			  BufferedReader b = new BufferedReader (new InputStreamReader(client.getInputStream()));
   		      PrintWriter enviar = new PrintWriter(new OutputStreamWriter(client.getOutputStream()),true);
   		 
   		
			  enviar.println("recivirarchivo");
			  String respuesta = b.readLine();
    		  System.out.println(respuesta);
			 
         	  
			 bis = new BufferedInputStream(new FileInputStream(localFile));
			 bos = new BufferedOutputStream(client.getOutputStream());
			 //Enviamos el nombre del fichero
			 DataOutputStream dos=new DataOutputStream(client.getOutputStream());
			 dos.writeUTF(localFile.getName());
			 //Enviamos el fichero
			 byteArray = new byte[8192];
			 while ((in = bis.read(byteArray)) != -1){
				 bos.write(byteArray,0,in);
			 }
			 bis.close();
			 bos.close();
			 client.close();
			 
			 localFile.delete();
			 
			 
		}catch ( Exception e ) {
			System.err.println(e);
		}
	
	}
	List<String> extraerfichero(String ip, String puerto){
		
		String curDir = System.getProperty("user.dir");		
		File dir = new File(curDir);
		String[] ficheros = dir.list();
		List<String> list = new ArrayList<String>();
		List<String> archivos = new ArrayList<String>();
		if (ficheros == null)
			  System.out.println("No hay ficheros en el directorio especificado");
		else { 
		  for (int x=0;x<ficheros.length;x++)
		    list.add(ficheros[x]);
		}
		for(int i = 0;i< list.size();i++){
            
            if(list.get(i).split("&").length ==7){        
            	if(list.get(i).split("&")[2].equals(ip) && list.get(i).split("&")[3].equals(puerto)){
            		archivos.add(list.get(i));
            	}	
            }
		}
		
		return archivos;
	}
	void recibirfichero(String destino){
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
			 
			 File archivo=new File(destino.split("&")[6].split("=")[1]);
		     archivo.renameTo(new File(destino));
		 
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
		 
	
		 servidor = new ServerSocket(puerto);
		
		while(true ) 
		{
			 Server nuevoserver = new Server(servidor.accept()); 
			 nuevoserver.start();
		}

	}

}