package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SalaEnListaViewController {

    @FXML
    private Button btnSala;

    public void setData(String nombre){
        btnSala.setText(nombre);
    }
}
