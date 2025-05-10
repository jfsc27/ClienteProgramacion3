package com.example;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DashboardviewController{

    private double xOffset = 0;
    private double yOffset = 0;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private boolean schedulerActivo = false;

    @FXML
    private StackPane PaneSuperior;
    @FXML
    private Label lblNumConectados;

    public void inicializar() {
        schedulerActivo = false;
        PaneSuperior.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        PaneSuperior.setOnMouseDragged(event -> {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        iniciarActualizacionConectados();
        enviarDatosUsuario();

    }

    private void iniciarActualizacionConectados() {
        if (!schedulerActivo) {
            scheduler = Executors.newScheduledThreadPool(1);
            schedulerActivo = true;

            scheduler.scheduleAtFixedRate(() -> {
                try {
                    String mensaje = SocketCliente.getInstancia().enviarComando("usuarios_conectados");
                    Platform.runLater(() -> lblNumConectados.setText(mensaje));
                    mostrarSalas();
                } catch (Exception e) {
                    Platform.runLater(() -> lblNumConectados.setText("Error de conexión"));
                    e.printStackTrace();
                }
            }, 0, 10, TimeUnit.SECONDS);
        }

    }

    public void enviarDatosUsuario() {
        new Thread(() -> {
            try {
                String datos = "nombre_user," + user + "," + pass;
                String nombre = SocketCliente.getInstancia().enviarComando(datos);

                Platform.runLater(() -> labelNombreUser.setText(nombre));

            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> labelNombreUser.setText("Error"));
            }
        }).start();;
    }
    /**
     *
     *
     */
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    public Label getLblNumConectados() {
        return lblNumConectados;
    }
    public void setLblNumConectados(Label lblNumConectados) {
        this.lblNumConectados = lblNumConectados;
    }

    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnCuadrito;

    @FXML
    private Button btnExplorar;

    @FXML
    private Button btnMinimizar;

    @FXML
    private Label labelNombreUser;

    @FXML
    private Button btnSalir;

    @FXML
    private Button btnCrear;

    @FXML
    private StackPane PaneConversacion;

    @FXML
    private StackPane PaneExplorar;

    @FXML
    private GridPane gridCanales;

    @FXML
    private GridPane gridExplorar;

    private String sala_activa_id;
    private String user;
    private String pass;
    private String user_id;

    public void mostrarSalas(){
        new Thread(() -> {
            try {
                int columna = 0;
                int fila = 0;
                String comando = "obtener_salas," + user_id;
                String ids = SocketCliente.getInstancia().enviarComando(comando);

                if (ids != null && !ids.isEmpty()) {
                    String[] salasIds = ids.split("/");

                    for(String sala_id: salasIds){

                        String comando2 = "nombre_sala," + sala_id;
                        String nombreSala = SocketCliente.getInstancia().enviarComando(comando2);

                        if (nombreSala != null && !nombreSala.isEmpty()) {
                            final int fColumna = columna;
                            final int fFila = fila;
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sala-btn-view.fxml"));
                            Button boton = loader.load();

                            SalaEnListaViewController controller = loader.getController();
                            controller.setData(nombreSala);

                            boton.setOnAction(event -> {/*mostrarConversacion(sala_id);*/});
                            Platform.runLater(() -> {
                                gridCanales.add(boton, fColumna, fFila);
                            });
                            fila++;
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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
    void clickExplorar(ActionEvent event) {
        PaneExplorar.setVisible(true);
        PaneConversacion.setVisible(false);
    }

    @FXML
    void clickCrear(ActionEvent event) throws IOException {
        abrir_crear_sala();
    }
    public void abrir_crear_sala() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/crea_sala.fxml"));
        Scene scene = new Scene(loader.load(), 360, 452);
        CrearSalaViewController controller = loader.getController();
        controller.inicializar();
        controller.setUser_id(user_id);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.showAndWait();

        controller.getSala_id();
        stage.close();
    }
    @FXML
    void clickMinimizar(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    void clickSalir(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 349, 645);
        Stage stage = new Stage();

        stage.setScene(scene);

        Stage StageCerrar = (Stage) btnCuadrito.getScene().getWindow();
        StageCerrar.close();

        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        new Thread(() -> {
            try {
                String datos = "desconeccion," + user + "," + pass;
                String respuesta = SocketCliente.getInstancia().enviarComando(datos);
                System.out.println(respuesta);
                SocketCliente.getInstancia().cerrar();

            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> labelNombreUser.setText("Error"));
            }
        }).start();
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getSala_activa_id() {
        return sala_activa_id;
    }
    public void setSala_activa_id(String sala_activa_id) {
        this.sala_activa_id = sala_activa_id;
    }
    public void reiniciarScheduler() {
        scheduler = Executors.newScheduledThreadPool(1);
    }


}


