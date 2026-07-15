package it.italiandudes.iiot_smartroom.javafx.controllers.tabs;

import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.iiot_smartroom.mqtt.DataCollectorAndManager;
import it.italiandudes.iiot_smartroom.simulation.DirectorVariables;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.jetbrains.annotations.NotNull;

public final class ControllerSceneSimulationTabWindow {

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
    private void toggleCircleColor(final boolean isOpen) {
        Platform.runLater(() -> circleOpen.setFill(isOpen ? Color.GREEN : Color.RED));
    }

    // Graphic Elements
    @FXML private Circle circleOpen;
    @FXML private ToggleButton toggleButtonOpen;

    // Initialize
    @FXML
    private void initialize() {
        toggleButtonOpen.setSelected(DirectorVariables.IS_WINDOW_OPEN);
        JFXUtils.startVoidServiceTask(() -> {
            while (!configurationComplete) Thread.onSpinWait();
            boolean lastSensorState = dcm.isWindowOpen();
            toggleCircleColor(lastSensorState);
            while (dcm.isConnected()) {
                if (dcm.isWindowOpen() != lastSensorState) {
                    lastSensorState = dcm.isWindowOpen();
                    toggleCircleColor(lastSensorState);
                }
            }
        });
    }

    // EDT
    @FXML
    private void toggleDoorOpen() {
        DirectorVariables.IS_WINDOW_OPEN = toggleButtonOpen.isSelected();
        if (toggleButtonOpen.isSelected()) {
            toggleButtonOpen.setText("APERTA");
            toggleButtonOpen.setTextFill(Color.GREEN);
            circleOpen.setFill(Color.GREEN);
        } else {
            toggleButtonOpen.setText("CHIUSA");
            toggleButtonOpen.setTextFill(Color.RED);
            circleOpen.setFill(Color.RED);
        }
    }
}
