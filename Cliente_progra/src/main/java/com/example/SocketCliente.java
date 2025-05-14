package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketCliente {
    private static SocketCliente instancia;
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;

    private static String HOST = "127.0.0.1";
    private static int PORT = 4040;

    private SocketCliente() throws IOException {
        conectar();
    }
    /**
     * Metodo para conectar crear los escritores y lectores en el host y puesto indicados
     * @throws IOException
     */
    private void conectar() throws IOException {
        socket = new Socket(HOST, PORT);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }
    /**
     * Singleton de esta clase, para que todas las conexiones sean de un
     * mismo canal
     * @return
     * @throws IOException
     */
    public static synchronized SocketCliente getInstancia() throws IOException {
        if (instancia == null || instancia.socket.isClosed()) {
            instancia = new SocketCliente();
        }
        return instancia;
    }
    /**
     * Metodo para enviar un comando al servidor y esperar la respuesta.
     * @param comando
     * @return
     * @throws IOException
     */
    public synchronized String enviarComando(String comando) throws IOException {
        output.write(comando + "\n");
        output.flush();
        return input.readLine();
    }

    public static void setHost(String host) {
        if (instancia != null) {
            throw new IllegalStateException("No se puede cambiar el HOST después de que la instancia fue creada");
        }
        HOST = host;
    }
    
    /**
     * Metodo para cerrar todas las conexiones cuando sea necesario
     */
    public void cerrar() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}