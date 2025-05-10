package com.example;
import java.io.*;
import java.net.Socket;

public class ClienteTCP {

    public static void main(String[] args) {
        String servidorIP = "192.168.56.1"; // <- IP del servidor en la red WiFi
        int puerto = 4040;

        try (Socket socket = new Socket(servidorIP, puerto);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Ejemplo: enviar credenciales de login
            String mensaje = "pepito07,1234\n";
            writer.write(mensaje);
            writer.flush();

            // Leer la respuesta del servidor
            String respuesta = reader.readLine();
            System.out.println("Respuesta del servidor: " + respuesta);

        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

