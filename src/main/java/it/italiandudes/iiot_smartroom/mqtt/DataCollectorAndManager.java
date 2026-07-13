package it.italiandudes.iiot_smartroom.mqtt;

import it.italiandudes.idl.logger.InfoFlags;
import it.italiandudes.idl.logger.Logger;
import it.italiandudes.iiot_smartroom.mqtt.devices.*;
import it.italiandudes.iiot_smartroom.mqtt.devices.data.AirConditionerMode;
import it.italiandudes.iiot_smartroom.mqtt.devices.data.Weather;
import it.italiandudes.iiot_smartroom.utils.Defs;
import it.italiandudes.iiot_smartroom.utils.RoomDefs;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class DataCollectorAndManager extends SimulatedMqttDevice {

    // Constants
    public static final long DOOR_WINDOW_OPEN_CONDITIONER_OFF_MILLIS = 10000L;

    // Data Cache
    @Getter private boolean isDoorOpen = false;
    @Getter private boolean isWindowOpen = false;
    @Getter private boolean isPowerOn = false;
    @Getter private boolean isConditionerOn = false;
    @Getter private double externalTemperature = 30;
    @Getter private double externalHumidity = 65;
    @Getter private int pm10 = 0;
    @Getter private double wind = 0;
    @Getter private double rain = 0;
    @Getter private int energyConsumption = 0;
    @Getter private double roomTemperature = 25;
    @Getter private double roomHumidity = 45;
    @Getter private double conditionerTemperatureSetpoint = 25;
    @Getter private AirConditionerMode conditionerMode = AirConditionerMode.FAN;
    @Getter private Weather weather = Weather.CLEAR;
    private long doorOpenTimestampMillis = -1;
    private long windowOpenTimestampMillis = -1;
    @Getter @Setter private volatile boolean conditionerDoorWindowOverride = false;

    // Constructor
    public DataCollectorAndManager(@NotNull String deviceId, @NotNull String brokerUrl) {
        super(deviceId, brokerUrl);
    }

    // Methods
    @Override
    protected void onConnected() {
        subscribe(RoomDefs.TOPIC_ALL_SENSORS, MQTTQoS.QoS_1, this::handleData);
        subscribe(RoomDefs.TOPIC_ALL_STATES, MQTTQoS.QoS_1, this::handleData);
    }
    public void actionChangeAirConditionerMode(@NotNull final AirConditionerMode mode) {
        if (conditionerMode == mode) return;
        publish(SmartAirConditioner.TOPIC_MODE_SET, mode.name(), MQTTQoS.QoS_1, true);
    }
    public void actionChangeSetpointTemperature(final double temperature) {
        if (temperature == conditionerTemperatureSetpoint) return;
        publish(SmartAirConditioner.TOPIC_SETPOINT_TEMPERATURE_SET, String.valueOf(temperature), MQTTQoS.QoS_1, true);
    }
    public void actionChangeConditionerOnOff(final boolean newState) {
        if (newState == isConditionerOn) return;
        publish(SmartAirConditioner.TOPIC_IS_ON_SET, String.valueOf(newState), MQTTQoS.QoS_1, true);
    }
    public void actionChangeElectricalPanelOnOff(final boolean newState) {
        if (newState == isPowerOn) return;
        publish(SmartElectricalPanel.TOPIC_IS_ON_SET, String.valueOf(newState), MQTTQoS.QoS_1, true);
    }
    private void handleData(String topic, String rawPayload) {
        if (topic == null || rawPayload == null) return;
        try {
            JSONObject jsonPayload = new JSONArray(rawPayload).getJSONObject(0);
            switch (topic) {
                case DoorSensor.TOPIC -> {
                    isDoorOpen = jsonPayload.getBoolean("vb");
                    if (!conditionerDoorWindowOverride && isConditionerOn && isDoorOpen) {
                        if (doorOpenTimestampMillis == -1) doorOpenTimestampMillis = System.currentTimeMillis();
                    } else doorOpenTimestampMillis = -1;
                }
                case WindowSensor.TOPIC -> {
                    isWindowOpen = jsonPayload.getBoolean("vb");
                    if (!conditionerDoorWindowOverride && isConditionerOn && isWindowOpen) {
                        if (windowOpenTimestampMillis == -1) windowOpenTimestampMillis = System.currentTimeMillis();
                    } else windowOpenTimestampMillis = -1;
                }
                case SmartElectricalPanel.TOPIC_IS_ON -> isPowerOn = jsonPayload.getBoolean("vb");
                case SmartElectricalPanel.TOPIC_ENERGY_CONSUMPTION -> energyConsumption = jsonPayload.getInt("v");
                case SmartAirConditioner.TOPIC_HUMIDITY -> roomHumidity = jsonPayload.getDouble("v");
                case SmartAirConditioner.TOPIC_TEMPERATURE -> roomTemperature = jsonPayload.getDouble("v");
                case SmartAirConditioner.TOPIC_IS_ON -> isConditionerOn = jsonPayload.getBoolean("vb");
                case SmartAirConditioner.TOPIC_SETPOINT_TEMPERATURE -> conditionerTemperatureSetpoint = jsonPayload.getDouble("v");
                case EnvironmentalMonitoringSmartObject.TOPIC_HUMIDITY -> externalHumidity = jsonPayload.getDouble("v");
                case EnvironmentalMonitoringSmartObject.TOPIC_TEMPERATURE -> externalTemperature = jsonPayload.getDouble("v");
                case EnvironmentalMonitoringSmartObject.TOPIC_PM10 -> pm10 = jsonPayload.getInt("v");
                case EnvironmentalMonitoringSmartObject.TOPIC_RAIN -> {
                    rain = jsonPayload.getDouble("v");
                    aggregateExternalWeatherData();
                }
                case EnvironmentalMonitoringSmartObject.TOPIC_WIND -> {
                    wind = jsonPayload.getDouble("v");
                    aggregateExternalWeatherData();
                }
                case SmartAirConditioner.TOPIC_MODE -> {
                    try {
                        conditionerMode = AirConditionerMode.valueOf(jsonPayload.getString("vs"));
                    } catch (IllegalArgumentException e) {
                        Logger.log("[WARN] Invalid conditioner mode: " + jsonPayload.getString("vs"), new InfoFlags(true, false), Defs.LOGGER_CONTEXT);
                        return;
                    }
                }
                default -> Logger.log("[WARN] Unhandled topic " + topic + ": " + jsonPayload.toString(), new InfoFlags(false, false, false, true), Defs.LOGGER_CONTEXT);
            }
            updateDisplay();
            if ((doorOpenTimestampMillis > 0 && System.currentTimeMillis() - doorOpenTimestampMillis > DOOR_WINDOW_OPEN_CONDITIONER_OFF_MILLIS) ||
                    (windowOpenTimestampMillis > 0 && System.currentTimeMillis() - windowOpenTimestampMillis > DOOR_WINDOW_OPEN_CONDITIONER_OFF_MILLIS)) {
                shutdownAirConditioner();
            }
        } catch (JSONException e) {
            Logger.log("[WARN] Invalid payload for topic " + topic + ": " + rawPayload, new InfoFlags(true, false, false, true), Defs.LOGGER_CONTEXT);
        }
    }
    private void updateDisplay() {
        JSONObject updateAction = new JSONObject();
        updateAction.put("wind", wind);
        updateAction.put("rain", rain);
        updateAction.put("pm10", pm10);
        updateAction.put("door_open", isDoorOpen);
        updateAction.put("window_open", isWindowOpen);
        updateAction.put("external_temperature", externalTemperature);
        updateAction.put("external_humidity", externalHumidity);
        updateAction.put("energy_consumption", energyConsumption);
        updateAction.put("power_on", isPowerOn);
        updateAction.put("conditioner_on", isConditionerOn);
        updateAction.put("room_temperature", roomTemperature);
        updateAction.put("room_humidity", roomHumidity);
        updateAction.put("override_door_window_sensors", conditionerDoorWindowOverride);
        updateAction.put("conditioner_setpoint_temperature", conditionerTemperatureSetpoint);
        updateAction.put("conditioner_mode", conditionerMode);
        updateAction.put("weather", weather.name());
        publish(InformationDisplay.TOPIC, updateAction.toString(), MQTTQoS.QoS_1, false);
    }
    private void aggregateExternalWeatherData() {
        if (wind > Weather.STORM.windThreshold && rain > Weather.STORM.rainThreshold) {
            weather = Weather.STORM;
        } else if (wind > Weather.WINDY.windThreshold) {
            weather =  Weather.WINDY;
        } else if (rain > Weather.RAIN.rainThreshold) {
            weather =  Weather.RAIN;
        } else {
            weather =  Weather.CLEAR;
        }
    }
    private void shutdownAirConditioner() {
        doorOpenTimestampMillis = -1;
        windowOpenTimestampMillis = -1;
        if (isConditionerOn) {
            publish(SmartAirConditioner.TOPIC_ENVIRONMENT_OPEN, "{\"environment_open\":true}", MQTTQoS.QoS_1, false);
        }
    }
}
