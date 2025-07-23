module org.example.sgef_petalex_v_09 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires javafx.graphics;

    opens org.example.sgef_petalex_v_09 to javafx.fxml;
    opens org.example.sgef_petalex_v_09.controllers to javafx.fxml;
    opens org.example.sgef_petalex_v_09.models to javafx.base, javafx.fxml;

    exports org.example.sgef_petalex_v_09;
}
