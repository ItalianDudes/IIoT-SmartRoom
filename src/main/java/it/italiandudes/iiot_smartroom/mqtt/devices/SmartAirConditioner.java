package it.italiandudes.iiot_smartroom.mqtt.devices;

import it.italiandudes.idl.logger.Logger;
import it.italiandudes.iiot_smartroom.mqtt.devices.data.AirConditionerMode;
import it.italiandudes.iiot_smartroom.mqtt.interfaces.ISimulatedSensor;
import it.italiandudes.iiot_smartroom.mqtt.MQTTQoS;
import it.italiandudes.iiot_smartroom.mqtt.SenMLRecord;
import it.italiandudes.iiot_smartroom.utils.Defs;
import it.italiandudes.iiot_smartroom.utils.DataGenerator;
import it.italiandudes.iiot_smartroom.simulation.DirectorVariables;
import it.italiandudes.iiot_smartroom.utils.RoomDefs;

public final class SmartAirConditioner extends SimulatedMqttDevice implements ISimulatedSensor {

    // Topics
    public static final String TOPIC = RoomDefs.ROOT_TOPIC + "conditioner/";
    public static final String TOPIC_TEMPERATURE = TOPIC + RoomDefs.SENSORS_TOPIC + "temperature";
    public static final String TOPIC_HUMIDITY = TOPIC + RoomDefs.SENSORS_TOPIC + "humidity";
    public static final String TOPIC_SETPOINT_TEMPERATURE = TOPIC + RoomDefs.STATES_TOPIC + "setpoint_temperature";
    public static final String TOPIC_MODE = TOPIC + RoomDefs.STATES_TOPIC + "mode";
    public static final String TOPIC_IS_ON = TOPIC + RoomDefs.STATES_TOPIC + "is_on";
    public static final String TOPIC_SETPOINT_TEMPERATURE_SET = TOPIC + RoomDefs.ACTUATORS_TOPIC + "setpoint_temperature";
    public static final String TOPIC_MODE_SET = TOPIC + RoomDefs.ACTUATORS_TOPIC + "mode";
    public static final String TOPIC_IS_ON_SET = TOPIC + RoomDefs.ACTUATORS_TOPIC + "is_on";

    // Simulation Period
    public static final long SIMULATION_PERIOD_MILLIS = 1000;

    // Air Conditioner Tech Specs
    public static final int MIN_SETPOINT_TEMPERATURE = 18;
    public static final int MAX_SETPOINT_TEMPERATURE = 30;
    public static final int ECO_SETPOINT_TEMPERATURE = 25;
    private static final double TEMPERATURE_ACTIVE_MIN_STEP = 0.005;  // Conditioner ON
    private static final double TEMPERATURE_ACTIVE_MAX_STEP = 0.01;   // Conditioner ON
    private static final double TEMPERATURE_PASSIVE_MIN_STEP = 0.001; // Conditioner OFF
    private static final double TEMPERATURE_PASSIVE_MAX_STEP = 0.002; // Conditioner OFF
    private static final double HUMIDITY_ACTIVE_MIN_STEP = 0.01;   // Conditioner ON
    private static final double HUMIDITY_ACTIVE_MAX_STEP = 0.05;   // Conditioner ON
    private static final double HUMIDITY_PASSIVE_MIN_STEP = 0.005; // Conditioner OFF
    private static final double HUMIDITY_PASSIVE_MAX_STEP = 0.01;  // Conditioner OFF


    // Attributes
    private double temperature = DirectorVariables.EXTERNAL_TEMPERATURE_CEL;
    private double humidity = DirectorVariables.EXTERNAL_HUMIDITY_RH;
    private double setpointTemperature = 25.0;
    private AirConditionerMode mode = AirConditionerMode.FAN;
    private boolean isOn = true;

    // Constructors
    public SmartAirConditioner(String deviceId, String brokerUrl) {
        super(deviceId, brokerUrl);
    }

    // Methods
    @Override
    protected void onConnected() {
        startSimulation(SIMULATION_PERIOD_MILLIS);
        subscribe(TOPIC_SETPOINT_TEMPERATURE_SET, MQTTQoS.QoS_1, this::handleTargetTempChange);
        subscribe(TOPIC_MODE_SET, MQTTQoS.QoS_1, this::handleModeChange);
        subscribe(TOPIC_IS_ON_SET, MQTTQoS.QoS_1, this::handleIsOnChange);
    }
    private void handleTargetTempChange(String payload) {
        try {
            double value = Double.parseDouble(payload.trim());
            if (value < MIN_SETPOINT_TEMPERATURE || value > MAX_SETPOINT_TEMPERATURE) return;
            setpointTemperature = value;
            publish(TOPIC_SETPOINT_TEMPERATURE, singleMeasure(System.currentTimeMillis() / 1000L, "setpoint_temperature", "Cel", setpointTemperature), MQTTQoS.QoS_1, true);
        } catch (NumberFormatException e) {
            Logger.log(e, Defs.LOGGER_CONTEXT);
        }
    }
    private void handleModeChange(String payload) {
        try {
            mode = AirConditionerMode.valueOf(payload.trim().toUpperCase());
            publish(TOPIC_MODE, SenMLRecord.builder(deviceId, "mode").stringValue(mode.name()).build().toJson(), MQTTQoS.QoS_1, true);
        } catch (Exception e) {
            Logger.log(e, Defs.LOGGER_CONTEXT);
        }
    }
    private void handleIsOnChange(String payload) {
        try {
            isOn = Boolean.parseBoolean(payload.trim());
            publish(TOPIC_IS_ON, SenMLRecord.builder(deviceId, "is_on").boolValue(isOn).build().toJson(), MQTTQoS.QoS_1, true);
        } catch (Exception e) {
            Logger.log(e, Defs.LOGGER_CONTEXT);
        }
    }
    @Override
    public void simulateAndPublish() {
        genValues();
        long timestamp = System.currentTimeMillis() / 1000L;
        publish(TOPIC_TEMPERATURE, singleMeasure(timestamp, "temperature", "Cel", temperature), MQTTQoS.QoS_1, false);
        publish(TOPIC_HUMIDITY, singleMeasure(timestamp, "humidity", "%RH", humidity), MQTTQoS.QoS_1, false);
    }
    private void genValues() {
        if (!isOn || mode == AirConditionerMode.FAN) {
            temperature = DataGenerator.driftTowards(temperature, DirectorVariables.EXTERNAL_TEMPERATURE_CEL, TEMPERATURE_PASSIVE_MIN_STEP, TEMPERATURE_PASSIVE_MAX_STEP);
            humidity = DataGenerator.driftTowards(humidity, DirectorVariables.EXTERNAL_HUMIDITY_RH, HUMIDITY_PASSIVE_MIN_STEP, HUMIDITY_PASSIVE_MAX_STEP);
            return;
        }

        switch (mode) {
            case HEAT -> {
                temperature = DataGenerator.increaseValueTowards(temperature, TEMPERATURE_ACTIVE_MIN_STEP, TEMPERATURE_ACTIVE_MAX_STEP, setpointTemperature);
                humidity = DataGenerator.Jitter.approachAndSettleAtFloor(humidity, HUMIDITY_ACTIVE_MIN_STEP, HUMIDITY_ACTIVE_MAX_STEP, RoomDefs.IDEAL_ROOM_HUMIDITY);
            }
            case COOL -> {
                temperature = DataGenerator.decreaseValueTowards(temperature, TEMPERATURE_ACTIVE_MIN_STEP, TEMPERATURE_ACTIVE_MAX_STEP, setpointTemperature);
                humidity = DataGenerator.Jitter.approachAndSettleAtFloor(humidity, HUMIDITY_ACTIVE_MIN_STEP, HUMIDITY_ACTIVE_MAX_STEP, RoomDefs.IDEAL_ROOM_HUMIDITY);
            }
            case DRY -> {
                temperature = DataGenerator.driftTowards(temperature, DirectorVariables.EXTERNAL_TEMPERATURE_CEL, TEMPERATURE_PASSIVE_MIN_STEP, TEMPERATURE_PASSIVE_MAX_STEP);
                humidity = DataGenerator.Jitter.approachAndSettleAtFloor(humidity, HUMIDITY_ACTIVE_MIN_STEP * 2, HUMIDITY_ACTIVE_MAX_STEP * 2, RoomDefs.IDEAL_ROOM_HUMIDITY);
            }
            case ECO -> {
                temperature = DataGenerator.decreaseValueTowards(temperature, TEMPERATURE_ACTIVE_MIN_STEP, TEMPERATURE_ACTIVE_MAX_STEP, ECO_SETPOINT_TEMPERATURE);
                humidity = DataGenerator.Jitter.approachAndSettleAtFloor(humidity, HUMIDITY_ACTIVE_MIN_STEP, HUMIDITY_ACTIVE_MAX_STEP, RoomDefs.IDEAL_ROOM_HUMIDITY);
            }
        }
    }
    private String singleMeasure(long bt, String measureName, String unit, double value) {
        return SenMLRecord.builder(deviceId, measureName)
                .timestamp(bt)
                .unit(unit)
                .value(value)
                .build()
                .toJson();
    }
}
