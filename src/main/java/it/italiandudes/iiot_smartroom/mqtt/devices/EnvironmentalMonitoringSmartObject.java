package it.italiandudes.iiot_smartroom.mqtt.devices;

import it.italiandudes.iiot_smartroom.mqtt.interfaces.ISimulatedSensor;
import it.italiandudes.iiot_smartroom.mqtt.MQTTQoS;
import it.italiandudes.iiot_smartroom.mqtt.SenMLRecord;
import it.italiandudes.iiot_smartroom.simulation.DirectorVariables;
import it.italiandudes.iiot_smartroom.utils.DataGenerator;
import it.italiandudes.iiot_smartroom.utils.RoomDefs;

public final class EnvironmentalMonitoringSmartObject extends SimulatedMqttDevice implements ISimulatedSensor {

    // Topics
    public static final String TOPIC = RoomDefs.ROOT_TOPIC + RoomDefs.SENSORS_TOPIC + "outside/";
    public static final String TOPIC_TEMPERATURE = TOPIC + "temperature";
    public static final String TOPIC_HUMIDITY = TOPIC + "humidity";
    public static final String TOPIC_PM10 = TOPIC + "pm10";
    public static final String TOPIC_WIND = TOPIC + "wind";
    public static final String TOPIC_RAIN = TOPIC + "rain";

    // Simulation Period
    public static final long SIMULATION_PERIOD_MILLIS = 1000;

    // Jitter Amplitudes
    private static final double TEMPERATURE_JITTER_MIN = 0.01;
    private static final double TEMPERATURE_JITTER_MAX = 0.02;
    private static final double HUMIDITY_JITTER_MIN = 0.01;
    private static final double HUMIDITY_JITTER_MAX = 0.02;
    private static final double HUMIDITY_MIN = 0.0;
    private static final double HUMIDITY_MAX = 100.0;
    private static final int PM10_JITTER_MIN = 1;
    private static final int PM10_JITTER_MAX = 5;
    private static final int PM10_MIN = 0;
    private static final int PM10_MAX = Integer.MAX_VALUE;
    private static final double WIND_JITTER_MIN = 0.3;
    private static final double WIND_JITTER_MAX = 1.5;
    private static final double WIND_MIN = 0.0;
    private static final double WIND_MAX = Integer.MAX_VALUE;
    private static final double RAIN_JITTER_MIN = 0.01;
    private static final double RAIN_JITTER_MAX = 0.05;
    private static final double RAIN_MIN = 0.0;
    private static final double RAIN_MAX = Integer.MAX_VALUE;

    // Attributes
    private double temperature = DirectorVariables.EXTERNAL_TEMPERATURE_CEL;
    private double humidity = DirectorVariables.EXTERNAL_HUMIDITY_RH;
    private int pm10 = DirectorVariables.PM10_UG_M3;
    private double wind = DirectorVariables.WIND_SPEED_KMH;
    private double rain = DirectorVariables.RAIN_VOLUME_MM;

    // Constructors
    public EnvironmentalMonitoringSmartObject(String deviceId, String brokerUrl) {
        super(deviceId, brokerUrl);
    }

    // Methods
    @Override
    protected void onConnected() {
        startSimulation(SIMULATION_PERIOD_MILLIS);
    }
    @Override
    public void simulateAndPublish() {
        genValues();
        long timestamp = System.currentTimeMillis() / 1000L;
        publish(TOPIC_TEMPERATURE, singleMeasure(timestamp, "temperature", "Cel", temperature), MQTTQoS.QoS_1, false);
        publish(TOPIC_HUMIDITY, singleMeasure(timestamp, "humidity", "%RH", humidity), MQTTQoS.QoS_1, false);
        publish(TOPIC_PM10, singleMeasure(timestamp, "pm10", "ug/m3", pm10), MQTTQoS.QoS_1, false);
        publish(TOPIC_WIND, singleMeasure(timestamp, "wind", "km/h", wind), MQTTQoS.QoS_1, false);
        publish(TOPIC_RAIN, singleMeasure(timestamp, "rain", "mm", rain), MQTTQoS.QoS_1, false);
    }
    private void genValues() {
        temperature = DataGenerator.Jitter.jitter(DirectorVariables.EXTERNAL_TEMPERATURE_CEL, TEMPERATURE_JITTER_MIN, TEMPERATURE_JITTER_MAX);
        humidity = DataGenerator.Jitter.jitter(DirectorVariables.EXTERNAL_HUMIDITY_RH, HUMIDITY_JITTER_MIN, HUMIDITY_JITTER_MAX, HUMIDITY_MIN, HUMIDITY_MAX);
        pm10 = DataGenerator.Jitter.jitter(DirectorVariables.PM10_UG_M3, PM10_JITTER_MIN, PM10_JITTER_MAX, PM10_MIN, PM10_MAX);
        wind = DataGenerator.Jitter.jitter(DirectorVariables.WIND_SPEED_KMH, WIND_JITTER_MIN, WIND_JITTER_MAX, WIND_MIN, WIND_MAX);
        rain = DataGenerator.Jitter.jitter(DirectorVariables.RAIN_VOLUME_MM, RAIN_JITTER_MIN, RAIN_JITTER_MAX, RAIN_MIN, RAIN_MAX);
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
