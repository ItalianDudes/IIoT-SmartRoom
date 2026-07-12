package it.italiandudes.iiot_smartroom.mqtt.devices;

import it.italiandudes.idl.logger.Logger;
import it.italiandudes.iiot_smartroom.mqtt.MQTTQoS;
import it.italiandudes.iiot_smartroom.utils.Defs;
import it.italiandudes.iiot_smartroom.utils.RoomDefs;

public final class InformationDisplay extends SimulatedMqttDevice {

    // Topics
    public static final String TOPIC = RoomDefs.ACTUATORS_TOPIC + "display";

    // Constructors
    public InformationDisplay(String deviceId, String brokerUrl) {
        super(deviceId, brokerUrl);
    }

    // Attributes
    private String displayMessage = ""; // TODO: somewhere this value needs to be displayed, probably UI.

    // Methods
    @Override
    protected void onConnected() {
        subscribe(TOPIC, MQTTQoS.QoS_1, this::handleDisplayMessage);
    }
    private void handleDisplayMessage(String payload) {
        this.displayMessage = payload;
        if (Defs.IS_DEBUG_MODE) Logger.log("[DEBUG] Display Payload: " + payload, Defs.LOGGER_CONTEXT);
    }
}
