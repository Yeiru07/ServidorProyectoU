module proyectou.servidorproyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens proyectou.servidorproyecto to javafx.fxml;
    opens Controlador to javafx.fxml;
    opens Modelo to javafx.fxml;
    opens MySQL to javafx.fxml;
    exports servidor_cliente;
}
