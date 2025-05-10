package com.example;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CrearSalaViewController {
    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnCrear;

    @FXML
    private Button btnCuadrito;

    @FXML
    private Button btnMinimizar;

    @FXML
    private TextField fieldNombre;

    @FXML
    private TextArea txtAreaDescripcion;

    @FXML
    private StackPane PaneSuperior;

    private String sala_id;
    private String user_id;
    private double xOffset = 0;
    private double yOffset = 0;


    public void inicializar(){

        PaneSuperior.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        PaneSuperior.setOnMouseDragged(event -> {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
    @FXML
    void clickCrear(ActionEvent event) {

        if (!fieldNombre.getText().isEmpty() && !txtAreaDescripcion.getText().isEmpty()) {
            new Thread(() -> {
                try (Socket socket = new Socket("192.168.1.70", 4040);
                     BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                    btnCrear.setDisable(true);
                    String nombre = fieldNombre.getText().replace("\n", "").replace("\r", "");
                    String descripcion = txtAreaDescripcion.getText().replace("\n", " ").replace("\r", " ");
                    String datos = "crear_sala," + nombre + "," + descripcion + "," + this.user_id +"\n";
                    writer.write(datos);
                    writer.flush();

                    String id = input.readLine();
                    this.sala_id = id;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }
    }

    @FXML
    void clickCerrar(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void clickCuadrito(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());;
    }

    @FXML
    void clickMinimizar(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
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

