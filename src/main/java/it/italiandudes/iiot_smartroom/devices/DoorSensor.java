package it.italiandudes.iiot_smartroom.devices;

import it.italiandudes.iiot_smartroom.interfaces.ISimulatedSensor;
import it.italiandudes.iiot_smartroom.mqtt.MQTTQoS;
import it.italiandudes.iiot_smartroom.mqtt.SenMLRecord;
import it.italiandudes.iiot_smartroom.utils.DataGenerator;

public final class DoorSensor extends SimulatedMqttDevice implements ISimulatedSensor {

    // Constants
    private static final String TOPIC = "room6329/door";

    // Attributes
    private boolean isOpen = false;

    // Constructors
    public DoorSensor(String deviceId, String brokerUrl) {
        super(deviceId, brokerUrl);
    }

    // Methods
    @Override
    public void simulateAndPublish() { // TODO: replace this with console/ui interaction
        isOpen = DataGenerator.randomBetween(0, 100) > 90;
        String payload = SenMLRecord.builder(deviceId, "is_open")
                .boolValue(isOpen)
                .build()
                .toJson();
        publish(TOPIC, payload, MQTTQoS.QoS_1, true);
    }
}
