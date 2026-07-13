package it.italiandudes.iiot_smartroom.mqtt.devices;

import it.italiandudes.iiot_smartroom.mqtt.MQTTQoS;
import it.italiandudes.iiot_smartroom.mqtt.SenMLRecord;
import it.italiandudes.iiot_smartroom.mqtt.interfaces.ISimulatedSensor;
import it.italiandudes.iiot_smartroom.simulation.DirectorVariables;
import it.italiandudes.iiot_smartroom.utils.DataGenerator;
import it.italiandudes.iiot_smartroom.utils.RoomDefs;

public final class SmartElectricalPanel extends SimulatedMqttDevice implements ISimulatedSensor {

    // Topics
    public static final String TOPIC = RoomDefs.ROOT_TOPIC + "electrical_panel/";
    public static final String TOPIC_ENERGY_CONSUMPTION = TOPIC + RoomDefs.SENSORS_TOPIC + "energy_consumption";
    public static final String TOPIC_IS_ON = TOPIC + RoomDefs.STATES_TOPIC + "is_on";
    public static final String TOPIC_IS_ON_SET = TOPIC + RoomDefs.ACTUATORS_TOPIC + "is_on";

    // Simulation Period
    public static final long SIMULATION_PERIOD_MILLIS = 1000;

    // Jitter Amplitudes
    private static final int ENERGY_CONSUMPTION_JITTER_MIN = 1;
    private static final int ENERGY_CONSUMPTION_JITTER_MAX = 5;

    // Attributes
    private boolean isOn = false;

    // Constructors
    public SmartElectricalPanel(String deviceId, String brokerUrl) {
        super(deviceId, brokerUrl);
    }

    // Methods
    @Override
    protected void onConnected() {
        startSimulation(SIMULATION_PERIOD_MILLIS);
        subscribe(TOPIC_IS_ON_SET, MQTTQoS.QoS_1, this::handleIsOnChange);
    }
    private void handleIsOnChange(String payload) {
        try {
            isOn = Boolean.parseBoolean(payload.trim());
            publish(TOPIC_IS_ON, SenMLRecord.builder(deviceId, "is_on").boolValue(isOn).build().toJson(), MQTTQoS.QoS_1, true);
        } catch (Exception ignored) {}
    }
    @Override
    public void simulateAndPublish() {
        int energyConsumption = isOn ? DataGenerator.Jitter.jitter(DirectorVariables.ENERGY_CONSUMPTION_W, ENERGY_CONSUMPTION_JITTER_MIN, ENERGY_CONSUMPTION_JITTER_MAX) : 0;
        publish(TOPIC_ENERGY_CONSUMPTION, SenMLRecord.builder(deviceId, "energy_consumption").unit("W").value(energyConsumption).build().toJson(), MQTTQoS.QoS_1, false);
    }
}
