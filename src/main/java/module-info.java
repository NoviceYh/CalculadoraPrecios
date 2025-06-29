module org.project.calculadoraprecios {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.project.calculadoraprecios to javafx.fxml;
    exports org.project.calculadoraprecios;
}