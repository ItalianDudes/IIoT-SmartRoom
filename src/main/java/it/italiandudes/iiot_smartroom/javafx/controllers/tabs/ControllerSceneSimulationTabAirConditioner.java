package it.italiandudes.iiot_smartroom.javafx.controllers.tabs;

import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.javafx.alert.ErrorAlert;
import it.italiandudes.iiot_smartroom.javafx.Client;
import it.italiandudes.iiot_smartroom.mqtt.DataCollectorAndManager;
import it.italiandudes.iiot_smartroom.mqtt.devices.SmartAirConditioner;
import it.italiandudes.iiot_smartroom.mqtt.devices.data.AirConditionerMode;
import it.italiandudes.iiot_smartroom.utils.RoomDefs;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.jetbrains.annotations.NotNull;

public final class ControllerSceneSimulationTabAirConditioner {

    // Attributes
    private DataCollectorAndManager dcm = null;
    private volatile boolean configurationComplete = false;
    private double cacheTemperature = 0;
    private double cacheHumidity = 0;
    private double cacheSetpointTemperature = 25;
    private AirConditionerMode cacheMode = AirConditionerMode.FAN;
    private boolean cacheIsOn = true;

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
    @FXML private Label labelSetpointTemperature;
    @FXML private Label labelMode;
    @FXML private Circle circleIsOn;
    @FXML private ToggleButton toggleButtonIsOn;
    @FXML private TextField textFieldSetpointTemperature;
    @FXML private Button buttonApplySetpointTemperature;
    @FXML private ComboBox<AirConditionerMode> comboBoxMode;

    // Initialize
    @FXML
    private void initialize() {
        JFXUtils.startVoidServiceTask(() -> {
            while (!configurationComplete) Thread.onSpinWait();
            Platform.runLater(() -> {
                toggleButtonIsOn.setSelected(dcm.isConditionerOn());
                textFieldSetpointTemperature.setText(RoomDefs.DECIMAL_FORMATTER.format(dcm.getConditionerTemperatureSetpoint()));
                comboBoxMode.getItems().addAll(AirConditionerMode.values());
                comboBoxMode.getSelectionModel().select(dcm.getConditionerMode());
                applySetpointTemperature();
                applyMode();
                applyIsOn();
            });
            while (dcm.isConnected()) {
                if (cacheTemperature != dcm.getRoomTemperature() || cacheHumidity != dcm.getRoomHumidity() ||
                        cacheSetpointTemperature != dcm.getConditionerTemperatureSetpoint() || cacheMode != dcm.getConditionerMode() ||
                        cacheIsOn != dcm.isConditionerOn()) {
                    cacheTemperature = dcm.getRoomTemperature();
                    cacheHumidity = dcm.getRoomHumidity();
                    cacheSetpointTemperature = dcm.getConditionerTemperatureSetpoint();
                    cacheMode = dcm.getConditionerMode();
                    cacheIsOn = dcm.isConditionerOn();
                    Platform.runLater(() -> {
                        if (!dcm.isConditionerOn() && toggleButtonIsOn.isSelected()) {
                            toggleButtonIsOn.setText("SPENTO");
                            toggleButtonIsOn.setSelected(false);
                        }
                        labelSensorTemperature.setText(RoomDefs.DECIMAL_FORMATTER.format(cacheTemperature));
                        labelSensorHumidity.setText(RoomDefs.DECIMAL_FORMATTER.format(cacheHumidity));
                        labelSetpointTemperature.setText(RoomDefs.DECIMAL_FORMATTER.format(cacheSetpointTemperature));
                        labelMode.setText(cacheMode.name());
                        circleIsOn.setFill(cacheIsOn ? Color.GREEN : Color.RED);
                    });
                }
            }
        });
    }

    // EDT
    @FXML
    private void applySetpointTemperature() {
        try {
            double value = Double.parseDouble(textFieldSetpointTemperature.getText().replace(',', '.'));
            if (value < SmartAirConditioner.MIN_SETPOINT_TEMPERATURE || value > SmartAirConditioner.MAX_SETPOINT_TEMPERATURE) throw new NumberFormatException("Value out of bounds");
            JFXUtils.startVoidServiceTask(() -> dcm.actionChangeSetpointTemperature(value));
        } catch (NumberFormatException e) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore Formato Numero", "Il campo di testo deve contenere un numero intero o decimale compreso tra 18 e 30.");
        }
    }
    @FXML
    private void applyMode() {
        AirConditionerMode newMode = comboBoxMode.getSelectionModel().getSelectedItem();
        if (newMode == null) return;
        if (newMode == AirConditionerMode.ECO) {
            textFieldSetpointTemperature.setDisable(true);
            textFieldSetpointTemperature.setText(RoomDefs.DECIMAL_FORMATTER.format(SmartAirConditioner.ECO_SETPOINT_TEMPERATURE));
            buttonApplySetpointTemperature.setDisable(true);
            applySetpointTemperature();
        } else {
            textFieldSetpointTemperature.setDisable(false);
            buttonApplySetpointTemperature.setDisable(false);
        }
        JFXUtils.startVoidServiceTask(() -> dcm.actionChangeAirConditionerMode(newMode));
    }
    @FXML
    private void applyIsOn() {
        if (!dcm.isConditionerDoorWindowOverride() && (dcm.isDoorOpen() || dcm.isWindowOpen())) {
            toggleButtonIsOn.setSelected(!toggleButtonIsOn.isSelected());
            new ErrorAlert(Client.getStage(), "ERRORE", "Azione Bloccata", "Impossibile accendere il climatizzatore finche' la porta o la finestra sono aperte.\nPer disattivare questa limitazione attivare l'override dei sensori porta/finestra dal DCM.");
            return;
        }
        toggleButtonIsOn.setText(toggleButtonIsOn.isSelected() ? "ACCESO" : "SPENTO");
        JFXUtils.startVoidServiceTask(() -> dcm.actionChangeConditionerOnOff(toggleButtonIsOn.isSelected()));
    }
}
