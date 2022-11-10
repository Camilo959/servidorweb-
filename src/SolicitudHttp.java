import java.io.*;
import java.net.*;
import java.util.*;

final public class SolicitudHttp implements Runnable{

    final static  String CRLF = "\r\n";
    Socket socket;

    public SolicitudHttp(Socket client) throws Exception {
        this.socket = client;
    }

    // Por cada cliente se crea un hilo
    @Override
    public void run() {

        try {
            procesoSolicitud();
        } catch (Exception e) {

            System.out.println(e);
        }

    }

    // run la tare de este - Delegar su tarea (invocar) -
    private void procesoSolicitud() throws Exception {

        // Referencia al stream de salida del socket. -Saca bites- -escritor-
        DataOutputStream os = new DataOutputStream((socket.getOutputStream()));

        // Referencia y filtros (InputStreamReader y BuffreredReader)para el stram de entrada. -Lector del buffer- -Objeto para ller -
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Recoge la linea de solicitud HTTP del mensaje.
        String lineaDesolicitud = br.readLine();

        // Muestra la linea de solicitud en pantalla.
        System.out.println();
        System.out.println(lineaDesolicitud);

        // Extrae el nombre del archivo de la linea de solicitud.
        StringTokenizer partesLinea = new StringTokenizer(lineaDesolicitud);
        partesLinea.nextToken(); // "salta" sobre el metodo, se supone que debe se ser  "GET".
        String nombreArchivo = partesLinea.nextToken();

        // Anexa un ".", de tal forma que el archivo solicitado debe estar en el directorio actual.
        nombreArchivo = "." + nombreArchivo;

        //Abre el archivo seleccionaod.
        FileInputStream fis = null;

        boolean existeArchivo = true;
        try {
            fis = new FileInputStream(nombreArchivo);
        } catch (FileNotFoundException e) {
            existeArchivo = false;
        }

        String lineaDeEstado = null;
        String lineaDeTipoContenido = null;
        String cuerpoMensaje = null;

        if (existeArchivo) {
            lineaDeEstado = "HTTP/1.0 200 Document Follows" + CRLF;
            lineaDeTipoContenido = "Content-type" +
                                    contentType( nombreArchivo ) + CRLF;

        } else {
            lineaDeEstado = "HTTP/1.0 40 Not Found " + CRLF;
            lineaDeTipoContenido = "Content-type: text/html" + CRLF;
            cuerpoMensaje = "<HTML>" +
                    "<HEAD><TITLE>404 Not Found</TITLE></HEAD>" +
                    "<BODY><b>404</b> Pagina no encontrada - Univalle</BODY></HTML>";
        }

        // Envia la linea de estado.
        os.writeBytes(lineaDeEstado);

        // Envia el contenido de la linea content-type.
        os.writeBytes(lineaDeTipoContenido);

        // Envia una linea en blanco para indicar el final de las linea header.
        os.writeBytes(CRLF);

        // Envia el cuerpo del mensaje.
        if (existeArchivo) {
            enviarBytes(fis,os);
            fis.close();
        } else {
            os.writeBytes(cuerpoMensaje);
        }

        //recoge la muestra las lineas de header.
        String lineaDelHeader = null;
        while ((lineaDelHeader = br.readLine()).length() != 0) {
            System.out.println(lineaDelHeader);
        }

        // Cierra los streams y el socket
        os.close();
        br.close();
        socket.close();

        // Token son palabras que hay en un mensaje. T
    }

    private static void enviarBytes(FileInputStream fis, OutputStream os ) throws Exception {

        // Contrue un buffer de 1KB para guardar los byes cuando van hacia el socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;

        // Copia el archivo solicitado hacia el output strea del socket.
        while((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0,bytes);
        }
    }

    private static String contentType(String nombreArchivo) {

        if(nombreArchivo.endsWith(".htm") || nombreArchivo.endsWith(".html")) {
            return "text/html";
        }

        if(nombreArchivo.endsWith(".jpg")) {
            return "image/jpeg";
        }

        if(nombreArchivo.endsWith(".gif")) {
            return "image/gif";
        }

        return "applitacion/octet-stream";
    }
}
