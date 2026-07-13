package it.italiandudes.iiot_smartroom.mqtt.devices;

import it.italiandudes.idl.logger.InfoFlags;
import it.italiandudes.idl.logger.Logger;
import it.italiandudes.iiot_smartroom.mqtt.MQTTQoS;
import it.italiandudes.iiot_smartroom.mqtt.devices.data.AirConditionerMode;
import it.italiandudes.iiot_smartroom.mqtt.devices.data.Weather;
import it.italiandudes.iiot_smartroom.utils.Defs;
import it.italiandudes.iiot_smartroom.utils.RoomDefs;
import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;

public final class InformationDisplay extends SimulatedMqttDevice {

    // Topics
    public static final String TOPIC = RoomDefs.ROOT_TOPIC + "display/" + RoomDefs.ACTUATORS_TOPIC + "display";

    // Constructors
    public InformationDisplay(String deviceId, String brokerUrl) {
        super(deviceId, brokerUrl);
    }

    // Attributes
    @Getter private volatile String displayMessage = "";

    // Methods
    @Override
    protected void onConnected() {
        subscribe(TOPIC, MQTTQoS.QoS_1, this::handleDisplayMessage);
    }
    private void handleDisplayMessage(String payload) {
        try {
            JSONObject payloadJSON = new JSONObject(payload);
            @SuppressWarnings("StringBufferReplaceableByString") StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Stato Porta: ").append(payloadJSON.getBoolean("door_open") ? "APERTA" : "CHIUSA").append('\n');
            messageBuilder.append("Stato Finestra: ").append(payloadJSON.getBoolean("window_open") ? "APERTA" : "CHIUSA").append('\n');
            messageBuilder.append("Pannello Elettrico:").append('\n')
                    .append("- Stato: ").append(payloadJSON.getBoolean("power_on") ? "ALIMENTATO" : "NON ALIMENTATO").append('\n')
                    .append("- Consumo [W]: ").append(payloadJSON.getInt("energy_consumption")).append('\n');
            messageBuilder.append("Climatizzatore:").append('\n')
                    .append("- Stato: ").append(payloadJSON.getBoolean("conditioner_on") ? "ACCESO" : "SPENTO").append('\n')
                    .append("- Modalita': ").append(AirConditionerMode.valueOf(payloadJSON.getString("conditioner_mode")).displayName).append('\n')
                    .append("- Temperatura Impostata [°C]: ").append(RoomDefs.DECIMAL_FORMATTER.format(payloadJSON.getDouble("conditioner_setpoint_temperature"))).append('\n')
                    .append("- Temperatura Stanza [°C]: ").append(RoomDefs.DECIMAL_FORMATTER.format(payloadJSON.getDouble("room_temperature"))).append('\n')
                    .append("- Umidita' Stanza [RH%]: ").append(RoomDefs.DECIMAL_FORMATTER.format(payloadJSON.getDouble("room_humidity"))).append('\n');
            messageBuilder.append("Monitor Ambientale:").append('\n')
                    .append("- Temperatura Esterna [°C]: ").append(RoomDefs.DECIMAL_FORMATTER.format(payloadJSON.getDouble("external_temperature"))).append('\n')
                    .append("- Umidita' Esterna [RH%]: ").append(RoomDefs.DECIMAL_FORMATTER.format(payloadJSON.getDouble("external_humidity"))).append('\n')
                    .append("- PM10 [ug/m^3]: ").append(payloadJSON.getInt("pm10")).append('\n')
                    .append("- Vento [Km/h]: ").append(RoomDefs.DECIMAL_FORMATTER.format(payloadJSON.getDouble("wind"))).append('\n')
                    .append("- Pioggia [mm]: ").append(RoomDefs.DECIMAL_FORMATTER.format(payloadJSON.getDouble("rain"))).append('\n')
                    .append("- Meteo: ").append(Weather.valueOf(payloadJSON.getString("weather")).displayName).append('\n');
            this.displayMessage = messageBuilder.toString();
            if (Defs.IS_DEBUG_MODE) Logger.log("[DEBUG] Display Payload: " + payload, Defs.LOGGER_CONTEXT);
        } catch (JSONException | IllegalArgumentException e) {
            Logger.log("[WARN] Invalid payload received from InformationDisplay: " + payload, new InfoFlags(true, false, false, true), Defs.LOGGER_CONTEXT);
            Logger.log(e, Defs.LOGGER_CONTEXT);
        }
    }
}
