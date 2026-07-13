package it.italiandudes.iiot_smartroom.javafx.controllers.tabs;

import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.iiot_smartroom.mqtt.devices.InformationDisplay;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.jetbrains.annotations.NotNull;

public final class ControllerSceneSimulationTabDisplay {

    // Attributes
    private InformationDisplay deviceDisplay = null;
    private volatile boolean configurationComplete = false;
    private String cacheDisplay = "";

    // Methods
    public void setDeviceDisplay(@NotNull final InformationDisplay deviceDisplay) {
        this.deviceDisplay = deviceDisplay;
    }
    public void configurationComplete() {
        configurationComplete = true;
    }

    // Graphic Elements
    @FXML private TextArea textAreaDisplay;

    // Initialize
    @FXML
    private void initialize() {
        textAreaDisplay.setStyle("-fx-font-weight: bold;");
        JFXUtils.startVoidServiceTask(() -> {
            while (!configurationComplete) Thread.onSpinWait();
            Platform.runLater(() -> textAreaDisplay.setText(deviceDisplay.getDisplayMessage()));
            while (deviceDisplay.isConnected()) {
                if (!cacheDisplay.equalsIgnoreCase(deviceDisplay.getDisplayMessage())) {
                    cacheDisplay = deviceDisplay.getDisplayMessage();
                    Platform.runLater(() -> textAreaDisplay.setText(cacheDisplay));
                }
            }
        });
    }
}
