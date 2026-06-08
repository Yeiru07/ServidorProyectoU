module proyectou.servidorproyecto {
    requires javafx.controls;
    requires javafx.fxml;

    opens proyectou.servidorproyecto to javafx.fxml;
    exports proyectou.servidorproyecto;
}
