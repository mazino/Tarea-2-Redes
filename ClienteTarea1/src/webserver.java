
import java.awt.Desktop;
import java.io.IOException;
import java.net.*;


public class webserver
{	
	int puerto = 8000;
	private ServerSocket s;

	public static void main(String [] array) 	
	{
		webserver instancia = new webserver();	
		instancia.arranca();
		

	}

	boolean arranca()
	{
		System.out.println("Arrancamos nuestro servidor");
		try
		{			
			String thisIp = InetAddress.getLocalHost().getHostAddress();
			System.out.println("IP:"+thisIp);

			s = new ServerSocket(puerto);
			String url = "http://localhost:"+ puerto;
			System.out.println(url);
			
			if(Desktop.isDesktopSupported()){
	            Desktop desktop = Desktop.getDesktop();
	            try {
	                desktop.browse(new URI(url));
	            } catch (IOException | URISyntaxException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }else{
	            Runtime runtime = Runtime.getRuntime();
	            try {
	                runtime.exec("xdg-open " + url);
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
			
			System.out.println("Quedamos a la espera de conexion");
			
			while(true)  
			{	
				Socket entrante = s.accept();		
				
				peticionWeb pCliente = new peticionWeb(entrante,puerto,thisIp);
				pCliente.start();

			}

		}
		catch(Exception e)
		{
			System.out.println(e.toString());

		}

		return true;
	}

}