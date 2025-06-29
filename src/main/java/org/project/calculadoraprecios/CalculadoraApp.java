package org.project.calculadoraprecios;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CalculadoraApp extends Application {

    private static Scene escenaPrincipal;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(CalculadoraApp.class.getResource("calculadora-view.fxml"));
        // Cargar el FXML y crear la escena principal con el tamaño deseado
        escenaPrincipal = new Scene(fxmlLoader.load(), 400, 500);

        // Modo oscuro activado por defecto
        escenaPrincipal.getStylesheets().add(CalculadoraApp.class.getResource("dark-theme.css").toExternalForm());

        stage.setTitle("Calculadora de Precios");
        stage.setResizable(false);
        stage.setScene(escenaPrincipal);
        aplicarTema(true); // o false si querés empezar en claro
        stage.show();
    }

    public static Scene getEscenaPrincipal() {
        return escenaPrincipal;
    }

    public static void aplicarTema(boolean oscuro) {
        var escena = getEscenaPrincipal();
        var hojaOscura = CalculadoraApp.class.getResource("dark-theme.css").toExternalForm();
        var hojaClara = CalculadoraApp.class.getResource("light-theme.css").toExternalForm();

        escena.getStylesheets().clear(); // Limpia el tema anterior
        if (oscuro) {
            escena.getStylesheets().add(hojaOscura);
        } else {
            escena.getStylesheets().add(hojaClara);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
