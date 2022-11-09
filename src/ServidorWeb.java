import javax.swing.plaf.TableHeaderUI;
import java.io.*;
import java.net.*;
import java.util.*;

public final class ServidorWeb {
    public static void main(String[] args) throws Exception
    {
        // Establece el numero de puerto.
        int puerto = 6789;

        // Estableciendo el socket de escucha.
        ServerSocket servidor = new ServerSocket(puerto);

        // Procesando las solicitudes HTTP en un ciclo infinito.
        while(true) {
            // Escuchando las solicitudes de conexion TCP.
            Socket client = servidor.accept();
            // Construye un objeto para procesar el mensaje de solicitud HTTP.
            SolicitudHttp solicitud = new SolicitudHttp(client);

            // Crea un nuevo hilo para procesar la solicittud.

        }



    }
}


