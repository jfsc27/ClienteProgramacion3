package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SalaEnListaViewController {
    
     @FXML
    private Button btnSala;
    /**
     * Metodo para setear la informacion entrante en la vista
     * @param nombre
     */
    public void setData(String nombre){
        btnSala.setText(nombre);
    }
}
