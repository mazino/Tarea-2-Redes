import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.commons.io.IOUtils;

class peticionWeb extends Thread
{
	private Socket s=null;
	
	static   int port;
	static String IPservidor ="127.0.0.1"; //o IPv4 correspondiente al pc que lo ejecuta 
	static String contacto,IPOrigen;
	private  Socket scliente = null;		
   	private  PrintWriter out = null;		
	private  BufferedReader in = null;
	

   	peticionWeb(Socket ps, int puerto, String ip)
   	{
		scliente = ps;
		port = puerto;
		IPOrigen = ip;
   	}

	public void run()
	{
		System.out.println("Procesamos conexion");
		
		try
		{	
			in = new BufferedReader (new InputStreamReader(scliente.getInputStream()));
  			out = new PrintWriter(new OutputStreamWriter(scliente.getOutputStream()),true) ;
  			
  			     
           		
  			List<String> list = new ArrayList<String>();
  			String line = "";
  			
  			while ((line = in.readLine()) != null ){
  				if(line.equals("recivirarchivo")){
  					recibirarchivo();
  				}else{
	  				list.add(line);
	  				System.out.println("--" + line + "-");			
	            	if(line.isEmpty()){
		            	cargarpagina(list); 
		            }
  				}
            	
            }
  			list.clear();
  			out.close();
                      
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}


		System.out.println("Hemos terminado");
	}


	void cargarpagina(List<String> lista) throws IOException
	{		

			String[] req = lista.get(0).split(" ");
			String url = req[1];

			if(req[0].equals("POST")){	
				
		    	  String datos;
	              char[] aux = new char[100];
	              in.read(aux);
	              for (int i=0;i<100;i++)
	                  if(aux[i]==(char)0)
	                      aux[i]='*';
	              datos=new String(aux);
	              datos = datos.replace("*","");
	              System.out.println("Datos: " + datos);
	              
	              if(url.equals("/inicio.html")){
	            	  agregarcontacto(datos);
	            	  
	              }
	              
	              else if(url.equals("/chat.html"))
				  {		
	            	 
	            	  if(datos.split("=")[0].equals("destinatario")){
	            		  contacto=datos;

	            		  String verificarusuario = obtenerdatoscontacto(contacto.split("=")[1]);
	            		  if(verificarusuario == null){
		            		  InputStream arch = new FileInputStream ("error.html");
		                      String html = IOUtils.toString(arch, "UTF-8");
		                      out.println(html); 
		                      out.close();
		                      return;
	            		  }
	            		
	            	  }else if(datos.split("=")[0].equals("archivo")){
	            		  
	            		  	  String datoscontacto = obtenerdatoscontacto(contacto.split("=")[1]);
	            		  	  String men = procesarmensaje2(datos+"&"+contacto+"&"+datoscontacto+
			  						"&PuertoOrigen="+port+"&IPOrigen="+IPOrigen);
	            		  	  System.out.println(men);
	            		  	  
	            			  Socket s = new Socket(IPservidor, 8080);
	            			  BufferedReader b = new BufferedReader (new InputStreamReader(s.getInputStream()));
		            		  PrintWriter enviar = new PrintWriter(new OutputStreamWriter(s.getOutputStream()),true);
		            		 
		            		
	            			  enviar.println(men);
	            			  
	            			  String respuesta = b.readLine(); 
		            		  System.out.println(respuesta);
		            		  if(respuesta.equals("OK")) {

		            		  BufferedInputStream bis;
		            		  BufferedOutputStream bos;
		            		  
		            		  String filename = datos.split("=")[1];
		            		  
		            		  int in;
		            		  byte[] byteArray;
		            		  try{
		            			  final File localFile = new File(filename);
		            			  bis = new BufferedInputStream(new FileInputStream(localFile));
		            			  bos = new BufferedOutputStream(s.getOutputStream());
	            			  
		            			  //Enviamos el nombre del fichero
		            			  DataOutputStream dos=new DataOutputStream(s.getOutputStream());
		            			  dos.writeUTF(localFile.getName());
		            			  //Enviamos el fichero
		            			  byteArray = new byte[8192];
		            			  while ((in = bis.read(byteArray)) != -1){
		            				  bos.write(byteArray,0,in);
		            			  }
	            			  
		            			  bis.close();
		            			  bos.close();
	
		            			  enviar.close();
					  			  b.close();
			            		  s.close();
		            		 
	            		  	  }catch ( Exception e ) {
	            			 	System.err.println(e);
	            			  }
	            			}
			            	  
	            	  }
	            	  else if(datos.split("=")[0].equals("mensaje")){
	            		 
		            	  Socket s;
		            	  s = new Socket(IPservidor,8080);
		            	  PrintWriter enviar = new PrintWriter(new OutputStreamWriter(s.getOutputStream()),true);
		            	  BufferedReader b = new BufferedReader (new InputStreamReader(s.getInputStream()));
		
		            	  String datoscontacto = obtenerdatoscontacto(contacto.split("=")[1]);
		            	 
		            	  if(datoscontacto != null){
		            		  //Guardar los mensajes enviados en un txt para mostrarlos   
		            		  String men = procesarmensaje(datos+"&"+contacto+"&"+datoscontacto+
		            				  						"&PuertoOrigen="+port+"&IPOrigen="+IPOrigen);
		            		  System.out.println(men);
		            		  enviar.println(men);
		            		  historial(men); //se agrega el mensaje que se quiere enviar en el historial propio para cada contacto
			            	  String respuesta = b.readLine();
			            	  System.out.println(respuesta);
			            	 
			            	  
			            	  enviar.close();
				  			  b.close();
				  			  s.close();
		            	  }
	            	  }
				  }
	             
	              
		      }
			//#######
			if(url.equals("/inicio.html") || url.equals("/")){
				File archivo = null;
			    FileReader fr = null;
			    BufferedReader br = null;

			    try {                
			    	archivo = new File("contactos.txt");
			    	if ( !archivo.exists())
			    	{	
			    		//Si el archivo contacto no existe se crea vacio
			    		archivo.createNewFile(); 
			    	}
			    	fr = new FileReader(archivo);
			    	br = new BufferedReader(fr);

			    	String linea;
			    	String[] aux;
			    	String nombre;
			    	
			    	out.println("<body>"); 
			    	out.println("<form action='chat.html' method='POST'> ");
			    	
			    	out.println("<table>");
			    	out.println("<tr><th scope='col'>Contactos</th>");
			    	out.println("<th scope='col'>IP</th>");
			    	out.println("<th scope='col'>Puerto</th></tr>");

			    	/*Lectura del archivo contactos.txt y escritura en el html*/
			        while((linea=br.readLine())!=null)
			        {	
			        	aux = linea.split("&");
			        	nombre = aux[0].split("=")[1].replace("+"," ");
			        	
			        	out.println("<tr>");			    			        	
		        		out.println("<td>"+nombre+"</a></td>");
		        		
		        		out.write("<td>"+ aux[1].split("=")[1] +"</td>\n");
		                out.write("<td>"+ aux[2].split("=")[1] +"</td>\n");
		        		out.println("</tr>");


			        } 

			        out.println("</table>");
			        out.println("<body></form>");

	    			InputStream a = new FileInputStream ("inicio.html");
	                String html = IOUtils.toString(a, "UTF-8");
	                out.println(html); 	
	                out.close();


		        } catch (Exception e) {

		        	e.printStackTrace();

		        }finally{
		         try{                    
		            if( null != fr ){   
		               fr.close();     
		            }                  
		         }catch (Exception e2){ 
		            e2.printStackTrace();
		         }
		      }
 
	    	}

			else if(req[1].equals("/agregarcontacto.html?")){
				try
	    		{	

					InputStream archivo = new FileInputStream ("agregarcontacto.html");
	                String html = IOUtils.toString(archivo, "UTF-8");
	                out.println(html); 
	                out.close();

	    		}

	    		catch(Exception e)
	    		{
	    			System.out.println(e.toString());
	    		}

			}
			
			if(url.equals("/chat.html")){
      			 	
          	  s = new Socket(IPservidor,8080);
          	  PrintWriter enviar = new PrintWriter(new OutputStreamWriter(s.getOutputStream()),true);
          	  BufferedReader b = new BufferedReader (new InputStreamReader(s.getInputStream()));
          	  enviar.println("msjnuevos&Puerto="+port+"&DirIP="+IPOrigen);

        	  String respuesta=""; 
        	  List<String> list = new ArrayList<String>();
        	  while(!(respuesta = b.readLine()).equals("fin###"))
        	  {
        		  System.out.println(respuesta);
        		  list.add(respuesta);
        	  }
        	  
        	  for(int i = 0;i< list.size();i++){  
        		  mensajesnuevos(list.get(i));
        	  }
        	 
           	 	
				 File archivo = null;
         	     FileReader fr = null;
					BufferedReader br = null;
					try {                
						archivo = new File(contacto.split("=")[1]+"historial.txt");
						if (!archivo.exists())
							archivo.createNewFile(); 
						
						fr = new FileReader(archivo);
						br = new BufferedReader(fr);
					
						String linea;
											
						out.println("<table>");
					    while((linea=br.readLine())!=null)/*Lectura del archivo historial.txt y escritura en el html*/
					    {	
					    	out.println("<tr>");			    			        	
							out.write("<td>"+ linea +"</td>\n");
							out.println("</tr>");
					    } 
					    
					    out.println("</table>");

					    
					} catch (Exception e) {
					
						e.printStackTrace();
					
					}finally{
					 try{                    
					    if( null != fr ){   
					       fr.close();     
					    }                  
					 }catch (Exception e2){ 
					    e2.printStackTrace();
					 }
					}
					
				enviar.println("ficherosnuevos&Puerto="+port+"&DirIP="+IPOrigen);
				
	        	List<String> archivos = new ArrayList<String>();
	        	while(!(respuesta = b.readLine()).equals("fin###"))
	        	{
	        		System.out.println(respuesta);
	        		archivos.add(respuesta);	        		
	        		
	        	}
	        	  
	        	for(int i = 0;i< archivos.size();i++){
	        		
	        		String user = origenmensaje(archivos.get(i).split("&")[4].split("=")[1],
	        						archivos.get(i).split("&")[5].split("=")[1]);

	        		if(user.equals(contacto.split("=")[1])){
	        			enviar.println("enviar##"+archivos.get(i));
	        			respuesta = b.readLine();
	        		}
	        		

	        	}
				
				InputStream arch = new FileInputStream ("chat.html");
                String html = IOUtils.toString(arch, "UTF-8");
                out.println(html); 
                out.close();
      		 
      		  
			} 

			
	}


	void recibirarchivo(){
		out.println("OK");
		BufferedInputStream bis;
	 	BufferedOutputStream bos;
	 	byte[] receivedData;
	 	int in;
	 	String file;
	 	
		try{
			
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
				 	
			 	File fichero=new File(file);
				fichero.renameTo(new File(file.split("&")[6].split("=")[1]) ); //renombramos los ficheros con sus nombres originales
				
		 }catch (Exception e ) {
			 System.err.println(e);
		 }
	}
	void agregarcontacto(String contacto){
		FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            fichero = new FileWriter("contactos.txt",true);
            pw = new PrintWriter(fichero);
              
            /**Nos aseguramos que los 3 campos sean rellenados */
            if (contacto.split("&")[0].split("=").length==2 &&
            		contacto.split("&")[1].split("=").length==2 &&
            			contacto.split("&")[2].split("=").length==2)
            				
            				pw.println(contacto);
 
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
	
	
	String obtenerdatoscontacto(String contacto){ 
        
        File fichero = new File("contactos.txt" );
        BufferedReader entrada;
        String usuario = null;
        try {
	        entrada = new BufferedReader(new FileReader(fichero));
	        String linea;
	        while(entrada.ready()){
	        	linea = entrada.readLine();
	        	if(linea.split("&")[0].split("=")[1].equals(contacto)){
	        		usuario = linea;
	        		entrada.close();
	        		break;
	
	        	}
	        }
	    }catch (IOException e) {
        	e.printStackTrace();
        }
		return usuario;	
	}
	
	//Funcion para unir el mensaje con la direccion IP y puerto de Destinario
	String procesarmensaje(String msj){ 
		String [] aux;
		aux = msj.split("&");
		/**Se asigna la palabra clave mensaje para el servidor reconosca que es un mensaje que se quiere enviar*/
		String mensaje = "mensaje&"+aux[1]+"&"+aux[3]+"&"+aux[4]+"&"+aux[6]+"&"+aux[5]+"&"+aux[0];
		return mensaje;
	}
	String procesarmensaje2(String msj){ 
		String [] aux;
		aux = msj.split("&");
		String mensaje = "archivo&"+aux[1]+"&"+aux[3]+"&"+aux[4]+"&"+aux[6]+"&"+aux[5]+"&"+aux[0];
		return mensaje;
	}
	void historial(String msj){ 
		FileWriter fichero = null;
        PrintWriter pw = null;

       String  mensaje ="yo: "+ msj.split("&")[6].split("=")[1].replace("+", " ")+".  to: "
    		   			+ msj.split("&")[1].split("=")[1].replace("+", " ");
        try
        {	
            fichero = new FileWriter(msj.split("&")[1].split("=")[1]+"historial.txt",true);
            pw = new PrintWriter(fichero);				
            pw.println(mensaje);
 
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
	void mensajesnuevos(String msj){ 
		FileWriter fichero = null;
        PrintWriter pw = null;
        String usersender = origenmensaje(msj.split("&")[4].split("=")[1],msj.split("&")[5].split("=")[1]);
       
        
       String  mensaje= usersender+" dice:  " 
    		   	+ msj.split("&")[6].split("=")[1].replace("+", " ");
        try
        {	
            fichero = new FileWriter(usersender+"historial.txt",true);
            pw = new PrintWriter(fichero);				
            pw.println(mensaje);
 
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
	String origenmensaje(String ip, String puerto){ //Para saber quien ha enviado el mensaje
        
        File fichero = new File("contactos.txt");
        BufferedReader entrada;
        String usuario = null;
        try {
	        entrada = new BufferedReader(new FileReader(fichero));
	        String linea;
	        while(entrada.ready()){
	        	linea = entrada.readLine();
	        	if(linea.split("&")[1].split("=")[1].equals(ip)&&
	        			linea.split("&")[2].split("=")[1].equals(puerto)){
	        		
	        		usuario = linea.split("&")[0].split("=")[1];
	        		entrada.close();
	        		break;
	
	        	}
	        }
	    }catch (IOException e) {
        	e.printStackTrace();
        }
		return usuario;	
	}

}

