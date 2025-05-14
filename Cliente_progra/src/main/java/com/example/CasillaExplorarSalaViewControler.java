package com.example;

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
    /**
     * Metodo para setear la informacion en la vista
     * @param nombre
     * @param descripcion
     * @param sala_id
     * @param user_id
     */
    public void setData(String nombre, String descripcion, String sala_id, String user_id) {
        labelDescripcion.setText(descripcion);
        labelNombreSala.setText(nombre);
        this.sala_id = sala_id;
        this.user_id = user_id;
    }
    /**
     * Metodo que al darle click a entrar envia al server la peticion de crear una nueva sala a nombre del usuario
     * @param event
     * @throws Exception
     */
    @FXML
    void clicEntrar(ActionEvent event) throws Exception {
        btnEntrar.setDisable(true);
        String comando = "agregar_sala_user," + sala_id + "," + user_id;
        String respuesta = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar(comando));
        System.out.println(respuesta);
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
