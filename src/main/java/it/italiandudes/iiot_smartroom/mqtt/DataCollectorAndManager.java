package it.italiandudes.iiot_smartroom.mqtt;

import it.italiandudes.idl.logger.InfoFlags;
import it.italiandudes.idl.logger.Logger;
import it.italiandudes.iiot_smartroom.mqtt.devices.*;
import it.italiandudes.iiot_smartroom.mqtt.devices.data.AirConditionerMode;
import it.italiandudes.iiot_smartroom.mqtt.devices.data.Weather;
import it.italiandudes.iiot_smartroom.utils.Defs;
import it.italiandudes.iiot_smartroom.utils.RoomDefs;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class DataCollectorAndManager extends SimulatedMqttDevice {

    // Data Cache
    private boolean isDoorOpen = false;
    private boolean isWindowOpen = false;
    private boolean isPowerOn = false;
    private boolean isConditionerOn = false;
    private double externalTemperature = 0;
    private double externalHumidity = 0;
    private double pm10 = 0;
    private double wind = 0;
    private double rain = 0;
    private int energyConsumption = 0;
    private double roomTemperature = 0;
    private double roomHumidity = 0;
    private double conditionerTemperatureSetpoint = 0;
    private AirConditionerMode conditionerMode = AirConditionerMode.FAN;
    private Weather weather = Weather.CLEAR;

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
    private void handleData(String topic, String rawPayload) {
        if (topic == null || rawPayload == null) return;
        try {
            JSONObject jsonPayload = new JSONArray(rawPayload).getJSONObject(0);
            switch (topic) {
                case DoorSensor.TOPIC -> isDoorOpen = jsonPayload.getBoolean("vb");
                case WindowSensor.TOPIC -> isWindowOpen = jsonPayload.getBoolean("vb");
                case SmartElectricalPanel.TOPIC_IS_ON -> isPowerOn = jsonPayload.getBoolean("vb");
                case SmartElectricalPanel.TOPIC_ENERGY_CONSUMPTION -> energyConsumption = jsonPayload.getInt("v");
                case SmartAirConditioner.TOPIC_HUMIDITY -> roomHumidity = jsonPayload.getDouble("v");
                case SmartAirConditioner.TOPIC_TEMPERATURE -> roomTemperature = jsonPayload.getDouble("v");
                case SmartAirConditioner.TOPIC_IS_ON -> isConditionerOn = jsonPayload.getBoolean("v");
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
        updateAction.put("conditioner_setpoint_temperature", conditionerTemperatureSetpoint);
        updateAction.put("conditioner_mode", conditionerMode);
        updateAction.put("weather", weather.name());
        publish(InformationDisplay.TOPIC, updateAction.toString(), MQTTQoS.QoS_1, false);
    }
    private void aggregateExternalWeatherData() {
        if (wind > Weather.STORM.windThreshold && rain > Weather.STORM.rainThreshold) {
            weather = Weather.STORM;
        } else if (wind > Weather.WINDY.windThreshold && rain < Weather.WINDY.rainThreshold) {
            weather =  Weather.WINDY;
        } else if (wind < Weather.RAIN.windThreshold && rain > Weather.RAIN.rainThreshold) {
            weather =  Weather.RAIN;
        } else {
            weather =  Weather.CLEAR;
        }
    }
}
