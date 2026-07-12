package it.italiandudes.iiot_smartroom.mqtt.devices;

import it.italiandudes.iiot_smartroom.mqtt.interfaces.ISimulatedSensor;
import it.italiandudes.iiot_smartroom.mqtt.MQTTQoS;
import it.italiandudes.iiot_smartroom.mqtt.SenMLRecord;
import it.italiandudes.iiot_smartroom.utils.DataGenerator;
import it.italiandudes.iiot_smartroom.utils.RoomDefs;

public final class WindowSensor extends SimulatedMqttDevice implements ISimulatedSensor {

    // Constants
    public static final String TOPIC = RoomDefs.ROOT_TOPIC + "window";

    // Simulation Period
    public static final long SIMULATION_PERIOD_MILLIS = 1000;

    // Attributes
    private boolean isOpen = false;

    // Constructors
    public WindowSensor(String deviceId, String brokerUrl) {
        super(deviceId, brokerUrl);
    }

    // Methods
    @Override
    protected void onConnected() {
        startSimulation(SIMULATION_PERIOD_MILLIS);
    }
    @Override
    public void simulateAndPublish() {  // TODO: replace this with console/ui interaction
        isOpen = DataGenerator.randomBetween(0, 100) > 95;
        publish(TOPIC, SenMLRecord.builder(deviceId, "is_open").boolValue(isOpen).build().toJson(), MQTTQoS.QoS_1, false);
    }
}
