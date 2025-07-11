module org.example.sgef_petalex_v_09 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;


    opens org.example.sgef_petalex_v_09 to javafx.fxml;
    exports org.example.sgef_petalex_v_09;
    exports org.example.sgef_petalex_v_09.controllers;
    opens org.example.sgef_petalex_v_09.controllers to javafx.fxml;
    opens org.example.sgef_petalex_v_09.models      to javafx.base, javafx.fxml;
}