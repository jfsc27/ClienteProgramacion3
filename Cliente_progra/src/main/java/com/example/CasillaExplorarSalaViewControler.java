package com.example;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CasillaExplorarSalaViewControler {

    @FXML
    private Button btnEntrar;

    @FXML
    private Label labelDescripcion;

    @FXML
    private Label labelNombreSala;
    private String sala_id;
    private String user_id;

    public void setData(String nombre, String descripcion, String sala_id, String user_id) {
        labelDescripcion.setText(descripcion);
        labelNombreSala.setText(nombre);
        this.sala_id = sala_id;
        this.user_id = user_id;
    }
    @FXML
    void clicEntrar(ActionEvent event) throws IOException {
        String comando = "agregar_sala_user," + sala_id + "," + user_id;
        String respuesta = SocketCliente.getInstancia().enviarComando(comando);
    }
    public String getSala_id() {
        return sala_id;
    }
    public void setSala_id(String sala_id) {
        this.sala_id = sala_id;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

}
