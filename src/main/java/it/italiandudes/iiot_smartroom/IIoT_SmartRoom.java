package it.italiandudes.iiot_smartroom;

import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;
import it.italiandudes.iiot_smartroom.mqtt.DataCollectorAndManager;
import it.italiandudes.iiot_smartroom.mqtt.broker.BrokerDefs;
import it.italiandudes.iiot_smartroom.mqtt.devices.*;
import it.italiandudes.iiot_smartroom.utils.Defs;
import it.italiandudes.idl.logger.InfoFlags;
import it.italiandudes.idl.logger.Logger;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;

public final class IIoT_SmartRoom {

    // Main Method
    public static void main(String[] args) {

        // Setting Charset to UTF-8
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        // Initializing the logger
        try {
            Logger.init();
            Logger.log("Logger initialized!", Defs.LOGGER_CONTEXT);
        } catch (IOException e) {
            Logger.log("An error has occurred during Logger initialization, exit...", Defs.LOGGER_CONTEXT);
            return;
        }

        // Configure the shutdown hooks
        Logger.log("Configuring Shutdown Hooks...", Defs.LOGGER_CONTEXT);
        Runtime.getRuntime().addShutdownHook(new Thread(Logger::close));
        Logger.log("Shutdown Hooks configured!", Defs.LOGGER_CONTEXT);

        // Check Java Version
        Logger.log("Verifying for Java 21...", Defs.LOGGER_CONTEXT);
        if (!SystemUtils.IS_JAVA_21) {
            Logger.log("The current java version is wrong, to run this jar you need Java 21!", new InfoFlags(true, true, true, true), Defs.LOGGER_CONTEXT);
            return;
        }
        Logger.log("Java 21 Verified!", Defs.LOGGER_CONTEXT);

        // Check OS
        Logger.log("Verifying OS...", Defs.LOGGER_CONTEXT);
        Logger.log("OS Name: " + SystemUtils.OS_NAME, Defs.LOGGER_CONTEXT);
        Logger.log("OS Arch: " + SystemUtils.OS_ARCH, Defs.LOGGER_CONTEXT);

        // Start Broker Service
        Logger.log("Starting embedded MQTT broker at port " + BrokerDefs.BROKER_PORT + "...", Defs.LOGGER_CONTEXT);
        Properties brokerProperties = new Properties();
        brokerProperties.setProperty(IConfig.HOST_PROPERTY_NAME, "0.0.0.0");
        brokerProperties.setProperty(IConfig.PORT_PROPERTY_NAME, String.valueOf(BrokerDefs.BROKER_PORT));
        IConfig brokerConfig = new MemoryConfig(brokerProperties);
        Server broker = new Server();
        try {
            broker.startServer(brokerConfig);
        } catch (IOException e) {
            Logger.log(e, Defs.LOGGER_CONTEXT);
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(broker::stopServer));
        Logger.log("Broker started at port " + BrokerDefs.BROKER_PORT + "!", Defs.LOGGER_CONTEXT);
        Logger.log("Broker URL: " + BrokerDefs.BROKER_URL, Defs.LOGGER_CONTEXT);

        // Start MQTT Devices
        Logger.log("Starting MQTT devices...", Defs.LOGGER_CONTEXT);
        EnvironmentalMonitoringSmartObject envMonitor = new EnvironmentalMonitoringSmartObject("env_monitor", BrokerDefs.BROKER_URL);
        DoorSensor doorSensor = new DoorSensor("door_sensor", BrokerDefs.BROKER_URL);
        WindowSensor windowSensor = new WindowSensor("window_sensor", BrokerDefs.BROKER_URL);
        SmartAirConditioner airConditioner = new SmartAirConditioner("air_conditioner", BrokerDefs.BROKER_URL);
        SmartElectricalPanel electricalPanel = new SmartElectricalPanel("electrical_panel", BrokerDefs.BROKER_URL);
        InformationDisplay display = new InformationDisplay("display", BrokerDefs.BROKER_URL);
        DataCollectorAndManager dcm = new DataCollectorAndManager("dcm", BrokerDefs.BROKER_URL);
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
            Logger.log(e, Defs.LOGGER_CONTEXT);
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dcm.disconnect();
            display.disconnect();
            electricalPanel.disconnect();
            airConditioner.disconnect();
            windowSensor.disconnect();
            doorSensor.disconnect();
            envMonitor.disconnect();
        }));
        Logger.log("MQTT devices initialized and connected!", Defs.LOGGER_CONTEXT);

        Logger.log("Type \"stop\" to shutdown the program...", Defs.LOGGER_CONTEXT);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                if (scanner.nextLine().equalsIgnoreCase("stop")) break;
            } catch (Exception ignored) {}
        }
        Logger.log("Stopping program...", Defs.LOGGER_CONTEXT);
        System.exit(0);

        // App Entry Point
    }
}
