package com.example;
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
            try {
                btnCrear.setDisable(true);
                String nombre = fieldNombre.getText().replace("\n", "").replace("\r", "");
                String descripcion = txtAreaDescripcion.getText().replace("\n", " ").replace("\r", " ");
                String datos = "crear_sala," + nombre + "," + descripcion + "," + this.user_id +"\n";
                String id_cript = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar(datos));
                String id = CryptoUtil.getInstance().desencriptar(id_cript);
                this.sala_id = id;
    
            } catch (Exception e) {
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
