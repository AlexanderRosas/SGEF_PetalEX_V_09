package org.example.sgef_petalex_v_09.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Estados {
    public static final ObservableList<String> ESTADOS_COMPRA= FXCollections.observableArrayList(
        "Recibida", "Hidratada", "En Cuarto Frío", "Inactiva"
    );
      public static final ObservableList<String> ESTADOS_PEDIDO= FXCollections.observableArrayList(
         "En Cuarto Frío", "Empacado", "Exportado", "Anulado" 
    );
}
