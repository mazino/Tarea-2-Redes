Integrantes:
	Nombre: Felipe Vásquez  Rol: 201104535-1
	Nombre: Cecilia Villarroel  Rol: 201104558-0

Consideraciones:
	Aquí se encuentra el servidor y un cliente local, este cliente tiene un puerto predefinido el cual es el 8000, 
	si se desea cambiar ir a webserver.java línea 9 y cambiar el puerto por el deseado.

	La IP del servidor se asume conocida, como este cliente es local se utiliza la IP 127.0.0.1 (localhost), también se puede
	utilizar la IPv4 del pc que contiene al servidor (si se desea cambiar la IP del servidor en el código cliente, abrir el 
	código peticionweb.java línea 12 y cambiar el String IPservidor por el correspondiente.

	Para la apertura de los html se utilizó la librería commons-io-2.4 contenida en la carpeta lib.
	
	La página del chat (chat.html) se recarga cada 10 seg. para preguntar al servidor si existen archivos y mensajes
	nuevos.

Ejecución:
	Al momento de agregar un contacto utilizar IPv4 correspondiente al pc donde se ejecutara ese contacto.

	Para entrar a chatear con un contacto escribir el nombre del contacto en el input  y apretar botton chatear.

	Para envió de archivos, el archivo debe encontrarse en la carpeta de la aplicación, para efectos de esta tarea en la 
	carpeta ClienteTarea1(según repositorio github).
