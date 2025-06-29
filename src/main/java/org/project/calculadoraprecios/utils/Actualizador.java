package org.project.calculadoraprecios.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Actualizador {

    private static final String VERSION_ACTUAL = "1.0.0"; // se puede cambiar desde config
    private static final String URL_VERSION = "https://raw.githubusercontent.com/TU_USUARIO/TU_REPO/main/version.txt";
    private static final String URL_CHANGELOG = "https://raw.githubusercontent.com/TU_USUARIO/TU_REPO/main/changelog.md";

    public static void verificarActualizacion() {
        try {
            String versionRemota = leerTextoDesdeURL(URL_VERSION).trim();

            if (!VERSION_ACTUAL.equals(versionRemota)) {
                String changelog = leerTextoDesdeURL(URL_CHANGELOG);
                mostrarVentanaActualizacion(versionRemota, changelog);
            } else {
                mostrarSinActualizaciones();
            }

        } catch (Exception e) {
            System.err.println("Error al verificar actualización: " + e.getMessage());
        }
    }

    private static String leerTextoDesdeURL(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine).append("\n");
        }
        in.close();
        return response.toString();
    }

    private static void mostrarVentanaActualizacion(String nuevaVersion, String changelog) {
        // Implementar con JavaFX si querés una ventana modal linda
        System.out.println("\n⚡ Versión nueva disponible: " + nuevaVersion);
        System.out.println(changelog);
    }

    private static void mostrarSinActualizaciones() {
        System.out.println("No hay actualizaciones disponibles. Estás usando la última versión.");
    }

    public static String obtenerVersionServidor(String rutaInterna) {
        try (InputStream in = Actualizador.class.getResourceAsStream(rutaInterna)) {
            if (in == null) return null;
            return new BufferedReader(new InputStreamReader(in)).readLine().trim();
        } catch (IOException e) {
            return null;
        }
    }

    public static String obtenerChangelog(String rutaInterna) {
        try (InputStream in = Actualizador.class.getResourceAsStream(rutaInterna)) {
            if (in == null) return null;
            return new String(in.readAllBytes());
        } catch (IOException e) {
            return null;
        }
    }

    public static String obtenerCambiosDeVersion(String changelog, String versionBuscada) {
        if (changelog == null || versionBuscada == null) return "";

        String[] secciones = changelog.split("### ");
        for (String seccion : secciones) {
            if (seccion.startsWith("Versión " + versionBuscada)) {
                return seccion.replaceFirst("Versión " + versionBuscada + "\\n?", "").trim();
            }
        }
        return "No se encontraron cambios para la versión " + versionBuscada;
    }
}

