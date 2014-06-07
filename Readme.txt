Integrantes:
	Nombre: Felipe V�squez  Rol: 201104535-1
	Nombre: Cecilia Villarroel  Rol: 201104558-0

Consideraciones:
	Aqu� se encuentra el servidor y un cliente local, este cliente tiene un puerto predefinido el cual es el 8000, 
	si se desea cambiar ir a webserver.java l�nea 9 y cambiar el puerto por el deseado.

	La IP del servidor se asume conocida, como este cliente es local se utiliza la IP 127.0.0.1 (localhost), tambi�n se puede
	utilizar la IPv4 del pc que contiene al servidor (si se desea cambiar la IP del servidor en el c�digo cliente, abrir el 
	c�digo peticionweb.java l�nea 12 y cambiar el String IPservidor por el correspondiente.

	Para la apertura de cada html se utiliz� la librer�a commons-io-2.4 contenida en la carpeta lib.
	
	La p�gina del chat (chat.html) se recarga cada 10 seg. para preguntar al servidor si existen archivos y mensajes
	nuevos.

Ejecuci�n:
	Al momento de agregar un contacto utilizar IPv4 correspondiente al pc donde se ejecutara ese contacto.

	Para entrar a chatear con un contacto escribir el nombre del contacto en el input  y apretar botton chatear.

	Para envi� de archivos, el archivo debe encontrarse en la carpeta de la aplicaci�n, para efectos de esta tarea en la 
	carpeta ClienteTarea1(seg�n repositorio github).
