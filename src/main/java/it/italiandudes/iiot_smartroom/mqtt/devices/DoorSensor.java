package it.italiandudes.iiot_smartroom.mqtt.devices;

import it.italiandudes.iiot_smartroom.mqtt.MQTTQoS;
import it.italiandudes.iiot_smartroom.mqtt.SenMLRecord;
import it.italiandudes.iiot_smartroom.mqtt.interfaces.ISimulatedSensor;
import it.italiandudes.iiot_smartroom.simulation.DirectorVariables;
import it.italiandudes.iiot_smartroom.utils.RoomDefs;

public final class DoorSensor extends SimulatedMqttDevice implements ISimulatedSensor {

    // Constants
    public static final String TOPIC = RoomDefs.ROOT_TOPIC + "door/" + RoomDefs.SENSORS_TOPIC + "is_open";
    public static final long SIMULATION_PERIOD_MILLIS = 1000;

    // Constructors
    public DoorSensor(String deviceId, String brokerUrl) {
        super(deviceId, brokerUrl);
    }

    // Methods
    @Override
    protected void onConnected() {
        startSimulation(SIMULATION_PERIOD_MILLIS);
    }
    @Override
    public void simulateAndPublish() {
        publish(TOPIC, SenMLRecord.builder(deviceId, "is_open").boolValue(DirectorVariables.IS_DOOR_OPEN).build().toJson(), MQTTQoS.QoS_1, false);
    }
}
