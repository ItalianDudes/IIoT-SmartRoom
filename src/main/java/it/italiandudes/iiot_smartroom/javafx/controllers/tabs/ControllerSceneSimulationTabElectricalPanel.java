package it.italiandudes.iiot_smartroom.javafx.controllers.tabs;

import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.javafx.alert.ErrorAlert;
import it.italiandudes.iiot_smartroom.javafx.Client;
import it.italiandudes.iiot_smartroom.mqtt.DataCollectorAndManager;
import it.italiandudes.iiot_smartroom.simulation.DirectorVariables;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.jetbrains.annotations.NotNull;

public final class ControllerSceneSimulationTabElectricalPanel {

    // Attributes
    private DataCollectorAndManager dcm = null;
    private volatile boolean configurationComplete = false;
    private int cacheConsumption = 0;
    private boolean cacheIsPowerOn = true;

    // Methods
    public void setDCM(@NotNull final DataCollectorAndManager dcm) {
        this.dcm = dcm;
    }
    public void configurationComplete() {
        configurationComplete = true;
    }

    // Graphic Elements
    @FXML private Circle circlePowered;
    @FXML private Label labelConsume;
    @FXML private ToggleButton toggleButtonPowerSwitch;
    @FXML private TextField textFieldConsume;

    // Initialize
    @FXML
    private void initialize() {
        JFXUtils.startVoidServiceTask(() -> {
            while (!configurationComplete) Thread.onSpinWait();
            Platform.runLater(() -> {
                toggleButtonPowerSwitch.setSelected(dcm.isPowerOn());
                togglePowerSwitch();
                textFieldConsume.setText(String.valueOf(DirectorVariables.ENERGY_CONSUMPTION_W));
                applyConsume();
            });
            while (dcm.isConnected()) {
                if (cacheIsPowerOn != dcm.isPowerOn() || cacheConsumption != dcm.getEnergyConsumption()) {
                    cacheIsPowerOn = dcm.isPowerOn();
                    cacheConsumption = dcm.getEnergyConsumption();
                    Platform.runLater(() -> {
                        circlePowered.setFill(cacheIsPowerOn ? Color.GREEN : Color.RED);
                        labelConsume.setText(String.valueOf(cacheConsumption));
                    });
                }
            }
        });
    }

    // EDT
    @FXML
    private void togglePowerSwitch() {
        if (toggleButtonPowerSwitch.isSelected()) {
            toggleButtonPowerSwitch.setText("SI");
            toggleButtonPowerSwitch.setTextFill(Color.GREEN);
            circlePowered.setFill(Color.GREEN);
        } else {
            toggleButtonPowerSwitch.setText("NO");
            toggleButtonPowerSwitch.setTextFill(Color.RED);
            circlePowered.setFill(Color.RED);
        }
        JFXUtils.startVoidServiceTask(() -> dcm.actionChangeElectricalPanelOnOff(toggleButtonPowerSwitch.isSelected()));
    }
    @FXML
    private void applyConsume() {
        try {
            int value = Integer.parseInt(textFieldConsume.getText());
            if (value < 0) throw new NumberFormatException("Value must be greater than 0");
            DirectorVariables.ENERGY_CONSUMPTION_W = value;
        } catch (NumberFormatException e) {
            new ErrorAlert(Client.getStage(), "ERRORE", "Errore Formato Numero", "Il campo di testo deve contenere un numero intero positivo o nullo.");
        }
    }
}
