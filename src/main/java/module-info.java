module proyectou.servidorproyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

   
    opens Controlador to javafx.fxml;
    opens Modelo to javafx.fxml;
    opens MySQL to javafx.fxml;
    opens servidor_cliente to javafx.fxml;
    exports servidor_cliente;
}
