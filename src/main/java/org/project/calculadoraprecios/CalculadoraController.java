package org.project.calculadoraprecios;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.project.calculadoraprecios.utils.Actualizador;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class CalculadoraController {

    @FXML private TextField interesDebito;
    @FXML private TextField interesCredito;
    @FXML private TextField precioBase;
    @FXML private TextField resultadoDebito;
    @FXML private TextField resultadoCredito;
    @FXML private Label labelError;
    @FXML private CheckBox modoAutomatico;
    @FXML private Button botonCalcular;
    @FXML private ToggleButton toggleModoOscuro;
    @FXML private Button btnInfoActualizaciones;

    private final ChangeListener<String> listenerPrecio = (obs, oldVal, newVal) -> calcular();
    private final ChangeListener<String> listenerDebito = (obs, oldVal, newVal) -> calcular();
    private final ChangeListener<String> listenerCredito = (obs, oldVal, newVal) -> calcular();


    private final Path configPath = Paths.get("config/config.properties");

    @FXML
    private void initialize() {
        setupNumberValidation(interesDebito);
        setupNumberValidation(interesCredito);
        setupNumberValidation(precioBase);

        loadIntereses();

        labelError.setVisible(false);
        modoAutomatico.setSelected(true);
        botonCalcular.setVisible(false);
        addListeners(); // arranca en modo automático

    }

    private void addListeners() {
        precioBase.textProperty().addListener(listenerPrecio);
        interesDebito.textProperty().addListener(listenerDebito);
        interesCredito.textProperty().addListener(listenerCredito);
    }

    private void removeListeners() {
        precioBase.textProperty().removeListener(listenerPrecio);
        interesDebito.textProperty().removeListener(listenerDebito);
        interesCredito.textProperty().removeListener(listenerCredito);
    }

    @FXML
    private void mostrarInfoActualizaciones() {
        String versionActual = "1.2.0"; // o tomalo de una constante central si querés

        String changelog = Actualizador.obtenerChangelog("/servidor/changelog.md");
        String versionServidor = Actualizador.obtenerVersionServidor("/servidor/version.txt");

        if (versionServidor == null) {
            mostrarAlerta("Error", "No se pudo obtener la información de la versión.");
            return;
        }

        if (versionServidor.equals(versionActual)) {
            mostrarAlerta("Sin actualizaciones", "Estás usando la última versión.");
        } else {
            String cambios = Actualizador.obtenerCambiosDeVersion(changelog, versionServidor);
            mostrarAlerta("Nueva versión disponible: " + versionServidor, cambios);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void alternarModo() {
        boolean esAuto = modoAutomatico.isSelected();

        if (esAuto) {
            addListeners();
            botonCalcular.setVisible(false);
            calcular(); // actualiza al activar
        } else {
            removeListeners();
            botonCalcular.setVisible(true);
        }
    }

    @FXML
    protected void calcular() {
        try {

            String textoPrecio = precioBase.getText();
            String textoDebito = interesDebito.getText();
            String textoCredito = interesCredito.getText();

            if (textoPrecio.isEmpty() || textoDebito.isEmpty() || textoCredito.isEmpty()) {
                throw new NumberFormatException("Campo vacío");
            }

            double precio = Double.parseDouble(precioBase.getText());
            double interesDeb = Double.parseDouble(interesDebito.getText());
            double interesCred = Double.parseDouble(interesCredito.getText());

            double precioDeb = precio * (1 + interesDeb / 100);
            double precioCred = precio * (1 + interesCred / 100);

            resultadoDebito.setText(String.format("$ %.2f", precioDeb));
            resultadoCredito.setText(String.format("$ %.2f", precioCred));

            labelError.setVisible(false); // ocultar error si salió bien

            // Guardar los intereses ingresados
            saveIntereses(interesDeb, interesCred);

        } catch (NumberFormatException e) {
            resultadoDebito.setText("");
            resultadoCredito.setText("");
            labelError.setVisible(true);
        }
    }

    private void setupNumberValidation(TextField campo) {
        Pattern validDouble = Pattern.compile("\\d{0,7}(\\.\\d{0,2})?");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            return validDouble.matcher(text).matches() ? change : null;
        };
        campo.setTextFormatter(new TextFormatter<>(filter));
    }

    private void loadIntereses() {
        try {
            if (Files.exists(configPath)) {
                Properties props = new Properties();
                try (InputStream in = Files.newInputStream(configPath)) {
                    props.load(in);
                }

                interesDebito.setText(props.getProperty("debito", "10.59"));
                interesCredito.setText(props.getProperty("credito", "13.83"));
            } else {
                interesDebito.setText("10.59");
                interesCredito.setText("13.83");
            }
        } catch (IOException e) {
            System.err.println("No se pudo cargar configuración: " + e.getMessage());
        }
    }

    private void saveIntereses(double debito, double credito) {
        try {
            Properties props = new Properties();
            props.setProperty("debito", String.valueOf(debito));
            props.setProperty("credito", String.valueOf(credito));

            // Asegura que exista el directorio
            Files.createDirectories(configPath.getParent());

            try (OutputStream out = Files.newOutputStream(configPath)) {
                props.store(out, "Configuración de intereses");
            }
        } catch (IOException e) {
            System.err.println("No se pudo guardar configuración: " + e.getMessage());
        }
    }

    @FXML
    private void cambiarTema() {
        boolean modoOscuro = toggleModoOscuro.isSelected();
        toggleModoOscuro.setText(modoOscuro ? "🌙 Modo oscuro" : "☀️ Modo claro");
        CalculadoraApp.aplicarTema(modoOscuro);
    }
}
