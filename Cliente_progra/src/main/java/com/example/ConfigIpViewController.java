package com.example;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ConfigIpViewController {
    
    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnCuadrito;

    @FXML
    private Button btnIngresarIp;

    @FXML
    private Button btnMinimizar;

    @FXML
    private StackPane PaneSuperior;

    @FXML
    private TextField txtIp;

    private double xOffset = 0;
    private double yOffset = 0;

    public void inicializar() {
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
    void clickIngresarIp(ActionEvent event) throws IOException {
        if (!txtIp.getText().isEmpty()) {
            SocketCliente.setHost(txtIp.getText());
            iniciarLogin();
        }
    }
    public void iniciarLogin() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Main.fxml"));
        Scene scene = new Scene(loader.load(), 349, 667);
        MainController controller = loader.getController();
        controller.inicializar();

        Stage stage = new Stage();
        stage.setScene(scene);
        Stage stageCerrar = (Stage) btnIngresarIp.getScene().getWindow();
        stageCerrar.close();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show(); 
    }
    /**
     * Click para minimizar la ventana del dashboard
     * @param event
     */
    @FXML
    void clickMinimizar(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
    /**
     * Click para cerrar y desconectar al cliente al salir de la ventana
     * @param event
     * @throws IOException
     */
    @FXML
    void clickCerrar(ActionEvent event){
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    /**
     * Click para ajustar el tamaño de la ventana
     * @param event
     */
    @FXML
    void clickCuadrito(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());;
    }
}
