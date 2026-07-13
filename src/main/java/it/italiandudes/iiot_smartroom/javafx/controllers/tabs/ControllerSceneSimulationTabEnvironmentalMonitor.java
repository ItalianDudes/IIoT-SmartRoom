package it.italiandudes.iiot_smartroom.javafx.controllers.tabs;

import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.javafx.alert.ErrorAlert;
import it.italiandudes.iiot_smartroom.javafx.Client;
import it.italiandudes.iiot_smartroom.mqtt.DataCollectorAndManager;
import it.italiandudes.iiot_smartroom.mqtt.devices.data.Weather;
import it.italiandudes.iiot_smartroom.simulation.DirectorVariables;
import it.italiandudes.iiot_smartroom.utils.RoomDefs;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

public final class ControllerSceneSimulationTabEnvironmentalMonitor {

    // Attributes
    private DataCollectorAndManager dcm = null;
    private volatile boolean configurationComplete = false;
    private double cacheTemperature = 0;
    private double cacheHumidity = 0;
    private int cachePM10 = 0;
    private double cacheWind = 0;
    private double cacheRain = 0;
    private Weather cacheWeather = Weather.CLEAR;

    // Methods
    public void setDCM(@NotNull final DataCollectorAndManager dcm) {
        this.dcm = dcm;
    }
    public void configurationComplete() {
        configurationComplete = true;
    }

    // Graphic Elements
    @FXML private Label labelSensorTemperature;
    @FXML private Label labelSensorHumidity;
    @FXML private Label labelSensorPM10;
    @FXML private Label labelSensorWind;
    @FXML private Label labelSensorRain;
    @FXML private Label labelAggregatedWeather;
    @FXML private TextField textFieldTemperature;
    @FXML private TextField textFieldHumidity;
    @FXML private TextField textFieldPM10;
    @FXML private TextField textFieldWind;
    @FXML private TextField textFieldRain;

    // Initialize
    @FXML
    private void initialize() {
        textFieldTemperature.setText(RoomDefs.DECIMAL_FORMATTER.format(DirectorVariables.EXTERNAL_TEMPERATURE_CEL));
        textFieldHumidity.setText(RoomDefs.DECIMAL_FORMATTER.format(DirectorVariables.EXTERNAL_HUMIDITY_RH));
        textFieldPM10.setText(String.valueOf(DirectorVariables.PM10_UG_M3));
        textFieldWind.setText(RoomDefs.DECIMAL_FORMATTER.format(DirectorVariables.WIND_SPEED_KMH));
        textFieldRain.setText(RoomDefs.DECIMAL_FORMATTER.format(DirectorVariables.RAIN_VOLUME_MM));
        JFXUtils.startVoidServiceTask(() -> {
            while (!configurationComplete) Thread.onSpinWait();
            while (dcm.isConnected()) {
                if (cacheTemperature != dcm.getExternalTemperature() || cacheHumidity != dcm.getExternalHumidity() ||
                cachePM10 != dcm.getPm10() || cacheWind != dcm.getWind() || cacheRain != dcm.getRain()) {
                    cacheTemperature = dcm.getExternalTemperature();
                    cacheHumidity = dcm.getExternalHumidity();
                    cachePM10 = dcm.getPm10();
                    cacheWind = dcm.getWind();
                    cacheRain = dcm.getRain();
                    cacheWeather = dcm.getWeather();
                    Platform.runLater(() -> {
                        labelSensorTemperature.setText(RoomDefs.DECIMAL_FORMATTER.format(cacheTemperature));
                        labelSensorHumidity.setText(RoomDefs.DECIMAL_FORMATTER.format(cacheHumidity));
                        labelSensorPM10.setText(String.valueOf(cachePM10));
                        labelSensorWind.setText(RoomDefs.DECIMAL_FORMATTER.format(cacheWind));
                        labelSensorRain.setText(RoomDefs.DECIMAL_FORMATTER.format(cacheRain));
                        labelAggregatedWeather.setText(cacheWeather.displayName);
                    });
                }
            }
        });
    }

    // EDT
    @FXML
    private void applyTemperature() {
        try {
            DirectorVariables.EXTERNAL_TEMPERATURE_CEL = Double.parseDouble(textFieldTemperature.getText());
        } catch (NumberFormatException e) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore Formato Numero", "Il campo di testo deve contenere un numero intero o decimale positivo o negativo.");
        }
    }
    @FXML
    private void applyHumidity() {
        try {
            double value = Double.parseDouble(textFieldHumidity.getText());
            if (value < 0) throw new NumberFormatException("Negative number not allowed");
            DirectorVariables.EXTERNAL_HUMIDITY_RH = value;
        } catch (NumberFormatException e) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore Formato Numero", "Il campo di testo deve contenere un numero intero o decimale positivo o nullo.");
        }
    }
    @FXML
    private void applyPM10() {
        try {
            int value = Integer.parseInt(textFieldPM10.getText());
            if (value < 0) throw new NumberFormatException("Negative number not allowed");
            DirectorVariables.PM10_UG_M3 = value;
        } catch (NumberFormatException e) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore Formato Numero", "Il campo di testo deve contenere un numero intero positivo o nullo.");
        }
    }
    @FXML
    private void applyWind() {
        try {
            double value = Double.parseDouble(textFieldWind.getText());
            if (value < 0) throw new NumberFormatException("Negative number not allowed");
            DirectorVariables.WIND_SPEED_KMH = value;
        } catch (NumberFormatException e) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore Formato Numero", "Il campo di testo deve contenere un numero intero o decimale positivo o nullo.");
        }
    }
    @FXML
    private void applyRain() {
        try {
            double value = Double.parseDouble(textFieldRain.getText());
            if (value < 0) throw new NumberFormatException("Negative number not allowed");
            DirectorVariables.RAIN_VOLUME_MM = value;
        } catch (NumberFormatException e) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore Formato Numero", "Il campo di testo deve contenere un numero intero o decimale positivo o nullo.");
        }
    }
}
