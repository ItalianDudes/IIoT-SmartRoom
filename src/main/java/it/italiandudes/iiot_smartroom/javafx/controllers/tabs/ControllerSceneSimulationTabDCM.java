package it.italiandudes.iiot_smartroom.javafx.controllers.tabs;

import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.iiot_smartroom.mqtt.DataCollectorAndManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import org.jetbrains.annotations.NotNull;

public final class ControllerSceneSimulationTabDCM {

    // Attributes
    private DataCollectorAndManager dcm = null;
    private volatile boolean configurationComplete = false;

    // Methods
    public void setDCM(@NotNull final DataCollectorAndManager dcm) {
        this.dcm = dcm;
    }
    public void configurationComplete() {
        configurationComplete = true;
    }

    // Graphic Elements
    @FXML private ToggleButton toggleButtonSensorsOverride;

    // Initialize
    @FXML
    private void initialize() {
        JFXUtils.startVoidServiceTask(() -> {
            while (!configurationComplete) Thread.onSpinWait();
            Platform.runLater(() -> {
                toggleButtonSensorsOverride.setSelected(dcm.isConditionerDoorWindowOverride());
                toggleButtonSensorsOverride.setText(dcm.isConditionerDoorWindowOverride() ? "ATTIVO" : "DISATTIVO");
            });
        });
    }

    // EDT
    @FXML
    private void toggleSensorsOverride() {
        dcm.setConditionerDoorWindowOverride(toggleButtonSensorsOverride.isSelected());
        toggleButtonSensorsOverride.setText(dcm.isConditionerDoorWindowOverride() ? "ATTIVO" : "DISATTIVO");
    }
}
