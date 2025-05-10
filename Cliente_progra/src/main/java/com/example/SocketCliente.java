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

    private final String HOST = "192.168.1.70";
    private final int PORT = 4040;

    private SocketCliente() throws IOException {
        conectar();
    }

    private void conectar() throws IOException {
        socket = new Socket(HOST, PORT);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public static synchronized SocketCliente getInstancia() throws IOException {
        if (instancia == null || instancia.socket.isClosed()) {
            instancia = new SocketCliente();
        }
        return instancia;
    }

    public synchronized String enviarComando(String comando) throws IOException {
        output.write(comando + "\n");
        output.flush();
        return input.readLine();
    }

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
