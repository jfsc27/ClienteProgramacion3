package com.example;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SignUpViewController {
    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnCrearUsuario;

    @FXML
    private Button btnCuadrito;

    @FXML
    private Button btnMinimizar;

    @FXML
    private TextField txtContraseña;

    @FXML
    private TextField txtEdad;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtNombreUsuario;

    @FXML
    private StackPane PaneSuperior;

    private String user;
    private String pass;
    private String user_id;
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
    void CrearUsuario(ActionEvent event) throws IOException, Exception {
        if (!txtContraseña.getText().isEmpty() && !txtEdad.getText().isEmpty() && !txtNombre.getText().isEmpty() && !txtNombreUsuario.getText().isEmpty()) {
            btnCrearUsuario.setDisable(true);
            String comando = "crear_usuario," + txtNombre.getText() + "," + txtEdad.getText() + "," + txtNombreUsuario.getText() + "," + txtContraseña.getText();
            String respuesta_cript = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar(comando));
            String datos = CryptoUtil.getInstance().desencriptar(respuesta_cript);

            String[] mensajes = datos.split(",");
            String confirmacion = mensajes[0].trim();
            String user_id = mensajes.length > 1 ? mensajes[1].trim() : "";

            if (confirmacion.equals("Usuario creado")) {
                this.user = txtNombreUsuario.getText();
                this.pass = txtContraseña.getText();
                this.user_id = user_id;
                abrirDashboard();
            }
        }
        
    }
    public void abrirDashboard() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Dashboard.fxml"));
        Parent root = loader.load();

        DashboardviewController controller = loader.getController();
       
        controller.setPass(this.pass);
        controller.setUser(this.user);
        controller.setUser_id(user_id);
        controller.inicializar();
        
        Scene scene = new Scene(root, 818, 558);
        Stage stage = new Stage();

        stage.setScene(scene);

        Stage StageCerrar = (Stage) btnCrearUsuario.getScene().getWindow();
        StageCerrar.close();

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
