package it.italiandudes.iiot_smartroom.javafx.controllers;

import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import it.italiandudes.idl.javafx.JFXUtils;
import it.italiandudes.idl.javafx.components.SceneController;
import it.italiandudes.idl.logger.Logger;
import it.italiandudes.iiot_smartroom.javafx.Client;
import it.italiandudes.iiot_smartroom.javafx.controllers.tabs.*;
import it.italiandudes.iiot_smartroom.javafx.scene.tabs.*;
import it.italiandudes.iiot_smartroom.javafx.utils.Settings;
import it.italiandudes.iiot_smartroom.mqtt.DataCollectorAndManager;
import it.italiandudes.iiot_smartroom.mqtt.broker.BrokerDefs;
import it.italiandudes.iiot_smartroom.mqtt.devices.*;
import it.italiandudes.iiot_smartroom.utils.Defs;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import lombok.Getter;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("unused")
public final class ControllerSceneSimulation {

    // Attributes
    @NotNull private final Thread shutdownThread = new Thread(this::shutdownMQTT);
    @NotNull private final Server broker = new Server();
    @NotNull private final DoorSensor doorSensor = new DoorSensor("door_sensor" + System.currentTimeMillis(), BrokerDefs.getBrokerURL(Client.getBrokerPort()));
    @NotNull private final WindowSensor windowSensor = new WindowSensor("window_sensor" + System.currentTimeMillis(), BrokerDefs.getBrokerURL(Client.getBrokerPort()));
    @NotNull private final EnvironmentalMonitoringSmartObject envMonitor = new EnvironmentalMonitoringSmartObject("env_monitor" + System.currentTimeMillis(), BrokerDefs.getBrokerURL(Client.getBrokerPort()));
    @NotNull private final SmartAirConditioner airConditioner = new SmartAirConditioner("air_conditioner" + System.currentTimeMillis(), BrokerDefs.getBrokerURL(Client.getBrokerPort()));
    @NotNull private final SmartElectricalPanel electricalPanel = new SmartElectricalPanel("electrical_panel" + System.currentTimeMillis(), BrokerDefs.getBrokerURL(Client.getBrokerPort()));
    @NotNull private final InformationDisplay display = new InformationDisplay("display" + System.currentTimeMillis(), BrokerDefs.getBrokerURL(Client.getBrokerPort()));
    @NotNull private final DataCollectorAndManager dcm = new DataCollectorAndManager("dcm" + System.currentTimeMillis(), BrokerDefs.getBrokerURL(Client.getBrokerPort()));
    @Getter private volatile boolean simulationReady = false;

    // Scene Controllers
    private ControllerSceneSimulationTabDoor controllerDoor = null;
    private ControllerSceneSimulationTabWindow controllerWindow = null;
    private ControllerSceneSimulationTabEnvironmentalMonitor controllerEnvironmentalMonitor = null;
    private ControllerSceneSimulationTabAirConditioner controllerAirConditioner = null;
    private ControllerSceneSimulationTabElectricalPanel controllerElectricalPanel = null;
    private ControllerSceneSimulationTabDisplay controllerDisplay = null;
    private ControllerSceneSimulationTabSettings controllerSettings = null;

    // Controller Providers
    public @NotNull ControllerSceneSimulationTabDoor getControllerDoor() {
        return controllerDoor;
    }
    public @NotNull ControllerSceneSimulationTabSettings getControllerSettings() {
        return controllerSettings;
    }

    // Methods
    public void shutdownMQTT() {
        shutdownDevices();
        shutdownBroker();
        try {
            Runtime.getRuntime().removeShutdownHook(shutdownThread);
            Logger.log("Graceful MQTT shutdown detected, shutdown hook removed.", Defs.LOGGER_CONTEXT);
        } catch (IllegalStateException ignored) {}
    }
    private void shutdownDevices() {
        dcm.disconnect();
        display.disconnect();
        electricalPanel.disconnect();
        airConditioner.disconnect();
        windowSensor.disconnect();
        doorSensor.disconnect();
        envMonitor.disconnect();
    }
    private void shutdownBroker() {
        broker.stopServer();
    }

    // Graphic Elements
    @FXML private Tab tabDoorSensor;
    @FXML private Tab tabWindowSensor;
    @FXML private Tab tabEnvironmentalMonitoring;
    @FXML private Tab tabDisplay;
    @FXML private Tab tabConditioner;
    @FXML private Tab tabElectricalPanel;
    @FXML private Tab tabDirector;
    @FXML private Tab tabSettings;

    // Initialize
    @FXML
    private void initialize() {
        JFXUtils.startVoidServiceTask(() -> {

            // Start Broker Service
            Logger.log("Starting embedded MQTT broker at port " + Client.getBrokerPort() + "...", Defs.LOGGER_CONTEXT);
            Properties brokerProperties = new Properties();
            brokerProperties.setProperty(IConfig.HOST_PROPERTY_NAME, "0.0.0.0");
            brokerProperties.setProperty(IConfig.PORT_PROPERTY_NAME, String.valueOf(Settings.getSettings().getInt("broker_port")));
            IConfig brokerConfig = new MemoryConfig(brokerProperties);
            try {
                broker.startServer(brokerConfig);
            } catch (IOException e) {
                Logger.log("An error has occurred while starting the broker, shutting down...", Defs.LOGGER_CONTEXT);
                shutdownMQTT();
                Client.showMessageAndGoToMenu(e);
                return;
            }
            Logger.log("Broker started at port " + Client.getBrokerPort() + "!", Defs.LOGGER_CONTEXT);
            Logger.log("Broker URL: " + BrokerDefs.getBrokerURL(Client.getBrokerPort()), Defs.LOGGER_CONTEXT);

            // Starting MQTT Devices
            Logger.log("Starting MQTT devices...", Defs.LOGGER_CONTEXT);
            try {
                envMonitor.connect();
                doorSensor.connect();
                windowSensor.connect();
                airConditioner.connect();
                electricalPanel.connect();
                display.connect();
                dcm.connect();
            } catch (MqttException e) {
                Logger.log("A MQTT failed to connect to the broker, shutting down...", Defs.LOGGER_CONTEXT);
                shutdownMQTT();
                Client.showMessageAndGoToMenu(e);
                return;
            }
            Runtime.getRuntime().addShutdownHook(shutdownThread);
            Logger.log("MQTT devices initialized and connected!", Defs.LOGGER_CONTEXT);

            // Rendering UI
            Platform.runLater(() -> {
                SceneController sceneControllerDoor = SceneSimulationTabDoor.getScene(dcm);
                controllerDoor = (ControllerSceneSimulationTabDoor) sceneControllerDoor.controller();
                tabDoorSensor.setContent(sceneControllerDoor.getParent());

                SceneController sceneControllerWindow = SceneSimulationTabWindow.getScene(dcm);
                controllerWindow = (ControllerSceneSimulationTabWindow) sceneControllerWindow.controller();
                tabWindowSensor.setContent(sceneControllerWindow.getParent());

                SceneController sceneControllerEnvironmentMonitor = SceneSimulationTabEnvironmentalMonitor.getScene(dcm);
                controllerEnvironmentalMonitor = (ControllerSceneSimulationTabEnvironmentalMonitor) sceneControllerEnvironmentMonitor.controller();
                tabEnvironmentalMonitoring.setContent(sceneControllerEnvironmentMonitor.getParent());

                SceneController sceneControllerConditioner = SceneSimulationTabAirConditioner.getScene(dcm);
                controllerAirConditioner = (ControllerSceneSimulationTabAirConditioner) sceneControllerConditioner.getController();
                tabConditioner.setContent(sceneControllerConditioner.getParent());

                SceneController sceneControllerElectricalPanel = SceneSimulationTabElectricalPanel.getScene(dcm);
                controllerElectricalPanel = (ControllerSceneSimulationTabElectricalPanel) sceneControllerElectricalPanel.getController();
                tabElectricalPanel.setContent(sceneControllerElectricalPanel.getParent());

                SceneController sceneControllerSettings = SceneSimulationTabSettings.getScene(this);
                controllerSettings = (ControllerSceneSimulationTabSettings) sceneControllerSettings.controller();
                tabSettings.setContent(sceneControllerSettings.getParent());

                SceneController sceneControllerDisplay = SceneSimulationTabDisplay.getScene(display);
                controllerDisplay = (ControllerSceneSimulationTabDisplay) sceneControllerDisplay.getController();
                tabDisplay.setContent(sceneControllerDisplay.getParent());

                simulationReady = true; // Loading Stops, UI being shown.
            });
        });
    }

    // EDT
}
