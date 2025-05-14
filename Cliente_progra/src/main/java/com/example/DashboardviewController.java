package com.example;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DashboardviewController{
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
    
    @FXML
    private StackPane PaneSuperior;

    @FXML
    private Label lblNumConectados;

    @FXML
    private Button btnEnviar;

    @FXML
    private GridPane gridConversacion;

    @FXML
    private Label labelNombreSala;

    @FXML
    private TextField textEnviarMensaje;

    @FXML
    private ScrollPane scrollPaneMensajes;

    @FXML
    private Button btnSalirSala;

    private double xOffset = 0;
    private double yOffset = 0;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private boolean schedulerActivo = false;
    private ScheduledExecutorService schedulerMensajes = Executors.newSingleThreadScheduledExecutor();
    private boolean schedulerMensajesActivo = false;
    
    private String sala_activa_id;
    private String nombre_sala_activa;
    private String user;
    private String pass;
    private String user_id;
    private String ultimoEstadoMensajes = "";

    /**
     * Metodo para inicializar la ventana dashboard
     */
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
        if (schedulerMensajes != null && !schedulerMensajes.isShutdown()) {
            schedulerMensajes.shutdownNow();
            schedulerMensajesActivo = false;
        }
        PaneExplorar.setVisible(true);
        PaneConversacion.setVisible(false);
        mostrar_salas_nuevas();
        iniciarActualizacionConectados();
        enviarDatosUsuario();
        
    }
    /**
     * Metodo para actualizar los mensajes de una sala abierta por el ususario en la vista cada 2 segundos
     * @param sala_id
     */
    private void iniciarActualizacionMensajes(String sala_id){
        
        if (!schedulerMensajesActivo) {
            schedulerMensajes = Executors.newScheduledThreadPool(1);
            schedulerMensajesActivo = true;

            schedulerMensajes.scheduleAtFixedRate(() -> {
                try { 
                    int columna_user = 1;
                    int columna_envia = 0;
                    String comando = "actualizar_mensajes_sala," + sala_id;
                    String mensajes = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar(comando));

                    if (mensajes != null && !mensajes.isEmpty()) {
                        mensajes = CryptoUtil.getInstance().desencriptar(mensajes);
                        if (!mensajes.equals(ultimoEstadoMensajes)) {
                            ultimoEstadoMensajes = mensajes;
                            String[] mensajes_sala = mensajes.split("\\|");
                            Platform.runLater(()-> {
                                int fila = 0;
                                gridConversacion.getChildren().clear();
                                for(String mensaje: mensajes_sala){

                                    String[] datos_mensaje = mensaje.split("~");

                                    if (datos_mensaje.length == 3) {

                                        String fecha = datos_mensaje[0];
                                        String user_id_msj = datos_mensaje[1];
                                        String texto = datos_mensaje[2];
                                        String nombre_user_cript;
                                        String nombre_user;
                                        try {
                                            FXMLLoader loader;
                                            String comando_nombre = "nombre_user," + user_id_msj;
                                            nombre_user_cript = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar(comando_nombre));
                                            nombre_user = CryptoUtil.getInstance().desencriptar(nombre_user_cript);
                                            final int fColumna_user = columna_user;
                                            final int fColumna_envia = columna_envia;
                                            final int fFila = fila;
                                
                                            if (this.user_id.equals(user_id_msj)) {
                                                loader = new FXMLLoader(getClass().getResource("/com/example/casilla_sala_user.fxml"));
                                                AnchorPane pane = loader.load();
                                                CasillaMensajeViewController controller = loader.getController();
                                                controller.setData(fecha, texto, nombre_user);

                                            
                                                gridConversacion.add(pane, fColumna_user, fFila);
                                            
                                                fila++;
                                            }else{
                                                loader = new FXMLLoader(getClass().getResource("/com/example/casilla_sala.fxml"));
                                                AnchorPane pane = loader.load();
                                                CasillaMensajeViewController controller = loader.getController();
                                                controller.setData(fecha, texto, nombre_user);

                                                gridConversacion.add(pane, fColumna_envia, fFila);
                                            
                                                fila++;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } 
                                    }
                                }
                                PauseTransition pause = new PauseTransition(Duration.millis(50));
                                pause.setOnFinished(event -> scrollPaneMensajes.setVvalue(1.0));
                                pause.play();
                            });
                        }
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> lblNumConectados.setText("Error de conexión"));
                     e.printStackTrace();
                }
            }, 0, 2, TimeUnit.SECONDS);
        }
    }
    /**
     * Metodo que actualiza el numero de usuarios conectados al servidor
     */
    private void iniciarActualizacionConectados() {
        if (!schedulerActivo) {
            scheduler = Executors.newScheduledThreadPool(1);
            schedulerActivo = true;

            scheduler.scheduleAtFixedRate(() -> {
                try {
                     String mensaje_cript = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar("usuarios_conectados"));
                     String mensaje = CryptoUtil.getInstance().desencriptar(mensaje_cript);
                     Platform.runLater(() -> lblNumConectados.setText(mensaje));
                     mostrarSalas();
                } catch (Exception e) {
                    Platform.runLater(() -> lblNumConectados.setText("Error de conexión"));
                     e.printStackTrace();
                }
            }, 0, 10, TimeUnit.SECONDS);
        }
        
    }
    /**
     * Metodo que pide al servidor el nombre del usuario enviando el user_id
     */
    public void enviarDatosUsuario() {
        new Thread(() -> {
            try {
                String datos = "nombre_user," + user_id;
                String nombre_cript = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar(datos));
                String nombre = CryptoUtil.getInstance().desencriptar(nombre_cript);
    
                Platform.runLater(() -> labelNombreUser.setText(nombre));
    
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> labelNombreUser.setText("Error"));
            }
        }).start();;
    }
    /**
     * Metodo que pide al server las salas a las que esta inscrito el usuario activo y las muestra
     */
    public void mostrarSalas(){
        new Thread(() -> {
            try {
            Platform.runLater(() -> gridCanales.getChildren().clear());
            int columna = 0;
            int fila = 0;
            String comando = "obtener_salas," + user_id;
            String ids_cript = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar(comando));
            String ids = CryptoUtil.getInstance().desencriptar(ids_cript);

            if (ids != null && !ids.isEmpty()) {
                String[] salasIds = ids.split("/");

                for(String sala_id: salasIds){

                    String comando2 = "nombre_sala," + sala_id;
                    String nombreSala_cript = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar(comando2));
                    String nombreSala = CryptoUtil.getInstance().desencriptar(nombreSala_cript);

                    if (nombreSala != null && !nombreSala.isEmpty()) {
                        final int fColumna = columna;
                        final int fFila = fila;
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sala-btn-view.fxml"));
                        Button boton = loader.load();

                        SalaEnListaViewController controller = loader.getController();
                        controller.setData(nombreSala);

                        boton.setOnAction(event -> {
                            btnSalirSala.setDisable(false);
                            if (schedulerMensajes != null && !schedulerMensajes.isShutdown()) {
                                schedulerMensajes.shutdownNow();
                                schedulerMensajesActivo = false;
                            }
                            mostrarConversacion(sala_id, nombreSala);
                        });
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
    /**
     * Metodo que prepara el ambiente y activa otros metodos para mostrar las conversaciones de la sala activa
     * @param sala_id
     * @param nombreSala
     */
    public void mostrarConversacion(String sala_id, String nombreSala) {
        this.sala_activa_id = sala_id;
        this.nombre_sala_activa = nombreSala;
        labelNombreSala.setText(nombre_sala_activa);
        iniciarActualizacionMensajes(sala_activa_id);
        PaneConversacion.setVisible(true);
        PaneExplorar.setVisible(false);
    }
    /**
     * Metodo que al darle click al boton explorar, prepara la vista para mostrar las salas 
     * que no esta inscrito el usuario
     * @param event
     */
    @FXML
    void clickExplorar(ActionEvent event) {
        if (schedulerMensajes != null && !schedulerMensajes.isShutdown()) {
            schedulerMensajes.shutdownNow();
            schedulerMensajesActivo = false;
        }
        PaneExplorar.setVisible(true);
        PaneConversacion.setVisible(false);
        mostrar_salas_nuevas();
    }
    /**
     * Metodo que muestra las salas a las que no esta inscrito el usuario
     */
    public void mostrar_salas_nuevas() {
        new Thread(() ->{
            try {
                Platform.runLater(() -> gridExplorar.getChildren().clear());
                int columna = 0;
                int fila = 0;
                String comando = "obtener_not_salas," + user_id;
                String ids_cript = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar(comando));
                String ids = CryptoUtil.getInstance().desencriptar(ids_cript);

                if (ids != null && !ids.isEmpty()) {
                String[] salasIds = ids.split("/");

                for(String sala_id: salasIds){

                    String comando2 = "nombre_sala_descripcion," + sala_id;
                    String datos_cript = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar(comando2));
                    String datos = CryptoUtil.getInstance().desencriptar(datos_cript);

                    String[] datos_sala = datos.split(",");
                    String nombreSala = datos_sala[0];
                    String descripcion = datos_sala[1];

                    if (nombreSala != null && !nombreSala.isEmpty()) {
                        final int fColumna = columna;
                        final int fFila = fila;
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Saña.fxml"));
                        AnchorPane pane = loader.load();

                        CasillaExplorarSalaViewControler controller = loader.getController();
                        controller.setData(nombreSala, descripcion, sala_id, user_id);
                        Platform.runLater(() -> {
                            gridExplorar.add(pane, fColumna, fFila);
                        });
                        if (columna == 2) {
                            fila++;
                            columna = 0;
                        }else{
                            columna++;
                        }
                    }
                }
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    /**
     * Metodo para abrir la ventana para crear una nueva sala
     * @throws IOException
     */
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
    /**
     * Metodo que al darle click al boton de salir retorne al login
     * @param event
     * @throws IOException
     */
    @FXML
    void clickSalir(ActionEvent event) throws IOException {
        desconectarCliente();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Main.fxml"));
        Parent root = loader.load();
        
        MainController controller = loader.getController();
        controller.inicializar();

        Scene scene = new Scene(root, 349, 667);
        Stage stage = new Stage();

        stage.setScene(scene);

        Stage StageCerrar = (Stage) btnCuadrito.getScene().getWindow();
        StageCerrar.close();

        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }
    /**
     * Metodo para que al darle click a salir, cierre todos los canales de comunicacion con el server
     * y le notifique la desconeccion del usuario
     * @param event
     * @throws IOException
     */
    public void desconectarCliente() throws IOException{
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            schedulerActivo = false;
        }
        if (schedulerMensajes != null && !schedulerMensajes.isShutdown()) {
            schedulerMensajes.shutdownNow();
            schedulerMensajesActivo = false;
        }

        new Thread(() -> {
            try {
                String datos = "desconeccion," + user + "," + pass;
                String respuesta = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar(datos));
                System.out.println(respuesta);
                SocketCliente.getInstancia().cerrar();
                
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> labelNombreUser.setText("Error"));
            }
        }).start();
    }
    /**
     * Metodo que al darle click a salir de una sala, envia el comando para que el servidor haga la desconeccion
     * @param event
     */
    @FXML
    void clickSalirSala(ActionEvent event) {
        new Thread(() ->{
            try {
                btnSalirSala.setDisable(true);
                if (schedulerMensajes != null && !schedulerMensajes.isShutdown()) {
                    schedulerMensajes.shutdownNow();
                    schedulerMensajesActivo = false;
                }
                PaneExplorar.setVisible(true);
                PaneConversacion.setVisible(false);
                mostrar_salas_nuevas();
                mostrarSalas();
                String comando = "salir_sala_user_id," + sala_activa_id + "," + user_id;
                String respuesta = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar(comando));
                System.out.println(respuesta);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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
    void clickCerrar(ActionEvent event) throws IOException {
        desconectarCliente();
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
    /**
     * Cicl para abrir la ventana de creacion de una nueva sala
     * @param event
     * @throws IOException
     */
     @FXML
    void clickCrear(ActionEvent event) throws IOException {
        abrir_crear_sala();
    }
    /*
     * ////////////////////////////////////////////////////////////// CONVERSACION //////////////////////////////////////////////////////////////////
     */
    /**
     * Metodo que al darle click al boton de enviar en una sala, envia el mensaje escrito al server
     * @param event
     * @throws IOException
     * @throws InterruptedException
     */
    @FXML
    void clickEnviar(ActionEvent event) throws IOException, InterruptedException {
        if (!textEnviarMensaje.getText().isEmpty()) {
            new Thread(() ->{
                try {
                    String comando = "enviar_mensaje_sala,"+ sala_activa_id + "," + user_id + "," + textEnviarMensaje.getText();
                    Platform.runLater(() -> textEnviarMensaje.clear());
                    String respuesta_cript = SocketCliente.getInstancia().enviarComando(CryptoUtil.getInstance().encriptar(comando));
                    System.out.println(respuesta_cript);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }else {
            new Thread(() -> {
                Platform.runLater(() -> {
                    textEnviarMensaje.setText("Debes escribir algo antes de enviar");
                    try {
                        wait(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    textEnviarMensaje.clear();
                });
            }).start();
        }
    }
    /**
     * 
     * /////////////////////////////////////////////////////////// SECCION GETTERS Y SETTERS///////////////////////////////////////////////////////////////////////////
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

