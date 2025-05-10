module proyectos.cliente_progra {
    requires javafx.controls;
    requires javafx.fxml;


    opens proyectos.cliente_progra to javafx.fxml;
    exports proyectos.cliente_progra;
}